from fastapi import FastAPI

app = FastAPI(
    title="NLP API",
    description="Natural Language Processing API for summarizing and embedding plenary protocols.",
    version="0.1.0"
)

@app.get("/")
def read_root():
    return {"message": "Hello World. I'm the NLP API"}

# for development
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
