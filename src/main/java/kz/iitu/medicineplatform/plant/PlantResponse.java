package kz.iitu.medicineplatform.plant;

import java.time.OffsetDateTime;

public record PlantResponse(
        Long id,
        String scientificName,
        String displayName,
        String family,
        String description,
        String nameRu,
        String nameKk,
        String synonyms,
        String distributionRegions,
        String usedParts,
        String reviewStatus,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static PlantResponse from(Plant plant) {
        return new PlantResponse(plant.getId(), plant.getScientificName(), plant.getDisplayName(), plant.getFamily(),
                plant.getDescription(), plant.getNameRu(), plant.getNameKk(), plant.getSynonyms(),
                plant.getDistributionRegions(), plant.getUsedParts(), plant.getReviewStatus(),
                plant.getCreatedAt(), plant.getUpdatedAt());
    }
}
