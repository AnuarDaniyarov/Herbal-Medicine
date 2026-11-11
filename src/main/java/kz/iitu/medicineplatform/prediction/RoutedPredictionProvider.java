package kz.iitu.medicineplatform.prediction;

public class RoutedPredictionProvider implements PredictionProvider {
    private final DemoPredictionProvider demoProvider;
    private final AChEBaselinePredictionProvider baselineProvider;

    public RoutedPredictionProvider(DemoPredictionProvider demoProvider,
                                   AChEBaselinePredictionProvider baselineProvider) {
        this.demoProvider = demoProvider;
        this.baselineProvider = baselineProvider;
    }

    @Override
    public PredictionResult evaluate(String smiles, String activityCode, ModelVersion modelVersion) {
        if ("ML_BASELINE".equalsIgnoreCase(modelVersion.getExecutionType())) {
            return baselineProvider.evaluate(smiles, activityCode, modelVersion);
        }
        return demoProvider.evaluate(smiles, activityCode, modelVersion);
    }
}
