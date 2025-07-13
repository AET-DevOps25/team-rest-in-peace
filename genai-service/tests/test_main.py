import pytest
from fastapi import HTTPException
import os
os.environ.setdefault("NLP_GENAI_API_KEY", "testkey")
os.environ.setdefault("NLP_DB_USERNAME", "user")
os.environ.setdefault("NLP_DB_PASSWORD", "pass")
os.environ.setdefault("DB_HOST", "localhost")
os.environ.setdefault("DB_PORT", "5432")
os.environ.setdefault("DB_NAME", "postgres")


from app.main import (
    summarize_protocol,
    embed_text,
    summarize_and_embed,
    process_speeches_task,
    SummaryRequest,
    EmbeddingRequest,
    CombinedRequest,
)


class DummyResult:
    def __init__(self, content):
        self.content = content


@pytest.fixture(autouse=True)
def mock_llm_and_embeddings(monkeypatch):
    import app.main as m

    # Mock the llm.invoke(...) call
    dummy_llm = type("L", (), {"invoke": lambda self, prompt: DummyResult("mocked summary")})()
    monkeypatch.setattr(m, "llm", dummy_llm)

    # Mock the embeddings.embed_query(...) call
    dummy_emb = type("E", (), {"embed_query": lambda self, text: [0.1, 0.2, 0.3]})()
    monkeypatch.setattr(m, "embeddings", dummy_emb)


def test_summarize_protocol_returns_mocked_summary():
    req = SummaryRequest(text="anything")
    resp = summarize_protocol(req)
    assert resp.summary == "mocked summary"


def test_embed_text_returns_mocked_vector():
    req = EmbeddingRequest(text="anything")
    resp = embed_text(req)
    assert resp.embedding == [0.1, 0.2, 0.3]


def test_summarize_and_embed_combines_both():
    req = CombinedRequest(text="anything")
    resp = summarize_and_embed(req)
    assert resp.summary == "mocked summary"
    assert resp.embedding == [0.1, 0.2, 0.3]


@pytest.mark.asyncio
async def test_process_speeches_task_empty_list_raises():
    with pytest.raises(HTTPException) as excinfo:
        await process_speeches_task([])
    assert "speech_ids cannot be empty" in str(excinfo.value.detail)
