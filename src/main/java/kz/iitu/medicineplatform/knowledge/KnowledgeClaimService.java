package kz.iitu.medicineplatform.knowledge;

import java.util.List;
import kz.iitu.medicineplatform.api.PageResponse;
import kz.iitu.medicineplatform.auth.UserAccount;
import kz.iitu.medicineplatform.plant.Plant;
import kz.iitu.medicineplatform.plant.PlantRepository;
import kz.iitu.medicineplatform.preparation.Preparation;
import kz.iitu.medicineplatform.preparation.PreparationRepository;
import kz.iitu.medicineplatform.source.DataSource;
import kz.iitu.medicineplatform.source.DataSourceRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.jdbc.core.JdbcTemplate;

@Service
@Transactional(readOnly = true)
public class KnowledgeClaimService {
    private final KnowledgeClaimRepository claims;
    private final KnowledgeClaimReviewRepository reviews;
    private final PlantRepository plants;
    private final PreparationRepository preparations;
    private final DataSourceRepository sources;
    private final KnowledgeClaimRevisionRepository revisions;
    private final JdbcTemplate jdbcTemplate;

    public KnowledgeClaimService(KnowledgeClaimRepository claims, KnowledgeClaimReviewRepository reviews,
                                 PlantRepository plants, PreparationRepository preparations,
                                 DataSourceRepository sources, KnowledgeClaimRevisionRepository revisions,
                                 JdbcTemplate jdbcTemplate) {
        this.claims = claims;
        this.reviews = reviews;
        this.plants = plants;
        this.preparations = preparations;
        this.sources = sources;
        this.revisions = revisions;
        this.jdbcTemplate = jdbcTemplate;
    }

    public PageResponse<KnowledgeClaimResponse> verifiedForPlant(long plantId, ClaimType type, Pageable pageable) {
        if (!plants.existsById(plantId)) throw notFound("Plant");
        var result = type == null
                ? claims.findByPlant_IdAndReviewStatus(plantId, ReviewStatus.VERIFIED, pageable)
                : claims.findByPlant_IdAndReviewStatusAndClaimType(plantId, ReviewStatus.VERIFIED, type, pageable);
        return PageResponse.from(result.map(KnowledgeClaimResponse::from));
    }

    public PageResponse<KnowledgeClaimResponse> verifiedForPreparation(long preparationId, ClaimType type, Pageable pageable) {
        if (!preparations.existsById(preparationId)) throw notFound("Preparation");
        var result = type == null
                ? claims.findByPreparation_IdAndReviewStatus(preparationId, ReviewStatus.VERIFIED, pageable)
                : claims.findByPreparation_IdAndReviewStatusAndClaimType(preparationId, ReviewStatus.VERIFIED, type, pageable);
        return PageResponse.from(result.map(KnowledgeClaimResponse::from));
    }

    public PageResponse<KnowledgeClaimResponse> reviewQueue(Pageable pageable) {
        return PageResponse.from(claims.findByReviewStatusIn(List.of(ReviewStatus.UNVERIFIED, ReviewStatus.PENDING), pageable)
                .map(KnowledgeClaimResponse::from));
    }

    @Transactional
    public KnowledgeClaimResponse submit(ClaimSubmissionRequest request, UserAccount author) {
        boolean hasPlant = request.plantId() != null;
        boolean hasPreparation = request.preparationId() != null;
        if (hasPlant == hasPreparation) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide exactly one plantId or preparationId");
        }
        if (request.claimType() == ClaimType.INTERACTION
                && (request.interactionPartner() == null || request.interactionPartner().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An interaction partner is required");
        }
        Plant plant = hasPlant ? plants.findById(request.plantId()).orElseThrow(() -> notFound("Plant")) : null;
        Preparation preparation = hasPreparation
                ? preparations.findById(request.preparationId()).orElseThrow(() -> notFound("Preparation")) : null;
        DataSource source = sources.findById(request.sourceId()).orElseThrow(() -> notFound("Source"));
        KnowledgeClaim saved = claims.save(new KnowledgeClaim(plant, preparation, request.claimType(),
                request.title().trim(), request.claimText().trim(), normalize(request.interactionPartner()),
                source, normalize(request.sourceLocator()), author));
        return KnowledgeClaimResponse.from(saved);
    }

    @Transactional
    public KnowledgeClaimResponse review(long id, ClaimReviewRequest request, UserAccount reviewer) {
        if (request.reviewStatus() != ReviewStatus.VERIFIED && request.reviewStatus() != ReviewStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review status must be VERIFIED or REJECTED");
        }
        if (hasClaimEdits(request)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Edit claim data separately before reviewing it");
        }
        if (reviewer == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        KnowledgeClaim claim = claims.findById(id).orElseThrow(() -> notFound("Claim"));
        ReviewStatus previous = claim.getReviewStatus();

        if (claim.getCreatedBy() != null && reviewer != null
                && claim.getCreatedBy().getId().equals(reviewer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "SELF_REVIEW_FORBIDDEN");
        }

        claim.review(reviewer, request.reviewStatus(), normalize(request.reviewNote()));
        reviews.save(new KnowledgeClaimReview(claim, reviewer, previous, request.reviewStatus(), normalize(request.reviewNote())));
        return KnowledgeClaimResponse.from(claim);
    }

    @Transactional
    public KnowledgeClaimResponse updateClaim(long id, ClaimReviewRequest request, UserAccount editor) {
        if (request.reviewStatus() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Claim edits and review status changes must use separate requests");
        }
        if (editor == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        KnowledgeClaim claim = claims.findById(id).orElseThrow(() -> notFound("Claim"));
        String oldTitle = claim.getTitle();
        String oldClaimText = claim.getClaimText();
        String oldInteractionPartner = claim.getInteractionPartner();
        String oldSourceLocator = claim.getSourceLocator();
        ReviewStatus oldStatus = claim.getReviewStatus();
        claim.updateData(request.title(), request.claimText(), request.interactionPartner(), request.sourceLocator());
        boolean changed = !java.util.Objects.equals(oldTitle, claim.getTitle())
                || !java.util.Objects.equals(oldClaimText, claim.getClaimText())
                || !java.util.Objects.equals(oldInteractionPartner, claim.getInteractionPartner())
                || !java.util.Objects.equals(oldSourceLocator, claim.getSourceLocator());
        if (changed) {
            revisions.save(new KnowledgeClaimRevision(claim, editor, oldTitle, claim.getTitle(),
                    oldClaimText, claim.getClaimText(), oldInteractionPartner, claim.getInteractionPartner(),
                    oldSourceLocator, claim.getSourceLocator()));
        }
        if (changed) {
            if (oldStatus == ReviewStatus.VERIFIED) {
                claim.resetVerification();
            }
            clearEmbedding(id);
        }
        return KnowledgeClaimResponse.from(claim);
    }

    private boolean hasClaimEdits(ClaimReviewRequest request) {
        return request.title() != null || request.claimText() != null
                || request.interactionPartner() != null || request.sourceLocator() != null;
    }

    private void clearEmbedding(long claimId) {
        Integer columns = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM information_schema.columns
                WHERE lower(table_name) = 'knowledge_claims'
                  AND lower(column_name) IN ('embedding', 'embedding_model')
                """, Integer.class);
        if (columns != null && columns == 2) {
            jdbcTemplate.update("UPDATE knowledge_claims SET embedding = NULL, embedding_model = NULL WHERE id = ?",
                    claimId);
        }
    }

    private String normalize(String value) { return value == null || value.isBlank() ? null : value.trim(); }

    private ResponseStatusException notFound(String type) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, type + " was not found");
    }
}
