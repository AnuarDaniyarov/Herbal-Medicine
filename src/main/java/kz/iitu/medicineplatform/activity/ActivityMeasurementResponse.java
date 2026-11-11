package kz.iitu.medicineplatform.activity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import kz.iitu.medicineplatform.source.SourceResponse;
import kz.iitu.medicineplatform.extract.Extract;
import kz.iitu.medicineplatform.sample.PlantSample;

public record ActivityMeasurementResponse(
        Long id,
        Long compoundId,
        ExtractResponse extract,
        AssayResponse assay,
        SourceResponse source,
        BigDecimal value,
        String unit,
        String relation,
        String experimentalConditions,
        String qualityStatus,
        OffsetDateTime createdAt
) {
    public static ActivityMeasurementResponse from(ActivityMeasurement measurement) {
        return new ActivityMeasurementResponse(
                measurement.getId(), measurement.getCompound() == null ? null : measurement.getCompound().getId(),
                measurement.getExtract() == null ? null : ExtractResponse.from(measurement.getExtract()),
                AssayResponse.from(measurement.getAssay()),
                SourceResponse.from(measurement.getSource()), measurement.getValue(), measurement.getUnit(),
                measurement.getRelation(), measurement.getExperimentalConditions(), measurement.getQualityStatus(),
                measurement.getCreatedAt());
    }

    public record ExtractResponse(Long id, String method, String solvent, String conditions, String batchIdentifier,
                                  SampleResponse sample) {
        static ExtractResponse from(Extract extract) {
            return new ExtractResponse(extract.getId(), extract.getExtractionMethod(), extract.getSolvent(),
                    extract.getConditions(), extract.getBatchIdentifier(), SampleResponse.from(extract.getSample()));
        }
    }

    public record SampleResponse(Long id, Long plantId, String plantScientificName, String plantPart, String origin,
                                 String batchIdentifier) {
        static SampleResponse from(PlantSample sample) {
            return new SampleResponse(sample.getId(), sample.getPlant().getId(), sample.getPlant().getScientificName(),
                    sample.getPlantPart(), sample.getOrigin(), sample.getBatchIdentifier());
        }
    }
}
