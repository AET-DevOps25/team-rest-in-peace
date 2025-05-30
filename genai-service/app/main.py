import os
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_google_genai import GoogleGenerativeAIEmbeddings
from typing import List
from app.config import MODEL_CONFIG, PROMPTS

app = FastAPI(title="German Plenary Protocol API", version="1.0.0")

NLP_API_KEY = os.getenv("NLP_GENAI_API_KEY")

if not NLP_API_KEY:
    raise ValueError("Please set the NLP_GENAI_API_KEY in your environment variables.")

# Initialize models
llm = ChatGoogleGenerativeAI(
    model=MODEL_CONFIG["llm_model"],
    google_api_key=NLP_API_KEY
)

embeddings = GoogleGenerativeAIEmbeddings(
    model=MODEL_CONFIG["embedding_model"],
    google_api_key=NLP_API_KEY
)


# Request/Response Models
class SummaryRequest(BaseModel):
    text: str


class EmbeddingRequest(BaseModel):
    text: str


class CombinedRequest(BaseModel):
    text: str


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

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)