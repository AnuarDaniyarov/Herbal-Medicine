package kz.iitu.medicineplatform.prediction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PredictionResponse(
        Long predictionId,
        String status,
        String activityCode,
        String inputSmiles,
        String predictedLabel,
        BigDecimal predictedValue,
        BigDecimal probability,
        String confidenceLevel,
        String modelName,
        String modelVersion,
        boolean isDemo,
        BigDecimal uncertainty,
        String modelExecutionType,
        String modelDatasetDescription,
        String modelMetrics,
        String warning,
        OffsetDateTime createdAt
) {
    public static PredictionResponse from(Prediction prediction) {
        return new PredictionResponse(
                prediction.getId(), prediction.getStatus(), prediction.getActivityCode(), prediction.getInputSmiles(),
                prediction.getPredictedLabel(), prediction.getPredictedValue(), prediction.getProbability(),
                prediction.getConfidenceLevel(), prediction.getModelVersion().getName(),
                prediction.getModelVersion().getVersion(),
                "DEMO".equalsIgnoreCase(prediction.getStatus())
                        || "STUB".equalsIgnoreCase(prediction.getStatus())
                        || "STUB".equalsIgnoreCase(prediction.getModelVersion().getExecutionType())
                        || "DEMO".equalsIgnoreCase(prediction.getModelVersion().getExecutionType()),
                prediction.getUncertainty(), prediction.getModelVersion().getExecutionType(),
                prediction.getModelVersion().getTrainingDataDescription(), prediction.getModelVersion().getMetricsJson(),
                prediction.getWarning(), prediction.getCreatedAt());
    }
}
