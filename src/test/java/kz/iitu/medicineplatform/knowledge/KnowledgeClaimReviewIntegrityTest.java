package kz.iitu.medicineplatform.knowledge;

import kz.iitu.medicineplatform.auth.UserAccount;
import kz.iitu.medicineplatform.auth.UserAccountRepository;
import kz.iitu.medicineplatform.auth.UserRole;
import kz.iitu.medicineplatform.plant.Plant;
import kz.iitu.medicineplatform.plant.PlantRepository;
import kz.iitu.medicineplatform.source.DataSource;
import kz.iitu.medicineplatform.source.DataSourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class KnowledgeClaimReviewIntegrityTest {

    @Autowired private KnowledgeClaimService claimService;
    @Autowired private KnowledgeClaimRepository claimRepository;
    @Autowired private PlantRepository plantRepository;
    @Autowired private DataSourceRepository sourceRepository;
    @Autowired private UserAccountRepository userRepository;
    @Autowired private KnowledgeClaimRevisionRepository revisionRepository;
    @Autowired private KnowledgeClaimReviewRepository reviewRepository;

    private Plant testPlant;
    private DataSource testSource;
    private UserAccount author;
    private UserAccount reviewer;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        claimRepository.deleteAll();
        revisionRepository.deleteAll();
        plantRepository.deleteAll();
        sourceRepository.deleteAll();
        userRepository.deleteAll();

        testPlant = plantRepository.save(new Plant("Hypericum perforatum", "St John's Wort", null, null));
        testSource = sourceRepository.save(new DataSource("WHO Monograph", "WHO", 2009, "10.1234/who",
                "https://who.int", "MONOGRAPH", OffsetDateTime.now(ZoneOffset.UTC)));

        author = userRepository.save(new UserAccount("author-" + UUID.randomUUID() + "@example.test", "Author", "hash", UserRole.EXPERT));
        reviewer = userRepository.save(new UserAccount("reviewer-" + UUID.randomUUID() + "@example.test", "Reviewer", "hash", UserRole.EXPERT));
    }

    @Test
    @DisplayName("R25: Editing verified claim text without review status must reset to PENDING")
    void editingVerifiedClaimResetsStatusToPending() {
        // Create and verify claim
        KnowledgeClaim claim = new KnowledgeClaim(testPlant, null, ClaimType.INDICATION,
                "Original title", "Original claim text", null, testSource, "p. 1", author);
        claim.review(reviewer, ReviewStatus.VERIFIED, "Initial verification");
        claim = claimRepository.save(claim);
        assertEquals(ReviewStatus.VERIFIED, claim.getReviewStatus());

        // Now edit text without passing reviewStatus
        ClaimReviewRequest editRequest = new ClaimReviewRequest(null, null, "Edited title", "Edited text", null, "p. 2");
        KnowledgeClaimResponse updated = claimService.updateClaim(claim.getId(), editRequest, author);

        assertEquals(ReviewStatus.PENDING, updated.reviewStatus());
        KnowledgeClaim saved = claimRepository.findById(claim.getId()).orElseThrow();
        assertEquals(ReviewStatus.PENDING, saved.getReviewStatus());
        assertNull(saved.getReviewedBy());
        assertNull(saved.getReviewedAt());
        assertEquals(1, revisionRepository.countByClaim_Id(claim.getId()));
    }

    @Test
    @DisplayName("R25 / D4: Four-eyes rule blocks author from self-reviewing their own claim")
    void authorCannotReviewOwnClaim() {
        KnowledgeClaim claim = new KnowledgeClaim(testPlant, null, ClaimType.INDICATION,
                "Author's claim", "Some claim text", null, testSource, "p. 1", author);
        final KnowledgeClaim savedClaim = claimRepository.save(claim);

        ClaimReviewRequest reviewRequest = new ClaimReviewRequest(ReviewStatus.VERIFIED, "Self-approved");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                claimService.review(savedClaim.getId(), reviewRequest, author));

        assertEquals(403, exception.getStatusCode().value());
        assertEquals("SELF_REVIEW_FORBIDDEN", exception.getReason());
    }

    @Test
    @DisplayName("Claim verification records the reviewer decision")
    void reviewCreatesAuditEntry() {
        KnowledgeClaim claim = claimRepository.save(new KnowledgeClaim(testPlant, null, ClaimType.INDICATION,
                "Reviewed title", "Reviewed claim text", null, testSource, "p. 1", author));

        claimService.review(claim.getId(), new ClaimReviewRequest(ReviewStatus.VERIFIED, "Source checked"), reviewer);

        var reviewEvent = reviewRepository.findAll().get(0);
        assertEquals(claim.getId(), reviewEvent.getClaim().getId());
        assertEquals(ReviewStatus.VERIFIED, reviewEvent.getNewStatus());
        assertEquals(reviewer.getId(), reviewEvent.getReviewer().getId());
    }
}
