package kz.iitu.medicineplatform.plant;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "plants")
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scientific_name", nullable = false, unique = true, length = 250)
    private String scientificName;

    @Column(name = "display_name", nullable = false, length = 250)
    private String displayName;

    @Column(length = 250)
    private String family;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "name_ru", length = 250)
    private String nameRu;

    @Column(name = "name_kk", length = 250)
    private String nameKk;

    @Column(columnDefinition = "text")
    private String synonyms;

    @Column(name = "distribution_regions", columnDefinition = "text")
    private String distributionRegions;

    @Column(name = "used_parts", columnDefinition = "text")
    private String usedParts;

    @Column(name = "review_status", nullable = false, length = 30)
    private String reviewStatus = "UNVERIFIED";

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Plant() {
    }

    public Plant(String scientificName, String displayName, String family, String description) {
        this.scientificName = scientificName;
        this.displayName = displayName;
        this.family = family;
        this.description = description;
    }

    public Plant(String scientificName, String displayName, String family, String description, String nameRu,
                 String nameKk, String synonyms, String distributionRegions, String usedParts, String reviewStatus) {
        this(scientificName, displayName, family, description);
        this.nameRu = nameRu;
        this.nameKk = nameKk;
        this.synonyms = synonyms;
        this.distributionRegions = distributionRegions;
        this.usedParts = usedParts;
        this.reviewStatus = reviewStatus == null ? "UNVERIFIED" : reviewStatus;
    }

    public Long getId() { return id; }
    public String getScientificName() { return scientificName; }
    public String getDisplayName() { return displayName; }
    public String getFamily() { return family; }
    public String getDescription() { return description; }
    public String getNameRu() { return nameRu; }
    public String getNameKk() { return nameKk; }
    public String getSynonyms() { return synonyms; }
    public String getDistributionRegions() { return distributionRegions; }
    public String getUsedParts() { return usedParts; }
    public String getReviewStatus() { return reviewStatus; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
