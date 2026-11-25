package kz.iitu.medicineplatform.admin;

import io.swagger.v3.oas.annotations.Operation;
import kz.iitu.medicineplatform.auth.UserAccountRepository;
import kz.iitu.medicineplatform.auth.UserRole;
import kz.iitu.medicineplatform.compound.CompoundRepository;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaimRepository;
import kz.iitu.medicineplatform.knowledge.ReviewStatus;
import kz.iitu.medicineplatform.plant.PlantRepository;
import kz.iitu.medicineplatform.preparation.PreparationRepository;
import kz.iitu.medicineplatform.search.EmbeddingIndexer;
import kz.iitu.medicineplatform.source.DataSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final PlantRepository plants;
    private final CompoundRepository compounds;
    private final PreparationRepository preparations;
    private final KnowledgeClaimRepository claims;
    private final DataSourceRepository sources;
    private final UserAccountRepository users;
    private final Optional<EmbeddingIndexer> embeddingIndexer;

    public AdminDashboardController(
            PlantRepository plants,
            CompoundRepository compounds,
            PreparationRepository preparations,
            KnowledgeClaimRepository claims,
            DataSourceRepository sources,
            UserAccountRepository users,
            @Autowired(required = false) Optional<EmbeddingIndexer> embeddingIndexer) {
        this.plants = plants;
        this.compounds = compounds;
        this.preparations = preparations;
        this.claims = claims;
        this.sources = sources;
        this.users = users;
        this.embeddingIndexer = embeddingIndexer != null ? embeddingIndexer : Optional.empty();
    }

    @GetMapping("/stats")
    @Operation(summary = "Get platform overview metrics and database entity counts")
    public PlatformStats stats() {
        Object[] plantCounts = plants.countAndReviewStatus("VERIFIED");
        long totalPlants = ((Number) plantCounts[0]).longValue();
        long verifiedPlants = ((Number) plantCounts[1]).longValue();

        long totalCompounds = compounds.count();
        Object[] preparationCounts = preparations.countAndReviewStatus(ReviewStatus.VERIFIED);
        long totalPreparations = ((Number) preparationCounts[0]).longValue();
        long verifiedPreparations = ((Number) preparationCounts[1]).longValue();

        Map<ReviewStatus, Long> claimCounts = new java.util.EnumMap<>(ReviewStatus.class);
        for (Object[] row : claims.countGroupedByReviewStatus()) {
            claimCounts.put((ReviewStatus) row[0], ((Number) row[1]).longValue());
        }
        long totalClaims = claimCounts.values().stream().mapToLong(Long::longValue).sum();
        long verifiedClaims = claimCounts.getOrDefault(ReviewStatus.VERIFIED, 0L);
        long pendingClaims = claimCounts.getOrDefault(ReviewStatus.PENDING, 0L);
        long unverifiedClaims = claimCounts.getOrDefault(ReviewStatus.UNVERIFIED, 0L);
        long rejectedClaims = claimCounts.getOrDefault(ReviewStatus.REJECTED, 0L);

        long totalSources = sources.count();
        long totalUsers = users.count();

        Map<String, Long> userRoles = new HashMap<>();
        for (UserRole role : UserRole.values()) userRoles.put(role.name(), 0L);
        for (Object[] row : users.countGroupedByRole()) {
            userRoles.put(((UserRole) row[0]).name(), ((Number) row[1]).longValue());
        }

        return new PlatformStats(
                totalPlants,
                verifiedPlants,
                totalCompounds,
                totalPreparations,
                verifiedPreparations,
                totalClaims,
                verifiedClaims,
                pendingClaims,
                unverifiedClaims,
                rejectedClaims,
                totalSources,
                totalUsers,
                userRoles
        );
    }

    @PostMapping("/import/reindex-embeddings")
    @Operation(summary = "Trigger vector embedding computation for verified claims without embeddings")
    public ReindexResponse reindexEmbeddings() {
        if (embeddingIndexer.isEmpty()) {
            return new ReindexResponse(false, 0, "Vector search/indexing is disabled in the current configuration.");
        }
        int indexed = embeddingIndexer.get().indexUnembeddedClaims();
        return new ReindexResponse(true, indexed, "Successfully reindexed " + indexed + " claims.");
    }

    public record PlatformStats(
            long totalPlants,
            long verifiedPlants,
            long totalCompounds,
            long totalPreparations,
            long verifiedPreparations,
            long totalClaims,
            long verifiedClaims,
            long pendingClaims,
            long unverifiedClaims,
            long rejectedClaims,
            long totalSources,
            long totalUsers,
            Map<String, Long> usersByRole
    ) {}

    public record ReindexResponse(
            boolean success,
            int indexedCount,
            String message
    ) {}
}
