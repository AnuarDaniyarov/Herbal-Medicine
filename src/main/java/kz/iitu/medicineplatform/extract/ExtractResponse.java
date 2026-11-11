package kz.iitu.medicineplatform.extract;

import java.time.OffsetDateTime;
import kz.iitu.medicineplatform.sample.PlantSampleResponse;

public record ExtractResponse(Long id, String extractionMethod, String solvent, String conditions,
                              String batchIdentifier, PlantSampleResponse sample, OffsetDateTime createdAt) {
    public static ExtractResponse from(Extract extract) {
        return new ExtractResponse(extract.getId(), extract.getExtractionMethod(), extract.getSolvent(),
                extract.getConditions(), extract.getBatchIdentifier(),
                PlantSampleResponse.from(extract.getSample()), extract.getCreatedAt());
    }
}
