CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE "plenary_protocol" (
  "id" integer PRIMARY KEY,
  "election_period" integer,
  "document_number" integer,
  "publisher" varchar,
  "date" date
);

CREATE TABLE "agenda_item"(
    "id" serial PRIMARY KEY,
    "name" varchar,
    "title" varchar,
    "plenary_protocol_id" integer
);

CREATE TABLE "speech" (
  "id" integer PRIMARY KEY,
  "agenda_item_id" integer,
  "person_id" integer,
  "text_plain" text,
  "text_summary" text,
  "text_embedding" vector
);

CREATE TABLE "speech_chunk" (
  "id" serial PRIMARY KEY,
  "speech_id" integer NOT NULL,
  "index" integer NOT NULL,
  "type" varchar NOT NULL,
  "text" text NOT NULL
);

CREATE TABLE "person" (
  "id" integer PRIMARY KEY,
  "first_name" varchar NOT NULL,
  "last_name" varchar NOT NULL,
  "party" varchar
);

CREATE TABLE "notification_setting" (
                                "id" serial PRIMARY KEY,
                                "email" varchar NOT NULL,
                                "type" varchar NOT NULL,
                                "person_id" integer,
                                "party" varchar
);

ALTER TABLE "speech" ADD FOREIGN KEY ("agenda_item_id") REFERENCES "agenda_item" ("id") ON DELETE CASCADE;

ALTER TABLE "speech" ADD FOREIGN KEY ("person_id") REFERENCES "person" ("id");

ALTER TABLE "agenda_item" ADD FOREIGN KEY ("plenary_protocol_id") REFERENCES "plenary_protocol" ("id") ON DELETE CASCADE;

ALTER TABLE "speech_chunk" ADD FOREIGN KEY ("speech_id") REFERENCES "speech" ("id") ON DELETE CASCADE;

ALTER TABLE "notification_setting" ADD FOREIGN KEY ("person_id") REFERENCES "person" ("id") ON DELETE SET NULL;

CREATE USER "data-fetching-service" WITH PASSWORD 'welovedevops';

CREATE USER "nlp-service" WITH PASSWORD 'welovedevops';

CREATE USER "notfication-service" WITH PASSWORD 'welovedevops';

CREATE USER "browsing-service" WITH PASSWORD 'welovedevops';

GRANT SELECT, INSERT, UPDATE ON "plenary_protocol", "agenda_item", "speech", "speech_chunk", "person" TO "data-fetching-service";
GRANT SELECT, INSERT, UPDATE ON "speech", "speech_chunk" TO "nlp-service";
GRANT SELECT ON "plenary_protocol", "agenda_item", "speech", "speech_chunk", "person" TO "browsing-service";

GRANT USAGE, SELECT ON SEQUENCE speech_chunk_id_seq TO "data-fetching-service";
GRANT USAGE, SELECT ON SEQUENCE agenda_item_id_seq TO "data-fetching-service";
