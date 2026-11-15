package kz.iitu.medicineplatform.search;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import kz.iitu.medicineplatform.knowledge.ClaimType;
import kz.iitu.medicineplatform.compound.Compound;
import kz.iitu.medicineplatform.compound.CompoundRepository;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaim;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaimRepository;
import kz.iitu.medicineplatform.knowledge.ReviewStatus;
import kz.iitu.medicineplatform.plant.Plant;
import kz.iitu.medicineplatform.plant.PlantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchService {
    private static final Pattern TOKEN_SPLIT = Pattern.compile("[^\\p{L}\\p{N}]+");
    private static final Set<String> STOP_WORDS = Set.of(
            "что", "это", "как", "для", "при", "есть", "можно", "какие", "какой", "какая", "про", "или",
            "the", "what", "with", "from", "and", "for");

    private final PlantRepository plants;
    private final CompoundRepository compounds;
    private final KnowledgeClaimRepository claims;
    private final VectorClaimSearch vectorClaimSearch;
    private final boolean vectorEnabled;

    public SearchService(PlantRepository plants, CompoundRepository compounds,
                         KnowledgeClaimRepository claims,
                         VectorClaimSearch vectorClaimSearch,
                         @Value("${search.vector.enabled:true}") boolean vectorEnabled) {
        this.plants = plants;
        this.compounds = compounds;
        this.claims = claims;
        this.vectorClaimSearch = vectorClaimSearch;
        this.vectorEnabled = vectorEnabled;
    }

    @Transactional(readOnly = true)
    public SearchDtos.SearchResponse search(String query, String claimType, int page, int size) {
        String q = query == null ? "" : query.trim();
        if (q.isBlank()) {
            return new SearchDtos.SearchResponse(List.of(), 0, 0, 0, "keyword");
        }

        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<Plant> plantPage = plants.search(q, "", pageable);
        Page<Compound> compoundPage = compounds.search(q, null, null, pageable);

        List<SearchDtos.SearchResult> plantResults = plantPage.getContent().stream()
                .map(p -> new SearchDtos.SearchResult(
                        "plant",
                        p.getId(),
                        p.getDisplayName(),
                        p.getScientificName(),
                        p.getNameRu() != null ? p.getNameRu() : "",
                        p.getFamily() != null ? p.getFamily() : "",
                        "VERIFIED".equalsIgnoreCase(p.getReviewStatus()),
                        p.getReviewStatus(),
                        "/plants/" + p.getId()))
                .toList();

        List<SearchDtos.SearchResult> compoundResults = compoundPage.getContent().stream()
                .map(c -> new SearchDtos.SearchResult(
                        "compound",
                        c.getId(),
                        c.getName(),
                        c.getMolecularFormula() != null ? c.getMolecularFormula() : "",
                        "",
                        "",
                        false,
                        "CATALOG",
                        "/compounds/" + c.getId()))
                .toList();

        ClaimType filterType = claimType != null && !claimType.isBlank()
                ? ClaimType.valueOf(claimType.toUpperCase(Locale.ROOT)) : null;
        HybridClaims hybrid = searchVerifiedClaims(q, Math.min(size, 20), filterType);
        List<SearchDtos.SearchResult> claimResults = hybrid.claims().stream()
                .map(c -> {
                    String subject = c.getPlant() != null ? c.getPlant().getDisplayName()
                            : (c.getPreparation() != null ? c.getPreparation().getName() : "");
                    String text = c.getClaimText();
                    return new SearchDtos.SearchResult(
                            "claim",
                            c.getId(),
                            c.getTitle(),
                            subject,
                            text.length() > 200 ? text.substring(0, 200) + "..." : text,
                            c.getClaimType().name(),
                            true,
                            ReviewStatus.VERIFIED.name(),
                            linkFor(c));
                })
                .toList();

        List<SearchDtos.SearchResult> all = new ArrayList<>();
        all.addAll(claimResults);
        all.addAll(plantResults);
        all.addAll(compoundResults);

        return new SearchDtos.SearchResponse(
                all,
                plantPage.getTotalElements(),
                compoundPage.getTotalElements(),
                (long) claimResults.size(),
                hybrid.mode());
    }

    @Transactional(readOnly = true)
    public List<KnowledgeClaim> searchVerifiedClaimsForAssistant(String normalizedQuestion, int limit) {
        return searchVerifiedClaims(normalizedQuestion, limit, null).claims();
    }

    private HybridClaims searchVerifiedClaims(String query, int limit, ClaimType claimTypeFilter) {
        Set<String> terms = tokens(query);
        LinkedHashSet<KnowledgeClaim> candidates = new LinkedHashSet<>();
        String mode = "keyword";

        for (String term : terms) {
            candidates.addAll(claims.searchVerifiedCandidates(ReviewStatus.VERIFIED, term, PageRequest.of(0, 10)));
            if (candidates.size() >= 30) break;
        }
        if (candidates.isEmpty() && !query.isBlank()) {
            candidates.addAll(claims.searchVerifiedCandidates(ReviewStatus.VERIFIED, query, PageRequest.of(0, limit)));
        }

        if (vectorEnabled) {
            List<KnowledgeClaim> vectorHits = vectorClaimSearch.findNearestVerified(query, limit);
            if (!vectorHits.isEmpty()) {
                mode = candidates.isEmpty() ? "vector" : "hybrid";
                candidates.addAll(vectorHits);
            }
        }

        List<KnowledgeClaim> ranked = candidates.stream()
                .filter(claim -> claimTypeFilter == null || claim.getClaimType() == claimTypeFilter)
                .map(claim -> Map.entry(claim, score(claim, terms, query)))
                .filter(entry -> entry.getValue() > 0 || terms.isEmpty())
                .sorted(Map.Entry.<KnowledgeClaim, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();

        if (ranked.isEmpty() && !candidates.isEmpty()) {
            ranked = candidates.stream().limit(limit).toList();
        }
        return new HybridClaims(ranked, mode);
    }

    private static String linkFor(KnowledgeClaim claim) {
        if (claim.getPlant() != null) return "/plants/" + claim.getPlant().getId();
        if (claim.getPreparation() != null) return "/preparations/" + claim.getPreparation().getId();
        return "/assistant";
    }

    private static int score(KnowledgeClaim claim, Set<String> terms, String rawQuery) {
        String searchable = String.join(" ",
                claim.getTitle(),
                claim.getClaimText(),
                claim.getInteractionPartner() == null ? "" : claim.getInteractionPartner(),
                claim.getPlant() == null ? "" : String.join(" ",
                        nullToEmpty(claim.getPlant().getScientificName()),
                        nullToEmpty(claim.getPlant().getDisplayName()),
                        nullToEmpty(claim.getPlant().getNameRu()),
                        nullToEmpty(claim.getPlant().getNameKk()),
                        nullToEmpty(claim.getPlant().getSynonyms())),
                claim.getPreparation() == null ? "" : nullToEmpty(claim.getPreparation().getName()))
                .toLowerCase(Locale.ROOT);
        int score = 0;
        String q = rawQuery.toLowerCase(Locale.ROOT);
        if (!q.isBlank() && searchable.contains(q)) score += 12;
        for (String term : terms) {
            if (searchable.contains(term)) score += 3;
        }
        return score;
    }

    private static Set<String> tokens(String text) {
        LinkedHashMap<String, Boolean> terms = new LinkedHashMap<>();
        for (String token : TOKEN_SPLIT.split(text.toLowerCase(Locale.ROOT))) {
            if (token.length() < 3 || STOP_WORDS.contains(token)) continue;
            terms.put(token, Boolean.TRUE);
        }
        return terms.keySet();
    }

    /**
     * Levenshtein distance for fuzzy matching — allows up to 1 character difference.
     */
    private static int levenshteinDistance(String s, String t) {
        if (s.length() == 0) return t.length();
        if (t.length() == 0) return s.length();
        int[] prev = new int[t.length() + 1];
        int[] curr = new int[t.length() + 1];
        for (int j = 0; j <= t.length(); j++) prev[j] = j;
        for (int i = 1; i <= s.length(); i++) {
            curr[0] = i;
            for (int j = 1; j <= t.length(); j++) {
                int cost = s.charAt(i - 1) == t.charAt(j - 1) ? 0 : 1;
                curr[j] = Math.min(Math.min(curr[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
            }
            int[] tmp = prev;
            prev = curr;
            curr = tmp;
        }
        return prev[t.length()];
    }

    /**
     * Check if any term is a fuzzy match (edit distance <= 1) against the searchable text.
     */
    private static boolean fuzzyMatch(String searchable, Set<String> terms) {
        String[] words = searchable.split("\\s+");
        for (String term : terms) {
            for (String word : words) {
                if (Math.abs(word.length() - term.length()) > 1) continue;
                if (levenshteinDistance(word, term) <= 1) return true;
            }
        }
        return false;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private record HybridClaims(List<KnowledgeClaim> claims, String mode) { }
}
