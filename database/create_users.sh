#!/bin/bash
set -e

# Connect to PostgreSQL and create users with passwords from environment variables
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER "data-fetching-service" WITH PASSWORD '$DF_DB_PASSWORD';
    CREATE USER "nlp-service" WITH PASSWORD '$NLP_DB_PASSWORD';
    CREATE USER "notfication-service" WITH PASSWORD '$NS_DB_PASSWORD';
    CREATE USER "browsing-service" WITH PASSWORD '$BS_DB_PASSWORD';

    GRANT SELECT, INSERT, UPDATE ON "plenary_protocol", "agenda_item", "speech", "speech_chunk", "person" TO "data-fetching-service";
    GRANT SELECT, INSERT, UPDATE ON "plenary_protocol", "agenda_item", "speech", "speech_chunk", "person" TO "nlp-service";
    GRANT SELECT ON "plenary_protocol", "agenda_item", "speech", "speech_chunk", "person" TO "browsing-service";

    GRANT USAGE, SELECT ON SEQUENCE speech_chunk_id_seq TO "data-fetching-service";
    GRANT USAGE, SELECT ON SEQUENCE agenda_item_id_seq TO "data-fetching-service";

    GRANT SELECT, INSERT, DELETE ON "notification_setting" TO "notfication-service";
    GRANT SELECT ON "person", "agenda_item", "plenary_protocol", "speech" TO "notfication-service";
    GRANT USAGE, SELECT ON SEQUENCE notification_setting_id_seq TO "notfication-service";
EOSQL