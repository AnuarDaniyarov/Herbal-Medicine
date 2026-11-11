package kz.iitu.medicineplatform.prediction;

import java.math.BigDecimal;

public record PredictionResult(String label, BigDecimal value, BigDecimal probability, BigDecimal uncertainty,
                               String confidenceLevel, String status, String warning) { }
