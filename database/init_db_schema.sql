CREATE EXTENSION IF NOT EXISTS vector;

CREATE TYPE "publisher_type" AS ENUM (
  'BT',
  'BR',
  'BV'
);

CREATE TYPE "party" AS ENUM (
  'CDU_CSU',
  'AFD',
  'FDP',
  'GRUENE',
  'SPD',
  'LINKE',
  'BSW',
  'NO_PARTY'
);

CREATE TYPE "speech_chunk_type" AS ENUM (
  'SPEECH',
  'COMMENT'
);

CREATE TABLE "plenary_protocol" (
  "id" integer PRIMARY KEY,
  "election_period" integer,
  "document_number" integer,
  "publisher" publisher_type
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
  "type" speech_chunk_type NOT NULL,
  "text" text NOT NULL
);

CREATE TABLE "person" (
  "id" integer PRIMARY KEY,
  "speaker_id" integer,
  "first_name" varchar NOT NULL,
  "last_name" varchar NOT NULL,
  "party" party NOT NULL
);

ALTER TABLE "speech" ADD FOREIGN KEY ("plenary_protocol_id") REFERENCES "plenary_protocol" ("id");

ALTER TABLE "speech" ADD FOREIGN KEY ("speaker_id") REFERENCES "person" ("speaker_id");

ALTER TABLE "speech_chunk" ADD FOREIGN KEY ("speech_id") REFERENCES "speech" ("id");
