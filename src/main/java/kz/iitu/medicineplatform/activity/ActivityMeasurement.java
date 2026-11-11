package kz.iitu.medicineplatform.activity;

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
import kz.iitu.medicineplatform.source.DataSource;
import kz.iitu.medicineplatform.extract.Extract;

@Entity
@Table(name = "activity_measurements")
public class ActivityMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id")
    private Compound compound;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extract_id")
    private Extract extract;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assay_id", nullable = false)
    private Assay assay;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private DataSource source;

    @Column(name = "measurement_value", nullable = false, precision = 38, scale = 12)
    private BigDecimal value;

    @Column(nullable = false, length = 50)
    private String unit;

    @Column(nullable = false, length = 10)
    private String relation;

    @Column(name = "experimental_conditions", columnDefinition = "text")
    private String experimentalConditions;

    @Column(name = "quality_status", nullable = false, length = 30)
    private String qualityStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected ActivityMeasurement() {
    }

    public ActivityMeasurement(Compound compound, Assay assay, DataSource source, BigDecimal value, String unit,
                               String relation, String experimentalConditions, String qualityStatus) {
        if (compound == null) throw new IllegalArgumentException("compound is required for a compound measurement");
        this.compound = compound;
        this.assay = assay;
        this.source = source;
        this.value = value;
        this.unit = unit;
        this.relation = relation;
        this.experimentalConditions = experimentalConditions;
        this.qualityStatus = qualityStatus;
    }

    public ActivityMeasurement(Extract extract, Assay assay, DataSource source, BigDecimal value, String unit,
                               String relation, String experimentalConditions, String qualityStatus) {
        if (extract == null) throw new IllegalArgumentException("extract is required for an extract measurement");
        this.compound = null;
        this.assay = assay;
        this.source = source;
        this.value = value;
        this.unit = unit;
        this.relation = relation;
        this.experimentalConditions = experimentalConditions;
        this.qualityStatus = qualityStatus;
        this.extract = extract;
    }

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public Long getId() { return id; }
    public Compound getCompound() { return compound; }
    public Extract getExtract() { return extract; }
    public Assay getAssay() { return assay; }
    public DataSource getSource() { return source; }
    public BigDecimal getValue() { return value; }
    public String getUnit() { return unit; }
    public String getRelation() { return relation; }
    public String getExperimentalConditions() { return experimentalConditions; }
    public String getQualityStatus() { return qualityStatus; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
