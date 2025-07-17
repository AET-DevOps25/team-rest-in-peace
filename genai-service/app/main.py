import os
import time

from fastapi import FastAPI, HTTPException, BackgroundTasks, Request, Response
from pydantic import BaseModel
from typing import List

import asyncpg
from fastapi import FastAPI, HTTPException
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_google_genai import GoogleGenerativeAIEmbeddings
from typing import List
from app.config import MODEL_CONFIG, PROMPTS
import asyncpg
import asyncio
import logging
from prometheus_client import (
    start_http_server,
    Summary,
    Counter,
    Gauge,
    generate_latest,
    CONTENT_TYPE_LATEST,
)
from starlette.responses import Response as StarletteResponse

app = FastAPI(title="German Plenary Protocol API", version="1.0.0", root_path="/api/genai")

NLP_API_KEY = os.getenv("NLP_GENAI_API_KEY")
NLP_DB_USERNAME = os.getenv("NLP_DB_USERNAME")
NLP_DB_PASSWORD = os.getenv("NLP_DB_PASSWORD")
NLP_DB_HOST = os.getenv("DB_HOST", "localhost")
NLP_DB_PORT = os.getenv("DB_PORT", "5432")
NLP_DB_NAME = os.getenv("DB_NAME", "postgres")

# Basic metrics
REQUEST_COUNT = Counter("http_requests_total", "Total HTTP requests")
REQUEST_LATENCY = Summary(
    "http_request_latency_seconds", "HTTP request latency in seconds"
)

if not NLP_API_KEY:
    raise ValueError("Please set the NLP_GENAI_API_KEY in your environment variables.")

if not NLP_DB_USERNAME or not NLP_DB_PASSWORD:
    raise ValueError(
        "Please set NLP_DB_USERNAME and NLP_DB_PASSWORD in your environment variables."
    )

# Initialize models
llm = ChatGoogleGenerativeAI(
    model=MODEL_CONFIG["llm_model"], google_api_key=NLP_API_KEY
)

embeddings = GoogleGenerativeAIEmbeddings(
    model=MODEL_CONFIG["embedding_model"], google_api_key=NLP_API_KEY
)

# Configure logging
logging.basicConfig(
    level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s"
)
logger = logging.getLogger(__name__)


# Database connection
async def get_db_connection():
    return await asyncpg.connect(
        user=NLP_DB_USERNAME,
        password=NLP_DB_PASSWORD,
        host=NLP_DB_HOST,
        port=NLP_DB_PORT,
        database=NLP_DB_NAME,
    )


@app.middleware("http")
async def prometheus_middleware(request: Request, call_next):
    REQUEST_COUNT.inc()
    start_time = time.time()
    response = await call_next(request)
    latency = time.time() - start_time
    REQUEST_LATENCY.observe(latency)
    return response


@app.get("/metrics")
def metrics():
    data = generate_latest()
    return StarletteResponse(content=data, media_type=CONTENT_TYPE_LATEST)


# Request/Response Models
class SummaryRequest(BaseModel):
    text: str


class EmbeddingRequest(BaseModel):
    text: str


class CombinedRequest(BaseModel):
    text: str


class ProcessSpeechesRequest(BaseModel):
    speech_ids: List[int]
    plenary_id: int


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
        raise HTTPException(
            status_code=500, detail=f"Summary generation failed: {str(e)}"
        )


@app.post("/embedding", response_model=EmbeddingResponse)
def embed_text(request: EmbeddingRequest):
    """Generate embeddings for text"""
    try:
        embedding_vector = embeddings.embed_query(request.text)

        return EmbeddingResponse(embedding=embedding_vector)

    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"Embedding generation failed: {str(e)}"
        )


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

        return CombinedResponse(summary=summary, embedding=embedding_vector)

    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"Combined processing failed: {str(e)}"
        )


@app.post("/process-speeches", status_code=202)
async def process_speeches(request: ProcessSpeechesRequest):
    asyncio.create_task(process_speeches_task(request.speech_ids, request.plenary_id))
    return {"message": "Speech processing started"}


async def process_speeches_task(speech_ids: List[int], plenary_id: int):
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

                text_plain = speech_record["text_plain"]
                if not text_plain or text_plain.strip() == "":
                    failed_speeches.append(speech_id)
                    continue

                full_prompt = PROMPTS["summary"].format(text=text_plain)
                summary_result = llm.invoke(full_prompt)
                summary = summary_result.content

                embedding_vector = embeddings.embed_query(summary)
                embedding_str = "[" + ",".join(map(str, embedding_vector)) + "]"

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

    # After all speeches are processed, generate plenary summary
    if processed_count > 0:
        logger.info(
            f"All speeches processed. Generating plenary summary for protocol {plenary_id}"
        )
        await generate_plenary_summary(plenary_id)


async def generate_plenary_summary(plenary_id: int):
    """Generate and save a summary of the entire plenary protocol based on speech summaries"""
    conn = None

    try:
        conn = await get_db_connection()

        # Get all speech summaries for this plenary protocol
        summaries_query = """
            SELECT s.text_summary, p.first_name, p.last_name, p.party, ai.name as agenda_item
            FROM speech s
            JOIN person p ON s.person_id = p.id
            JOIN agenda_item ai ON s.agenda_item_id = ai.id
            WHERE ai.plenary_protocol_id = $1
            AND s.text_summary IS NOT NULL
            ORDER BY ai.id, s.id
        """

        speech_summaries = await conn.fetch(summaries_query, plenary_id)

        if not speech_summaries:
            logger.warning(f"No speech summaries found for plenary {plenary_id}")
            return

        # Combine all speech summaries into one text
        combined_summaries = []
        for record in speech_summaries:
            speaker = (
                f"{record['first_name']} {record['last_name']} ({record['party']})"
            )
            agenda_item = record["agenda_item"]
            summary = record["text_summary"]
            combined_summaries.append(f"[{agenda_item}] {speaker}: {summary}")

        combined_text = "\n\n".join(combined_summaries)

        plenary_prompt = PROMPTS["plenary_summary"].format(text=combined_text)

        summary_result = llm.invoke(plenary_prompt)
        plenary_summary = summary_result.content

        # Update the plenary_protocol table with the summary
        update_query = """
            UPDATE plenary_protocol 
            SET summary = $1 
            WHERE id = $2
        """
        await conn.execute(update_query, plenary_summary, plenary_id)

        logger.info(f"Generated plenary summary for protocol {plenary_id}")

    except Exception as e:
        logger.error(f"Error generating plenary summary for {plenary_id}: {str(e)}")

    finally:
        if conn:
            await conn.close()


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)
