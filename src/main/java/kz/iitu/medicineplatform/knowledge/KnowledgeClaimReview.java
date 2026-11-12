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
import kz.iitu.medicineplatform.auth.UserAccount;

@Entity
@Table(name = "knowledge_claim_reviews")
public class KnowledgeClaimReview {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claim_id", nullable = false)
    private KnowledgeClaim claim;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private UserAccount reviewer;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false, length = 30)
    private ReviewStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 30)
    private ReviewStatus newStatus;

    @Column(name = "review_note", columnDefinition = "text")
    private String reviewNote;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected KnowledgeClaimReview() { }

    public KnowledgeClaimReview(KnowledgeClaim claim, UserAccount reviewer, ReviewStatus previousStatus,
                                ReviewStatus newStatus, String reviewNote) {
        this.claim = claim;
        this.reviewer = reviewer;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.reviewNote = reviewNote;
    }

    @PrePersist
    void onCreate() { createdAt = OffsetDateTime.now(ZoneOffset.UTC); }
}
