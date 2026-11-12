package kz.iitu.medicineplatform.source;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateSourceRequest(@NotBlank @Size(max = 1000) String title,
                                  @Size(max = 1000) String authors,
                                  @Min(1500) @Max(2100) Integer publicationYear,
                                  @Size(max = 300) String doi,
                                  @Size(max = 2000) String url,
                                  @NotBlank @Pattern(regexp = "ARTICLE|REGISTRY|MONOGRAPH|DATASET|OTHER") String sourceType,
                                  @Size(max = 5000) String licenseNote) { }
