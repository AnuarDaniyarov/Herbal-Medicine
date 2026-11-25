-- Seed content is not expert-reviewed. Do not manufacture a reviewer or a review timestamp.
UPDATE knowledge_claims
SET review_status = 'UNVERIFIED'
WHERE review_status = 'VERIFIED'
  AND (reviewed_by IS NULL
       OR reviewed_at IS NULL
       OR (created_by IS NOT NULL AND reviewed_by = created_by)
       OR NOT EXISTS (
           SELECT 1
           FROM knowledge_claim_reviews review_event
           WHERE review_event.claim_id = knowledge_claims.id
             AND review_event.reviewer_id = knowledge_claims.reviewed_by
             AND review_event.new_status = knowledge_claims.review_status
       ));

ALTER TABLE knowledge_claims
    ADD CONSTRAINT chk_claims_verified_has_reviewer
    CHECK (review_status <> 'VERIFIED'
           OR (reviewed_by IS NOT NULL AND reviewed_at IS NOT NULL
               AND (created_by IS NULL OR reviewed_by <> created_by)));
