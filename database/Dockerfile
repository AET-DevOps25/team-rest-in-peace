FROM pgvector/pgvector:pg16

# Set environment variables (customize as needed)
ENV POSTGRES_DB=db
ENV POSTGRES_USER=admin
ENV POSTGRES_PASSWORD=welovedevops

# Copy your initialization SQL script into the image
COPY *.sql /docker-entrypoint-initdb.d/
