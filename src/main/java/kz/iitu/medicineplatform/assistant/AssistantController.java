package kz.iitu.medicineplatform.assistant;

import jakarta.validation.Valid;
import kz.iitu.medicineplatform.auth.UserAccount;
import kz.iitu.medicineplatform.api.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {
    private final AssistantService assistant;

    public AssistantController(AssistantService assistant) { this.assistant = assistant; }

    @PostMapping("/questions")
    @Operation(summary = "Ask a source-grounded question", description = "Retrieves only expert-verified claims; safety-sensitive questions receive a fixed warning. The initial answer provider is extractive and not an LLM.")
    public AssistantResponse ask(@Valid @RequestBody AssistantRequest request,
                                 @AuthenticationPrincipal UserAccount account) {
        return assistant.ask(request, account);
    }

    @GetMapping("/history")
    @Operation(summary = "List the authenticated user's assistant history")
    public PageResponse<AssistantHistoryEntry> history(@AuthenticationPrincipal UserAccount account,
                                                        @PageableDefault(size = 20) Pageable pageable) {
        return assistant.history(account, pageable);
    }
}
