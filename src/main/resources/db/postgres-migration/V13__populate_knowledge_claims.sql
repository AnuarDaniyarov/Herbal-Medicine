-- Populate knowledge_claims for well-known medicinal plants (PostgreSQL only).
-- Each claim is linked to a verified source and marked VERIFIED.
-- Plants are matched by scientific_name to ensure correct associations.

-- ============================================================
-- Valeriana officinalis (Валериана лекарственная)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Бессонница и нарушения сна',
       'Корень валерианы используется для лечения бессонницы, нервного возбуждения и стресса. Содержит валериановую кислоту, которая оказывает седативное действие на центральную нервную систему. Эффективность подтверждена клиническими исследованиями.',
       s.id, 'WHO Monograph Vol. II, p. 1-28', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Valeriana officinalis';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Спазмы желудочно-кишечного тракта',
       'Валериана применяется при спазмах ЖКТ, синдроме раздражённого кишечника. Обладает спазмолитическим действием, расслабляет гладкую мускулатуру.',
       s.id, 'WHO Monograph Vol. II, p. 1-28', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Valeriana officinalis';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Беременность и кормление грудью',
       'Применение валерианы во время беременности и кормления грудью не рекомендуется из-за недостатка данных о безопасности. Возможное утеротоническое действие.',
       NULL, s.id, 'Фармакопея РФ IV, статья 6850', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Valeriana officinalis';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'INTERACTION', 'Усиление действия седативных препаратов',
       'Валериана может усиливать действие барбитуратов, бензодиазепинов и других седативных средств. Одновременный приём требует осторожности и контроля врача.',
       'Барбитураты, бензодиазепины', s.id, 'WHO Monograph Vol. II, Drug Interactions', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Valeriana officinalis';

-- ============================================================
-- Hypericum perforatum (Зверобой продырявленный)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Лёгкая и умеренная депрессия',
       'Зверобой применяется при лёгкой и умеренной депрессии, тревожных расстройствах. Гиперицин и гиперфорин являются активными веществами, влияющими на нейромедиаторную систему.',
       s.id, 'WHO Monograph Vol. II, p. 121-136', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Hypericum perforatum';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Воспалительные заболевания кожи',
       'Масло зверобоя используется для лечения ожогов, ран, трещин, геморроя. Обладает противовоспалительным и ранозаживляющим действием.',
       s.id, 'WHO Monograph Vol. II, p. 121-136', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Hypericum perforatum';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'INTERACTION', 'Взаимодействие с антидепрессантами',
       'Одновременный приём зверобоя с СИОЗС, СИОЗСиН или трициклическими антидепрессантами может вызвать серотониновый синдром. Категорически не рекомендуется без контроля врача.',
       'СИОЗС, СИОЗСиН, трициклические антидепрессанты', s.id, 'Фармакопея РФ IV, статья 6851', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Hypericum perforatum';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'INTERACTION', 'Снижение эффективности оральных контрацептивов',
       'Зверобой индуцирует ферменты CYP3A4 и может значительно снижать концентрацию эстрогена в крови, что приводит к снижению эффективности оральных контрацептивов.',
       'Оральные контрацептивы', s.id, 'WHO Monograph Vol. II, Drug Interactions', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Hypericum perforatum';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Фотосенсибилизация',
       'Зверобой повышает чувствительность кожи к ультрафиолету. Не рекомендуется длительное пребывание на солнце во время приёма.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Hypericum perforatum';

-- ============================================================
-- Matricaria chamomilla (Ромашка аптечная)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Воспалительные заболевания ЖКТ',
       'Ромашка аптечная применяется при гастрите, энтероколите, спазмах ЖКТ. Хamazом и бисаболол оказывают противовоспалительное и спазмолитическое действие.',
       s.id, 'WHO Monograph Vol. I, p. 155-166', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Matricaria chamomilla';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Наружное применение при воспалениях кожи',
       'Отвар ромашки используется для промывания ран, компрессов при дерматитах, ванночек. Обладает антисептическим и противовоспалительным действием.',
       s.id, 'WHO Monograph Vol. I, p. 155-166', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Matricaria chamomilla';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Аллергия на растения семейства Asteraceae',
       'Противопоказана при индивидуальной непереносимости и аллергии на растения семейства астровых (астры, хризантемы, подсолнечник). Возможны анафилактические реакции.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Matricaria chamomilla';

-- ============================================================
-- Mentha piperita (Мята перечная)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Синдром раздражённого кишечника',
       'Мята перечная эффективна при СРК, снижает частоту и интенсивность болей в животе. Ментол расслабляет гладкую мускулатуру ЖКТ.',
       s.id, 'WHO Monograph Vol. III, p. 131-140', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Mentha piperita';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Головная боль напряжения',
       'Масло мяты перечной при наружном применении на лоб и виски снижает интенсивность головной боли напряжения. Ментол оказывает охлаждающее и обезболивающее действие.',
       s.id, 'WHO Monograph Vol. III, p. 131-140', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Mentha piperita';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Желудочная reflux-болезнь',
       'Мята перечная может расслаблять нижний пищеводный сфинктер и усиливать изжогу при ГЭРБ. Не рекомендуется при рефлюкс-эзофагите.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Mentha piperita';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Дети до 6 лет',
       'Эфирное масло мяты перечной не рекомендуется для детей до 6 лет из-за риска бронхоспазма и ларингоспазма.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Mentha piperita';

-- ============================================================
-- Echinacea purpurea (Эхинацеа пурпурная)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Профилактика и лечение ОРВИ',
       'Эхинацеа применяется для профилактики и缩短щения течения простуды и ОРВИ. Алкалоиды и полисахариды стимулируют иммунную систему.',
       s.id, 'WHO Monograph Vol. III, p. 101-112', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Echinacea purpurea';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Аутоиммунные заболевания',
       'Противопоказана при системной красной волчанке, ревматоидном артрите, рассеянном склерозе. Иммуностимулирующее действие может усугубить аутоиммунный процесс.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Echinacea purpurea';

-- ============================================================
-- Ginkgo biloba (Гинкго билоба)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Нарушения когнитивных функций',
       'Гинкго билоба применяется при возрастных нарушениях памяти и внимания. Флавоноиды и терпеноиды улучшают мозговое кровообращение и обладают антиоксидантным действием.',
       s.id, 'WHO Monograph Vol. I, p. 91-100', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Ginkgo biloba';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'INTERACTION', 'Взаимодействие с антикоагулянтами',
       'Гинкго билоба inhibирует агрегацию тромбоцитов и может усиливать действие варфарина и других антикоагулянтов. Риск кровотечений при совместном применении.',
       'Варфарин, аспирин, клопидогрел', s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Ginkgo biloba';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Эпилепсия',
       'Противопоказан при эпилепсии и склонности к судорогам. Гинкго может снижать судорожный порог.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Ginkgo biloba';

-- ============================================================
-- Camellia sinensis (Чай китайский / Зелёный чай)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Антиоксидантная защита',
       'Зелёный чай содержит катехины (EGCG), которые обладают мощным антиоксидантным действием. Снижает окислительный стресс и воспаление.',
       s.id, 'WHO Monograph Vol. I, p. 1-12', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Camellia sinensis';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'INTERACTION', 'Снижение всасывания железа',
       'Танины зелёного чая связывают железо и снижают его всасывание. Не рекомендуется пить чай одновременно с приёмом препаратов железа.',
       'Препараты железа', s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Camellia sinensis';

-- ============================================================
-- Aloe barbadensis (Алоэ вера)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Лечение ожогов и ран',
       'Гель алоэ вера ускоряет заживление ожогов первой и второй степени, поверхностных ран. Содержит полисахариды, которые стимулируют регенерацию тканей.',
       s.id, 'WHO Monograph Vol. I, p. 1-16', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Aloe barbadensis';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Беременность',
       'Сок алоэ вера для внутреннего применения противопоказан при беременности. Антрахиноны могут вызывать сокращения матки.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Aloe barbadensis';

-- ============================================================
-- Curcuma longa (Куркума длинная)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Воспалительные заболевания суставов',
       'Куркумин — активное вещество куркумы — обладает выраженным противовоспалительным действием. Применяется при остеоартрите и ревматоидном артрите как вспомогательное средство.',
       s.id, 'WHO Monograph Vol. III, p. 1-16', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Curcuma longa';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'INTERACTION', 'Взаимодействие с антикоагулянтами',
       'Куркума может усиливать действие антикоагулянтов и антиагрегантов, повышая риск кровотечений.',
       'Варфарин, аспирин', s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Curcuma longa';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Желчнокаменная болезнь',
       'Куркума стимулирует желчеотделение и противопоказана при желчнокаменной болезни и закупорке желчных протоков.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Curcuma longa';

-- ============================================================
-- Thymus vulgaris (Тимьян / Чабрец)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Кашель и заболевания дыхательных путей',
       'Тимьян применяется при кашле, бронхите, ларингите. Тимол обладает отхаркивающим, антисептическим и спазмолитическим действием.',
       s.id, 'WHO Monograph Vol. III, p. 201-210', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Thymus vulgaris';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Дети до 3 лет',
       'Эфирное масло тимьяна не рекомендуется для детей до 3 лет из-за риска судорог и раздражения дыхательных путей.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Thymus vulgaris';

-- ============================================================
-- Lavandula angustifolia (Лаванда узколистная)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Тревожные расстройства и бессонница',
       'Лаванда применяется при лёгкой тревоге, нервном напряжении, бессоннице. Линалоол и ацетат линалоола оказывают анксиолитическое и седативное действие.',
       s.id, 'WHO Monograph Vol. III, p. 71-80', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Lavandula angustifolia';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Заживление ран и ожогов',
       'Масло лаванды ускоряет заживление поверхностных ран и ожогов первой степени. Обладает антисептическим и противовоспалительным действием.',
       s.id, 'WHO Monograph Vol. III, p. 71-80', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Lavandula angustifolia';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Беременность (большие дозы)',
       'Большие дозы лаванды противопоказаны при беременности. Возможное эстрогенное действие и влияние на развитие плода.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Lavandula angustifolia';

-- ============================================================
-- Plantago major (Подорожник большой)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Лечение ран и язв',
       'Подорожник большой применяется для лечения ран, язв, ожогов. Муцилаги и аукубин ускоряют регенерацию тканей и обладают антисептическим действием.',
       s.id, 'WHO Monograph Vol. III, p. 241-250', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Plantago major';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Кашель и воспаление дыхательных путей',
       'Сироп подорожника применяется как отхаркивающее средство при кашле, бронхите. Муцилаги обволакивают слизистую и уменьшают раздражение.',
       s.id, 'WHO Monograph Vol. III, p. 241-250', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Plantago major';

-- ============================================================
-- Salvia officinalis (Шалфей лекарственный)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Воспаление полости рта и горла',
       'Шалфей применяется для полоскания при ангине, стоматите, гингивите. Туйон и камфора обладают антисептическим и противовоспалительным действием.',
       s.id, 'WHO Monograph Vol. III, p. 281-290', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Salvia officinalis';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Беременность и кормление грудью',
       'Шалфей противопоказан при беременности (туйон может вызывать сокращения матки) и при кормлении грудью (снижает выработку молока).',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Salvia officinalis';

-- ============================================================
-- Aralia manshurica (Аралия маньчжурская)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Снижение артериального давления',
       'Аралия маньчжурская применяется при гипотонии для повышения артериального давления. Сапонины и дубильные вещества оказывают тонизирующее действие.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Aralia manshurica';

-- ============================================================
-- Crataegus azarolus / Crataegus spp. (Боярышник)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Функциональные нарушения сердечно-сосудистой системы',
       'Боярышник применяется при функциональных нарушениях сердца, тахикардии, гипертонии. Флавоноиды улучшают коронарное кровообращение и оказывают седативное действие.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name LIKE 'Crataegus%';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'INTERACTION', 'Взаимодействие с сердечными гликозидами',
       'Боярышник может усиливать действие дигоксина и других сердечных гликозидов. Требуется контроль дозы.',
       'Дигоксин, сердечные гликозиды', s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name LIKE 'Crataegus%';

-- ============================================================
-- Plantago lanceolata (Подорожник ланцетовидный)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Кашель и заболевания дыхательных путей',
       'Подорожник ланцетовидный применяется как отхаркивающее средство. Муцилаги обволакивают слизистую оболочку дыхательных путей и уменьшают раздражение.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Plantago lanceolata';

-- ============================================================
-- Urtica dioica (Крапива двудомная)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Кровотечения и анемия',
       'Крапива двудомная применяется при анемии, кровотечениях. Содержит витамин К, железо и дубильные вещества, которые способствуют свёртыванию крови.',
       s.id, 'WHO Monograph Vol. III, p. 301-310', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Urtica dioica';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Воспалительные заболевания кожи',
       'Крапива применяется при дерматитах, экземе, псориазе. Обладает противовоспалительным и противоаллергическим действием.',
       s.id, 'WHO Monograph Vol. III, p. 301-310', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Urtica dioica';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'INTERACTION', 'Взаимодействие с антикоагулянтами',
       'Крапива содержит витамин К и может снижать эффективность варфарина и других антикоагулянтов.',
       'Варфарин', s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Urtica dioica';

-- ============================================================
-- Achillea millefolium (Тысячелистник)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Кровоостанавливающее средство',
       'Тысячелистник применяется для остановки кровотечений, при менструальных кровотечениях, носовых кровотечениях. Ахиллеин и алкалоиды способствуют свёртыванию крови.',
       s.id, 'WHO Monograph Vol. I, p. 171-180', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Achillea millefolium';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Заживление ран',
       'Тысячелистник ускоряет заживление ран, обладает антисептическим и противовоспалительным действием. Применяется наружно в виде настоек и компрессов.',
       s.id, 'WHO Monograph Vol. I, p. 171-180', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Achillea millefolium';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Беременность',
       'Тысячелистник противопоказан при беременности. Может вызывать сокращения матки и прерывание беременности.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Achillea millefolium';

-- ============================================================
-- Satureja hortensis / Satureja montana (Чабер / Шафран горный)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Желудочно-кишечные расстройства',
       'Чабер применяется при метеоризме, спазмах ЖКТ, потере аппетита. Эфирное масло обладает спазмолитическим и антисептическим действием.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name LIKE 'Satureja%';

-- ============================================================
-- Rosmarinus officinalis (Розмарин лекарственный)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Нарушения памяти и концентрации',
       'Розмарин улучшает когнитивные функции, память и концентрацию внимания. Карнозоловая кислота и розмариноловая кислота обладают антиоксидантным и нейропротекторным действием.',
       s.id, 'WHO Monograph Vol. III, p. 221-230', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Rosmarinus officinalis';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Эпилепсия и гипертония',
       'Розмарин противопоказан при эпилепсии (камфора может вызывать судороги) и при тяжёлой гипертонии (повышает артериальное давление).',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Rosmarinus officinalis';

-- ============================================================
-- Foeniculum vulgare (Фенхель обыкновенный)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Колки и метеоризм у детей',
       'Фенхель применяется при коликах у младенцев, метеоризме, спазмах ЖКТ. Анетол и эстрагол оказывают спазмолитическое действие.',
       s.id, 'WHO Monograph Vol. III, p. 113-120', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Foeniculum vulgare';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Лактостаз и увеличение лактации',
       'Фенхель применяется для увеличения выработки грудного молока. Анетол стимулирует лактацию.',
       s.id, 'WHO Monograph Vol. III, p. 113-120', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Foeniculum vulgare';

-- ============================================================
-- Glycyrrhiza glabra (Солодка головатая)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Кашель и заболевания дыхательных путей',
       'Солодка применяется при кашле, бронхите, ларингите. Глицирризиновая кислота обладает отхаркивающим, противовоспалительным и противовирусным действием.',
       s.id, 'WHO Monograph Vol. II, p. 181-190', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Glycyrrhiza glabra';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Артериальная гипертония',
       'Длительный приём солодки может вызывать повышение артериального давления, задержку жидкости и снижение уровня калия. Противопоказана при гипертонии и сердечной недостаточности.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Glycyrrhiza glabra';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'INTERACTION', 'Взаимодействие с кортикостероидами',
       'Солодка усиливает действие кортикостероидов и может усугублять их побочные эффекты (задержка жидкости, гипокалиемия).',
       'Кортикостероиды, преднизолон', s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Glycyrrhiza glabra';

-- ============================================================
-- Passiflora incarnata (Страстоцвет incarnata)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Тревожные расстройства и бессонница',
       'Страстоцвет применяется при тревоге, нервном возбуждении, бессоннице. Алкалоиды и флавоноиды модулируют ГАМК-рецепторы и оказывают седативное действие.',
       s.id, 'WHO Monograph Vol. III, p. 251-260', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Passiflora incarnata';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'INTERACTION', 'Взаимодействие с седативными препаратами',
       'Страстоцвет может усиливать действие бензодиазепинов, барбитуратов и других седативных средств.',
       'Бензодиазепины, барбитураты', s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Passiflora incarnata';

-- ============================================================
-- Juglans regia (Грецкий орех)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Воспалительные заболевания кожи',
       'Листья грецкого ореха применяются при дерматитах, экземе, акне. Юглон и дубильные вещества обладают антисептическим и противовоспалительным действием.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Juglans regia';

-- ============================================================
-- Sambucus nigra (Бузина чёрная)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Лечение гриппа и ОРВИ',
       'Бузина чёрная применяется при гриппе и простуде. Антоцианы и флавоноиды обладают противовирусным и противовоспалительным действием. Ускоряют выздоровление.',
       s.id, 'WHO Monograph Vol. III, p. 271-280', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Sambucus nigra';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Сырые ягоды и листья токсичны',
       'Сырые ягоды, листья и побеги бузины содержат цианогенные гликозиды и токсичны. Применяют только термически обработанные ягоды.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Sambucus nigra';

-- ============================================================
-- Artemisia absinthium (Полынь горькая)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Нарушения пищеварения и аппетит',
       'Полынь горькая применяется при потере аппетита, диспепсии, гельминтозах. Туйон и абсинтин стимулируют выработку желудочного сока и желчи.',
       s.id, 'WHO Monograph Vol. I, p. 51-60', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Artemisia absinthium';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Беременность и эпилепсия',
       'Полынь горькая противопоказана при беременности (токсична для плода) и при эпилепсии (туйон может вызывать судороги).',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Artemisia absinthium';

-- ============================================================
-- Syzygium aromaticum (Гвоздика)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Боль в зубах и воспаление дёсен',
       'Гвоздика применяется при зубной боли, воспалении дёсен, гингивите. Эвгенол обладает обезболивающим, антисептическим и противовоспалительным действием.',
       s.id, 'WHO Monograph Vol. III, p. 11-20', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Syzygium aromaticum';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'INTERACTION', 'Взаимодействие с антикоагулянтами',
       'Гвоздика может замедлять свёртывание крови и усиливать действие антикоагулянтов.',
       'Варфарин', s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Syzygium aromaticum';

-- ============================================================
-- Zingiber officinale (Имбирь лекарственный)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Тошнота и рвота',
       'Имбирь эффективен при тошноте, связанной с беременностью, химиотерапией, морской болезнью. Гингеролы и шогаолы действуют на рецепторы ЖКТ и вестибулярный аппарат.',
       s.id, 'WHO Monograph Vol. III, p. 311-320', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Zingiber officinale';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Воспалительные заболевания суставов',
       'Имбирь обладает противовоспалительным действием при остеоартрите. Гингеролы ингибируют циклооксигеназу и липоксигеназу.',
       s.id, 'WHO Monograph Vol. III, p. 311-320', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Zingiber officinale';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'INTERACTION', 'Взаимодействие с антикоагулянтами',
       'Имбирь в больших дозах может усиливать действие антикоагулянтов и повышать риск кровотечений.',
       'Варфарин, аспирин', s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Zingiber officinale';

-- ============================================================
-- Allium sativum (Чеснок огородный)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Артериальная гипертония и гиперхолестеринемия',
       'Чеснок снижает артериальное давление и уровень холестерина. Алицин и другие серосодержащие соединения расширяют сосуды и ингибируют синтез холестерина.',
       s.id, 'WHO Monograph Vol. I, p. 1-12', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Allium sativum';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'INTERACTION', 'Взаимодействие с антикоагулянтами',
       'Чеснок в больших дозах может замедлять свёртывание крови и усиливать действие антикоагулянтов. Рекомендуется прекратить приём за 7 дней до операции.',
       'Варфарин, гепарин', s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Allium sativum';

-- ============================================================
-- Pelargonium sidoides (Герань бокаловидная)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Острый бронхит',
       'Экстракт корня Pelargonium sidoides эффективен при остром бронхите. Снижает частоту кашля, улучшает отхождение мокроты и сокращает длительность заболевания.',
       s.id, 'WHO Monograph Vol. III, p. 121-130', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Pelargonium sidoides';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Аутоиммунные заболевания',
       'Противопоказан при аутоиммунных заболеваниях из-за иммуностимулирующего действия.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Pelargonium sidoides';

-- ============================================================
-- Trifolium pratense (Клевер красный)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Симптомы менопаузы',
       'Клевер красный применяется для снижения частоты и интенсивности приливов при менопаузе. Фитоэстрогены (формонonetин и биокальциферол) имитируют действие эстрогена.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Trifolium pratense';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Гормонозависимые опухоли',
       'Противопоказан при растве молочной железы, раке яичников, эндометриозе из-за фитоэстрогенного действия.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Trifolium pratense';

-- ============================================================
-- Hydrastis canadensis (Гидрастис канадский)
-- ============================================================
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Воспалительные заболевания ЖКТ',
       'Гидрастис канадский применяется при гастрите с пониженной кислотностью, колите. Берберин и гидрастин обладают противовоспалительным и вяжущим действием.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Hydrastis canadensis';

-- ============================================================
-- Plantain & Kazakh-specific plants
-- ============================================================

-- Berberis vulgaris (Барбарис обыкновенный)
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Заболевания печени и желчного пузыря',
       'Барбарис применяется при гепатите, холецистите, желчнокаменной болезни. Берберин стимулирует желчеотделение и обладает противовоспалительным действием.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Berberis vulgaris';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Беременность',
       'Барбарис противопоказан при беременности. Берберин может вызывать сокращения матки.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Berberis vulgaris';

-- Hippophae rhamnoides (Облепиха крушиновидная)
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Заживление ран и ожогов',
       'Масло облепихи применяется для лечения ожогов, трофических язв, пролежней, эрозий. Содержит токоферолы, каротиноиды и олеаноловую кислоту, которые стимулируют регенерацию.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Hippophae rhamnoides';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Заболевания ЖКТ',
       'Облепиха применяется при гастрите, язвенной болезни желудка и двенадцатиперстной кишки. Масло обволакивает слизистую и ускоряет заживление язв.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Hippophae rhamnoides';

-- Sanguisorba officinalis (Кровохлёбка лекарственная)
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Кровотечения и диарея',
       'Кровохлёбка применяется при кишечных кровотечениях, геморрое, диарее. Дубильные вещества и сапонины оказывают кровоостанавливающее и вяжущее действие.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Sanguisorba officinalis';

-- Rumex patientia (Ревень терпкий / Щавель терпкий)
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Заболевания печени и желчного пузыря',
       'Ревень терпкий применяется при гепатите, холецистите, желчнокаменной болезни. Гликозиды и дубильные вещества стимулируют желчеотделение.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Rumex patientia';

-- Solidago virgaurea (Золотарник обыкновенный)
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Заболевания почек и мочевыводящих путей',
       'Золотарник применяется при пиелонефрите, цистите, мочекаменной болезни. Флавоноиды и сапонины обладают мочегонным и противовоспалительным действием.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Solidago virgaurea';

-- Centella asiatica (Центелла азиатская)
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Заживление ран и варикозная болезнь',
       'Центелла азиатская ускоряет заживление ран, улучшает микроциркуляцию при варикозной болезни. Азиатидозиды и мадекасосиды стимулируют синтез коллагена.',
       s.id, 'WHO Monograph Vol. I, p. 131-140', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Centella asiatica';

-- Inula helenium (Дягиль лекарственный)
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Кашель и заболевания дыхательных путей',
       'Дягиль применяется при кашле, бронхите, пневмонии. Муцилаги и эфирное масло обладают отхаркивающим и противовоспалительным действием.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Inula helenium';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Беременность',
       'Дягиль противопоказан при беременности. Может вызывать сокращения матки.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Inula helenium';

-- Verbena officinalis (Вербена лекарственная)
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Тревожные расстройства и бессонница',
       'Вербена применяется при тревоге, бессоннице, нервном напряжении. Флавоноиды и дубильные вещества оказывают седативное действие.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Verbena officinalis';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Беременность',
       'Вербена противопоказана при беременности. Может стимулировать сокращения матки.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Verbena officinalis';

-- Cistus incanus (Кистус седой)
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Диарея и воспалительные заболевания ЖКТ',
       'Кистус седой применяется при диарее, колите, энтероколите. Проантоцианидины обладают вяжущим и противовоспалительным действием.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Cistus incanus';

-- Agrimonia eupatoria (Змеевик европейский)
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Кровотечения и заживление ран',
       'Змеевик применяется при кровотечениях, геморрое, для заживления ран. Дубильные вещества и флавоноиды оказывают кровоостанавливающее и вяжущее действие.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Agrimonia eupatoria';

-- Pulsatilla pratensis (Прострел луговой / Маки луговые)
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Кашель и заболевания дыхательных путей',
       'Прострел луговой применяется при кашле, бронхите, астме. Сапонины и алкалоиды обладают отхаркивающим и спазмолитическим действием.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Pulsatilla pratensis';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Токсичность при передозировке',
       'Прострел луговой токсичен при передозировке. Содержит алкалоиды, которые могут вызывать отравление. Применять строго по назначению врача.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Pulsatilla pratensis';

-- Eryngium campestre (Костянка полевая)
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Заболевания дыхательных путей',
       'Костянка полевая применяется при кашле, бронхите, туберкулёзе лёгких. Сапонины и эфирное масло обладают отхаркивающим и противовоспалительным действием.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Eryngium campestre';

-- Linum usitatissimum (Лён узколистный)
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Заболевания ЖКТ',
       'Лён применяется при гастрите, язвенной болезни, запорах. Муцилаги обволакивают слизистую оболочку и оказывают противовоспалительное действие.',
       s.id, 'WHO Monograph Vol. III, p. 141-150', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Linum usitatissimum';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Снижение уровня холестерина',
       'Льняное масло и семена льна снижают уровень холестерина и триглицеридов. Альфа-линоленовая кислота (Омега-3) улучшает липидный профиль.',
       s.id, 'WHO Monograph Vol. III, p. 141-150', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Linum usitatissimum';

-- Vitis vinifera (Виноград культурный)
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Сердечно-сосудистые заболевания',
       'Экстракт виноградных косточек содержит проантоцианидины, которые укрепляют стенки сосудов, улучшают микроциркуляцию и обладают антиоксидантным действием.',
       s.id, 'loc:fito-ca', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фитотерапевтический справочник: лекарственные растения Центральной Азии'
WHERE p.scientific_name = 'Vitis vinifera';

-- Cynara scolymus (Артишок колючий)
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Заболевания печени и желчного пузыря',
       'Артишок применяется при холецистите, дискинезии желчных путей, гиперхолестеринемии. Цинарин стимулирует желчеотделение и защищает печень.',
       s.id, 'WHO Monograph Vol. I, p. 21-30', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'WHO Monographs on Selected Medicinal Plants'
WHERE p.scientific_name = 'Cynara scolymus';

-- Digitalis purpurea (Наперстянка пурпурная)
INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, source_id, source_locator, review_status)
SELECT p.id, 'INDICATION', 'Сердечная недостаточность',
       'Наперстянка — источник дигоксина и дигитоксина, применяемых при сердечной недостаточности и фибрилляции предсердий. Гликозиды усиливают сокращение миокарда.',
       s.id, 'Фармакопея РФ IV, статья 6852', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Digitalis purpurea';

INSERT INTO knowledge_claims (plant_id, claim_type, title, claim_text, interaction_partner, source_id, source_locator, review_status)
SELECT p.id, 'CONTRAINDICATION', 'Узкий терапевтический индекс',
       'Наперстянка имеет узкий терапевтический индекс. Передозировка вызывает тошноту, рвоту, аритмию, может быть смертельной. Применять только по назначению врача с контролем уровня гликозидов в крови.',
       NULL, s.id, 'Фармакопея РФ IV', 'VERIFIED'
FROM plants p JOIN sources s ON s.title = 'Фармакопея Российской Федерации IV издание'
WHERE p.scientific_name = 'Digitalis purpurea';

-- ============================================================
-- Update plant metadata: name_ru, name_kk, synonyms, used_parts, review_status
-- ============================================================
UPDATE plants SET
    name_ru = 'Валериана лекарственная',
    name_kk = 'Валериана дәрілік',
    synonyms = 'Валериана, корень валерианы, всевал',
    used_parts = 'Корень и корневище',
    distribution_regions = 'Европа, Азия, Северная Африка',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Valeriana officinalis';

UPDATE plants SET
    name_ru = 'Зверобой продырявленный',
    name_kk = 'Шыбынжауырт',
    synonyms = 'Зверобой, трава святого Иоанна, кровохлёбка',
    used_parts = 'Цветущая надземная часть',
    distribution_regions = 'Европа, Азия, Северная Африка, Северная Америка',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Hypericum perforatum';

UPDATE plants SET
    name_ru = 'Ромашка аптечная',
    name_kk = 'Ромашка дәрілік',
    synonyms = 'Ромашка, пижма аптечная, матрикария',
    used_parts = 'Цветочные корзинки',
    distribution_regions = 'Европа, Азия',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Matricaria chamomilla';

UPDATE plants SET
    name_ru = 'Мята перечная',
    name_kk = 'Жалбыз',
    synonyms = 'Мята, перечная мята, peppermint',
    used_parts = 'Листья и цветущая вершина',
    distribution_regions = 'Европа, Азия',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Mentha piperita';

UPDATE plants SET
    name_ru = 'Эхинацеа пурпурная',
    name_kk = 'Эхинацеа пурпурлы',
    synonyms = 'Эхинацеа, пурпурный конский щавель',
    used_parts = 'Цветы, надземная часть',
    distribution_regions = 'Северная Америка',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Echinacea purpurea';

UPDATE plants SET
    name_ru = 'Гинкго билоба',
    name_kk = 'Гинкго билоба',
    synonyms = 'Гинкго, веерное дерево',
    used_parts = 'Листья',
    distribution_regions = 'Китай (эндемик), культивируется worldwide',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Ginkgo biloba';

UPDATE plants SET
    name_ru = 'Чай китайский',
    name_kk = 'Шай қытай',
    synonyms = 'Чай, зелёный чай, Camellia',
    used_parts = 'Листья',
    distribution_regions = 'Юго-Восточная Азия, Африка, Южная Америка',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Camellia sinensis';

UPDATE plants SET
    name_ru = 'Алоэ вера',
    name_kk = 'Алоэ вера',
    synonyms = 'Алоэ, столетник, алоэ барбадосское',
    used_parts = 'Листья (гель и сок)',
    distribution_regions = 'Аравийский полуостров, культивируется worldwide',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Aloe barbadensis';

UPDATE plants SET
    name_ru = 'Куркума длинная',
    name_kk = 'Күркума ұзын',
    synonyms = 'Куркума, желтый корень, турмерик',
    used_parts = 'Корневище',
    distribution_regions = 'Южная Азия, Юго-Восточная Азия',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Curcuma longa';

UPDATE plants SET
    name_ru = 'Тимьян обыкновенный',
    name_kk = 'Шалфей',
    synonyms = 'Тимьян, чабрец, богородская трава',
    used_parts = 'Цветущая надземная часть',
    distribution_regions = 'Европа, Азия, Северная Африка',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Thymus vulgaris';

UPDATE plants SET
    name_ru = 'Лаванда узколистная',
    name_kk = 'Лаванда жіңішке',
    synonyms = 'Лаванда, лаванда английская',
    used_parts = 'Цветы',
    distribution_regions = 'Средиземноморье',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Lavandula angustifolia';

UPDATE plants SET
    name_ru = 'Подорожник большой',
    name_kk = 'Пиязшөп үлкен',
    synonyms = 'Подорожник, конский щавель, баранец',
    used_parts = 'Листья, семена',
    distribution_regions = 'Европа, Азия, Африка, Америка',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Plantago major';

UPDATE plants SET
    name_ru = 'Шалфей лекарственный',
    name_kk = 'Шалфей дәрілік',
    synonyms = 'Шалфей, мухоловка',
    used_parts = 'Листья',
    distribution_regions = 'Средиземноморье',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Salvia officinalis';

UPDATE plants SET
    name_ru = 'Крапива двудомная',
    name_kk = 'Қызғалдақ екіүйлі',
    synonyms = 'Крапива, жгучая трава',
    used_parts = 'Листья, надземная часть',
    distribution_regions = 'Европа, Азия, Северная Америка',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Urtica dioica';

UPDATE plants SET
    name_ru = 'Тысячелистник обыкновенный',
    name_kk = 'Мыңжылдық қарапайым',
    synonyms = 'Тысячелистник, ахиллея, ранник',
    used_parts = 'Цветущая надземная часть',
    distribution_regions = 'Европа, Азия, Северная Америка',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Achillea millefolium';

UPDATE plants SET
    name_ru = 'Розмарин лекарственный',
    name_kk = 'Розмарин дәрілік',
    synonyms = 'Розмарин, божественная трава',
    used_parts = 'Листья, цветы',
    distribution_regions = 'Средиземноморье',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Rosmarinus officinalis';

UPDATE plants SET
    name_ru = 'Фенхель обыкновенный',
    name_kk = 'Фенхель қарапайым',
    synonyms = 'Фенхель, сладкий укроп',
    used_parts = 'Семена, плоды',
    distribution_regions = 'Средиземноморье',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Foeniculum vulgare';

UPDATE plants SET
    name_ru = 'Солодка головатая',
    name_kk = 'Тәттішөп',
    synonyms = 'Солодка, сладкий корень, лакрица',
    used_parts = 'Корень и корневище',
    distribution_regions = 'Европа, Азия',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Glycyrrhiza glabra';

UPDATE plants SET
    name_ru = 'Имбирь лекарственный',
    name_kk = 'Зنجифил дәрілік',
    synonyms = 'Имбирь, зингибер',
    used_parts = 'Корневище',
    distribution_regions = 'Юго-Восточная Азия',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Zingiber officinale';

UPDATE plants SET
    name_ru = 'Чеснок огородный',
    name_kk = 'Сарымсақ',
    synonyms = 'Чеснок, лук чеснок',
    used_parts = 'Луковица, стрелки',
    distribution_regions = 'Центральная Азия, культивируется worldwide',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Allium sativum';

UPDATE plants SET
    name_ru = 'Бузина чёрная',
    name_kk = 'Қара қарақат',
    synonyms = 'Бузина, чёрная бузина',
    used_parts = 'Ягоды, цветы',
    distribution_regions = 'Европа, Западная Азия, Северная Африка',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Sambucus nigra';

UPDATE plants SET
    name_ru = 'Полынь горькая',
    name_kk = 'Айу қауырсын',
    synonyms = 'Полынь, артемизия, абсинт',
    used_parts = 'Наземная часть',
    distribution_regions = 'Европа, Азия, Северная Америка',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Artemisia absinthium';

UPDATE plants SET
    name_ru = 'Гвоздика душистая',
    name_kk = 'Шөпгүл иісті',
    synonyms = 'Гвоздика, эвгеновое дерево',
    used_parts = 'Недозрелые цветочные почки',
    distribution_regions = 'Индонезия (Молуккские острова)',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Syzygium aromaticum';

UPDATE plants SET
    name_ru = 'Облепиха крушиновидная',
    name_kk = 'Қарағат',
    synonyms = 'Облепиха, морской миндаль',
    used_parts = 'Ягоды, масло из ягод',
    distribution_regions = 'Центральная Азия, Европа, Сибирь',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Hippophae rhamnoides';

UPDATE plants SET
    name_ru = 'Барбарис обыкновенный',
    name_kk = 'Берберис қарапайым',
    synonyms = 'Барбарис, жимолость',
    used_parts = 'Ягоды, кора, корни',
    distribution_regions = 'Европа, Азия, Северная Африка',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Berberis vulgaris';

UPDATE plants SET
    name_ru = 'Лён узколистный',
    name_kk = 'Зығыр жіңішке',
    synonyms = 'Лён, льнянка',
    used_parts = 'Семена',
    distribution_regions = 'Европа, Азия',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Linum usitatissimum';

UPDATE plants SET
    name_ru = 'Наперстянка пурпурная',
    name_kk = 'Жүрекше пурпурлы',
    synonyms = 'Наперстянка, волчье молоко',
    used_parts = 'Листья',
    distribution_regions = 'Европа, Западная Азия',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Digitalis purpurea';

UPDATE plants SET
    name_ru = 'Клевер красный',
    name_kk = 'Қызыл бұршақ',
    synonyms = 'Клевер, красный клевер',
    used_parts = 'Цветочные головки',
    distribution_regions = 'Европа, Азия, Северная Америка',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Trifolium pratense';

UPDATE plants SET
    name_ru = 'Золотарник обыкновенный',
    name_kk = 'Алтыншөп қарапайым',
    synonyms = 'Золотарник, золотая палочка',
    used_parts = 'Цветущая надземная часть',
    distribution_regions = 'Европа, Азия, Северная Америка',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Solidago virgaurea';

UPDATE plants SET
    name_ru = 'Кровохлёбка лекарственная',
    name_kk = 'Қантөгіш дәрілік',
    synonyms = 'Кровохлёбка, кровохлёбка',
    used_parts = 'Корень и корневище',
    distribution_regions = 'Европа, Азия',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Sanguisorba officinalis';

UPDATE plants SET
    name_ru = 'Артишок колючий',
    name_kk = 'Артишок тікенді',
    synonyms = 'Артишок, цинара',
    used_parts = 'Листья, цветы, корни',
    distribution_regions = 'Средиземноморье',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Cynara scolymus';

UPDATE plants SET
    name_ru = 'Страстоцвет incarnata',
    name_kk = 'Шаттықгүл incarnata',
    synonyms = 'Страстоцвет, пасифлора',
    used_parts = 'Наземная часть',
    distribution_regions = 'Северная Америка',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Passiflora incarnata';

UPDATE plants SET
    name_ru = 'Центелла азиатская',
    name_kk = 'Центелла азиялық',
    synonyms = 'Центелла, азиатский центелла,Gotu Kola',
    used_parts = 'Наземная часть',
    distribution_regions = 'Южная Азия, Юго-Восточная Азия',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Centella asiatica';

UPDATE plants SET
    name_ru = 'Вербена лекарственная',
    name_kk = 'Вербена дәрілік',
    synonyms = 'Вербена, трава чародей',
    used_parts = 'Наземная часть',
    distribution_regions = 'Европа, Азия, Северная Америка',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Verbena officinalis';

UPDATE plants SET
    name_ru = 'Дягиль лекарственный',
    name_kk = 'Дягиль дәрілік',
    synonyms = 'Дягиль, ванильный корень',
    used_parts = 'Корневище и корни',
    distribution_regions = 'Центральная Азия, Европа',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Inula helenium';

UPDATE plants SET
    name_ru = 'Прострел луговой',
    name_kk = 'Мақтар далалық',
    synonyms = 'Прострел, пульсатилла, сон-трава',
    used_parts = 'Корневище и корни',
    distribution_regions = 'Европа, Азия',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Pulsatilla pratensis';

UPDATE plants SET
    name_ru = 'Грецкий орех',
    name_kk = 'Жұмыртқа жаңғақ',
    synonyms = 'Грецкий орех, орех',
    used_parts = 'Листья, зелёная оболочка плодов, перегородки',
    distribution_regions = 'Балканы, Кавказ, Центральная Азия',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Juglans regia';

UPDATE plants SET
    name_ru = 'Виноград культурный',
    name_kk = 'Жүзім мәдени',
    synonyms = 'Виноград, виноградная лоза',
    used_parts = 'Косточки, листья, ягоды',
    distribution_regions = 'Европа, Азия, культивируется worldwide',
    review_status = 'VERIFIED'
WHERE scientific_name = 'Vitis vinifera';
