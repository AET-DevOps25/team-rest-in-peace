CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE "plenary_protocol" (
  "id" integer PRIMARY KEY,
  "election_period" integer,
  "document_number" integer,
  "publisher" varchar
);

CREATE TABLE "speech" (
  "id" integer PRIMARY KEY,
  "plenary_protocol_id" integer,
  "speaker_id" integer,
  "text_plain" text,
  "text_embedding" vector
);

CREATE TABLE "speech_chunk" (
  "id" integer PRIMARY KEY,
  "speech_id" integer NOT NULL,
  "type" varchar NOT NULL,
  "text" text NOT NULL
);

CREATE TABLE "person" (
  "id" integer PRIMARY KEY,
  "speaker_id" integer UNIQUE,
  "first_name" varchar NOT NULL,
  "last_name" varchar NOT NULL,
  "party" varchar NOT NULL
);

ALTER TABLE "speech" ADD FOREIGN KEY ("plenary_protocol_id") REFERENCES "plenary_protocol" ("id");

ALTER TABLE "speech" ADD FOREIGN KEY ("speaker_id") REFERENCES "person" ("speaker_id");

ALTER TABLE "speech_chunk" ADD FOREIGN KEY ("speech_id") REFERENCES "speech" ("id");

CREATE USER "data-fetching-service" WITH PASSWORD 'welovedevops';

CREATE USER "nlp-service" WITH PASSWORD 'welovedevops';

CREATE USER "notfication-service" WITH PASSWORD 'welovedevops';

CREATE USER "browsing-service" WITH PASSWORD 'welovedevops';

GRANT SELECT, INSERT, UPDATE ON "plenary_protocol", "speech", "speech_chunk", "person" TO "data-fetching-service";
