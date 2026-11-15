package kz.iitu.medicineplatform.search;

import java.util.List;

public final class SearchDtos {
    private SearchDtos() { }

    public record SearchResult(
            String type,
            Long id,
            String title,
            String subtitle,
            String description,
            String category,
            boolean verified,
            String reviewStatus,
            String href
    ) {}

    public record SearchResponse(
            List<SearchResult> results,
            long plantsCount,
            long compoundsCount,
            long claimsCount,
            String searchMode
    ) {}
}
