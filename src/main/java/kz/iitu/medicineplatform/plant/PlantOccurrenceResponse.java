package kz.iitu.medicineplatform.plant;

import kz.iitu.medicineplatform.source.SourceResponse;
import kz.iitu.medicineplatform.extract.Extract;
import kz.iitu.medicineplatform.sample.PlantSample;
import kz.iitu.medicineplatform.sample.PlantSampleResponse;
import kz.iitu.medicineplatform.extract.ExtractResponse;

public record PlantOccurrenceResponse(
        Long plantId,
        String scientificName,
        String displayName,
        String plantPart,
        String sampleOrBatch,
        Long sampleId,
        Long extractId,
        PlantSampleResponse sample,
        ExtractResponse extract,
        SourceResponse source,
        String notes
) {
    public static PlantOccurrenceResponse from(PlantCompoundRelationship relationship) {
        Plant plant = relationship.getPlant();
        return new PlantOccurrenceResponse(
                plant.getId(), plant.getScientificName(), plant.getDisplayName(), relationship.getPlantPart(),
                relationship.getSampleOrBatch(), relationship.getSample() == null ? null : relationship.getSample().getId(),
                relationship.getExtract() == null ? null : relationship.getExtract().getId(),
                relationship.getSample() == null ? null : PlantSampleResponse.from(relationship.getSample()),
                relationship.getExtract() == null ? null : ExtractResponse.from(relationship.getExtract()),
                relationship.getSource() == null ? null : SourceResponse.from(relationship.getSource()),
                relationship.getNotes());
    }
}
