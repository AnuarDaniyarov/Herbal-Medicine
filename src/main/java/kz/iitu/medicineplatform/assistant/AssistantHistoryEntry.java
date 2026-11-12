package kz.iitu.medicineplatform.assistant;

import java.time.OffsetDateTime;

public record AssistantHistoryEntry(Long conversationId, OffsetDateTime updatedAt,
                                    String lastQuestion, String lastAnswer, String lastStatus) { }
