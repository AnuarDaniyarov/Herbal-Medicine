package kz.iitu.medicineplatform.source;

import jakarta.validation.Valid;
import kz.iitu.medicineplatform.api.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/admin/sources")
public class AdminSourceController {
    private final SourceService service;

    public AdminSourceController(SourceService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "List source records for administrators")
    public PageResponse<SourceResponse> list(@PageableDefault(size = 25, sort = "title") Pageable pageable) {
        return service.list(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a source with its license note and DOI or URL")
    public SourceResponse create(@Valid @RequestBody CreateSourceRequest request) {
        return service.create(request);
    }
}
