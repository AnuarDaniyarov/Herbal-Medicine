package kz.iitu.medicineplatform.search;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import kz.iitu.medicineplatform.knowledge.*;
import kz.iitu.medicineplatform.plant.Plant;
import kz.iitu.medicineplatform.plant.PlantRepository;
import kz.iitu.medicineplatform.preparation.Preparation;
import kz.iitu.medicineplatform.preparation.PreparationRepository;
import kz.iitu.medicineplatform.source.DataSource;
import kz.iitu.medicineplatform.source.DataSourceRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Benchmark test suite that evaluates search quality on 25 golden standard medical queries.
 * Computes:
 * 1. Precision@5: Fraction of top-5 retrieved items that are relevant.
 * 2. Mean Reciprocal Rank (MRR): Position of the first relevant result (1/rank).
 * 3. Intent Detection Accuracy: Correctness of query intent classification.
 *
 * Designed to provide reproducible, empirical evaluation results for the diploma thesis.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SearchQualityBenchmarkTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private PlantRepository plants;
    @Autowired private KnowledgeClaimRepository claims;
    @Autowired private DataSourceRepository sources;
    @Autowired private PreparationRepository preparations;
    @Autowired private kz.iitu.medicineplatform.auth.UserAccountRepository users;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private ObjectMapper mapper;

    private static boolean dataSeeded = false;

    @Test
    @DisplayName("Preparation search returns its source without lazy-loading failure")
    void preparationSearchLoadsSource() throws Exception {
        DataSource source = sources.findAll().stream().findFirst().orElseGet(() ->
                sources.save(new DataSource("Search regression source", "Test", 2025, null,
                        "https://example.test", "MONOGRAPH", null, "Test")));
        Preparation preparation = preparations.save(new Preparation("Valeriana Extract Regression", "Capsules",
                null, "SEARCH-LAZY-TEST", "KZ", null, source));
        jdbcTemplate.update("UPDATE preparations SET review_status = 'VERIFIED' WHERE id = ?", preparation.getId());

        MvcResult response = mockMvc.perform(get("/api/search")
                        .param("q", "Valeriana Extract Regression")
                        .param("type", "preparation"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode results = mapper.readTree(response.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .path("results");
        assertTrue(results.size() > 0, "Expected the verified preparation in search results");
        assertEquals(source.getTitle(), results.get(0).path("sourceName").asText());
    }

    @BeforeEach
    void setUpTestData() {
        if (dataSeeded) return;

        DataSource source = sources.findAll().stream().findFirst().orElseGet(() ->
                sources.save(new DataSource("WHO Monographs on Selected Medicinal Plants", "WHO", 2009, null,
                        "https://who.int", "MONOGRAPH", null, "Public domain")));

        Plant valeriana = savePlantIfNotExists("Valeriana officinalis", "Валериана лекарственная", "Caprifoliaceae",
                "Валериана лекарственная", "Валериана дәрілік", "Кошачья трава", "Корневища с корнями");
        Plant hypericum = savePlantIfNotExists("Hypericum perforatum", "Зверобой продырявленный", "Hypericaceae",
                "Зверобой продырявленный", "Шыбынжауырт", "Зверобой обыкновенный", "Трава");
        Plant matricaria = savePlantIfNotExists("Matricaria chamomilla", "Ромашка аптечная", "Asteraceae",
                "Ромашка аптечная", "Дәрілік түймедақ", "Ромашка лекарственная", "Цветки");
        Plant mentha = savePlantIfNotExists("Mentha piperita", "Мята перечная", "Lamiaceae",
                "Мята перечная", "Бұрыш жалбыз", "Холодная мята", "Листья");
        Plant glycyrrhiza = savePlantIfNotExists("Glycyrrhiza glabra", "Солодка голая", "Fabaceae",
                "Солодка голая", "Жатаған мия", "Лакричник", "Корни");
        Plant thymus = savePlantIfNotExists("Thymus vulgaris", "Тимьян обыкновенный", "Lamiaceae",
                "Чабрец ползучий", "Көкемарал", "Богородская трава", "Трава");
        Plant ginkgo = savePlantIfNotExists("Ginkgo biloba", "Гинкго билоба", "Ginkgoaceae",
                "Гинкго билоба", "Қос қалақты гинкго", "Храмовое дерево", "Листья");
        Plant crataegus = savePlantIfNotExists("Crataegus oxyacantha", "Боярышник колючий", "Rosaceae",
                "Боярышник", "Долана", "Боярка", "Плоды");
        Plant silybum = savePlantIfNotExists("Silybum marianum", "Расторопша пятнистая", "Asteraceae",
                "Расторопша", "Шұбар алатікен", "Марьин татарник", "Семена");
        Plant urtica = savePlantIfNotExists("Urtica dioica", "Крапива двудомная", "Urticaceae",
                "Крапива двудомная", "Қалақай", "Жгучка", "Листья");

        saveClaimIfNotExists(valeriana, ClaimType.INDICATION, "Бессонница и нарушения сна", "Седативное средство при расстройствах засыпания и тревоге.", source, null);
        saveClaimIfNotExists(valeriana, ClaimType.INTERACTION, "Потенцирование действия седативных средств", "Усиливает действие барбитуратов и снотворных.", source, "Барбитураты, снотворные");
        saveClaimIfNotExists(valeriana, ClaimType.CONTRAINDICATION, "Беременность и лактация", "Противопоказано при беременности из-за недостатка данных о безопасности.", source, null);

        saveClaimIfNotExists(hypericum, ClaimType.INDICATION, "Депрессия легкой и умеренной степени", "Применяется при снижении настроения, тревожности и депрессивных состояниях.", source, null);
        saveClaimIfNotExists(hypericum, ClaimType.CONTRAINDICATION, "Фотосенсибилизация", "Повышает чувствительность к солнечному свету, нельзя посещать солярий.", source, null);
        saveClaimIfNotExists(hypericum, ClaimType.INTERACTION, "Взаимодействие с антидепрессантами", "Опасность серотонинового синдрома при совместном приеме с СИОЗС.", source, "Антидепрессанты СИОЗС, трициклики");
        saveClaimIfNotExists(hypericum, ClaimType.INTERACTION, "Снижение эффекта оральных контрацептивов", "Индуктор CYP3A4, ослабляет действие контрацептивов.", source, "Оральные контрацептивы");

        saveClaimIfNotExists(matricaria, ClaimType.INDICATION, "Спазмы желудка и гастрит", "Снимает спазмы ЖКТ, противовоспалительное действие на слизистую желудка.", source, null);
        saveClaimIfNotExists(matricaria, ClaimType.INDICATION, "Воспалительные заболевания ротоглотки", "Полоскания при стоматите, тонзиллите и гингивите.", source, null);

        saveClaimIfNotExists(thymus, ClaimType.INDICATION, "Кашель и бронхит", "Отхаркивающее и бактерицидное действие тимьяна при воспалении легких и кашле.", source, null);
        saveClaimIfNotExists(glycyrrhiza, ClaimType.INDICATION, "Кашель с трудноотделяемой мокротой", "Глицирризин стимулирует выведение слизи и секрета при бронхитах.", source, null);
        saveClaimIfNotExists(glycyrrhiza, ClaimType.CONTRAINDICATION, "Артериальная гипертензия", "Противопоказано при высоком давлении из-за задержки натрия и потери калия.", source, null);

        saveClaimIfNotExists(ginkgo, ClaimType.INDICATION, "Ухудшение памяти и мозгового кровообращения", "Улучшает реологические свойства крови и память у пожилых.", source, null);
        saveClaimIfNotExists(ginkgo, ClaimType.INTERACTION, "Кровотечения с антикоагулянтами", "Повышает риск кровотечений при приеме аспирина и варфарина.", source, "Варфарин, аспирин");

        saveClaimIfNotExists(silybum, ClaimType.INDICATION, "Токсические поражения печени и гепатоз", "Гепатопротекторное действие силимарина для регенерации печеночных клеток.", source, null);
        saveClaimIfNotExists(crataegus, ClaimType.INDICATION, "Функциональные расстройства сердца и аритмия", "Усиливает коронарное кровообращение, снижает тахикардию.", source, null);
        saveClaimIfNotExists(urtica, ClaimType.INDICATION, "Кровотечения различного генеза", "Гемостатическое действие витамина К при носовых и маточных кровотечениях.", source, null);
        saveClaimIfNotExists(urtica, ClaimType.CONTRAINDICATION, "Тромбофлебит и варикозное расширение вен", "Противопоказано при гиперкоагуляции и тромбозах.", source, null);

        dataSeeded = true;
    }

    private record BenchmarkQuery(
            String queryText,
            String expectedIntent,
            List<String> relevantKeywords
    ) {}

    @Test
    @DisplayName("Run 25-query Search Quality Benchmark and Calculate Precision@5 & MRR")
    void testSearchQualityBenchmark() throws Exception {
        List<BenchmarkQuery> benchmarkSet = List.of(
                new BenchmarkQuery("растения от бессонницы", "INDICATION", List.of("Валериана", "бессонниц", "сна", "Седативное")),
                new BenchmarkQuery("что помогает для сна", "INDICATION", List.of("Валериана", "сна", "бессонниц")),
                new BenchmarkQuery("травы от кашля", "INDICATION", List.of("Тимьян", "Чабрец", "Солодка", "бронхит", "мокрот")),
                new BenchmarkQuery("отхаркивающее средство при бронхите", "INDICATION", List.of("Солодка", "Тимьян", "Чабрец", "бронхит")),
                new BenchmarkQuery("зверобой противопоказания", "CONTRAINDICATION", List.of("Зверобой", "Фотосенсибилизация", "солнц")),
                new BenchmarkQuery("валериана при беременности", "CONTRAINDICATION", List.of("Валериана", "Беременность")),
                new BenchmarkQuery("солодка высокое давление", "CONTRAINDICATION", List.of("Солодка", "гипертензи", "давлени")),
                new BenchmarkQuery("крапива тромбофлебит", "CONTRAINDICATION", List.of("Крапива", "Тромбофлебит", "варикоз")),
                new BenchmarkQuery("можно ли зверобой с антидепрессантами", "INTERACTION", List.of("Зверобой", "антидепрессант", "серотонин")),
                new BenchmarkQuery("валериана с алкоголем и барбитуратами", "INTERACTION", List.of("Валериана", "барбитурат", "снотворн")),
                new BenchmarkQuery("гинкго билоба и аспирин совместимость", "INTERACTION", List.of("Гинкго", "аспирин", "варфарин", "кровотечен")),
                new BenchmarkQuery("ромашка при спазмах желудка", "INDICATION", List.of("Ромашка", "спазм", "гастрит", "ЖКТ")),
                new BenchmarkQuery("воспаление горла полоскание", "INDICATION", List.of("Ромашка", "ротоглотк", "стоматит")),
                new BenchmarkQuery("расторопша для печени", "INDICATION", List.of("Расторопша", "печен", "гепатоз")),
                new BenchmarkQuery("боярышник для сердца", "INDICATION", List.of("Боярышник", "сердц", "аритми", "тахикарди")),
                new BenchmarkQuery("улучшение памяти гинкго", "INDICATION", List.of("Гинкго", "памят", "мозгов")),
                new BenchmarkQuery("остановка кровотечения крапива", "INDICATION", List.of("Крапива", "кровотечен", "гемостаз")),
                new BenchmarkQuery("мята перечная тошнота", "INDICATION", List.of("Мята", "тошнот", "спазм")),
                new BenchmarkQuery("валериана лекарственная", "GENERAL", List.of("Валериана")),
                new BenchmarkQuery("Hypericum perforatum", "GENERAL", List.of("Зверобой", "Hypericum")),
                new BenchmarkQuery("Matricaria chamomilla", "GENERAL", List.of("Ромашка", "Matricaria")),
                new BenchmarkQuery("ұйқысыздық шөптер", "INDICATION", List.of("Валериана", "ұйқы", "сна")),
                new BenchmarkQuery("жөтелге қарсы дәрілік өсімдіктер", "INDICATION", List.of("Тимьян", "Солодка", "жөтел", "кашл")),
                new BenchmarkQuery("бауырға арналған шөптер", "INDICATION", List.of("Расторопша", "печен", "бауыр")),
                new BenchmarkQuery("зверобой оральные контрацептивы", "INTERACTION", List.of("Зверобой", "контрацептив", "CYP3A4"))
        );

        double totalPrecisionAt5 = 0.0;
        double totalReciprocalRank = 0.0;
        int correctIntentCount = 0;
        int totalQueries = benchmarkSet.size();

        System.out.println("=========================================================================================");
        System.out.println("                   РЕЗУЛЬТАТЫ БЕНЧМАРКА КАЧЕСТВА ПОИСКА (25 ТЕСТОВЫХ ЗАПРОСОВ)           ");
        System.out.println("=========================================================================================");
        System.out.printf("%-35s | %-12s | %-10s | %-8s | %-6s\n", "Запрос", "Интент", "Интент OK", "P@5", "RR");
        System.out.println("-----------------------------------------------------------------------------------------");

        for (BenchmarkQuery bq : benchmarkSet) {
            MvcResult result = mockMvc.perform(get("/api/search").param("q", bq.queryText()))
                    .andExpect(status().isOk())
                    .andReturn();

            JsonNode root = mapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
            String detectedIntent = root.path("detectedIntent").asText("GENERAL");
            JsonNode resultsArray = root.path("results");

            boolean intentMatch = bq.expectedIntent().equalsIgnoreCase(detectedIntent);
            if (intentMatch) correctIntentCount++;

            int relevantInTop5 = 0;
            int firstRelevantRank = 0;

            for (int i = 0; i < resultsArray.size(); i++) {
                JsonNode item = resultsArray.get(i);
                String title = item.path("title").asText("");
                String subtitle = item.path("subtitle").asText("");
                String description = item.path("description").asText("");
                String fullText = (title + " " + subtitle + " " + description).toLowerCase();

                boolean isRelevant = bq.relevantKeywords().stream()
                        .anyMatch(kw -> fullText.contains(kw.toLowerCase()));

                if (isRelevant) {
                    if (firstRelevantRank == 0) {
                        firstRelevantRank = i + 1;
                    }
                    if (i < 5) {
                        relevantInTop5++;
                    }
                }
            }

            double pAt5 = (double) relevantInTop5 / 5.0;
            double rr = firstRelevantRank > 0 ? 1.0 / firstRelevantRank : 0.0;

            totalPrecisionAt5 += pAt5;
            totalReciprocalRank += rr;

            System.out.printf("%-35s | %-12s | %-10s | %-8.2f | %-6.2f\n",
                    bq.queryText().length() > 35 ? bq.queryText().substring(0, 32) + "..." : bq.queryText(),
                    detectedIntent,
                    intentMatch ? "✓ ДА" : "✗ НЕТ",
                    pAt5,
                    rr);
        }

        double meanPrecisionAt5 = totalPrecisionAt5 / totalQueries;
        double meanReciprocalRank = totalReciprocalRank / totalQueries;
        double intentAccuracy = (double) correctIntentCount / totalQueries * 100.0;

        System.out.println("=========================================================================================");
        System.out.printf("ИТОГОВЫЕ МЕТРИКИ КАЧЕСТВА ДЛЯ ДИПЛОМНОЙ РАБОТЫ:\n");
        System.out.printf("• Средняя точность в топ-5 (Mean Precision@5): %.2f (%.1f%%)\n", meanPrecisionAt5, meanPrecisionAt5 * 100.0);
        System.out.printf("• Средний обратный ранг (Mean Reciprocal Rank - MRR): %.3f\n", meanReciprocalRank);
        System.out.printf("• Точность классификации намерения (Intent Accuracy): %.1f%%\n", intentAccuracy);
        System.out.println("=========================================================================================");

        assertTrue(meanPrecisionAt5 >= 0.30, "Precision@5 should be at least 30% for the seeded benchmark set");
        assertTrue(meanReciprocalRank >= 0.70, "MRR should be at least 0.70 indicating first result is in top 1-2");
        assertTrue(intentAccuracy >= 80.0, "Intent detection accuracy should exceed 80%");
    }

    private Plant savePlantIfNotExists(String scientific, String display, String family, String ru, String kk, String syn, String parts) {
        return plants.findAll().stream()
                .filter(p -> p.getScientificName().equalsIgnoreCase(scientific))
                .findFirst()
                .orElseGet(() -> {
                    Plant p = new Plant(scientific, display, family, display, ru, kk, syn, "Центральная Азия", parts, "VERIFIED");
                    return plants.save(p);
                });
    }

    private void saveClaimIfNotExists(Plant plant, ClaimType type, String title, String text, DataSource source, String interactionPartner) {
        boolean exists = claims.findAll().stream()
                .anyMatch(c -> c.getPlant() != null && c.getPlant().getId().equals(plant.getId()) && c.getTitle().equalsIgnoreCase(title));
        if (!exists) {
            kz.iitu.medicineplatform.auth.UserAccount reviewer = users.findAll().stream().findFirst().orElseGet(() ->
                    users.save(new kz.iitu.medicineplatform.auth.UserAccount("benchmark.expert@example.test", "Benchmark Expert",
                            "hash", kz.iitu.medicineplatform.auth.UserRole.EXPERT)));
            KnowledgeClaim kc = new KnowledgeClaim(plant, null, type, title, text, interactionPartner, source, "WHO Monograph / Ph.Eur.", null);
            kc.review(reviewer, ReviewStatus.VERIFIED, "Автоматическая валидация эталонного набора");
            claims.save(kc);
        }
    }
}
