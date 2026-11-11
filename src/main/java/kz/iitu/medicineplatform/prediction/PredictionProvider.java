package kz.iitu.medicineplatform.prediction;

public interface PredictionProvider {
    PredictionResult evaluate(String smiles, String activityCode, ModelVersion modelVersion);
}
