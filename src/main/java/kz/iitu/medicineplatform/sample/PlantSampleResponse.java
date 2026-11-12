package kz.iitu.medicineplatform.sample;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import kz.iitu.medicineplatform.extract.ExtractResponse;

public record PlantSampleResponse(Long id, Long plantId, String scientificName, String plantPart, String origin,
                                  String batchIdentifier, LocalDate collectedOn, String metadata,
                                  OffsetDateTime createdAt, List<ExtractResponse> extracts) {
    public static PlantSampleResponse from(PlantSample sample) {
        return from(sample, List.of());
    }

    public static PlantSampleResponse from(PlantSample sample, List<ExtractResponse> extracts) {
        return new PlantSampleResponse(sample.getId(), sample.getPlant().getId(),
                sample.getPlant().getScientificName(), sample.getPlantPart(), sample.getOrigin(),
                sample.getBatchIdentifier(), sample.getCollectedOn(), sample.getMetadata(), sample.getCreatedAt(),
                extracts != null ? extracts : List.of());
    }
}
