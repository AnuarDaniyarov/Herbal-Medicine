package kz.iitu.medicineplatform.knowledge;

import jakarta.validation.constraints.Size;

public record ClaimReviewRequest(
        ReviewStatus reviewStatus,
        @Size(max = 5000) String reviewNote,
        @Size(max = 500) String title,
        @Size(max = 10000) String claimText,
        @Size(max = 500) String interactionPartner,
        @Size(max = 500) String sourceLocator
) {
    public ClaimReviewRequest(ReviewStatus reviewStatus, String reviewNote) {
        this(reviewStatus, reviewNote, null, null, null, null);
    }
}
