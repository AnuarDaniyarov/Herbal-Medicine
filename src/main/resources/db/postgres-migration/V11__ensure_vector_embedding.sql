-- Repair databases where the entity was deployed before the embedding column migration.
DO $$
DECLARE
    embedding_type TEXT;
BEGIN
    SELECT data_type
    INTO embedding_type
    FROM information_schema.columns
    WHERE table_schema = current_schema()
      AND table_name = 'knowledge_claims'
      AND column_name = 'embedding';

    IF embedding_type IS NULL THEN
        IF EXISTS (SELECT 1 FROM pg_extension WHERE extname = 'vector') THEN
            ALTER TABLE knowledge_claims ADD COLUMN embedding vector(384);
        ELSE
            ALTER TABLE knowledge_claims ADD COLUMN embedding TEXT;
        END IF;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'knowledge_claims'
          AND column_name = 'embedding'
          AND udt_name = 'vector'
    ) THEN
        CREATE INDEX IF NOT EXISTS ix_knowledge_claims_embedding
            ON knowledge_claims USING hnsw (embedding vector_cosine_ops);
    END IF;
END $$;
