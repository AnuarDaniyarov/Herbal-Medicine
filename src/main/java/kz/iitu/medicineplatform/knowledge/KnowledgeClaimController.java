package kz.iitu.medicineplatform.knowledge;

import kz.iitu.medicineplatform.api.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/knowledge/claims")
public class KnowledgeClaimController {
    private final KnowledgeClaimService service;

    public KnowledgeClaimController(KnowledgeClaimService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "Find expert-verified knowledge claims", description = "Returns only VERIFIED claims with a mandatory source. Unreviewed and rejected claims never appear here.")
    public PageResponse<KnowledgeClaimResponse> findVerified(
            @RequestParam(required = false) Long plantId,
            @RequestParam(required = false) Long preparationId,
            @RequestParam(required = false) ClaimType type,
            @PageableDefault(size = 20, sort = "reviewedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if ((plantId == null) == (preparationId == null)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Provide exactly one plantId or preparationId");
        }
        return plantId != null ? service.verifiedForPlant(plantId, type, pageable)
                : service.verifiedForPreparation(preparationId, type, pageable);
    }
}
