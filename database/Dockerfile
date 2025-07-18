FROM pgvector/pgvector:pg16

# Environment variables will be provided from .env file via docker-compose
# Copy your initialization SQL script into the image
COPY init_db_schema.sql /docker-entrypoint-initdb.d/01_init_db_schema.sql
COPY create_users.sh /docker-entrypoint-initdb.d/02_create_users.sh
RUN chmod +x /docker-entrypoint-initdb.d/02_create_users.sh
