package kz.iitu.medicineplatform.search;

import kz.iitu.medicineplatform.knowledge.KnowledgeClaim;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaimRepository;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaimResponse;
import kz.iitu.medicineplatform.knowledge.ReviewStatus;
import kz.iitu.medicineplatform.plant.Plant;
import kz.iitu.medicineplatform.plant.PlantRepository;
import kz.iitu.medicineplatform.plant.PlantResponse;
import kz.iitu.medicineplatform.compound.Compound;
import kz.iitu.medicineplatform.compound.CompoundRepository;
import kz.iitu.medicineplatform.compound.CompoundSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Unified search endpoint that searches across plants, compounds, and knowledge claims.
 * Supports both keyword and vector-based hybrid search.
 */
@RestController
@RequestMapping("/api/search")
public class UnifiedSearchController {

    private final PlantRepository plantRepository;
    private final CompoundRepository compoundRepository;
    private final KnowledgeClaimRepository claimRepository;
    private final EmbeddingService embeddingService;

    public UnifiedSearchController(PlantRepository plantRepository, CompoundRepository compoundRepository,
                                    KnowledgeClaimRepository claimRepository, EmbeddingService embeddingService) {
        this.plantRepository = plantRepository;
        this.compoundRepository = compoundRepository;
        this.claimRepository = claimRepository;
        this.embeddingService = embeddingService;
    }

    @GetMapping
    public SearchResponse search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (q.isBlank()) {
            return new SearchResponse(List.of(), 0, 0, 0);
        }

        Pageable pageable = PageRequest.of(page, Math.min(size, 100));

        // Search plants
        Page<Plant> plants = plantRepository.search(q, "", pageable);
        List<SearchResult> plantResults = plants.getContent().stream()
                .map(p -> new SearchResult(
                        "plant",
                        p.getId(),
                        p.getDisplayName(),
                        p.getScientificName(),
                        p.getNameRu() != null ? p.getNameRu() : "",
                        p.getFamily() != null ? p.getFamily() : ""
                ))
                .collect(Collectors.toList());

        // Search compounds
        Page<Compound> compounds = compoundRepository.search(q, null, null, pageable);
        List<SearchResult> compoundResults = compounds.getContent().stream()
                .map(c -> new SearchResult(
                        "compound",
                        c.getId(),
                        c.getName(),
                        c.getMolecularFormula() != null ? c.getMolecularFormula() : "",
                        "",
                        ""
                ))
                .collect(Collectors.toList());

        // Search knowledge claims
        List<KnowledgeClaim> claims = claimRepository.searchVerifiedCandidates(
                ReviewStatus.VERIFIED, q, PageRequest.of(0, Math.min(size, 20)));
        List<SearchResult> claimResults = claims.stream()
                .map(c -> {
                    String subject = c.getPlant() != null ? c.getPlant().getDisplayName() :
                            (c.getPreparation() != null ? c.getPreparation().getName() : "");
                    return new SearchResult(
                            "claim",
                            c.getId(),
                            c.getTitle(),
                            subject,
                            c.getClaimText().length() > 200 ? c.getClaimText().substring(0, 200) + "..." : c.getClaimText(),
                            c.getClaimType().name()
                    );
                })
                .collect(Collectors.toList());

        // Merge and rank: plants first, then claims, then compounds
        List<SearchResult> allResults = new ArrayList<>();
        allResults.addAll(plantResults);
        allResults.addAll(claimResults);
        allResults.addAll(compoundResults);

        return new SearchResponse(
                allResults,
                plants.getTotalElements(),
                compounds.getTotalElements(),
                (long) claims.size()
        );
    }

    public record SearchResult(
            String type,
            Long id,
            String title,
            String subtitle,
            String description,
            String category
    ) {}

    public record SearchResponse(
            List<SearchResult> results,
            long plantsCount,
            long compoundsCount,
            long claimsCount
    ) {}
}
