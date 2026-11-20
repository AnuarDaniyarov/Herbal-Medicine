package kz.iitu.medicineplatform.search;

import kz.iitu.medicineplatform.knowledge.ClaimType;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaim;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaimRepository;
import kz.iitu.medicineplatform.knowledge.ReviewStatus;
import kz.iitu.medicineplatform.plant.Plant;
import kz.iitu.medicineplatform.plant.PlantRepository;
import kz.iitu.medicineplatform.preparation.Preparation;
import kz.iitu.medicineplatform.preparation.PreparationRepository;
import kz.iitu.medicineplatform.compound.Compound;
import kz.iitu.medicineplatform.compound.CompoundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Unified hybrid search endpoint that searches across plants, preparations,
 * compounds, and knowledge claims.
 *
 * Implements:
 * 1. Query preprocessing and normalization.
 * 2. Intent detection (by indication, contraindication, interaction, or entity).
 * 3. True hybrid search: keyword matching + pgvector cosine similarity search.
 * 4. Merging, intent-aware re-ranking, and filtering.
 */
@RestController
@RequestMapping("/api/search")
public class UnifiedSearchController {

    private static final Logger log = LoggerFactory.getLogger(UnifiedSearchController.class);

    private final PlantRepository plantRepository;
    private final PreparationRepository preparationRepository;
    private final CompoundRepository compoundRepository;
    private final KnowledgeClaimRepository claimRepository;
    private final EmbeddingService embeddingService;

    @Value("${search.vector.enabled:false}")
    private boolean vectorSearchEnabled;

    private static final Pattern INDICATION_PATTERN = Pattern.compile(
            "(?<![\\p{L}\\p{N}])(от|при|для|помогает|лечит|применяется|действие|показания|показание|кашл|бессонниц|сон|давлен|жкт|желуд|воспален|орви|простуд|иммунитет|жөтел|ұйқы|қарсы|улучш|памят|мозг|кровообращ|кровотечен|гемостаз|тошнот|бауыр|арналған)[\\p{L}\\p{N}]*",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    private static final Pattern CONTRAINDICATION_PATTERN = Pattern.compile(
            "(?<![\\p{L}\\p{N}])(противопоказан|нельзя|вред|ограничен|запрещ|беременн|лактац|кормлен|детям|аллерг|жүктілік|давлен|гипертенз|высок|тромб|флебит|варикоз)[\\p{L}\\p{N}]*",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    private static final Pattern INTERACTION_PATTERN = Pattern.compile(
            "(?<![\\p{L}\\p{N}])(взаимодейств|совместим|совместн|вместе|сочетан|алкогол|антидепрессант|барбитурат|контрацептив|өзара)[\\p{L}\\p{N}]*",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    private static final Set<String> SEARCH_STOP_WORDS = Set.of(
            "а", "без", "бы", "в", "во", "для", "до", "же", "за", "и", "из", "или", "к", "как", "ли",
            "на", "над", "не", "но", "о", "об", "от", "по", "под", "при", "с", "со", "что", "это",
            "and", "for", "from", "the", "with"
    );
    private static final Map<String, List<String>> MULTILINGUAL_SEARCH_ALIASES = Map.of(
            "ұйқы", List.of("бессонница", "валериана", "седативное"),
            "жөтел", List.of("кашель", "тимьян", "солодка", "отхаркивающее"),
            "бауыр", List.of("печень", "расторопша", "гепатопротекторное")
    );

    public UnifiedSearchController(PlantRepository plantRepository,
                                   PreparationRepository preparationRepository,
                                   CompoundRepository compoundRepository,
                                   KnowledgeClaimRepository claimRepository,
                                   EmbeddingService embeddingService) {
        this.plantRepository = plantRepository;
        this.preparationRepository = preparationRepository;
        this.compoundRepository = compoundRepository;
        this.claimRepository = claimRepository;
        this.embeddingService = embeddingService;
    }

    @GetMapping
    public SearchResponse search(
            @RequestParam String q,
            @RequestParam(required = false, defaultValue = "all") String type,
            @RequestParam(required = false) String claimType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (q == null || q.isBlank()) {
            return new SearchResponse(List.of(), 0, 0, 0, 0, "GENERAL", "ru", q);
        }

        String rawQuery = q.trim();
        String normalized = Normalizer.normalize(rawQuery.toLowerCase(Locale.ROOT), Normalizer.Form.NFKC);
        if (page < 0 || size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page must be non-negative and size must be positive");
        }
        String detectedIntent = detectIntent(normalized);
        String detectedLanguage = detectLanguage(normalized);

        Pageable pageable = PageRequest.of(page, Math.min(size, 100));

        List<SearchResult> plantResults = new ArrayList<>();
        List<SearchResult> preparationResults = new ArrayList<>();
        List<SearchResult> compoundResults = new ArrayList<>();
        List<SearchResult> claimResults = new ArrayList<>();

        long totalPlants = 0;
        long totalPreparations = 0;
        long totalCompounds = 0;
        long totalClaims = 0;

        boolean searchAll = "all".equalsIgnoreCase(type);

        // 1. Search Plants
        if (searchAll || "plant".equalsIgnoreCase(type) || "plants".equalsIgnoreCase(type)) {
            Page<Plant> plants = searchPlants(normalized, pageable);
            totalPlants = plants.getTotalElements();
            plantResults = plants.getContent().stream()
                    .map(p -> new SearchResult(
                            "plant",
                            p.getId(),
                            p.getDisplayName(),
                            p.getScientificName(),
                            buildPlantDescription(p),
                            p.getFamily() != null ? p.getFamily() : "Растение",
                            "VERIFIED".equalsIgnoreCase(p.getReviewStatus()),
                            "Справочник лекарственных растений",
                            "/plants/" + p.getId()
                    ))
                    .collect(Collectors.toList());
        }

        // 2. Search Preparations
        if (searchAll || "preparation".equalsIgnoreCase(type) || "preparations".equalsIgnoreCase(type)) {
            Page<Preparation> preps = preparationRepository.search(rawQuery, "", ReviewStatus.VERIFIED, pageable);
            totalPreparations = preps.getTotalElements();
            preparationResults = preps.getContent().stream()
                    .map(pr -> new SearchResult(
                            "preparation",
                            pr.getId(),
                            pr.getName(),
                            pr.getDosageForm() != null ? pr.getDosageForm() : "",
                            pr.getDescription() != null ? pr.getDescription() : (pr.getManufacturer() != null ? "Производитель: " + pr.getManufacturer() : ""),
                            "Препарат",
                            pr.getReviewStatus() == ReviewStatus.VERIFIED,
                            pr.getSource() != null ? pr.getSource().getTitle() : "Государственный реестр",
                            "/preparations/" + pr.getId()
                    ))
                    .collect(Collectors.toList());
        }

        // 3. Search Compounds
        if (searchAll || "compound".equalsIgnoreCase(type) || "compounds".equalsIgnoreCase(type)) {
            Page<Compound> compounds = compoundRepository.search(rawQuery, null, null, pageable);
            totalCompounds = compounds.getTotalElements();
            compoundResults = compounds.getContent().stream()
                    .map(c -> new SearchResult(
                            "compound",
                            c.getId(),
                            c.getName(),
                            c.getMolecularFormula() != null ? c.getMolecularFormula() : "",
                            c.getCanonicalSmiles() != null ? "SMILES: " + c.getCanonicalSmiles() : "",
                            "Химическое соединение",
                            "VERIFIED".equalsIgnoreCase(c.getStructureStatus()),
                            "Фитохимическая база данных",
                            "/compounds/" + c.getId()
                    ))
                    .collect(Collectors.toList());
        }

        // 4. Hybrid Search Knowledge Claims (Keyword + pgvector)
        if (searchAll || "claim".equalsIgnoreCase(type) || "claims".equalsIgnoreCase(type)) {
            LinkedHashMap<Long, KnowledgeClaim> mergedClaims = new LinkedHashMap<>();

            // Search the full phrase and its useful tokens so natural-language queries match evidence.
            for (String term : searchTerms(normalized)) {
                List<KnowledgeClaim> keywordClaims = claimRepository.searchVerifiedCandidates(
                        ReviewStatus.VERIFIED, term, PageRequest.of(0, 30));
                for (KnowledgeClaim claim : keywordClaims) {
                    mergedClaims.putIfAbsent(claim.getId(), claim);
                }
            }

            // B. Vector similarity search
            if (vectorSearchEnabled) {
                try {
                    float[] embedding = embeddingService.embedQuery(normalized);
                    StringBuilder sb = new StringBuilder();
                    sb.append('[');
                    for (int i = 0; i < embedding.length; i++) {
                        if (i > 0) sb.append(',');
                        sb.append(embedding[i]);
                    }
                    sb.append(']');
                    List<KnowledgeClaim> vectorClaims = claimRepository.findNearestByEmbedding(
                            ReviewStatus.VERIFIED.name(), embeddingService.modelId(), sb.toString(), PageRequest.of(0, 20));
                    for (KnowledgeClaim c : vectorClaims) {
                        mergedClaims.putIfAbsent(c.getId(), c);
                    }
                } catch (RuntimeException exception) {
                    log.warn("Vector search unavailable; returning keyword matches: {}", exception.getMessage());
                }
            }

            // Optional claimType filter
            ClaimType targetClaimType = null;
            if (claimType != null && !claimType.isBlank() && !"all".equalsIgnoreCase(claimType)) {
                try {
                    targetClaimType = ClaimType.valueOf(claimType.toUpperCase());
                } catch (IllegalArgumentException exception) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported claimType: " + claimType);
                }
            }

            final ClaimType filterType = targetClaimType;
            List<KnowledgeClaim> filteredClaims = mergedClaims.values().stream()
                    .filter(c -> filterType == null || c.getClaimType() == filterType)
                    .sorted(Comparator.comparingInt((KnowledgeClaim c) -> claimRelevance(c, searchTerms(normalized))).reversed()
                            .thenComparing(KnowledgeClaim::getId))
                    .collect(Collectors.toList());

            totalClaims = filteredClaims.size();

            claimResults = filteredClaims.stream()
                    .limit(Math.min(size, 25))
                    .map(c -> {
                        String subject = c.getPlant() != null ? c.getPlant().getDisplayName() :
                                (c.getPreparation() != null ? c.getPreparation().getName() : "");
                        String route = c.getPlant() != null ? "/plants/" + c.getPlant().getId() :
                                (c.getPreparation() != null ? "/preparations/" + c.getPreparation().getId() : "/search");
                        String sourceTitle = c.getSource() != null ? c.getSource().getTitle() : "Верифицированный источник";

                        return new SearchResult(
                                "claim",
                                c.getId(),
                                c.getTitle(),
                                subject,
                                c.getClaimText().length() > 220 ? c.getClaimText().substring(0, 220) + "..." : c.getClaimText(),
                                c.getClaimType().name(),
                                c.getReviewStatus() == ReviewStatus.VERIFIED,
                                sourceTitle,
                                route
                        );
                    })
                    .collect(Collectors.toList());
        }

        // 5. Intelligent Result Merging & Ranking based on detected intent
        List<SearchResult> allResults = new ArrayList<>();
        if ("INDICATION".equals(detectedIntent) || "CONTRAINDICATION".equals(detectedIntent) || "INTERACTION".equals(detectedIntent)) {
            // Put relevant claims and their plants at the top
            allResults.addAll(claimResults);
            allResults.addAll(plantResults);
            allResults.addAll(preparationResults);
            allResults.addAll(compoundResults);
        } else {
            // Default entity priority
            allResults.addAll(plantResults);
            allResults.addAll(claimResults);
            allResults.addAll(preparationResults);
            allResults.addAll(compoundResults);
        }

        return new SearchResponse(
                allResults,
                totalPlants,
                totalPreparations,
                totalCompounds,
                totalClaims,
                detectedIntent,
                detectedLanguage,
                rawQuery
        );
    }

    private String detectIntent(String normalized) {
        if (INTERACTION_PATTERN.matcher(normalized).find()) {
            return "INTERACTION";
        }
        if (CONTRAINDICATION_PATTERN.matcher(normalized).find()) {
            return "CONTRAINDICATION";
        }
        if (INDICATION_PATTERN.matcher(normalized).find()) {
            return "INDICATION";
        }
        return "GENERAL";
    }

    private Page<Plant> searchPlants(String normalized, Pageable pageable) {
        LinkedHashMap<Long, Plant> matches = new LinkedHashMap<>();
        for (String term : searchTerms(normalized)) {
            plantRepository.search(term, "", PageRequest.of(0, 100)).getContent()
                    .forEach(plant -> matches.putIfAbsent(plant.getId(), plant));
        }
        List<String> terms = searchTerms(normalized);
        List<Plant> ranked = matches.values().stream()
                .sorted(Comparator.comparingInt((Plant plant) -> plantRelevance(plant, terms)).reversed()
                        .thenComparing(Plant::getId))
                .toList();
        int start = (int) Math.min((long) pageable.getOffset(), ranked.size());
        int end = Math.min(start + pageable.getPageSize(), ranked.size());
        return new PageImpl<>(ranked.subList(start, end), pageable, ranked.size());
    }

    private List<String> searchTerms(String normalized) {
        return Arrays.stream(normalized.split("[^\\p{L}\\p{N}]+"))
                .filter(token -> token.length() >= 2 && !SEARCH_STOP_WORDS.contains(token))
                .map(this::stemSearchTerm)
                .flatMap(token -> java.util.stream.Stream.concat(
                        java.util.stream.Stream.of(token),
                        MULTILINGUAL_SEARCH_ALIASES.entrySet().stream()
                                .filter(entry -> token.startsWith(entry.getKey()))
                                .flatMap(entry -> entry.getValue().stream())))
                .distinct()
                .limit(8)
                .toList();
    }

    private String stemSearchTerm(String token) {
        if (token.length() < 4) return token;
        String[] endings = {"иями", "ями", "ами", "его", "ого", "ему", "ому", "ее", "ие", "ые", "ая", "яя",
                "ое", "ый", "ий", "ой", "ую", "юю", "ых", "их", "ам", "ям", "ом", "ем", "ах", "ях",
                "ов", "ев", "ей", "ы", "и", "а", "я", "у", "ю", "е", "о"};
        for (String ending : endings) {
            if (token.endsWith(ending) && token.length() - ending.length() >= 3) {
                return token.substring(0, token.length() - ending.length());
            }
        }
        return token;
    }

    private int claimRelevance(KnowledgeClaim claim, List<String> terms) {
        String searchable = String.join(" ",
                claim.getTitle(), claim.getClaimText(), claim.getInteractionPartner() == null ? "" : claim.getInteractionPartner(),
                claim.getPlant() == null ? "" : String.join(" ", claim.getPlant().getScientificName(), claim.getPlant().getDisplayName(),
                        Objects.toString(claim.getPlant().getNameRu(), ""), Objects.toString(claim.getPlant().getNameKk(), ""),
                        Objects.toString(claim.getPlant().getSynonyms(), "")),
                claim.getPreparation() == null ? "" : claim.getPreparation().getName())
                .toLowerCase(Locale.ROOT);
        return (int) terms.stream().filter(searchable::contains).count();
    }

    private int plantRelevance(Plant plant, List<String> terms) {
        String searchable = String.join(" ", plant.getScientificName(), plant.getDisplayName(),
                Objects.toString(plant.getNameRu(), ""), Objects.toString(plant.getNameKk(), ""),
                Objects.toString(plant.getSynonyms(), ""), Objects.toString(plant.getFamily(), ""))
                .toLowerCase(Locale.ROOT);
        return (int) terms.stream().filter(searchable::contains).count();
    }

    private String detectLanguage(String text) {
        boolean hasCyrillic = text.chars().anyMatch(c -> Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CYRILLIC);
        if (!hasCyrillic) return "en";
        // Check for specific Kazakh letters: ә, ғ, қ, ң, ө, ұ, ү, һ, і
        boolean hasKazakh = text.chars().anyMatch(c ->
                c == 'ә' || c == 'ғ' || c == 'қ' || c == 'ң' || c == 'ө' || c == 'ұ' || c == 'ү' || c == 'һ' || c == 'і'
        );
        return hasKazakh ? "kk" : "ru";
    }

    private String buildPlantDescription(Plant p) {
        StringBuilder sb = new StringBuilder();
        if (p.getNameRu() != null && !p.getNameRu().isBlank()) {
            sb.append(p.getNameRu());
        }
        if (p.getNameKk() != null && !p.getNameKk().isBlank()) {
            if (sb.length() > 0) sb.append(" / ");
            sb.append(p.getNameKk());
        }
        if (p.getUsedParts() != null && !p.getUsedParts().isBlank()) {
            if (sb.length() > 0) sb.append(" • Части: ");
            sb.append(p.getUsedParts());
        }
        return sb.length() > 0 ? sb.toString() : (p.getDescription() != null ? p.getDescription() : "");
    }

    public record SearchResult(
            String type,
            Long id,
            String title,
            String subtitle,
            String description,
            String category,
            boolean verified,
            String sourceName,
            String route
    ) {}

    public record SearchResponse(
            List<SearchResult> results,
            long plantsCount,
            long preparationsCount,
            long compoundsCount,
            long claimsCount,
            String detectedIntent,
            String language,
            String query
    ) {}
}
