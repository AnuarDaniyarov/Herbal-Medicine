package kz.iitu.medicineplatform.prediction;

public interface PredictionService {
    PredictionResponse predict(PredictionRequest request);
    default PredictionResponse predict(PredictionRequest request, kz.iitu.medicineplatform.auth.UserAccount account) {
        return predict(request);
    }
    PredictionResponse findById(Long id);
    default PredictionResponse findById(Long id, kz.iitu.medicineplatform.auth.UserAccount account) {
        return findById(id);
    }
}
