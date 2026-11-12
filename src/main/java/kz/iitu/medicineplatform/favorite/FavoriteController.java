package kz.iitu.medicineplatform.favorite;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Map;
import kz.iitu.medicineplatform.auth.UserAccount;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService service;

    public FavoriteController(FavoriteService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get user's favorites", description = "Returns saved plants, compounds, and preparations.")
    public List<FavoriteResponse> findAll(@AuthenticationPrincipal UserAccount account) {
        return service.findAll(account);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add an item to favorites")
    public FavoriteResponse add(@AuthenticationPrincipal UserAccount account,
                                @RequestBody AddFavoriteRequest request) {
        return service.add(account, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove an item from favorites")
    public void remove(@AuthenticationPrincipal UserAccount account,
                       @PathVariable Long id) {
        service.remove(account, id);
    }

    @GetMapping("/check")
    @Operation(summary = "Check if an item is in favorites")
    public Map<String, Object> check(@AuthenticationPrincipal UserAccount account,
                                     @RequestParam(required = false) Long plantId,
                                     @RequestParam(required = false) Long compoundId,
                                     @RequestParam(required = false) Long preparationId) {
        return service.check(account, plantId, compoundId, preparationId);
    }
}
