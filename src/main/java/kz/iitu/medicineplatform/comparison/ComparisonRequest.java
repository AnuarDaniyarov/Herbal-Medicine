package kz.iitu.medicineplatform.comparison;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ComparisonRequest(
        @NotEmpty @Size(min = 2, max = 20) List<@NotNull @Positive Long> candidateIds,
        @NotBlank @Size(max = 100) String activityCode
) { }
