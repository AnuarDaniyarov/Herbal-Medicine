package kz.iitu.medicineplatform.prediction;

public interface PredictionService {
    PredictionResponse predict(PredictionRequest request);
    PredictionResponse findById(Long id);
}
