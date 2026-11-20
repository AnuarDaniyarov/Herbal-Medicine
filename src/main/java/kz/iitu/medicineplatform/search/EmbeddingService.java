package kz.iitu.medicineplatform.search;

import com.pgvector.PGvector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Generates fixed-length vector embeddings from text using a semantic-aware projection.
 * Combines token hashing with a domain-specific medical and multilingual concept ontology.
 * This bridges natural language symptom queries (e.g., "от кашля", "для сна", "бессонница")
 * with pharmacopeial clinical descriptions (e.g., "отхаркивающее", "седативное").
 *
 * Produces 384-dimensional unit-normalized vectors suitable for pgvector cosine similarity search.
 */
@Service
public class EmbeddingService {

    public static final int DIMENSIONS = 384;
    private static final String LOCAL_MODEL_ID = "local-ontology-hash-v1";
    private static final int[] PRIMES = {31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101};
    private static final long FNV_OFFSET = 0xCBF29CE484222325L;
    private static final long FNV_PRIME = 0x100000001B3L;

    private final String embeddingUrl;
    private final String externalModelId;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public EmbeddingService(@Value("${search.embedding.url:}") String embeddingUrl,
                            @Value("${search.embedding.model-id:intfloat/multilingual-e5-small}") String externalModelId,
                            ObjectMapper objectMapper) {
        this.embeddingUrl = embeddingUrl == null ? "" : embeddingUrl.trim();
        this.externalModelId = externalModelId;
        this.objectMapper = objectMapper;
        if (!this.embeddingUrl.isEmpty()) {
            URI uri = URI.create(this.embeddingUrl);
            if (!Set.of("http", "https").contains(uri.getScheme()) || uri.getHost() == null) {
                throw new IllegalArgumentException("search.embedding.url must be an absolute HTTP(S) URL");
            }
        }
    }

    /** Medical semantic concept clusters mapping query/text stems to canonical concept keys */
    private static final Map<String, String> CONCEPT_CLUSTERS = new HashMap<>();

    static {
        // Respiratory / Cough / Cold
        addCluster("__concept_respiratory_cough__",
                "кашель", "кашля", "кашле", "кашлем", "отхаркивающее", "отхаркивающий", "бронхит", "бронхита",
                "бронхолитическое", "простуда", "простуды", "трахеит", "респираторный", "легкие", "легких",
                "жөтел", "қақырық", "тыныс", "cough", "expectorant", "bronchitis", "respiratory");

        // Insomnia / Sleep / Sedative
        addCluster("__concept_insomnia_sedative__",
                "бессонница", "бессонницы", "бессоннице", "сна", "сон", "засыпание", "успокоительное", "успокаивающее",
                "седативное", "седативный", "снотворное", "нервное", "нервозность", "возбудимость",
                "ұйқысыздық", "тыныштандыратын", "ұйқы", "insomnia", "sleeplessness", "sedative", "sleep", "calming");

        // Anxiety / Depression / Mood
        addCluster("__concept_anxiety_depression__",
                "депрессия", "депрессии", "депрессивный", "тревожность", "тревоги", "тревога", "страх",
                "антидепрессант", "стресс", "стресса", "настроение", "нейромедиатор",
                "мазасыздық", "күйзеліс", "depression", "anxiety", "antidepressant", "stress");

        // Gastrointestinal / Spasm / Digestion
        addCluster("__concept_gastrointestinal__",
                "жкт", "желудок", "желудка", "кишечник", "кишечника", "гастрит", "гастрита", "язва", "язвы",
                "спазм", "спазмы", "спазмолитическое", "пищеварение", "метеоризм", "колики", "ветрогонное",
                "асқазан", "ішек", "түйілу", "spasm", "spasmolytic", "gastric", "gastrointestinal", "digestion");

        // Cardiovascular / Blood Pressure
        addCluster("__concept_cardiovascular_pressure__",
                "давление", "давления", "гипертония", "гипотензивное", "аритмия", "сердце", "сердца",
                "сосуды", "сосудов", "сосудорасширяющее", "стенокардия", "кардио",
                "қан", "қысым", "жүрек", "hypertension", "hypotensive", "cardiac", "cardiovascular", "pressure");

        // Inflammation / Wound Healing / Skin
        addCluster("__concept_inflammation__",
                "воспаление", "воспаления", "противовоспалительное", "рана", "раны", "ожог", "ожоги",
                "ранозаживляющее", "антисептическое", "антисептик", "дерматит", "кожа", "кожи",
                "қабыну", "жара", "тері", "inflammation", "antiinflammatory", "wound", "antiseptic", "skin");

        // Liver / Bile / Hepatoprotective
        addCluster("__concept_liver_bile__",
                "печень", "печени", "гепатопротекторное", "желчегонное", "желчь", "холецистит", "гепатит",
                "бауыр", "өт", "liver", "hepatoprotective", "choleretic", "bile");

        // Immunity / Antiviral / Tonic
        addCluster("__concept_immune_tonic__",
                "иммунитет", "иммунитета", "иммуномодулирующее", "противовирусное", "тонизирующее", "адаптоген",
                "сопротивляемость", "инфекция", "вирус",
                "иммундық", "вирусқа", "immune", "immunomodulatory", "antiviral", "adaptogen", "tonic");

        // Contraindications / Pregnancy / Children
        addCluster("__concept_contraindication__",
                "противопоказание", "противопоказания", "противопоказано", "нельзя", "запрещено", "беременность",
                "беременным", "лактация", "кормление", "дети", "детям", "аллергия",
                "жүктілік", "қарсы", "contraindication", "pregnancy", "nursing", "children");

        // Interactions / Adverse effects
        addCluster("__concept_interaction__",
                "взаимодействие", "взаимодействия", "несовместимо", "совместимость", "совместный",
                "усиление", "ослабление", "антагонизм", "синергизм", "барбитураты", "антидепрессанты",
                "өзара", "әрекеттесу", "interaction", "incompatible", "contraindicated");
    }

    private static void addCluster(String clusterKey, String... stems) {
        for (String stem : stems) {
            CONCEPT_CLUSTERS.put(stem.toLowerCase().trim(), clusterKey);
        }
    }

    /**
     * Generates a 384-dimensional embedding vector from the given text.
     * Incorporates both literal token hashing and semantic concept projections.
     */
    public float[] embed(String text) {
        return embedQuery(text);
    }

    public float[] embedQuery(String text) {
        if (isExternalModelEnabled()) return embedWithModel("query: ", text);
        return embedLocally(text);
    }

    public float[] embedDocument(String text) {
        if (isExternalModelEnabled()) return embedWithModel("passage: ", text);
        return embedLocally(text);
    }

    public String modelId() {
        return isExternalModelEnabled() ? externalModelId : LOCAL_MODEL_ID;
    }

    private boolean isExternalModelEnabled() {
        return !embeddingUrl.isEmpty();
    }

    private float[] embedWithModel(String prefix, String text) {
        if (text == null || text.isBlank()) {
            return new float[DIMENSIONS];
        }

        try {
            String requestBody = objectMapper.writeValueAsString(Map.of("inputs", List.of(prefix + text)));
            HttpRequest request = HttpRequest.newBuilder(URI.create(embeddingUrl))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Embedding service returned HTTP " + response.statusCode());
            }

            float[][] vectors = objectMapper.readValue(response.body(), float[][].class);
            if (vectors.length != 1 || vectors[0].length != DIMENSIONS) {
                throw new IllegalStateException("Embedding service must return one " + DIMENSIONS + "-dimensional vector");
            }
            for (float value : vectors[0]) {
                if (!Float.isFinite(value)) {
                    throw new IllegalStateException("Embedding service returned a non-finite value");
                }
            }
            return normalize(vectors[0]);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while requesting a text embedding", exception);
        } catch (Exception exception) {
            if (exception instanceof IllegalStateException stateException) throw stateException;
            throw new IllegalStateException("Failed to request a text embedding", exception);
        }
    }

    private float[] embedLocally(String text) {
        if (text == null || text.isBlank()) {
            return new float[DIMENSIONS];
        }

        float[] vector = new float[DIMENSIONS];
        String normalized = text.toLowerCase().trim();

        // Split into tokens
        String[] tokens = normalized.split("[^\\p{L}\\p{N}]+");
        Set<String> triggeredConcepts = new HashSet<>();

        for (String token : tokens) {
            if (token.length() < 2) continue;

            // Literal token projection
            float[] tokenVector = hashToken(token);
            for (int i = 0; i < DIMENSIONS; i++) {
                vector[i] += tokenVector[i];
            }

            // Semantic ontology expansion
            for (Map.Entry<String, String> entry : CONCEPT_CLUSTERS.entrySet()) {
                String stem = entry.getKey();
                if (token.contains(stem) || stem.contains(token)) {
                    triggeredConcepts.add(entry.getValue());
                }
            }
        }

        // Apply semantic concept boosts (heavier weight to align synonyms)
        for (String concept : triggeredConcepts) {
            float[] conceptVector = hashToken(concept);
            for (int i = 0; i < DIMENSIONS; i++) {
                vector[i] += conceptVector[i] * 3.5f;
            }
        }

        // L2 normalize
        return normalize(vector);
    }

    /**
     * Hashes a single token into a sparse vector using multiple hash functions.
     */
    private float[] hashToken(String token) {
        float[] vector = new float[DIMENSIONS];
        byte[] bytes = token.getBytes(StandardCharsets.UTF_8);
        long hash = hashFNV(bytes);

        for (int i = 0; i < PRIMES.length; i++) {
            int prime = PRIMES[i];
            long h = hash * prime + i;
            int dim = (int) (Math.abs(h) % DIMENSIONS);
            vector[dim] = (float) Math.sin(h * 0.1);
        }

        return vector;
    }

    /**
     * FNV-1a hash function (64-bit).
     */
    private long hashFNV(byte[] data) {
        long hash = FNV_OFFSET;
        for (byte b : data) {
            hash ^= (b & 0xFF);
            hash *= FNV_PRIME;
        }
        return hash;
    }

    /**
     * L2-normalizes a vector to unit length.
     */
    private float[] normalize(float[] vector) {
        double magnitude = 0;
        for (float v : vector) {
            magnitude += v * v;
        }
        magnitude = Math.sqrt(magnitude);
        if (magnitude == 0) return vector;

        float[] normalized = new float[DIMENSIONS];
        for (int i = 0; i < DIMENSIONS; i++) {
            normalized[i] = (float) (vector[i] / magnitude);
        }
        return normalized;
    }

    /**
     * Converts a float array to a PGvector instance for database operations.
     */
    public PGvector toPgVector(float[] embedding) {
        return new PGvector(embedding);
    }
}
