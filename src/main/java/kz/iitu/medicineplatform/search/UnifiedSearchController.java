package kz.iitu.medicineplatform.search;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Unified search across verified knowledge claims, plants and compounds.
 * Claim retrieval uses keyword search and, when available, pgvector hybrid ranking.
 */
@RestController
@RequestMapping("/api/search")
public class UnifiedSearchController {

    private final SearchService searchService;

    public UnifiedSearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    @Operation(summary = "Hybrid catalogue and verified-knowledge search",
            description = "Returns verified knowledge claims first when matched, then plants and compounds. "
                    + "Does not invent answers; only indexed database records are returned.")
    public SearchDtos.SearchResponse search(
            @RequestParam String q,
            @RequestParam(required = false) String claimType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return searchService.search(q, claimType, page, size);
    }
}
