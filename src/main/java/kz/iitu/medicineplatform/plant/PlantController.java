package kz.iitu.medicineplatform.plant;

import kz.iitu.medicineplatform.api.PageResponse;
import kz.iitu.medicineplatform.compound.CompoundSummaryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

    private final PlantService service;

    public PlantController(PlantService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Search plants", description = "Returns a paginated catalog of plants; query matches scientific and display names or family.")
    public PageResponse<PlantResponse> findAll(@RequestParam(required = false) String query,
                                               @RequestParam(required = false) String family,
                                               @PageableDefault(size = 20, sort = "scientificName") Pageable pageable) {
        return service.findAll(query, family, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get plant", description = "Returns the plant record or a structured 404 error.")
    public PlantResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/{id}/compounds")
    @Operation(summary = "List plant compounds", description = "Returns linked compounds with pagination.")
    public PageResponse<CompoundSummaryResponse> findCompounds(
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.findCompounds(id, pageable);
    }
}
