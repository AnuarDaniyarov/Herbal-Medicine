package kz.iitu.medicineplatform.knowledge;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClaimReviewRequest(@NotNull ReviewStatus reviewStatus, @Size(max = 5000) String reviewNote) { }
