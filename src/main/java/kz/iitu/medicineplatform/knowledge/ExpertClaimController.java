package kz.iitu.medicineplatform.knowledge;

import jakarta.validation.Valid;
import kz.iitu.medicineplatform.api.PageResponse;
import kz.iitu.medicineplatform.auth.UserAccount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/expert/claims")
public class ExpertClaimController {
    private final KnowledgeClaimService service;

    public ExpertClaimController(KnowledgeClaimService service) { this.service = service; }

    @GetMapping("/queue")
    @Operation(summary = "List claims awaiting expert review")
    public PageResponse<KnowledgeClaimResponse> queue(@PageableDefault(size = 25, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.reviewQueue(pageable);
    }

    @PostMapping
    @Operation(summary = "Submit a source-backed claim to the review queue")
    public KnowledgeClaimResponse submit(@Valid @RequestBody ClaimSubmissionRequest request,
                                         @AuthenticationPrincipal UserAccount author) {
        return service.submit(request, author);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Approve or reject a claim and preserve the review audit")
    public KnowledgeClaimResponse review(@PathVariable long id,
                                         @Valid @RequestBody ClaimReviewRequest request,
                                         @AuthenticationPrincipal UserAccount reviewer) {
        return service.review(id, request, reviewer);
    }
}
