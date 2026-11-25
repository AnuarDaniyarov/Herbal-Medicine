package kz.iitu.medicineplatform.knowledge;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import kz.iitu.medicineplatform.auth.UserAccount;
import kz.iitu.medicineplatform.plant.Plant;
import kz.iitu.medicineplatform.preparation.Preparation;
import kz.iitu.medicineplatform.source.DataSource;

@Entity
@Table(name = "knowledge_claims")
public class KnowledgeClaim {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id")
    private Plant plant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preparation_id")
    private Preparation preparation;

    @Enumerated(EnumType.STRING)
    @Column(name = "claim_type", nullable = false, length = 40)
    private ClaimType claimType;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(name = "claim_text", nullable = false, columnDefinition = "text")
    private String claimText;

    @Column(name = "interaction_partner", length = 500)
    private String interactionPartner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private DataSource source;

    @Column(name = "source_locator", length = 500)
    private String sourceLocator;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", nullable = false, length = 30)
    private ReviewStatus reviewStatus = ReviewStatus.UNVERIFIED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserAccount createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private UserAccount reviewedBy;

    @Column(name = "review_note", columnDefinition = "text")
    private String reviewNote;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "reviewed_at")
    private OffsetDateTime reviewedAt;

    @Transient
    private Object embedding;

    protected KnowledgeClaim() { }

    public KnowledgeClaim(Plant plant, Preparation preparation, ClaimType claimType, String title, String claimText,
                         String interactionPartner, DataSource source, String sourceLocator, UserAccount createdBy) {
        this.plant = plant;
        this.preparation = preparation;
        this.claimType = claimType;
        this.title = title;
        this.claimText = claimText;
        this.interactionPartner = interactionPartner;
        this.source = source;
        this.sourceLocator = sourceLocator;
        this.createdBy = createdBy;
        this.reviewStatus = ReviewStatus.PENDING;
    }

    public void review(UserAccount reviewer, ReviewStatus status, String note) {
        if (status == ReviewStatus.VERIFIED && reviewer == null) {
            throw new IllegalArgumentException("A verified claim requires an assigned reviewer");
        }
        this.reviewStatus = status;
        this.reviewedBy = reviewer;
        this.reviewNote = note;
        this.reviewedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public void resetVerification() {
        this.reviewStatus = ReviewStatus.PENDING;
        this.reviewedBy = null;
        this.reviewedAt = null;
        this.reviewNote = null;
    }

    public void updateData(String title, String claimText, String interactionPartner, String sourceLocator) {
        if (title != null && !title.isBlank()) this.title = title.trim();
        if (claimText != null && !claimText.isBlank()) this.claimText = claimText.trim();
        if (interactionPartner != null) this.interactionPartner = interactionPartner.isBlank() ? null : interactionPartner.trim();
        if (sourceLocator != null) this.sourceLocator = sourceLocator.isBlank() ? null : sourceLocator.trim();
    }

    @PrePersist
    void onCreate() { if (createdAt == null) createdAt = OffsetDateTime.now(ZoneOffset.UTC); }

    public Long getId() { return id; }
    public Plant getPlant() { return plant; }
    public Preparation getPreparation() { return preparation; }
    public ClaimType getClaimType() { return claimType; }
    public String getTitle() { return title; }
    public String getClaimText() { return claimText; }
    public String getInteractionPartner() { return interactionPartner; }
    public DataSource getSource() { return source; }
    public String getSourceLocator() { return sourceLocator; }
    public ReviewStatus getReviewStatus() { return reviewStatus; }
    public UserAccount getCreatedBy() { return createdBy; }
    public UserAccount getReviewedBy() { return reviewedBy; }
    public String getReviewNote() { return reviewNote; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getReviewedAt() { return reviewedAt; }
    public Object getEmbedding() { return embedding; }
    public void setEmbedding(Object embedding) { this.embedding = embedding; }
}
