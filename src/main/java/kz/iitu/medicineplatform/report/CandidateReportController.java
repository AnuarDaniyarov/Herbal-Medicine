package kz.iitu.medicineplatform.report;

import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/reports")
public class CandidateReportController {
    private final CandidateReportService service;
    public CandidateReportController(CandidateReportService service) { this.service = service; }

    @GetMapping(value = "/candidates", produces = "text/csv")
    @Operation(summary = "Export candidates as CSV", description = "Streams compounds and their reported activity measurements as UTF-8 CSV. Optionally filter by activityCode and candidateIds.")
    public ResponseEntity<StreamingResponseBody> candidates(
            @RequestParam(required = false) String activityCode,
            @RequestParam(required = false) List<Long> candidateIds) {
        service.validate(activityCode, candidateIds);
        StreamingResponseBody body = output -> service.writeCsv(output, activityCode, candidateIds);
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", java.nio.charset.StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=plant-candidates.csv")
                .body(body);
    }
}
