package kz.iitu.medicineplatform.assistant;

import java.time.OffsetDateTime;
import java.util.List;
import kz.iitu.medicineplatform.source.SourceResponse;

public record AssistantHistoryMessage(Long id, String role, String content, String responseStatus,
                                      OffsetDateTime createdAt, List<SourceReference> sources) {
    public record SourceReference(String locator, String excerpt, SourceResponse source) { }
}
