package kz.iitu.medicineplatform.prediction;

import java.time.OffsetDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "model_versions", uniqueConstraints = @UniqueConstraint(
        name = "uq_model_versions_name_version", columnNames = {"name", "version"}))
public class ModelVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 100)
    private String version;

    @Column(name = "activity_code", nullable = false, length = 100)
    private String activityCode;

    @Column(name = "execution_type", nullable = false, length = 30)
    private String executionType;

    @Column(name = "training_data_description", columnDefinition = "text")
    private String trainingDataDescription;

    @Column(name = "metrics_json", columnDefinition = "text")
    private String metricsJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    protected ModelVersion() {
    }

    public ModelVersion(String name, String version, String activityCode, String executionType,
                        String trainingDataDescription, String metricsJson, boolean active) {
        this.name = name;
        this.version = version;
        this.activityCode = activityCode;
        this.executionType = executionType;
        this.trainingDataDescription = trainingDataDescription;
        this.metricsJson = metricsJson;
        this.active = active;
        this.createdAt = OffsetDateTime.now(java.time.ZoneOffset.UTC);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getVersion() { return version; }
    public String getActivityCode() { return activityCode; }
    public String getExecutionType() { return executionType; }
    public String getTrainingDataDescription() { return trainingDataDescription; }
    public String getMetricsJson() { return metricsJson; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public boolean isActive() { return active; }
}
