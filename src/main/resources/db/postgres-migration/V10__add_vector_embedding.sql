-- Add vector column to knowledge_claims for semantic search (PostgreSQL only).
-- Uses pgvector extension (384 dimensions) if available, otherwise TEXT fallback.

DO $$
BEGIN
    -- Add embedding column if it doesn't exist
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'knowledge_claims' AND column_name = 'embedding'
    ) THEN
        -- Try vector type if pgvector is available, otherwise use TEXT
        IF EXISTS (SELECT 1 FROM pg_extension WHERE extname = 'vector') THEN
            ALTER TABLE knowledge_claims ADD COLUMN embedding vector(384);
            CREATE INDEX ix_knowledge_claims_embedding ON knowledge_claims USING hnsw (embedding vector_cosine_ops);
        ELSE
            -- Fallback: store embeddings as JSON text when pgvector is not available
            ALTER TABLE knowledge_claims ADD COLUMN embedding TEXT;
        END IF;
    END IF;
END $$;
