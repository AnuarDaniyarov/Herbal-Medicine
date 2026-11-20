ALTER TABLE knowledge_claims
    ADD COLUMN embedding_model varchar(200);

CREATE INDEX idx_knowledge_claims_embedding_model
    ON knowledge_claims (review_status, embedding_model)
    WHERE embedding IS NOT NULL;
