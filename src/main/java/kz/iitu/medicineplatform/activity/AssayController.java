package kz.iitu.medicineplatform.activity;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assays")
public class AssayController {

    private final AssayService service;

    public AssayController(AssayService service) {
        this.service = service;
    }

    @GetMapping
    public List<AssayResponse> findAll() {
        return service.findAll();
    }
}
