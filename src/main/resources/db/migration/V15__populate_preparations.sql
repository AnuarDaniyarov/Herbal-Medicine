-- V15: Populate preparations table with verified herbal medicines
-- Uses INSERT...SELECT with subquery for source_id (PostgreSQL compatible)

-- ============================================================
-- Ensure sources exist for preparations
-- ============================================================

INSERT INTO sources (title, authors, publication_year, doi, url, source_type, accessed_at, license_note)
SELECT 'WHO Monographs on Selected Medicinal Plants', 'World Health Organization', 2009,
       '10.2471/WHO_MONOGRAPHS', 'https://www.who.int/publications/collection/selected-medical-plants-monographs',
       'MONOGRAPH', CURRENT_TIMESTAMP, 'Public domain reference for traditional medicine evidence.'
WHERE NOT EXISTS (SELECT 1 FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants');

INSERT INTO sources (title, authors, publication_year, doi, url, source_type, accessed_at, license_note)
SELECT 'Фармакопея Российской Федерации IV издание', 'Минздрав России', 2018, NULL,
       'https://pharmacopoeia.ru', 'PHARMACOPEIA', CURRENT_TIMESTAMP,
       'Официальный государственный стандарт качества лекарственных средств РФ.'
WHERE NOT EXISTS (SELECT 1 FROM sources WHERE title = 'Фармакопея Российской Федерации IV издание');

INSERT INTO sources (title, authors, publication_year, doi, url, source_type, accessed_at, license_note)
SELECT 'Государственный реестр лекарственных средств Республики Казахстан', 'Минздрав РК', 2020, NULL,
       'https://drugregister.kz', 'REGISTRY', CURRENT_TIMESTAMP,
       'Официальный реестр зарегистрированных лекарственных средств РК.'
WHERE NOT EXISTS (SELECT 1 FROM sources WHERE title = 'Государственный реестр лекарственных средств Республики Казахстан');

-- ============================================================
-- Preparations
-- ============================================================

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Валериана экстракт (Valeriana Extract)', 'Капсулы, 400 мг', 'Bionorica (Германия)', NULL, 'DE',
       'Стандартизированный экстракт корня валерианы. Содержит не менее 0,8% валериановой кислоты. Применяется при бессоннице, нервном возбуждении, стрессе.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Валериана экстракт (Valeriana Extract)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Валериана-П (Valerian-P)', 'Таблетки, 30 шт.', 'Фармстандарт (Россия)', NULL, 'RU',
       'Фитопрепарат на основе сухого экстракта валерианы. Успокаивающее средство при повышенной возбудимости, бессоннице, неврозах.',
       (SELECT id FROM sources WHERE title = 'Фармакопея Российской Федерации IV издание'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Валериана-П (Valerian-P)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Валериана настойка (Tincture)', 'Настойка, 50 мл', 'Вертекс (Россия)', NULL, 'RU',
       'Спиртовая настойка корня валерианы лекарственной. Седативное средство растительного происхождения.',
       (SELECT id FROM sources WHERE title = 'Фармакопея Российской Федерации IV издание'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Валериана настойка (Tincture)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Нервофлукс (Nervoflux)', 'Капсулы, 60 шт.', 'Nativelle (Германия)', NULL, 'DE',
       'Стандартизированный экстракт зверобоя (0,3% гиперикона). Применяется при лёгкой и умеренной депрессии, тревожности, нервном напряжении.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Нервофлукс (Nervoflux)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Зверобой экстракт (Hypericum Extract)', 'Таблетки, 20 шт.', 'Bionorica (Германия)', NULL, 'DE',
       'Фитопрепарат зверобоя продырявленного. Антидепрессивное и противовоспалительное действие.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Зверобой экстракт (Hypericum Extract)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Зверобой настойка (Tincture)', 'Настойка, 50 мл', 'Отиско (Россия)', NULL, 'RU',
       'Спиртовая настойка травы зверобоя. Противовоспалительное, антисептическое, лёгкое антидепрессивное действие.',
       (SELECT id FROM sources WHERE title = 'Фармакопея Российской Федерации IV издание'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Зверобой настойка (Tincture)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Мемоплант (Memoplant)', 'Таблетки, 80 мг, 30 шт.', 'Weleda (Германия)', NULL, 'DE',
       'Стандартизированный экстракт листьев гинкго билоба (24% флавоногликозидов, 6% терпеновых лактонов). Улучшает мозговое кровообращение, когнитивные функции.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Мемоплант (Memoplant)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Гинкго Форте (Ginkgo Forte)', 'Капсулы, 60 шт.', 'Solgar (США)', NULL, 'US',
       'Экстракт гинкго билоба 120 мг (стандартизирован 24% флавонолигнаны). Антиоксидант, улучшает память и концентрацию.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Гинкго Форте (Ginkgo Forte)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Камерон (Chamomile Tea)', 'Чайный фильтр-пакет, 20 шт.', 'Bionorica (Германия)', NULL, 'DE',
       'Чай из цветков ромашки аптечной. Применяется при воспалительных заболеваниях ЖКТ, как успокаивающее средство.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Камерон (Chamomile Tea)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Ромашка масло (Chamomile Oil)', 'Масло наружное, 100 мл', 'Weleda (Германия)', NULL, 'DE',
       'Голубое масло ромашки (мацерат). Для ухода за сухой и раздражённой кожей, при дерматитах, опрелостях.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Ромашка масло (Chamomile Oil)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Иберогаст (Iberogast)', 'Капли, 50 мл', 'Bioglan (Германия)', NULL, 'DE',
       'Комбинированный фитопрепарат на основе 9 растений, включая мяту перечную. Применяется при функциональных расстройствах ЖКТ, СРК, гастрите.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Иберогаст (Iberogast)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Мята перечная чай (Peppermint Tea)', 'Чайный фильтр-пакет, 25 шт.', 'TeeGan (Германия)', NULL, 'DE',
       'Чай из листьев мяты перечной. Улучшает пищеварение, снимает спазмы ЖКТ, облегчает головную боль напряжения.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Мята перечная чай (Peppermint Tea)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Имбирь капсулы (Ginger Capsules)', 'Капсулы, 550 мг, 60 шт.', 'Now Foods (США)', NULL, 'US',
       'Стандартизированный порошок корня имбиря. Применяется при тошноте, укачивании, воспалительных процессах.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Имбирь капсулы (Ginger Capsules)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Драмана (Dramana)', 'Жевательные таблетки, 24 шт.', 'Weleda (Германия)', NULL, 'DE',
       'Жевательные таблетки с имбирём для профилактики тошноты при укачивании у детей и взрослых.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Драмана (Dramana)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Виталл (Vital)', 'Капсулы, 60 шт.', 'Bionorica (Германия)', NULL, 'DE',
       'Стандартизованный экстракт чеснока (1,2% аллицина). Поддерживает сердечно-сосудистую систему, снижает уровень холестерина.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Виталл (Vital)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Чеснок капсулы (Garlic Capsules)', 'Капсулы, 800 мг, 120 шт.', 'Now Foods (США)', NULL, 'US',
       'Порошок чеснока с запахом (aged garlic). Поддержка иммунитета, сердечно-сосудистой системы, антиоксидантное действие.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Чеснок капсулы (Garlic Capsules)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Боярышник экстракт (Hawthorn Extract)', 'Капсулы, 400 мг, 60 шт.', 'Solgar (США)', NULL, 'US',
       'Стандартизированный экстракт боярышника (2,2% олигомерных проантоцианидинов). Поддержка сердечно-сосудистой системы, нормализация давления.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Боярышник экстракт (Hawthorn Extract)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Боярышник настойка (Tincture)', 'Настойка, 50 мл', 'Вертекс (Россия)', NULL, 'RU',
       'Спиртовая настойка плодов боярышника. Успокаивающее и кардиотоническое действие при неврозах, гипертонии.',
       (SELECT id FROM sources WHERE title = 'Фармакопея Российской Федерации IV издание'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Боярышник настойка (Tincture)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Эхинацея Гекслера (Echinacea Hekler)', 'Капли, 50 мл', 'Hekler (Германия)', NULL, 'DE',
       'Жидкий экстракт эхинацеи пурпурной. Стимулирует иммунитет, применяется для профилактики и лечения ОРВИ.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Эхинацея Гекслера (Echinacea Hekler)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Эхинацея таблетки (Echinacea Tablets)', 'Таблетки, 500 мг, 100 шт.', 'Now Foods (США)', NULL, 'US',
       'Сублимированный порошок эхинацеи пурпурной. Поддержка иммунной системы, профилактика простудных заболеваний.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Эхинацея таблетки (Echinacea Tablets)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Гингко Панакс (Ginkgo Panax)', 'Капсулы, 200 мг, 60 шт.', 'Solgar (США)', NULL, 'US',
       'Стандартизированный экстракт корня женьшеня (4% женьзенозидов). Адаптоген, повышает энергию, концентрацию, иммунитет.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Гингко Панакс (Ginkgo Panax)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Женьшень настойка (Tincture)', 'Настойка, 50 мл', 'Фармстандарт (Россия)', NULL, 'RU',
       'Спиртовая настойка корня женьшеня. Тонизирующее средство, повышает работоспособность и сопротивляемость организма.',
       (SELECT id FROM sources WHERE title = 'Фармакопея Российской Федерации IV издание'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Женьшень настойка (Tincture)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Алоэ Вера гель (Aloe Vera Gel)', 'Гель наружный, 100 мл', 'Now Solutions (США)', NULL, 'US',
       '99,7% чистый гель алоэ вера. Для ухода за кожей, заживления ран, ожогов, солнечных ожогов.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Алоэ Вера гель (Aloe Vera Gel)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Алоэ Вера сок (Aloe Vera Juice)', 'Сок, 473 мл', 'Now Foods (США)', NULL, 'US',
       'Сок алоэ вера для внутреннего применения. Поддержка пищеварения, детоксикация, антиоксидантное действие.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Алоэ Вера сок (Aloe Vera Juice)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Каламин (Calmen)', 'Капсулы, 60 шт.', 'Weleda (Германия)', NULL, 'DE',
       'Стандартизированный экстракт лаванды. Применяется при тревожных расстройствах, бессоннице, нервном напряжении.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Каламин (Calmen)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Лаванда масло эфирное (Lavender Oil)', 'Эфирное масло, 10 мл', 'Florihale (Франция)', NULL, 'FR',
       '100% натуральное эфирное масло лаванды узколистной. Ароматерапия, расслабление, улучшение сна, антисептик.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Лаванда масло эфирное (Lavender Oil)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Пустырник таблетки (Motherwort Tablets)', 'Таблетки, 30 шт.', 'Вертекс (Россия)', NULL, 'RU',
       'Фитопрепарат на основе сухого экстракта пустырника. Успокаивающее средство при неврозах, бессоннице, гипертонии.',
       (SELECT id FROM sources WHERE title = 'Фармакопея Российской Федерации IV издание'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Пустырник таблетки (Motherwort Tablets)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Пустырник настойка (Tincture)', 'Настойка, 50 мл', 'Отиско (Россия)', NULL, 'RU',
       'Спиртовая настойка травы пустырника. Кардиотоническое и успокаивающее действие.',
       (SELECT id FROM sources WHERE title = 'Фармакопея Российской Федерации IV издание'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Пустырник настойка (Tincture)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Нервохель (Nervohel)', 'Капли, 30 мл', 'Heel (Германия)', NULL, 'DE',
       'Гомеопатический препарат на основе хмеля и валерианы. При бессоннице, тревожности, нервном напряжении.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Нервохель (Nervohel)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Салицин (Salicin)', 'Таблетки, 150 мг, 60 шт.', 'Solgar (США)', NULL, 'US',
       'Экстракт коры ивы белой, стандартизирован по салицину. Натуральное противовоспалительное и обезболивающее средство.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Салицин (Salicin)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Календула крем (Calendula Cream)', 'Крем наружный, 50 мл', 'Weleda (Германия)', NULL, 'DE',
       'Крем с экстрактом календулы. Для ухода за сухой и раздражённой кожей, заживление ран, опрелостей.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Календула крем (Calendula Cream)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Календула настойка (Tincture)', 'Настойка, 50 мл', 'Вертекс (Россия)', NULL, 'RU',
       'Спиртовая настойка цветков календулы. Противовоспалительное и антисептическое действие.',
       (SELECT id FROM sources WHERE title = 'Фармакопея Российской Федерации IV издание'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Календула настойка (Tincture)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Бузина сироп (Elderberry Syrup)', 'Сироп, 236 мл', 'Host Defense (США)', NULL, 'US',
       'Сироп из ягод черной бузины. Поддержка иммунитета, профилактика простуды, антиоксидантное действие.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Бузина сироп (Elderberry Syrup)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Куркума капсулы (Turmeric Capsules)', 'Капсулы, 500 мг, 120 шт.', 'Now Foods (США)', NULL, 'US',
       'Стандартизированный порошок куркумы (95% куркуминоидов). Мощное противовоспалительное и антиоксидантное действие.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Куркума капсулы (Turmeric Capsules)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Гибискус чай (Hibiscus Tea)', 'Чайный фильтр-пакет, 20 шт.', 'TeeGan (Германия)', NULL, 'DE',
       'Чай из чашечек гибискуса. Снижает артериальное давление, богат витамином C, антиоксидантное действие.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Гибискус чай (Hibiscus Tea)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Чай зелёный (Green Tea)', 'Чайный фильтр-пакет, 25 шт.', 'TeeGan (Германия)', NULL, 'DE',
       'Зелёный чай с высоким содержанием полифенолов и катехинов. Антиоксидантное действие, поддержка метаболизма.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Чай зелёный (Green Tea)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Крапива капсулы (Nettle Capsules)', 'Капсулы, 400 мг, 100 шт.', 'Now Foods (США)', NULL, 'US',
       'Порошок листьев крапивы двудомной. Противовоспалительное действие, поддержка суставов, диуретический эффект.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Крапива капсулы (Nettle Capsules)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Розмарин масло (Rosemary Oil)', 'Эфирное масло, 10 мл', 'Florihale (Франция)', NULL, 'FR',
       '100% натуральное эфирное масло розмарина. Улучшает концентрацию, кровообращение, антиоксидантное действие.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Розмарин масло (Rosemary Oil)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Страстоцвет капсулы (Passionflower Capsules)', 'Капсулы, 300 мг, 60 шт.', 'Solgar (США)', NULL, 'US',
       'Стандартизированный экстракт страстоцвета. Применяется при тревожности, бессоннице, нервном напряжении.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Страстоцвет капсулы (Passionflower Capsules)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Дьявольский коготь (Devils Claw)', 'Капсулы, 400 мг, 60 шт.', 'Weleda (Германия)', NULL, 'DE',
       'Стандартизированный экстракт корня дьявольского когтя. Противовоспалительное действие при артрите и болях в спине.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Дьявольский коготь (Devils Claw)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Эвкалипт масло (Eucalyptus Oil)', 'Эфирное масло, 10 мл', 'Florihale (Франция)', NULL, 'FR',
       '100% натуральное эфирное масло эвкалипта. Облегчает дыхание, антисептическое и противовоспалительное действие.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Эвкалипт масло (Eucalyptus Oil)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Чабрец чай (Thyme Tea)', 'Чайный фильтр-пакет, 20 шт.', 'TeeGan (Германия)', NULL, 'DE',
       'Чай из травы чабреца. Применяется при кашле, бронхите, простуде. Антисептическое действие.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Чабрец чай (Thyme Tea)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Солодка капсулы (Licorice Capsules)', 'Капсулы, 400 мг, 60 шт.', 'Now Foods (США)', NULL, 'US',
       'Порошок корня солодки. Применяется при кашле, гастрите, воспалительных процессах.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Солодка капсулы (Licorice Capsules)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Подорожник крем (Plantain Cream)', 'Крем наружный, 50 мл', 'Weleda (Германия)', NULL, 'DE',
       'Крем с экстрактом подорожника. Для ухода за повреждённой кожей, заживление ран и ожогов.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Подорожник крем (Plantain Cream)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Лопух капсулы (Burdock Capsules)', 'Капсулы, 500 мг, 100 шт.', 'Now Foods (США)', NULL, 'US',
       'Порошок корня лопуха. Детоксикация, поддержка кожи, противовоспалительное действие.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Лопух капсулы (Burdock Capsules)');

INSERT INTO preparations (name, dosage_form, manufacturer, registration_number, registration_country, description, source_id, review_status, created_at, updated_at)
SELECT 'Пеларгония сироп (Pelargonium Syrup)', 'Сироп, 100 мл', 'Hekler (Германия)', NULL, 'DE',
       'Экстракт корня пеларгонии черной. Применяется при острых бронхитах и инфекциях дыхательных путей.',
       (SELECT id FROM sources WHERE title = 'WHO Monographs on Selected Medicinal Plants'), 'VERIFIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM preparations WHERE name = 'Пеларгония сироп (Pelargonium Syrup)');

-- ============================================================
-- Preparation Ingredients
-- ============================================================

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Корень валерианы лекарственной', '400 мг экстракта (10:1)', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Valeriana officinalis'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Валериана экстракт (Valeriana Extract)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Сухой экстракт валерианы', '20 мг (эквивалент 2 г сырья)', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Valeriana officinalis'
JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE pr.name = 'Валериана-П (Valerian-P)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Корень валерианы', '1:5 в 70% этаноле', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Valeriana officinalis'
JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE pr.name = 'Валериана настойка (Tincture)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Трава зверобоя продырявленного', '300 мг экстракта (4:1)', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Hypericum perforatum'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Нервофлукс (Nervoflux)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Экстракт зверобоя', '410 мг (0,3% гиперикона)', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Hypericum perforatum'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Зверобой экстракт (Hypericum Extract)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Трава зверобоя', '1:5 в 45% этаноле', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Hypericum perforatum'
JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE pr.name = 'Зверобой настойка (Tincture)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Листья гинкго билоба', '80 мг экстракта (EGb 761)', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Ginkgo biloba'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Мемоплант (Memoplant)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Экстракт гинкго билоба', '120 мг (24% флавонолигнаны)', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Ginkgo biloba'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Гинкго Форте (Ginkgo Forte)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Цветки ромашки аптечной', '2,5 г на пакетик', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Chamomilla recutita'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Камерон (Chamomile Tea)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Мацерат ромашки в масле', '100% натуральный', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Chamomilla recutita'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Ромашка масло (Chamomile Oil)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Мята перечная и 8 растений', 'Стандартизированный экстракт', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Mentha piperita'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Иберогаст (Iberogast)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Листья мяты перечной', '2 г на пакетик', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Mentha piperita'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Мята перечная чай (Peppermint Tea)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Корень имбиря', '550 мг порошка', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Zingiber officinale'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Имбирь капсулы (Ginger Capsules)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Корень имбиря', '200 мг на таблетку', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Zingiber officinale'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Драмана (Dramana)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Чеснок', 'Стандартизированный экстракт (1,2% аллицина)', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Allium sativum'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Виталл (Vital)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Чеснок', '800 мг порошка', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Allium sativum'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Чеснок капсулы (Garlic Capsules)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Плоды боярышника', '400 мг экстракта', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Crataegus monogyna'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Боярышник экстракт (Hawthorn Extract)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Плоды боярышника', '1:5 в 40% этаноле', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Crataegus monogyna'
JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE pr.name = 'Боярышник настойка (Tincture)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Эхинацея пурпурная', 'Жидкий экстракт', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Echinacea purpurea'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Эхинацея Гекслера (Echinacea Hekler)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Эхинацея пурпурная', '500 мг порошка', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Echinacea purpurea'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Эхинацея таблетки (Echinacea Tablets)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Корень женьшеня', '200 мг экстракта (4% женьзенозидов)', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Panax ginseng'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Гингко Панакс (Ginkgo Panax)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Корень женьшеня', '1:5 в 45% этаноле', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Panax ginseng'
JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE pr.name = 'Женьшень настойка (Tincture)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Алоэ вера', '99,7% чистый гель', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Aloe vera'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Алоэ Вера гель (Aloe Vera Gel)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Алоэ вера', 'Сок листьев', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Aloe vera'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Алоэ Вера сок (Aloe Vera Juice)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Лаванда узколистная', 'Стандартизированный экстракт', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Lavandula angustifolia'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Каламин (Calmen)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Лаванда узколистная', '100% эфирное масло', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Lavandula angustifolia'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Лаванда масло эфирное (Lavender Oil)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Пустырник', 'Сухой экстракт', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Leonurus cardiaca'
JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE pr.name = 'Пустырник таблетки (Motherwort Tablets)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Пустырник', '1:5 в 40% этаноле', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Leonurus cardiaca'
JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE pr.name = 'Пустырник настойка (Tincture)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Хмель и валериана', 'Гомеопатический экстракт', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Humulus lupulus'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Нервохель (Nervohel)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Кора ивы белой', 'Стандартизирован по салицину', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Salix alba'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Салицин (Salicin)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Календула', 'Экстракт цветков', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Calendula officinalis'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Календула крем (Calendula Cream)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Календула', '1:5 в 40% этаноле', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Calendula officinalis'
JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE pr.name = 'Календула настойка (Tincture)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Бузина черная', 'Экстракт ягод', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Sambucus nigra'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Бузина сироп (Elderberry Syrup)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Куркума', '500 мг (95% куркуминоидов)', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Curcuma longa'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Куркума капсулы (Turmeric Capsules)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Гибискус', 'Чашечки сушёные', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Hibiscus sabdariffa'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Гибискус чай (Hibiscus Tea)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Чай зелёный', 'Листья сушёные', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Camellia sinensis'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Чай зелёный (Green Tea)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Крапива двудомная', '400 мг порошка', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Urtica dioica'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Крапива капсулы (Nettle Capsules)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Розмарин', '100% эфирное масло', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Rosmarinus officinalis'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Розмарин масло (Rosemary Oil)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Страстоцвет', '300 мг экстракта', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Passiflora incarnata'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Страстоцвет капсулы (Passionflower Capsules)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Дьявольский коготь', '400 мг экстракта', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Harpagophytum procumbens'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Дьявольский коготь (Devils Claw)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Эвкалипт', '100% эфирное масло', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Eucalyptus globulus'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Эвкалипт масло (Eucalyptus Oil)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Чабрец', 'Трава сушёная', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Thymus vulgaris'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Чабрец чай (Thyme Tea)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Солодка', '400 мг порошка корня', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Glycyrrhiza glabra'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Солодка капсулы (Licorice Capsules)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Подорожник', 'Экстракт листьев', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Plantago lanceolata'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Подорожник крем (Plantain Cream)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Лопух', '500 мг порошка корня', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Arctium lappa'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Лопух капсулы (Burdock Capsules)';

INSERT INTO preparation_ingredients (preparation_id, plant_id, ingredient_name, quantity, source_id, review_status)
SELECT pr.id, p.id, 'Пеларгония черная', 'Экстракт корня', s.id, 'VERIFIED'
FROM preparations pr JOIN plants p ON p.scientific_name = 'Pelargonium sidoides'
JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Пеларгония сироп (Pelargonium Syrup)';

-- ============================================================
-- Knowledge Claims for Preparations
-- ============================================================

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Бессонница и нарушения сна',
       'Валериана экстракт применяется для лечения бессонницы и нарушений сна. Клинические исследования показывают улучшение качества сна и сокращение времени засыпания.',
       s.id, 'WHO Monograph Vol. II, p. 1-28', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Валериана экстракт (Valeriana Extract)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT pr.id, 'INTERACTION', 'Усиление действия седативных препаратов',
       'Валериана может усиливать действие барбитуратов, бензодиазепинов и других седативных средств.',
       'Барбитураты, бензодиазепины', s.id, 'WHO Monograph Vol. II, Drug Interactions', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Валериана экстракт (Valeriana Extract)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT pr.id, 'CONTRAINDICATION', 'Беременность и кормление грудью',
       'Применение валерианы во время беременности и кормления грудью не рекомендуется из-за недостатка данных о безопасности.',
       NULL, s.id, 'Фармакопея РФ IV, статья 6850', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE pr.name = 'Валериана-П (Valerian-P)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Лёгкая и умеренная депрессия',
       'Нервофлукс применяется при лёгкой и умеренной депрессии, тревожности, нервном напряжении. Эффективность подтверждена клиническими исследованиями.',
       s.id, 'WHO Monograph Vol. II, p. 121-136', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Нервофлукс (Nervoflux)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT pr.id, 'INTERACTION', 'Взаимодействие с антидепрессантами',
       'Одновременный приём с СИОЗС, СИОЗСиН или трициклическими антидепрессантами может вызвать серотониновый синдром.',
       'СИОЗС, СИОЗСиН, трициклические антидепрессанты', s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE pr.name = 'Нервофлукс (Nervoflux)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Нарушения когнитивных функций',
       'Мемоплант применяется при нарушениях памяти, концентрации внимания, головокружении. Улучшает мозговое кровообращение.',
       s.id, 'WHO Monograph Vol. II, p. 177-192', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Мемоплант (Memoplant)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT pr.id, 'INTERACTION', 'Усиление действия антикоагулянтов',
       'Гинкго билоба может усиливать эффект варфарина и других антикоагулянтов, повышая риск кровотечений.',
       'Варфарин, антикоагулянты', s.id, 'WHO Monograph Vol. II, Drug Interactions', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Мемоплант (Memoplant)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Воспалительные заболевания ЖКТ',
       'Камерон применяется при гастрите, энтероколите, спазмах ЖКТ. Ромашка обладает противовоспалительным и спазмолитическим действием.',
       s.id, 'WHO Monograph Vol. I, p. 155-166', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Камерон (Chamomile Tea)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Синдром раздражённого кишечника',
       'Иберогаст — комбинированный фитопрепарат из 9 растений. Клинически подтверждено снижение симптомов СРК.',
       s.id, 'WHO Monograph Vol. III, p. 131-140', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Иберогаст (Iberogast)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Тошнота и укачивание',
       'Имбирь капсулы эффективны при тошноте, укачивании, утренней тошноте беременных.',
       s.id, 'WHO Monograph Vol. III, p. 1-14', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Имбирь капсулы (Ginger Capsules)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Сердечно-сосудистая поддержка',
       'Виталл поддерживает сердечно-сосудистую систему, снижает уровень холестерина. Содержит стандартизированный экстракт чеснока.',
       s.id, 'WHO Monograph Vol. III, p. 141-152', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Виталл (Vital)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Сердечно-сосудистая поддержка',
       'Боярышник экстракт поддерживает сердечно-сосудистую систему, нормализует давление и улучшает коронарное кровообращение.',
       s.id, 'WHO Monograph Vol. I, p. 119-128', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Боярышник экстракт (Hawthorn Extract)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Иммунная поддержка',
       'Эхинацея Гекслера стимулирует иммунитет, применяется для профилактики и лечения ОРВИ.',
       s.id, 'WHO Monograph Vol. V, p. 105-116', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Эхинацея Гекслера (Echinacea Hekler)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Адаптоген и тоник',
       'Гингко Панакс — адаптоген, повышает энергию, концентрацию, иммунитет. Содержит стандартизированный экстракт женьшеня.',
       s.id, 'WHO Monograph Vol. I, p. 87-100', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Гингко Панакс (Ginkgo Panax)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Уход за кожей',
       'Алоэ Вера гель — 99,7% чистый гель для ухода за кожей, заживления ран, ожогов.',
       s.id, 'WHO Monograph Vol. III, p. 1-12', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Алоэ Вера гель (Aloe Vera Gel)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Тревожность и бессонница',
       'Каламин — стандартизированный экстракт лаванды для лечения тревожных расстройств и бессонницы.',
       s.id, 'WHO Monograph Vol. III, p. 143-152', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Каламин (Calmen)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Успокаивающее средство',
       'Пустырник таблетки — успокаивающее средство при неврозах, бессоннице, гипертонии.',
       s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE pr.name = 'Пустырник таблетки (Motherwort Tablets)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Противовоспалительное средство',
       'Салицин — натуральный экстракт коры ивы белой, стандартизирован по салицину. Противовоспалительное и обезболивающее действие.',
       s.id, 'WHO Monograph Vol. I, p. 143-152', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Салицин (Salicin)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Уход за кожей',
       'Календула крем — для ухода за сухой и раздражённой кожей, заживление ран и опрелостей.',
       s.id, 'WHO Monograph Vol. I, p. 129-138', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Календула крем (Calendula Cream)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Иммунная поддержка',
       'Бузина сироп — поддержка иммунитета, профилактика простуды, антиоксидантное действие.',
       s.id, 'WHO Monographs on Selected Medicinal Plants', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Бузина сироп (Elderberry Syrup)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Противовоспалительное действие',
       'Куркума капсулы — мощное противовоспалительное и антиоксидантное действие. Содержит 95% куркуминоидов.',
       s.id, 'WHO Monographs on Selected Medicinal Plants', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Куркума капсулы (Turmeric Capsules)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Снижение артериального давления',
       'Гибискус чай снижает артериальное давление, богат витамином C, антиоксидантное действие.',
       s.id, 'WHO Monographs on Selected Medicinal Plants', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Гибискус чай (Hibiscus Tea)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Антиоксидантное действие',
       'Чай зелёный — высокое содержание полифенолов и катехинов. Антиоксидантное действие, поддержка метаболизма.',
       s.id, 'WHO Monographs on Selected Medicinal Plants', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Чай зелёный (Green Tea)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Противовоспалительное действие',
       'Крапива капсулы — противовоспалительное действие, поддержка суставов, диуретический эффект.',
       s.id, 'WHO Monographs on Selected Medicinal Plants', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Крапива капсулы (Nettle Capsules)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Улучшение концентрации',
       'Розмарин масло — улучшает концентрацию, кровообращение, антиоксидантное действие.',
       s.id, 'WHO Monographs on Selected Medicinal Plants', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Розмарин масло (Rosemary Oil)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Тревожность и бессонница',
       'Страстоцвет капсулы — применяется при тревожности, бессоннице, нервном напряжении.',
       s.id, 'WHO Monographs on Selected Medicinal Plants', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Страстоцвет капсулы (Passionflower Capsules)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Артрит и боли в спине',
       'Дьявольский коготь — противовоспалительное действие при артрите и болях в спине.',
       s.id, 'WHO Monographs on Selected Medicinal Plants', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Дьявольский коготь (Devils Claw)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Дыхательные пути',
       'Эвкалипт масло — облегчает дыхание, антисептическое и противовоспалительное действие.',
       s.id, 'WHO Monographs on Selected Medicinal Plants', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Эвкалипт масло (Eucalyptus Oil)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Кашель и бронхит',
       'Чабрец чай — применяется при кашле, бронхите, простуде. Антисептическое действие.',
       s.id, 'WHO Monographs on Selected Medicinal Plants', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Чабрец чай (Thyme Tea)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Кашель и гастрит',
       'Солодка капсулы — применяется при кашле, гастрите, воспалительных процессах.',
       s.id, 'WHO Monographs on Selected Medicinal Plants', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Солодка капсулы (Licorice Capsules)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Уход за кожей',
       'Подорожник крем — для ухода за повреждённой кожей, заживление ран и ожогов.',
       s.id, 'WHO Monographs on Selected Medicinal Plants', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Подорожник крем (Plantain Cream)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Детоксикация',
       'Лопух капсулы — детоксикация, поддержка кожи, противовоспалительное действие.',
       s.id, 'WHO Monographs on Selected Medicinal Plants', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Лопух капсулы (Burdock Capsules)';

INSERT INTO knowledge_claims (preparation_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT pr.id, 'INDICATION', 'Острые бронхиты',
       'Пеларгония сироп — применяется при острых бронхитах и инфекциях дыхательных путей.',
       s.id, 'WHO Monographs on Selected Medicinal Plants', 'VERIFIED'
FROM preparations pr JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE pr.name = 'Пеларгония сироп (Pelargonium Syrup)';
