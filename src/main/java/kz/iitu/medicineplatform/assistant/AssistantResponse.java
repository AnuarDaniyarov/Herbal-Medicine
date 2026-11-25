package kz.iitu.medicineplatform.assistant;

import java.util.List;
import kz.iitu.medicineplatform.source.SourceResponse;

public record AssistantResponse(Long conversationId, String answer, String status, String answerMode,
                                boolean isAiGenerated, String safetyNotice, List<Citation> citations) {
    public record Citation(Long claimId, String title, String claimText, String subject,
                           String sourceLocator, SourceResponse source, String claimType) {
        public Citation(Long claimId, String title, String claimText, String subject,
                        String sourceLocator, SourceResponse source) {
            this(claimId, title, claimText, subject, sourceLocator, source, null);
        }
    }
}
