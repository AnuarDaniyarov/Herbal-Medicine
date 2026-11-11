package kz.iitu.medicineplatform.sample;

import kz.iitu.medicineplatform.api.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PlantSampleController {
    private final PlantSampleService service;
    public PlantSampleController(PlantSampleService service) { this.service = service; }
    @GetMapping("/plants/{plantId}/samples")
    public PageResponse<PlantSampleResponse> findForPlant(@PathVariable Long plantId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return service.findForPlant(plantId, pageable);
    }
    @GetMapping("/samples/{id}")
    public PlantSampleResponse findById(@PathVariable Long id) { return service.findById(id); }
}
