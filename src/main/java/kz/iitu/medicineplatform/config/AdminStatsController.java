package kz.iitu.medicineplatform.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaimRepository;
import kz.iitu.medicineplatform.knowledge.ReviewStatus;
import kz.iitu.medicineplatform.plant.PlantRepository;
import kz.iitu.medicineplatform.compound.CompoundRepository;
import kz.iitu.medicineplatform.preparation.PreparationRepository;
import kz.iitu.medicineplatform.source.DataSourceRepository;
import kz.iitu.medicineplatform.auth.UserAccountRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {
    private final AuditLoggingFilter auditFilter;
    private final PlantRepository plantRepository;
    private final CompoundRepository compoundRepository;
    private final PreparationRepository preparationRepository;
    private final KnowledgeClaimRepository claimRepository;
    private final DataSourceRepository sourceRepository;
    private final UserAccountRepository userRepository;

    public AdminStatsController(AuditLoggingFilter auditFilter,
                                 PlantRepository plantRepository,
                                 CompoundRepository compoundRepository,
                                 PreparationRepository preparationRepository,
                                 KnowledgeClaimRepository claimRepository,
                                 DataSourceRepository sourceRepository,
                                 UserAccountRepository userRepository) {
        this.auditFilter = auditFilter;
        this.plantRepository = plantRepository;
        this.compoundRepository = compoundRepository;
        this.preparationRepository = preparationRepository;
        this.claimRepository = claimRepository;
        this.sourceRepository = sourceRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // Database stats
        stats.put("plants", plantRepository.count());
        stats.put("compounds", compoundRepository.count());
        stats.put("preparations", preparationRepository.count());
        stats.put("sources", sourceRepository.count());
        stats.put("users", userRepository.count());

        // Claims by status
        Map<String, Long> claimsByStatus = claimRepository.groupByReviewStatus()
                .entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().name(),
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
        stats.put("claimsByStatus", claimsByStatus);
        stats.put("totalClaims", claimRepository.count());

        // Request stats
        stats.put("totalRequests", auditFilter.getTotalRequests());
        stats.put("averageLatencyMs", auditFilter.getAverageLatencyMs());
        stats.put("maxLatencyMs", auditFilter.getMaxLatencyMs());

        // Top endpoints by request count
        Map<String, Long> endpointCounts = auditFilter.getEndpointCounts();
        var topEndpoints = endpointCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(20)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
        stats.put("topEndpoints", topEndpoints);

        return stats;
    }
}
