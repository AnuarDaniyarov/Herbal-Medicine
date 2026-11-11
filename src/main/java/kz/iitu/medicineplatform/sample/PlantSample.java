package kz.iitu.medicineplatform.sample;

import java.time.LocalDate;
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
import kz.iitu.medicineplatform.plant.Plant;

@Entity
@Table(name = "plant_samples")
public class PlantSample {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @Column(name = "plant_part", length = 250)
    private String plantPart;
    @Column(length = 500)
    private String origin;
    @Column(name = "batch_identifier", length = 250)
    private String batchIdentifier;
    @Column(name = "collected_on")
    private LocalDate collectedOn;
    @Column(columnDefinition = "text")
    private String metadata;
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected PlantSample() { }

    public PlantSample(Plant plant, String plantPart, String origin, String batchIdentifier,
                       LocalDate collectedOn, String metadata) {
        this.plant = plant;
        this.plantPart = plantPart;
        this.origin = origin;
        this.batchIdentifier = batchIdentifier;
        this.collectedOn = collectedOn;
        this.metadata = metadata;
    }

    @PrePersist void onCreate() { createdAt = OffsetDateTime.now(java.time.ZoneOffset.UTC); }
    public Long getId() { return id; }
    public Plant getPlant() { return plant; }
    public String getPlantPart() { return plantPart; }
    public String getOrigin() { return origin; }
    public String getBatchIdentifier() { return batchIdentifier; }
    public LocalDate getCollectedOn() { return collectedOn; }
    public String getMetadata() { return metadata; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
