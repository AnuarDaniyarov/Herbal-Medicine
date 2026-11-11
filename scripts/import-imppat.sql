\set ON_ERROR_STOP on
BEGIN;

CREATE TEMP TABLE stg_plants (
    external_id text,
    scientific_name text,
    display_name text,
    family text
);
CREATE TEMP TABLE stg_compounds (
    external_id text,
    name text,
    canonical_smiles text,
    inchi_key text
);
CREATE TEMP TABLE stg_plant_compounds (
    plant_external_id text,
    compound_external_id text,
    plant_part text
);
CREATE TEMP TABLE stg_sources (
    source_code text,
    title text,
    url text,
    source_type text,
    license_note text
);
CREATE TEMP TABLE stg_targets (
    normalized_identifier text,
    name text,
    organism text
);
CREATE TEMP TABLE stg_assays (
    activity_code text,
    name text,
    target_name text,
    assay_type text,
    description text,
    standard_unit text,
    target_identifier text
);
CREATE TEMP TABLE stg_measurements (
    compound_external_id text,
    activity_code text,
    source_code text,
    measurement_value text,
    unit text,
    relation text,
    experimental_conditions text,
    quality_status text
);

\copy stg_plants FROM 'data/processed/plants.csv' WITH (FORMAT csv, HEADER true, ENCODING 'UTF8')
\copy stg_compounds FROM 'data/processed/compounds.csv' WITH (FORMAT csv, HEADER true, ENCODING 'UTF8')
\copy stg_plant_compounds FROM 'data/processed/plant_compounds.csv' WITH (FORMAT csv, HEADER true, ENCODING 'UTF8')
\copy stg_sources FROM 'data/processed/sources.csv' WITH (FORMAT csv, HEADER true, ENCODING 'UTF8')
\copy stg_targets FROM 'data/processed/targets.csv' WITH (FORMAT csv, HEADER true, ENCODING 'UTF8')
\copy stg_assays FROM 'data/processed/assays.csv' WITH (FORMAT csv, HEADER true, ENCODING 'UTF8')
\copy stg_measurements FROM 'data/processed/measurements.csv' WITH (FORMAT csv, HEADER true, ENCODING 'UTF8')

INSERT INTO sources (title, url, source_type, accessed_at, license_note)
SELECT DISTINCT s.title, s.url, s.source_type, CURRENT_TIMESTAMP, s.license_note
FROM stg_sources s
WHERE NOT EXISTS (
    SELECT 1 FROM sources existing WHERE existing.title = s.title AND existing.url = s.url
);
UPDATE sources existing
SET license_note = COALESCE(existing.license_note, staged.license_note)
FROM stg_sources staged
WHERE existing.title = staged.title
  AND existing.url = staged.url;

INSERT INTO plants (scientific_name, display_name, family)
SELECT DISTINCT ON (p.scientific_name)
    p.scientific_name,
    COALESCE(NULLIF(p.display_name, ''), p.scientific_name),
    NULLIF(p.family, '')
FROM stg_plants p
ORDER BY p.scientific_name, p.external_id
ON CONFLICT (scientific_name) DO UPDATE SET
    display_name = EXCLUDED.display_name,
    family = COALESCE(EXCLUDED.family, plants.family),
    updated_at = CURRENT_TIMESTAMP;

INSERT INTO targets (normalized_identifier, name, organism)
SELECT DISTINCT ON (t.normalized_identifier) t.normalized_identifier, t.name, NULLIF(t.organism, '')
FROM stg_targets t
ORDER BY t.normalized_identifier
ON CONFLICT (normalized_identifier) DO UPDATE SET
    name = EXCLUDED.name,
    organism = EXCLUDED.organism;

INSERT INTO compounds (name, canonical_smiles, inchi_key, external_id, structure_status)
SELECT DISTINCT ON (c.external_id)
    c.name, c.canonical_smiles, NULLIF(c.inchi_key, ''), c.external_id, 'UNVERIFIED'
FROM stg_compounds c
WHERE NULLIF(c.external_id, '') IS NOT NULL
  AND NULLIF(c.canonical_smiles, '') IS NOT NULL
ORDER BY c.external_id
ON CONFLICT (external_id) DO UPDATE SET
    name = EXCLUDED.name,
    canonical_smiles = EXCLUDED.canonical_smiles,
    inchi_key = EXCLUDED.inchi_key,
    structure_status = 'UNVERIFIED',
    updated_at = CURRENT_TIMESTAMP;

INSERT INTO assays (activity_code, name, target_name, assay_type, description, standard_unit, target_id)
SELECT DISTINCT ON (a.activity_code)
    a.activity_code, a.name, NULLIF(a.target_name, ''), a.assay_type,
    NULLIF(a.description, ''), NULLIF(a.standard_unit, ''), t.id
FROM stg_assays a
LEFT JOIN targets t ON t.normalized_identifier = a.target_identifier
ORDER BY a.activity_code
ON CONFLICT (activity_code) DO UPDATE SET
    name = EXCLUDED.name,
    target_name = EXCLUDED.target_name,
    assay_type = EXCLUDED.assay_type,
    description = EXCLUDED.description,
    standard_unit = EXCLUDED.standard_unit,
    target_id = EXCLUDED.target_id;

-- Make repeated imports replace only measurements and plant links from this dataset.
DELETE FROM activity_measurements am
USING sources s
WHERE am.source_id = s.id
  AND s.title = 'IMPPAT 3.0: Indian Medicinal Plants, Phytochemistry And Therapeutics';
DELETE FROM plant_compound_relationships pcr
USING sources s
WHERE pcr.source_id = s.id
  AND s.title = 'IMPPAT 3.0: Indian Medicinal Plants, Phytochemistry And Therapeutics';

INSERT INTO plant_compound_relationships (plant_id, compound_id, plant_part, source_id)
SELECT DISTINCT p.id, c.id, NULLIF(pc.plant_part, ''), s.id
FROM stg_plant_compounds pc
JOIN stg_plants sp ON sp.external_id = pc.plant_external_id
JOIN plants p ON p.scientific_name = sp.scientific_name
JOIN compounds c ON c.external_id = pc.compound_external_id
JOIN sources s ON s.title = 'IMPPAT 3.0: Indian Medicinal Plants, Phytochemistry And Therapeutics';

INSERT INTO activity_measurements (
    compound_id, assay_id, source_id, measurement_value, unit, relation,
    experimental_conditions, quality_status
)
SELECT c.id, a.id, s.id, m.measurement_value::numeric, m.unit, m.relation,
       NULLIF(m.experimental_conditions, ''), COALESCE(NULLIF(m.quality_status, ''), 'UNVERIFIED')
FROM stg_measurements m
JOIN compounds c ON c.external_id = m.compound_external_id
JOIN assays a ON a.activity_code = m.activity_code
JOIN sources s ON s.title = 'IMPPAT 3.0: Indian Medicinal Plants, Phytochemistry And Therapeutics';

COMMIT;

SELECT 'plants' AS imported_entity, COUNT(*) AS total FROM plants
UNION ALL SELECT 'compounds', COUNT(*) FROM compounds
UNION ALL SELECT 'plant_compound_relationships', COUNT(*) FROM plant_compound_relationships
UNION ALL SELECT 'assays', COUNT(*) FROM assays
UNION ALL SELECT 'activity_measurements', COUNT(*) FROM activity_measurements;
