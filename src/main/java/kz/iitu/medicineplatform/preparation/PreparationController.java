package kz.iitu.medicineplatform.preparation;

import kz.iitu.medicineplatform.api.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/preparations")
public class PreparationController {
    private final PreparationService service;

    public PreparationController(PreparationService service) { this.service = service; }

    @GetMapping
    public PageResponse<PreparationResponse> search(@RequestParam(required = false) String query,
                                                     @RequestParam(required = false) String dosageForm,
                                                     @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.search(query, dosageForm, pageable);
    }

    @GetMapping("/{id}")
    public PreparationResponse get(@PathVariable long id) { return service.get(id); }

    @GetMapping("/{id}/ingredients")
    public java.util.List<PreparationIngredientResponse> ingredients(@PathVariable long id) {
        return service.ingredients(id);
    }
}
