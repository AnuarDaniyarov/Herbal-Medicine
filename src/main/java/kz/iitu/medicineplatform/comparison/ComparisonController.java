package kz.iitu.medicineplatform.comparison;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/comparisons")
public class ComparisonController {
    private final ComparisonService service;
    public ComparisonController(ComparisonService service) { this.service = service; }
    @PostMapping
    @Operation(summary = "Compare candidate compounds", description = "Returns per-compound experimental records for one activity code; measurements are never combined across assays or units.")
    public ComparisonResponse compare(@Valid @RequestBody ComparisonRequest request) {
        return service.compare(request);
    }
}
