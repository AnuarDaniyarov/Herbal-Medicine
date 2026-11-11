package kz.iitu.medicineplatform.activity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "assays")
public class Assay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_code", nullable = false, unique = true, length = 100)
    private String activityCode;

    @Column(nullable = false, length = 250)
    private String name;

    @Column(name = "target_name", length = 250)
    private String targetName;

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "target_id")
    private Target target;

    @Column(name = "assay_type", length = 100)
    private String assayType;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "standard_unit", length = 50)
    private String standardUnit;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Assay() {
    }

    public Assay(String activityCode, String name, String targetName, String assayType, String description,
                 String standardUnit) {
        this.activityCode = activityCode;
        this.name = name;
        this.targetName = targetName;
        this.assayType = assayType;
        this.description = description;
        this.standardUnit = standardUnit;
    }

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public Long getId() { return id; }
    public String getActivityCode() { return activityCode; }
    public String getName() { return name; }
    public String getTargetName() { return targetName; }
    public Target getTarget() { return target; }
    public String getAssayType() { return assayType; }
    public String getDescription() { return description; }
    public String getStandardUnit() { return standardUnit; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
