package kz.iitu.medicineplatform.sample;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record PlantSampleResponse(Long id, Long plantId, String scientificName, String plantPart, String origin,
                                  String batchIdentifier, LocalDate collectedOn, String metadata,
                                  OffsetDateTime createdAt) {
    public static PlantSampleResponse from(PlantSample sample) {
        return new PlantSampleResponse(sample.getId(), sample.getPlant().getId(),
                sample.getPlant().getScientificName(), sample.getPlantPart(), sample.getOrigin(),
                sample.getBatchIdentifier(), sample.getCollectedOn(), sample.getMetadata(), sample.getCreatedAt());
    }
}
