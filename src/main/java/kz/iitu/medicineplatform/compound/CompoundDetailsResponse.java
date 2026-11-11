package kz.iitu.medicineplatform.compound;

import java.time.OffsetDateTime;
import java.util.List;
import kz.iitu.medicineplatform.plant.PlantOccurrenceResponse;

public record CompoundDetailsResponse(
        Long id,
        String name,
        String canonicalSmiles,
        String inchiKey,
        String externalId,
        String molecularFormula,
        String structureStatus,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<PlantOccurrenceResponse> plantOccurrences
) {
}
