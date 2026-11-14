-- Enable pgvector extension for vector similarity search.
-- This script runs automatically when the PostgreSQL container starts.

CREATE EXTENSION IF NOT EXISTS vector;
