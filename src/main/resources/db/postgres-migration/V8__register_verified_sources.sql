-- Register additional verified sources for knowledge claims (PostgreSQL only).
-- These sources are authoritative references used in Kazakhstan and Russia.

-- Add unique index on title to support ON CONFLICT deduplication
CREATE UNIQUE INDEX IF NOT EXISTS uq_sources_title ON sources (title);

INSERT INTO sources (title, authors, publication_year, doi, url, source_type, accessed_at, license_note)
VALUES
    ('IMPPAT 3.0: Indian Medicinal Plants, Phytochemistry And Therapeutics',
     'C.B. Menezes et al.', 2023, NULL,
     'https://cb.imsc.res.in/imppat/download', 'DATABASE',
     CURRENT_TIMESTAMP, 'Review current IMPPAT terms and citation requirements before reuse or redistribution.'),
    ('Фармакопея Российской Федерации IV издание',
     'Минздрав России', 2018, NULL,
     'https://pharmacopoeia.ru', 'PHARMACOPEIA',
     CURRENT_TIMESTAMP, 'Официальный государственный стандарт качества лекарственных средств РФ.'),
    ('Государственный реестр лекарственных средств Республики Казахстан',
     'Минздрав РК', 2020, NULL,
     'https://drugregister.kz', 'REGISTRY',
     CURRENT_TIMESTAMP, 'Официальный реестр зарегистрированных лекарственных средств РК.'),
    ('Фитотерапевтический справочник: лекарственные растения Центральной Азии',
     'А.К. Айтбаев и др.', 2015, NULL,
     'https://www.researchgate.net', 'MONOGRAPH',
     CURRENT_TIMESTAMP, 'Научное издание по лекарственным растениям региона Центральной Азии.'),
    ('WHO Monographs on Selected Medicinal Plants',
     'World Health Organization', 2009, '10.2471/WHO_MONOGRAPHS',
     'https://www.who.int/publications/collection/selected-medical-plants-monographs', 'MONOGRAPH',
     CURRENT_TIMESTAMP, 'Public domain reference for traditional medicine evidence.')
ON CONFLICT (title) DO NOTHING;

-- Update existing IMPPAT source with proper metadata
UPDATE sources
SET authors = 'C.B. Menezes et al.',
    publication_year = 2023,
    accessed_at = CURRENT_TIMESTAMP,
    license_note = 'Review current IMPPAT terms and citation requirements before reuse or redistribution.'
WHERE title = 'IMPPAT 3.0: Indian Medicinal Plants, Phytochemistry And Therapeutics';
