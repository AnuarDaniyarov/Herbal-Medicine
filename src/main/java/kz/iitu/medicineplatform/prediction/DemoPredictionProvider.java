package kz.iitu.medicineplatform.prediction;

public class DemoPredictionProvider implements PredictionProvider {
    private static final String WARNING =
            "DEMO ONLY: no inference model is connected for this activity. This is not a biological prediction or medical advice.";

    @Override
    public PredictionResult evaluate(String smiles, String activityCode, ModelVersion modelVersion) {
        return new PredictionResult(null, null, null, null, null, "DEMO", WARNING);
    }
}
