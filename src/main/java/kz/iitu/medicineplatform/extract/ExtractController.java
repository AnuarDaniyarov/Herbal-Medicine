package kz.iitu.medicineplatform.extract;

import java.util.List;
import kz.iitu.medicineplatform.activity.ActivityMeasurementResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ExtractController {
    private final ExtractService service;
    public ExtractController(ExtractService service) { this.service = service; }
    @GetMapping("/samples/{sampleId}/extracts")
    public List<ExtractResponse> findForSample(@PathVariable Long sampleId) { return service.findForSample(sampleId); }
    @GetMapping("/extracts/{id}")
    public ExtractResponse findById(@PathVariable Long id) { return service.findById(id); }
    @GetMapping("/extracts/{id}/measurements")
    public List<ActivityMeasurementResponse> findMeasurements(@PathVariable Long id) {
        return service.findMeasurements(id);
    }
}
