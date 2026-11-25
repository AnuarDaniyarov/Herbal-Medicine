package kz.iitu.medicineplatform.knowledge;

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
import kz.iitu.medicineplatform.auth.UserAccount;

@Entity
@Table(name = "knowledge_claim_revisions")
public class KnowledgeClaimRevision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claim_id", nullable = false)
    private KnowledgeClaim claim;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "editor_id", nullable = false)
    private UserAccount editor;

    @Column(name = "before_title", length = 500)
    private String beforeTitle;
    @Column(name = "after_title", length = 500)
    private String afterTitle;
    @Column(name = "before_claim_text", columnDefinition = "text")
    private String beforeClaimText;
    @Column(name = "after_claim_text", columnDefinition = "text")
    private String afterClaimText;
    @Column(name = "before_interaction_partner", length = 500)
    private String beforeInteractionPartner;
    @Column(name = "after_interaction_partner", length = 500)
    private String afterInteractionPartner;
    @Column(name = "before_source_locator", length = 500)
    private String beforeSourceLocator;
    @Column(name = "after_source_locator", length = 500)
    private String afterSourceLocator;
    @Column(name = "edited_at", nullable = false, updatable = false)
    private OffsetDateTime editedAt;

    protected KnowledgeClaimRevision() {
    }

    public KnowledgeClaimRevision(KnowledgeClaim claim, UserAccount editor,
                                  String beforeTitle, String afterTitle,
                                  String beforeClaimText, String afterClaimText,
                                  String beforeInteractionPartner, String afterInteractionPartner,
                                  String beforeSourceLocator, String afterSourceLocator) {
        this.claim = claim;
        this.editor = editor;
        this.beforeTitle = beforeTitle;
        this.afterTitle = afterTitle;
        this.beforeClaimText = beforeClaimText;
        this.afterClaimText = afterClaimText;
        this.beforeInteractionPartner = beforeInteractionPartner;
        this.afterInteractionPartner = afterInteractionPartner;
        this.beforeSourceLocator = beforeSourceLocator;
        this.afterSourceLocator = afterSourceLocator;
    }

    @PrePersist
    void onCreate() {
        if (editedAt == null) editedAt = OffsetDateTime.now();
    }
}
