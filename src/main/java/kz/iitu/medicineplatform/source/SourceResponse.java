package kz.iitu.medicineplatform.source;

import java.time.OffsetDateTime;

public record SourceResponse(
        Long id,
        String title,
        String authors,
        Integer publicationYear,
        String doi,
        String url,
        String sourceType,
        String licenseNote,
        OffsetDateTime accessedAt,
        OffsetDateTime createdAt
) {
    public static SourceResponse from(DataSource source) {
        return new SourceResponse(source.getId(), source.getTitle(), source.getAuthors(), source.getPublicationYear(),
                source.getDoi(), source.getUrl(), source.getSourceType(), source.getLicenseNote(),
                source.getAccessedAt(), source.getCreatedAt());
    }
}
