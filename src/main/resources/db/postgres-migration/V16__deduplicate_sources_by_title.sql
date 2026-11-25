CREATE TEMPORARY TABLE source_title_dedup ON COMMIT DROP AS
SELECT id AS duplicate_id,
       FIRST_VALUE(id) OVER (PARTITION BY lower(trim(title)) ORDER BY id) AS canonical_id
FROM sources;

WITH ranked_assistant_sources AS (
    SELECT assistant_source.id,
           ROW_NUMBER() OVER (
               PARTITION BY assistant_source.message_id, mapping.canonical_id, assistant_source.source_locator
               ORDER BY assistant_source.id
           ) AS row_number
    FROM assistant_message_sources assistant_source
    JOIN source_title_dedup mapping ON mapping.duplicate_id = assistant_source.source_id
)
DELETE FROM assistant_message_sources target
USING ranked_assistant_sources ranked
WHERE target.id = ranked.id
  AND ranked.row_number > 1;

UPDATE activity_measurements target
SET source_id = mapping.canonical_id
FROM source_title_dedup mapping
WHERE target.source_id = mapping.duplicate_id
  AND mapping.duplicate_id <> mapping.canonical_id;

UPDATE plant_compound_relationships target
SET source_id = mapping.canonical_id
FROM source_title_dedup mapping
WHERE target.source_id = mapping.duplicate_id
  AND mapping.duplicate_id <> mapping.canonical_id;

UPDATE preparations target
SET source_id = mapping.canonical_id
FROM source_title_dedup mapping
WHERE target.source_id = mapping.duplicate_id
  AND mapping.duplicate_id <> mapping.canonical_id;

UPDATE preparation_ingredients target
SET source_id = mapping.canonical_id
FROM source_title_dedup mapping
WHERE target.source_id = mapping.duplicate_id
  AND mapping.duplicate_id <> mapping.canonical_id;

UPDATE knowledge_claims target
SET source_id = mapping.canonical_id
FROM source_title_dedup mapping
WHERE target.source_id = mapping.duplicate_id
  AND mapping.duplicate_id <> mapping.canonical_id;

UPDATE assistant_message_sources target
SET source_id = mapping.canonical_id
FROM source_title_dedup mapping
WHERE target.source_id = mapping.duplicate_id
  AND mapping.duplicate_id <> mapping.canonical_id;

DELETE FROM sources source
USING source_title_dedup mapping
WHERE source.id = mapping.duplicate_id
  AND mapping.duplicate_id <> mapping.canonical_id;

CREATE UNIQUE INDEX uq_sources_normalized_title ON sources (lower(trim(title)));
