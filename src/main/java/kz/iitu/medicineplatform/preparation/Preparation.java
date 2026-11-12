package kz.iitu.medicineplatform.preparation;

import java.time.OffsetDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import kz.iitu.medicineplatform.knowledge.ReviewStatus;
import kz.iitu.medicineplatform.source.DataSource;
import java.time.ZoneOffset;

@Entity
@Table(name = "preparations")
public class Preparation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String name;

    @Column(name = "dosage_form", length = 200)
    private String dosageForm;

    @Column(length = 500)
    private String manufacturer;

    @Column(name = "registration_number", unique = true, length = 250)
    private String registrationNumber;

    @Column(name = "registration_country", length = 100)
    private String registrationCountry;

    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private DataSource source;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", nullable = false, length = 30)
    private ReviewStatus reviewStatus = ReviewStatus.UNVERIFIED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Preparation() { }

    public Preparation(String name, String dosageForm, String manufacturer, String registrationNumber,
                       String registrationCountry, String description, DataSource source) {
        this.name = name;
        this.dosageForm = dosageForm;
        this.manufacturer = manufacturer;
        this.registrationNumber = registrationNumber;
        this.registrationCountry = registrationCountry;
        this.description = description;
        this.source = source;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDosageForm() { return dosageForm; }
    public String getManufacturer() { return manufacturer; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getRegistrationCountry() { return registrationCountry; }
    public String getDescription() { return description; }
    public DataSource getSource() { return source; }
    public ReviewStatus getReviewStatus() { return reviewStatus; }

    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() { updatedAt = OffsetDateTime.now(ZoneOffset.UTC); }
}
