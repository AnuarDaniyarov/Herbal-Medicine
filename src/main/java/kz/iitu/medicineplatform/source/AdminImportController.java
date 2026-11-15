package kz.iitu.medicineplatform.source;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kz.iitu.medicineplatform.knowledge.ClaimType;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaim;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaimRepository;
import kz.iitu.medicineplatform.knowledge.ReviewStatus;
import kz.iitu.medicineplatform.plant.Plant;
import kz.iitu.medicineplatform.plant.PlantRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/import")
public class AdminImportController {
    private final PlantRepository plantRepository;
    private final KnowledgeClaimRepository claimRepository;
    private final DataSourceRepository sourceRepository;

    public AdminImportController(PlantRepository plantRepository,
                                  KnowledgeClaimRepository claimRepository,
                                  DataSourceRepository sourceRepository) {
        this.plantRepository = plantRepository;
        this.claimRepository = claimRepository;
        this.sourceRepository = sourceRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/plants")
    public Map<String, Object> importPlants(@RequestBody List<PlantImportRequest> plants) {
        int created = 0;
        int updated = 0;
        List<String> errors = new ArrayList<>();

        for (PlantImportRequest req : plants) {
            try {
                Plant existing = plantRepository.findByScientificName(req.scientificName()).orElse(null);
                if (existing != null) {
                    existing.setDisplayName(req.displayName());
                    existing.setFamily(req.family());
                    existing.setDescription(req.description());
                    existing.setNameRu(req.nameRu());
                    existing.setNameKk(req.nameKk());
                    existing.setSynonyms(req.synonyms());
                    existing.setDistributionRegions(req.distributionRegions());
                    existing.setUsedParts(req.usedParts());
                    if (req.reviewStatus() != null) {
                        existing.setReviewStatus(req.reviewStatus().name());
                    }
                    plantRepository.save(existing);
                    updated++;
                } else {
                    Plant plant = new Plant(req.scientificName, req.displayName(), req.family(), req.description(),
                            req.nameRu(), req.nameKk(), req.synonyms(), req.distributionRegions(), req.usedParts(),
                            req.reviewStatus() != null ? req.reviewStatus().name() : "UNVERIFIED");
                    plantRepository.save(plant);
                    created++;
                }
            } catch (Exception e) {
                errors.add("Failed to import plant " + req.scientificName() + ": " + e.getMessage());
            }
        }

        return Map.of(
                "created", created,
                "updated", updated,
                "errors", errors
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/claims")
    public Map<String, Object> importClaims(@RequestBody List<ClaimImportRequest> claims) {
        int created = 0;
        List<String> errors = new ArrayList<>();

        for (ClaimImportRequest req : claims) {
            try {
                // Find plant
                Plant plant = null;
                if (req.plantScientificName() != null) {
                    plant = plantRepository.findByScientificName(req.plantScientificName()).orElse(null);
                    if (plant == null) {
                        errors.add("Plant not found: " + req.plantScientificName());
                        continue;
                    }
                }

                // Find source
                DataSource source = null;
                if (req.sourceId() != null) {
                    source = sourceRepository.findById(req.sourceId()).orElse(null);
                    if (source == null) {
                        errors.add("Source not found: " + req.sourceId());
                        continue;
                    }
                }

                KnowledgeClaim claim = new KnowledgeClaim(plant, null, req.claimType(), req.title(), req.claimText(),
                        req.interactionPartner(), source, req.sourceLocator(), null);
                if (req.reviewStatus() != null) {
                    claim.setReviewStatus(req.reviewStatus());
                }
                claimRepository.save(claim);
                created++;
            } catch (Exception e) {
                errors.add("Failed to import claim: " + e.getMessage());
            }
        }

        return Map.of(
                "created", created,
                "errors", errors
        );
    }

    public record PlantImportRequest(
            String scientificName,
            String displayName,
            String family,
            String description,
            String nameRu,
            String nameKk,
            String synonyms,
            String distributionRegions,
            String usedParts,
            ReviewStatus reviewStatus
    ) { }

    public record ClaimImportRequest(
            String plantScientificName,
            Long sourceId,
            ClaimType claimType,
            String title,
            String claimText,
            String interactionPartner,
            String sourceLocator,
            ReviewStatus reviewStatus
    ) { }
}
