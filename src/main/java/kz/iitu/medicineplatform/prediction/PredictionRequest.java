package kz.iitu.medicineplatform.prediction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PredictionRequest(
        @NotBlank @Size(max = 2000) String smiles,
        @NotBlank @Size(max = 100) @Pattern(regexp = "[A-Za-z0-9_-]+") String activityCode,
        @Positive Long compoundId
) {
}
