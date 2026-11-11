package kz.iitu.medicineplatform.prediction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import kz.iitu.medicineplatform.compound.Compound;

@Entity
@Table(name = "predictions")
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id")
    private Compound compound;

    @Column(name = "input_smiles", nullable = false, length = 2000)
    private String inputSmiles;

    @Column(name = "activity_code", nullable = false, length = 100)
    private String activityCode;

    @Column(name = "predicted_label", length = 100)
    private String predictedLabel;

    @Column(name = "predicted_value", precision = 18, scale = 8)
    private BigDecimal predictedValue;

    @Column(precision = 8, scale = 7)
    private BigDecimal probability;

    @Column(precision = 18, scale = 8)
    private BigDecimal uncertainty;

    @Column(name = "confidence_level", length = 30)
    private String confidenceLevel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_version_id", nullable = false)
    private ModelVersion modelVersion;

    @Column(nullable = false, length = 2000)
    private String warning;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Prediction() {
    }

    public Prediction(Compound compound, String inputSmiles, String activityCode, ModelVersion modelVersion,
                      String warning, String status) {
        this.compound = compound;
        this.inputSmiles = inputSmiles;
        this.activityCode = activityCode;
        this.modelVersion = modelVersion;
        this.warning = warning;
        this.status = status;
    }

    public void setResult(PredictionResult result) {
        this.predictedLabel = result.label();
        this.predictedValue = result.value();
        this.probability = result.probability();
        this.uncertainty = result.uncertainty();
        this.confidenceLevel = result.confidenceLevel();
        this.warning = result.warning();
        this.status = result.status();
    }

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public Long getId() { return id; }
    public Compound getCompound() { return compound; }
    public String getInputSmiles() { return inputSmiles; }
    public String getActivityCode() { return activityCode; }
    public String getPredictedLabel() { return predictedLabel; }
    public BigDecimal getPredictedValue() { return predictedValue; }
    public BigDecimal getProbability() { return probability; }
    public BigDecimal getUncertainty() { return uncertainty; }
    public String getConfidenceLevel() { return confidenceLevel; }
    public ModelVersion getModelVersion() { return modelVersion; }
    public String getWarning() { return warning; }
    public String getStatus() { return status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
