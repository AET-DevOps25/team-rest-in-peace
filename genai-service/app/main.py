import asyncio
import logging
import os
import time
from typing import List

import asyncpg
from fastapi import FastAPI, HTTPException, Query
from fastapi import Request
from prometheus_fastapi_instrumentator import Instrumentator
from fastapi import FastAPI, HTTPException
from prometheus_fastapi_instrumentator import Instrumentator
from pydantic import BaseModel
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_google_genai import GoogleGenerativeAIEmbeddings
from typing import List
from app.config import MODEL_CONFIG, PROMPTS
import asyncpg
import asyncio
import logging
from prometheus_client import (
    Summary,
    Counter,
    generate_latest,
    CONTENT_TYPE_LATEST,
)
from pydantic import BaseModel
from starlette.responses import Response as StarletteResponse

from app.config import MODEL_CONFIG, PROMPTS

app = FastAPI(
    title="German Plenary Protocol API",
    version="1.0.0",
    description="""
This API processes and summarizes German parliamentary speeches. It supports generating text summaries, embeddings,
and aggregated plenary session summaries using Google Generative AI.

### Main Features

- Summarize individual speeches.
- Generate embeddings for semantic search.
- Perform combined summary + embedding tasks.
- Asynchronously process batches of speeches and update the database.
- Generate complete summaries for plenary protocols.
- Prometheus-compatible metrics endpoint.

Mainly called by data-fetching service to enrich the Protocols.
""",
)

NLP_API_KEY = os.getenv("NLP_GENAI_API_KEY")
NLP_DB_USERNAME = os.getenv("NLP_DB_USERNAME", "nlp-service")
NLP_DB_PASSWORD = os.getenv("NLP_DB_PASSWORD")
NLP_DB_HOST = os.getenv("DB_HOST", "localhost")
NLP_DB_PORT = os.getenv("DB_PORT", "5432")
NLP_DB_NAME = os.getenv("DB_NAME", "postgres")

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


class SummaryRequest(BaseModel):
    text: str = (
        "Dies ist eine Rede aus dem Deutschen Bundestag über die Energiepolitik."
    )


class SummaryResponse(BaseModel):
    summary: str = (
        "Die Rede behandelt energiepolitische Herausforderungen und geplante Maßnahmen."
    )


class EmbeddingRequest(BaseModel):
    text: str = (
        "Das Parlament diskutierte über Steuerreformen und soziale Gerechtigkeit."
    )


class EmbeddingResponse(BaseModel):
    embedding: List[float] = [0.123, 0.456, 0.789]


class CombinedRequest(BaseModel):
    text: str = "Im Bundestag wurde über Digitalisierung in der Bildung gesprochen."


class CombinedResponse(BaseModel):
    summary: str = "Die Rede fokussiert sich auf digitale Bildungsoffensiven."
    embedding: List[float] = [0.234, 0.567, 0.891]


class ProcessSpeechesRequest(BaseModel):
    speech_ids: List[int] = [101, 102, 103]
    plenary_id: int = 17


@app.get("/health", summary="Health Check", description="Check if the API is running.")
def health_check():
    return {"healthy": True}


@app.post("/summary", response_model=SummaryResponse, summary="Summarize Speech Text")
def summarize_protocol(request: SummaryRequest):
    """
    Generates a concise summary from a given plenary speech text.

    - **text**: Full raw text of the speech (plain format).

    **Returns**: Generated summary based on the input text.
    """
    try:
        full_prompt = PROMPTS["summary"].format(text=request.text)
        result = llm.invoke(full_prompt)

        return SummaryResponse(summary=result.content)

    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"Summary generation failed: {str(e)}"
        )


@app.get(
    "/embedding", response_model=EmbeddingResponse, summary="Generate Text Embedding"
)
def embed_text(text: str = Query(..., description="Text to generate embedding for")):
    """
    Generates a semantic embedding vector from input text using a generative embedding model.

    - **text**: Input text to be embedded.

    **Returns**: List of float values representing the embedding vector.
    """
    try:
        embedding_vector = embeddings.embed_query(text)

        return EmbeddingResponse(embedding=embedding_vector)

    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"Embedding generation failed: {str(e)}"
        )


@app.post(
    "/combined", response_model=CombinedResponse, summary="Summarize & Embed Text"
)
def summarize_and_embed(request: CombinedRequest):
    """
    Generates both a summary and an embedding vector from the input text.

    - **text**: Full speech text or segment to process.

    **Returns**:
    - `summary`: A textual summary.
    - `embedding`: A semantic vector for downstream applications.
    """
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


@app.post("/process-speeches", status_code=202, summary="Batch Process Speech IDs")
async def process_speeches(request: ProcessSpeechesRequest):
    """
    Asynchronously processes a batch of speech entries by ID:

    - Generates summaries and embeddings.
    - Updates the database entries for each speech.
    - Finally creates a combined summary of the entire plenary protocol.

    - **speech_ids**: List of speech record IDs to process.
    - **plenary_id**: ID of the plenary protocol session.

    **Returns**: Processing will run in the background. HTTP 202 indicates task has started.
    """
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


instrumentator = Instrumentator()
instrumentator.instrument(app)
instrumentator.expose(app, endpoint="/metrics")

if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)
