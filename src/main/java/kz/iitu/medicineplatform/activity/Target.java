package kz.iitu.medicineplatform.activity;

import java.time.OffsetDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "targets")
public class Target {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "normalized_identifier", nullable = false, unique = true, length = 200)
    private String normalizedIdentifier;
    @Column(nullable = false, length = 250)
    private String name;
    @Column(length = 250)
    private String organism;
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Target() { }
    public Target(String normalizedIdentifier, String name, String organism) {
        this.normalizedIdentifier = normalizedIdentifier;
        this.name = name;
        this.organism = organism;
    }
    @PrePersist void onCreate() { createdAt = OffsetDateTime.now(java.time.ZoneOffset.UTC); }
    public Long getId() { return id; }
    public String getNormalizedIdentifier() { return normalizedIdentifier; }
    public String getName() { return name; }
    public String getOrganism() { return organism; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
