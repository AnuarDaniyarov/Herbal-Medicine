package kz.iitu.medicineplatform.source;

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
@Table(name = "sources")
public class DataSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String title;

    @Column(length = 1000)
    private String authors;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(unique = true, length = 300)
    private String doi;

    @Column(length = 2000)
    private String url;

    @Column(name = "source_type", nullable = false, length = 50)
    private String sourceType;

    @Column(name = "accessed_at")
    private OffsetDateTime accessedAt;

    @Column(name = "license_note", columnDefinition = "text")
    private String licenseNote;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected DataSource() {
    }

    public DataSource(String title, String authors, Integer publicationYear, String doi, String url, String sourceType,
                      OffsetDateTime accessedAt) {
        this(title, authors, publicationYear, doi, url, sourceType, accessedAt, null);
    }

    public DataSource(String title, String authors, Integer publicationYear, String doi, String url, String sourceType,
                      OffsetDateTime accessedAt, String licenseNote) {
        this.title = title;
        this.authors = authors;
        this.publicationYear = publicationYear;
        this.doi = doi;
        this.url = url;
        this.sourceType = sourceType;
        this.accessedAt = accessedAt;
        this.licenseNote = licenseNote;
    }

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthors() { return authors; }
    public Integer getPublicationYear() { return publicationYear; }
    public String getDoi() { return doi; }
    public String getUrl() { return url; }
    public String getSourceType() { return sourceType; }
    public String getLicenseNote() { return licenseNote; }
    public OffsetDateTime getAccessedAt() { return accessedAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
