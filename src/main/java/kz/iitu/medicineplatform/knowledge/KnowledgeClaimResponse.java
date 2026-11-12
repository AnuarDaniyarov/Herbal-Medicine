package kz.iitu.medicineplatform.knowledge;

import java.time.OffsetDateTime;
import kz.iitu.medicineplatform.source.SourceResponse;

public record KnowledgeClaimResponse(Long id, Long plantId, String plantName, Long preparationId,
                                     String preparationName, ClaimType claimType, String title, String claimText,
                                     String interactionPartner, ReviewStatus reviewStatus,
                                     SourceResponse source, String sourceLocator, OffsetDateTime reviewedAt) {
    public static KnowledgeClaimResponse from(KnowledgeClaim claim) {
        return new KnowledgeClaimResponse(claim.getId(),
                claim.getPlant() == null ? null : claim.getPlant().getId(),
                claim.getPlant() == null ? null : claim.getPlant().getScientificName(),
                claim.getPreparation() == null ? null : claim.getPreparation().getId(),
                claim.getPreparation() == null ? null : claim.getPreparation().getName(),
                claim.getClaimType(), claim.getTitle(), claim.getClaimText(), claim.getInteractionPartner(),
                claim.getReviewStatus(), SourceResponse.from(claim.getSource()), claim.getSourceLocator(),
                claim.getReviewedAt());
    }
}
