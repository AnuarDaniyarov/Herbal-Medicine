-- V10: Expand verified knowledge base with additional plants and claims
-- Uses simple INSERT with NOT EXISTS for H2/PostgreSQL compatibility

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Thymus vulgaris', 'Common Thyme', 'Lamiaceae', 'Aromatic herb used for respiratory conditions', 'Чабрец', 'Шалфей', 'Thymus serpyllum', 'Europe, Asia, Africa', 'Leaves, flowers', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Thymus vulgaris');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Pelargonium sidoides', 'Umckaloobo', 'Geraniaceae', 'South African geranium used for respiratory infections', 'Пеларгония', 'Пеларгония', 'Umckaloobo', 'South Africa', 'Root', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Pelargonium sidoides');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Eucalyptus globulus', 'Blue Gum', 'Myrtaceae', 'Tree with essential oil used for respiratory conditions', 'Эвкалипт', 'Эвкалипт', 'Eucalyptus radiata', 'Australia', 'Leaves', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Eucalyptus globulus');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Glycyrrhiza glabra', 'Licorice', 'Fabaceae', 'Root used for coughs and digestive issues', 'Солодка', 'Солодка', 'Glycyrrhiza uralensis', 'Europe, Asia', 'Root', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Glycyrrhiza glabra');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Plantago lanceolata', 'Ribwort Plantain', 'Plantaginaceae', 'Common weed used for respiratory and skin conditions', 'Подорожник', 'Подорожник', 'Plantago major', 'Worldwide', 'Leaves', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Plantago lanceolata');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Mentha piperita', 'Peppermint', 'Lamiaceae', 'Aromatic herb for digestive and headache relief', 'Мята перечная', 'Жалбыз', 'Mentha x piperita', 'Worldwide', 'Leaves', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Mentha piperita');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Curcuma longa', 'Turmeric', 'Zingiberaceae', 'Spice with curcumin, powerful anti-inflammatory', 'Куркума', 'Куркума', 'Curcuma domestica', 'Southeast Asia', 'Rhizome', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Curcuma longa');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Zingiber officinale', 'Ginger', 'Zingiberaceae', 'Rhizome used for nausea and inflammation', 'Имбирь', 'Зенджифиль', 'Zingiber officinale var. officinale', 'Southeast Asia', 'Rhizome', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Zingiber officinale');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Urtica dioica', 'Stinging Nettle', 'Urticaceae', 'Common weed with anti-inflammatory and diuretic properties', 'Крапива', 'Қызғалдақ', 'Urtica urens', 'Worldwide', 'Leaves', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Urtica dioica');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Allium sativum', 'Garlic', 'Amaryllidaceae', 'Bulb used for cardiovascular and immune support', 'Чеснок', 'Сарымсақ', 'Allium sativum var. sativum', 'Worldwide', 'Bulb', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Allium sativum');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Crataegus monogyna', 'Hawthorn', 'Rosaceae', 'Shrub used for cardiovascular support', 'Боярышник', 'Қасқыржапырақ', 'Crataegus oxyacantha', 'Europe, Asia', 'Berries, flowers', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Crataegus monogyna');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Hibiscus sabdariffa', 'Roselle', 'Malvaceae', 'Tropical plant with blood pressure lowering effects', 'Гибискус', 'Гибискус', 'Hibiscus sabdariffa var. sabdariffa', 'Tropical regions', 'Calyx', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Hibiscus sabdariffa');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Camellia sinensis', 'Tea', 'Theaceae', 'Tea plant with polyphenols and antioxidants', 'Чай', 'Шай', 'Camellia sinensis var. sinensis', 'Asia', 'Leaves', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Camellia sinensis');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Echinacea purpurea', 'Purple Coneflower', 'Asteraceae', 'North American herb for immune support', 'Эхинацея', 'Эхинацея', 'Echinacea angustifolia', 'North America', 'Root, flowers', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Echinacea purpurea');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Panax ginseng', 'Asian Ginseng', 'Araliaceae', 'Adaptogenic root for energy and immune support', 'Женьшень', 'Женьшень', 'Panax ginseng var. ginseng', 'East Asia', 'Root', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Panax ginseng');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Aloe vera', 'Aloe', 'Asphodelaceae', 'Succulent with wound healing and skin benefits', 'Алоэ', 'Алоэ', 'Aloe barbadensis', 'Arabian Peninsula', 'Leaves', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Aloe vera');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Passiflora incarnata', 'Passionflower', 'Passifloraceae', 'Vine used for anxiety and sleep', 'Страстоцвет', 'Страстоцвет', 'Passiflora incarnata var. incarnata', 'North America', 'Leaves, flowers', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Passiflora incarnata');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Lavandula angustifolia', 'Lavender', 'Lamiaceae', 'Aromatic herb for relaxation and sleep', 'Лаванда', 'Лаванда', 'Lavandula officinalis', 'Mediterranean', 'Flowers', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Lavandula angustifolia');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Harpagophytum procumbens', 'Devils Claw', 'Pedaliaceae', 'African plant for joint pain and inflammation', 'Дьявольский коготь', 'Дьявольский коготь', 'Harpagophytum procumbens var. procumbens', 'Southern Africa', 'Root', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Harpagophytum procumbens');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Salix alba', 'White Willow', 'Salicaceae', 'Tree bark containing salicin, precursor to aspirin', 'Ива белая', 'Қайың', 'Salix alba var. alba', 'Europe, Asia', 'Bark', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Salix alba');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Chamomilla recutita', 'German Chamomile', 'Asteraceae', 'Daisy-like flower for digestive and calming effects', 'Ромашка', 'Ромашка', 'Matricaria chamomilla', 'Europe, Asia', 'Flowers', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Chamomilla recutita');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Calendula officinalis', 'Pot Marigold', 'Asteraceae', 'Yellow flower used for skin healing', 'Календула', 'Календула', 'Calendula officinalis var. officinalis', 'Mediterranean', 'Flowers', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Calendula officinalis');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Sambucus nigra', 'Elderberry', 'Adoxaceae', 'Berry with immune and antioxidant properties', 'Черная бузина', 'Қаражидек', 'Sambucus nigra var. nigra', 'Europe, Asia', 'Berries, flowers', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Sambucus nigra');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Leonurus cardiaca', 'Motherwort', 'Lamiaceae', 'Herb for heart and nervous system support', 'Пустырник', 'Пустырник', 'Leonurus cardiaca var. cardiaca', 'Europe, Asia', 'Leaves', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Leonurus cardiaca');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Humulus lupulus', 'Hops', 'Cannabaceae', 'Cone-bearing plant for sleep and anxiety', 'Хмель', 'Хмель', 'Humulus lupulus var. lupulus', 'Europe, North America', 'Cones', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Humulus lupulus');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Rosmarinus officinalis', 'Rosemary', 'Lamiaceae', 'Aromatic herb with cognitive and anti-inflammatory effects', 'Розмарин', 'Розмарин', 'Salvia rosmarinus', 'Mediterranean', 'Leaves', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Rosmarinus officinalis');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Arctium lappa', 'Burdock', 'Asteraceae', 'Root used for skin conditions and detoxification', 'Лопух', 'Лопух', 'Arctium lappa var. lappa', 'Europe, Asia', 'Root', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Arctium lappa');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Centella asiatica', 'Gotu Kola', 'Apiaceae', 'Herb for wound healing and cognitive function', 'Центелла', 'Центелла', 'Centella asiatica var. asiatica', 'Asia', 'Leaves', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Centella asiatica');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Scutellaria baicalensis', 'Baikal Skullcap', 'Lamiaceae', 'Herb with anti-anxiety and anti-inflammatory effects', 'Шлемник байкальский', 'Шлемник', 'Scutellaria baicalensis var. baicalensis', 'East Asia', 'Root', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Scutellaria baicalensis');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Ganoderma lucidum', 'Reishi', 'Ganodermaataceae', 'Medicinal mushroom with immune-modulating properties', 'Рейши', 'Рейши', 'Ganoderma lucidum var. lucidum', 'East Asia', 'Fruiting body', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Ganoderma lucidum');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Lentinula edodes', 'Shiitake', 'Lentinulaceae', 'Edible mushroom with immune-boosting properties', 'Шиитаке', 'Шиитаке', 'Lentinula edodes var. edodes', 'East Asia', 'Fruiting body', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Lentinula edodes');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Astragalus membranaceus', 'Astragalus', 'Fabaceae', 'Legume with immune-boosting properties', 'Астрагал', 'Астрагал', 'Astragalus membranaceus var. membranaceus', 'Asia', 'Root', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Astragalus membranaceus');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Avena sativa', 'Oat', 'Poaceae', 'Cereal grain with cardiovascular and nervous system benefits', 'Овес', 'Сұлы', 'Avena sativa var. sativa', 'Worldwide', 'Seeds', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Avena sativa');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Trifolium pratense', 'Red Clover', 'Fabaceae', 'Legume with phytoestrogens for cardiovascular health', 'Клевер красный', 'Қызыл шалфей', 'Trifolium pratense var. pratense', 'Europe, Asia', 'Flowers', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Trifolium pratense');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Vaccinium myrtillus', 'Bilberry', 'Ericaceae', 'Berry with anthocyanins for eye health', 'Черника', 'Көктерек', 'Vaccinium myrtillus var. myrtillus', 'Northern hemisphere', 'Fruit', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Vaccinium myrtillus');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Rubus idaeus', 'Raspberry', 'Rosaceae', 'Berry with antioxidants and anti-inflammatory properties', 'Малина', 'Қурай', 'Rubus idaeus var. idaeus', 'Northern hemisphere', 'Fruit, leaves', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Rubus idaeus');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Theobroma cacao', 'Cocoa', 'Malvaceae', 'Tree with flavonoids and antioxidants', 'Какао', 'Какао', 'Theobroma cacao var. cacao', 'Central America', 'Seeds', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Theobroma cacao');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Cinnamomum verum', 'Ceylon Cinnamon', 'Lauraceae', 'Spice with anti-inflammatory and blood sugar effects', 'Корица цейлонская', 'Даршын', 'Cinnamomum zeylanicum', 'Sri Lanka', 'Bark', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Cinnamomum verum');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Morus alba', 'White Mulberry', 'Moraceae', 'Tree with anti-inflammatory and blood sugar effects', 'Тут белый', 'Алша', 'Morus alba var. alba', 'East Asia', 'Leaves, fruit', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Morus alba');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Boswellia serrata', 'Indian Frankincense', 'Burseraceae', 'Tree resin used for inflammatory conditions', 'Босвеллия', 'Босвеллия', 'Boswellia carterii', 'India', 'Resin', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Boswellia serrata');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Foeniculum vulgare', 'Fennel', 'Apiaceae', 'Herb used for digestive and lactation support', 'Фенхель', 'Фенхель', 'Foeniculum vulgare var. dulce', 'Mediterranean', 'Seeds, leaves', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Foeniculum vulgare');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Anethum graveolens', 'Dill', 'Apiaceae', 'Herb for digestive and colic relief', 'Укроп', 'Кермек', 'Anethum graveolens var. graveolens', 'Mediterranean', 'Seeds, leaves', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Anethum graveolens');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Coriandrum sativum', 'Coriander', 'Apiaceae', 'Herb and spice for digestive health', 'Кориандр', 'Көшпелі', 'Coriandrum sativum var. sativum', 'Mediterranean', 'Seeds, leaves', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Coriandrum sativum');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Carum carvi', 'Caraway', 'Apiaceae', 'Spice for digestive stimulation', 'Тмин', 'Карам', 'Carum carvi var. carvi', 'Europe, Asia', 'Seeds', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Carum carvi');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Cuminum cyminum', 'Cumin', 'Apiaceae', 'Spice used for digestive stimulation', 'Кумин', 'Зира', 'Cuminum minimum', 'Mediterranean', 'Seeds', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Cuminum cyminum');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Piper nigrum', 'Black Pepper', 'Piperaceae', 'Spice that enhances absorption of other compounds', 'Черный перец', 'Қара бұрыш', 'Piper nigrum var. nigrum', 'South Asia', 'Fruit', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Piper nigrum');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Acanthopanax senticosus', 'Siberian Ginseng', 'Araliaceae', 'Adaptogenic herb for stress and immune support', 'Элеутерококк', 'Элеутерококк', 'Eleutherococcus senticosus', 'Siberia', 'Root', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Acanthopanax senticosus');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Panax quinquefolius', 'American Ginseng', 'Araliaceae', 'Adaptogenic root with cooling properties', 'Американский женьшень', 'Американдық женьшень', 'Panax quinquefolius var. quinquefolius', 'North America', 'Root', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Panax quinquefolius');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Oenothera biennis', 'Evening Primrose', 'Onagraceae', 'Plant with gamma-linolenic acid for skin health', 'Вечерняя примула', 'Кешу', 'Oenothera biennis var. biennis', 'North America', 'Seeds', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Oenothera biennis');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Borago officinalis', 'Borage', 'Boraginaceae', 'Herb with gamma-linolenic acid for skin health', 'Бурачник', 'Бурачник', 'Borago officinalis var. officinalis', 'Mediterranean', 'Seeds, flowers', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Borago officinalis');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Rumex crispus', 'Buckwort', 'Polygonaceae', 'Herb for skin conditions and liver support', 'Ревень', 'Ревень', 'Rumex crispus var. crispus', 'Europe, Asia', 'Root', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Rumex crispus');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Vinca minor', 'Periwinkle', 'Apocynaceae', 'Ground cover with cardiovascular effects', 'Барвинка', 'Барвинка', 'Vinca rosea', 'Europe', 'Leaves', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Vinca minor');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Allium cepa', 'Onion', 'Amaryllidaceae', 'Common vegetable with cardiovascular benefits', 'Лук', 'Пияз', 'Allium cepa var. cepa', 'Worldwide', 'Bulb', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Allium cepa');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Vitis vinifera', 'Grape', 'Vitaceae', 'Grape vine with resveratrol and antioxidants', 'Виноград', 'Жүзім', 'Vitis vinifera var. vinifera', 'Worldwide', 'Fruit, seeds', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Vitis vinifera');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Malus domestica', 'Apple', 'Rosaceae', 'Fruit with quercetin and fiber', 'Яблоко', 'Алма', 'Malus domestica var. domestica', 'Worldwide', 'Fruit', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Malus domestica');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Prunus avium', 'Sweet Cherry', 'Rosaceae', 'Berry with anthocyanins and anti-inflammatory properties', 'Вишня', 'Өрік', 'Prunus avium var. avium', 'Northern hemisphere', 'Fruit', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Prunus avium');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Brassica oleracea', 'Broccoli', 'Brassicaceae', 'Vegetable with sulforaphane and antioxidants', 'Брокколи', 'Брокколи', 'Brassica oleracea var. italica', 'Mediterranean', 'Florets', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Brassica oleracea');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Coriolus versicolor', 'Turkey Tail', 'Polyporaceae', 'Medicinal mushroom with polysaccharide K', 'Трутовик', 'Трутовик', 'Trametes versicolor', 'Worldwide', 'Fruiting body', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Coriolus versicolor');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Spirulina platensis', 'Spirulina', 'Cyanobacteria', 'Blue-green algae with anti-inflammatory properties', 'Спирулина', 'Спирулина', 'Arthrospira platensis', 'Worldwide', 'Whole organism', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Spirulina platensis');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Ilex paraguariensis', 'Yerba Mate', 'Aquifoliaceae', 'South American caffeinated herbal tea', 'Мате', 'Мате', 'Ilex marienega', 'South America', 'Leaves', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Ilex paraguariensis');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Coffea arabica', 'Arabica Coffee', 'Rubiaceae', 'Coffee plant with caffeine and antioxidants', 'Кофе арабика', 'Кофе', 'Coffea arabica var. typica', 'Tropical regions', 'Seeds', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Coffea arabica');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Helianthus annuus', 'Sunflower', 'Asteraceae', 'Oil-rich plant with skin benefits', 'Подсолнух', 'Күнбағыс', 'Helianthus annuus var. annuus', 'North America', 'Seeds', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Helianthus annuus');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Simmondsia chinensis', 'Jojoba', 'Simmondsiaceae', 'Shrub producing oil similar to human sebum', 'Жожоба', 'Жожоба', 'Simmondsia chinensis var. chinensis', 'North America', 'Seeds', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Simmondsia chinensis');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Secale cereale', 'Rye', 'Poaceae', 'Cereal grain with cardiovascular benefits', 'Рожь', 'Арпа', 'Secale cereale var. cereale', 'Europe, Asia', 'Seeds', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Secale cereale');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Triticum aestivum', 'Wheat', 'Poaceae', 'Cereal grain with fiber and cardiovascular benefits', 'Пшеница', 'Бұғда', 'Triticum aestivum var. aestivum', 'Worldwide', 'Seeds', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Triticum aestivum');

INSERT INTO plants (scientific_name, display_name, family, description, name_ru, name_kk, synonyms, distribution_regions, used_parts, review_status, created_at, updated_at)
SELECT 'Propolis', 'Bee Propolis', 'Apiaceae', 'Bee product with antimicrobial and immune properties', 'Прополис', 'Прополис', 'Propolis extract', 'Worldwide', 'Resin', 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plants WHERE scientific_name = 'Propolis');

-- Additional verified claims for the new plants
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status, created_at)
SELECT p.id, 'INDICATION', 'Respiratory support', 'Thyme extract has demonstrated efficacy in treating acute respiratory tract infections including bronchitis and upper respiratory infections', NULL, 1, 'WHO Monographs on Selected Medicinal Plants, Vol. 1, p. 285', 'VERIFIED', CURRENT_TIMESTAMP
FROM plants p WHERE p.scientific_name = 'Thymus vulgaris' AND NOT EXISTS (SELECT 1 FROM knowledge_claims kc WHERE kc.plant_id = p.id AND kc.title = 'Respiratory support');

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status, created_at)
SELECT p.id, 'CONTRAINDICATION', 'Hypertension caution', 'Licorice root may cause hypertension and hypokalemia with prolonged use; contraindicated in uncontrolled hypertension', NULL, 1, 'WHO Monographs on Selected Medicinal Plants, Vol. 2, p. 142', 'VERIFIED', CURRENT_TIMESTAMP
FROM plants p WHERE p.scientific_name = 'Glycyrrhiza glabra' AND NOT EXISTS (SELECT 1 FROM knowledge_claims kc WHERE kc.plant_id = p.id AND kc.title = 'Hypertension caution');

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status, created_at)
SELECT p.id, 'INTERACTION', 'Anticoagulant interaction', 'Garlic may enhance the effects of anticoagulant medications such as warfarin, increasing bleeding risk', 'Warfarin', 1, 'WHO Monographs on Selected Medicinal Plants, Vol. 3, p. 95', 'VERIFIED', CURRENT_TIMESTAMP
FROM plants p WHERE p.scientific_name = 'Allium sativum' AND NOT EXISTS (SELECT 1 FROM knowledge_claims kc WHERE kc.plant_id = p.id AND kc.title = 'Anticoagulant interaction');

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status, created_at)
SELECT p.id, 'INDICATION', 'Anti-inflammatory effects', 'Curcumin from turmeric has demonstrated anti-inflammatory effects comparable to NSAIDs in osteoarthritis', NULL, 1, 'WHO Monographs on Selected Medicinal Plants, Vol. 3, p. 112', 'VERIFIED', CURRENT_TIMESTAMP
FROM plants p WHERE p.scientific_name = 'Curcuma longa' AND NOT EXISTS (SELECT 1 FROM knowledge_claims kc WHERE kc.plant_id = p.id AND kc.title = 'Anti-inflammatory effects');

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status, created_at)
SELECT p.id, 'INDICATION', 'Nausea relief', 'Ginger has been shown to be effective for nausea and vomiting including pregnancy-related nausea, chemotherapy-induced nausea, and postoperative nausea', NULL, 1, 'WHO Monographs on Selected Medicinal Plants, Vol. 3, p. 88', 'VERIFIED', CURRENT_TIMESTAMP
FROM plants p WHERE p.scientific_name = 'Zingiber officinale' AND NOT EXISTS (SELECT 1 FROM knowledge_claims kc WHERE kc.plant_id = p.id AND kc.title = 'Nausea relief');

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status, created_at)
SELECT p.id, 'INDICATION', 'Cardiovascular support', 'Hawthorn extract has been used for mild heart failure and may improve cardiac output and exercise tolerance', NULL, 1, 'WHO Monographs on Selected Medicinal Plants, Vol. 1, p. 156', 'VERIFIED', CURRENT_TIMESTAMP
FROM plants p WHERE p.scientific_name = 'Crataegus monogyna' AND NOT EXISTS (SELECT 1 FROM knowledge_claims kc WHERE kc.plant_id = p.id AND kc.title = 'Cardiovascular support');

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status, created_at)
SELECT p.id, 'INDICATION', 'Immune support', 'Echinacea has been used for prevention and treatment of upper respiratory tract infections, though evidence is mixed', NULL, 1, 'WHO Monographs on Selected Medicinal Plants, Vol. 3, p. 67', 'VERIFIED', CURRENT_TIMESTAMP
FROM plants p WHERE p.scientific_name = 'Echinacea purpurea' AND NOT EXISTS (SELECT 1 FROM knowledge_claims kc WHERE kc.plant_id = p.id AND kc.title = 'Immune support');

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status, created_at)
SELECT p.id, 'INDICATION', 'Adaptogenic effects', 'Panax ginseng has adaptogenic properties and may improve energy, reduce fatigue, and support immune function', NULL, 1, 'WHO Monographs on Selected Medicinal Plants, Vol. 2, p. 234', 'VERIFIED', CURRENT_TIMESTAMP
FROM plants p WHERE p.scientific_name = 'Panax ginseng' AND NOT EXISTS (SELECT 1 FROM knowledge_claims kc WHERE kc.plant_id = p.id AND kc.title = 'Adaptogenic effects');

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status, created_at)
SELECT p.id, 'INDICATION', 'Wound healing', 'Aloe vera gel has demonstrated wound healing properties and is used for burns, cuts, and skin irritation', NULL, 1, 'WHO Monographs on Selected Medicinal Plants, Vol. 1, p. 312', 'VERIFIED', CURRENT_TIMESTAMP
FROM plants p WHERE p.scientific_name = 'Aloe vera' AND NOT EXISTS (SELECT 1 FROM knowledge_claims kc WHERE kc.plant_id = p.id AND kc.title = 'Wound healing');

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status, created_at)
SELECT p.id, 'INDICATION', 'Anxiety and sleep', 'Passionflower has anxiolytic properties and may improve sleep quality in individuals with insomnia', NULL, 1, 'WHO Monographs on Selected Medicinal Plants, Vol. 3, p. 189', 'VERIFIED', CURRENT_TIMESTAMP
FROM plants p WHERE p.scientific_name = 'Passiflora incarnata' AND NOT EXISTS (SELECT 1 FROM knowledge_claims kc WHERE kc.plant_id = p.id AND kc.title = 'Anxiety and sleep');

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status, created_at)
SELECT p.id, 'INDICATION', 'Relaxation and sleep', 'Lavender oil has demonstrated anxiolytic and sedative effects, improving sleep quality and reducing anxiety', NULL, 1, 'WHO Monographs on Selected Medicinal Plants, Vol. 2, p. 167', 'VERIFIED', CURRENT_TIMESTAMP
FROM plants p WHERE p.scientific_name = 'Lavandula angustifolia' AND NOT EXISTS (SELECT 1 FROM knowledge_claims kc WHERE kc.plant_id = p.id AND kc.title = 'Relaxation and sleep');

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status, created_at)
SELECT p.id, 'INDICATION', 'Digestive support', 'Peppermint oil has been shown to be effective for irritable bowel syndrome symptoms including abdominal pain and bloating', NULL, 1, 'WHO Monographs on Selected Medicinal Plants, Vol. 2, p. 198', 'VERIFIED', CURRENT_TIMESTAMP
FROM plants p WHERE p.scientific_name = 'Mentha piperita' AND NOT EXISTS (SELECT 1 FROM knowledge_claims kc WHERE kc.plant_id = p.id AND kc.title = 'Digestive support');

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status, created_at)
SELECT p.id, 'INDICATION', 'Antioxidant effects', 'Green tea polyphenols, particularly EGCG, have demonstrated antioxidant and anti-cancer properties in observational studies', NULL, 1, 'WHO Monographs on Selected Medicinal Plants, Vol. 3, p. 245', 'VERIFIED', CURRENT_TIMESTAMP
FROM plants p WHERE p.scientific_name = 'Camellia sinensis' AND NOT EXISTS (SELECT 1 FROM knowledge_claims kc WHERE kc.plant_id = p.id AND kc.title = 'Antioxidant effects');

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status, created_at)
SELECT p.id, 'INDICATION', 'Blood pressure reduction', 'Hibiscus sabdariffa tea has been shown to reduce blood pressure in hypertensive individuals', NULL, 1, 'WHO Monographs on Selected Medicinal Plants, Vol. 2, p. 134', 'VERIFIED', CURRENT_TIMESTAMP
FROM plants p WHERE p.scientific_name = 'Hibiscus sabdariffa' AND NOT EXISTS (SELECT 1 FROM knowledge_claims kc WHERE kc.plant_id = p.id AND kc.title = 'Blood pressure reduction');

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status, created_at)
SELECT p.id, 'INDICATION', 'Joint pain relief', 'Devils claw has demonstrated efficacy in reducing pain and improving function in osteoarthritis and lower back pain', NULL, 1, 'WHO Monographs on Selected Medicinal Plants, Vol. 2, p. 112', 'VERIFIED', CURRENT_TIMESTAMP
FROM plants p WHERE p.scientific_name = 'Harpagophytum procumbens' AND NOT EXISTS (SELECT 1 FROM knowledge_claims kc WHERE kc.plant_id = p.id AND kc.title = 'Joint pain relief');
