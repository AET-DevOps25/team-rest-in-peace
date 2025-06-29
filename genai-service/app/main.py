import os
from fastapi import FastAPI, HTTPException, BackgroundTasks
from pydantic import BaseModel
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_google_genai import GoogleGenerativeAIEmbeddings
from typing import List
from app.config import MODEL_CONFIG, PROMPTS
import asyncpg
import asyncio
import logging

app = FastAPI(title="German Plenary Protocol API", version="1.0.0")

NLP_API_KEY = os.getenv("NLP_GENAI_API_KEY")
NLP_DB_USERNAME = os.getenv("NLP_DB_USERNAME")
NLP_DB_PASSWORD = os.getenv("NLP_DB_PASSWORD")
NLP_DB_HOST = os.getenv("DB_HOST", "localhost")
NLP_DB_PORT = os.getenv("DB_PORT", "5432")
NLP_DB_NAME = os.getenv("DB_NAME", "postgres")

if not NLP_API_KEY:
    raise ValueError("Please set the NLP_GENAI_API_KEY in your environment variables.")

if not NLP_DB_USERNAME or not NLP_DB_PASSWORD:
    raise ValueError("Please set NLP_DB_USERNAME and NLP_DB_PASSWORD in your environment variables.")

# Initialize models
llm = ChatGoogleGenerativeAI(
    model=MODEL_CONFIG["llm_model"],
    google_api_key=NLP_API_KEY
)

embeddings = GoogleGenerativeAIEmbeddings(
    model=MODEL_CONFIG["embedding_model"],
    google_api_key=NLP_API_KEY
)

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s"
)
logger = logging.getLogger(__name__)

# Database connection
async def get_db_connection():
    return await asyncpg.connect(
        user=NLP_DB_USERNAME,
        password=NLP_DB_PASSWORD,
        host=NLP_DB_HOST,
        port=NLP_DB_PORT,
        database=NLP_DB_NAME
    )


# Request/Response Models
class SummaryRequest(BaseModel):
    text: str


class EmbeddingRequest(BaseModel):
    text: str


class CombinedRequest(BaseModel):
    text: str


class ProcessSpeechesRequest(BaseModel):
    speech_ids: List[int]


class SummaryResponse(BaseModel):
    summary: str


class EmbeddingResponse(BaseModel):
    embedding: List[float]


class CombinedResponse(BaseModel):
    summary: str
    embedding: List[float]


@app.get("/health")
def health_check():
    return {"healthy": True}


@app.post("/summary", response_model=SummaryResponse)
def summarize_protocol(request: SummaryRequest):
    """Summarize German plenary protocols"""
    try:
        full_prompt = PROMPTS["summary"].format(text=request.text)
        result = llm.invoke(full_prompt)

        return SummaryResponse(summary=result.content)

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Summary generation failed: {str(e)}")


@app.post("/embedding", response_model=EmbeddingResponse)
def embed_text(request: EmbeddingRequest):
    """Generate embeddings for text"""
    try:
        embedding_vector = embeddings.embed_query(request.text)

        return EmbeddingResponse(embedding=embedding_vector)

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Embedding generation failed: {str(e)}")


@app.post("/combined", response_model=CombinedResponse)
def summarize_and_embed(request: CombinedRequest):
    """Generate both summary and embedding for text"""
    try:
        # Generate summary
        full_prompt = PROMPTS["summary"].format(text=request.text)
        summary_result = llm.invoke(full_prompt)
        summary = summary_result.content

        # Generate embedding for the summary
        embedding_vector = embeddings.embed_query(summary)

        return CombinedResponse(
            summary=summary,
            embedding=embedding_vector
        )

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Combined processing failed: {str(e)}")

@app.post("/process-speeches", status_code=202)
async def process_speeches(request: ProcessSpeechesRequest):
    asyncio.create_task(process_speeches_task(request.speech_ids))
    return {"message": "Speech processing started"}

async def process_speeches_task(speech_ids: List[int]):
    """Process speeches by IDs: fetch from DB, generate summaries and embeddings, then update DB"""
    if not speech_ids:
        raise HTTPException(status_code=400, detail="speech_ids cannot be empty")

    conn = None
    processed_count = 0
    failed_speeches = []

    try:
        conn = await get_db_connection()

        for speech_id in speech_ids:
            try:
                logger.info(f"Summarizing speech with id: {speech_id}")

                speech_query = """
                    SELECT id, text_plain, text_summary, text_embedding 
                    FROM speech 
                    WHERE id = $1
                """
                speech_record = await conn.fetchrow(speech_query, speech_id)

                if not speech_record:
                    failed_speeches.append(speech_id)
                    continue

                text_plain = speech_record['text_plain']
                if not text_plain or text_plain.strip() == '':
                    failed_speeches.append(speech_id)
                    continue

                full_prompt = PROMPTS["summary"].format(text=text_plain)
                summary_result = llm.invoke(full_prompt)
                summary = summary_result.content

                embedding_vector = embeddings.embed_query(summary)
                embedding_str = '[' + ','.join(map(str, embedding_vector)) + ']'

                update_query = """
                    UPDATE speech 
                    SET text_summary = $1, text_embedding = $2 
                    WHERE id = $3
                """
                await conn.execute(update_query, summary, embedding_str, speech_id)

                processed_count += 1

            except Exception as e:
                logger.error(f"Error processing speech {speech_id}: {str(e)}")
                failed_speeches.append(speech_id)

    except Exception as e:
        logger.error(f"Database connection failed: {str(e)}")

    finally:
        if conn:
            await conn.close()


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)