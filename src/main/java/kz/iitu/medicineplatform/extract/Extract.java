package kz.iitu.medicineplatform.extract;

import java.time.OffsetDateTime;
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
import kz.iitu.medicineplatform.sample.PlantSample;

@Entity
@Table(name = "extracts")
public class Extract {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sample_id", nullable = false)
    private PlantSample sample;
    @Column(name = "extraction_method", length = 250)
    private String extractionMethod;
    @Column(length = 250)
    private String solvent;
    @Column(columnDefinition = "text")
    private String conditions;
    @Column(name = "batch_identifier", length = 250)
    private String batchIdentifier;
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Extract() { }
    public Extract(PlantSample sample, String extractionMethod, String solvent, String conditions,
                   String batchIdentifier) {
        this.sample = sample;
        this.extractionMethod = extractionMethod;
        this.solvent = solvent;
        this.conditions = conditions;
        this.batchIdentifier = batchIdentifier;
    }
    @PrePersist void onCreate() { createdAt = OffsetDateTime.now(java.time.ZoneOffset.UTC); }
    public Long getId() { return id; }
    public PlantSample getSample() { return sample; }
    public String getExtractionMethod() { return extractionMethod; }
    public String getSolvent() { return solvent; }
    public String getConditions() { return conditions; }
    public String getBatchIdentifier() { return batchIdentifier; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
