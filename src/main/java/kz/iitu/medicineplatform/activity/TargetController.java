package kz.iitu.medicineplatform.activity;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/targets")
public class TargetController {
    private final TargetService service;
    public TargetController(TargetService service) { this.service = service; }
    @GetMapping
    public List<TargetResponse> findAll() { return service.findAll(); }
}
