package kz.iitu.medicineplatform.plant;

import java.time.OffsetDateTime;

public record PlantResponse(
        Long id,
        String scientificName,
        String displayName,
        String family,
        String description,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static PlantResponse from(Plant plant) {
        return new PlantResponse(plant.getId(), plant.getScientificName(), plant.getDisplayName(), plant.getFamily(),
                plant.getDescription(), plant.getCreatedAt(), plant.getUpdatedAt());
    }
}
