package kz.iitu.medicineplatform.knowledge;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ClaimSubmissionRequest(Long plantId, Long preparationId,
                                     @NotNull ClaimType claimType,
                                     @NotBlank @Size(max = 500) String title,
                                     @NotBlank @Size(max = 10000) String claimText,
                                     @Size(max = 500) String interactionPartner,
                                     @NotNull @Positive Long sourceId,
                                     @Size(max = 500) String sourceLocator) { }
