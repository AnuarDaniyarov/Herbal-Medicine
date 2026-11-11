package kz.iitu.medicineplatform.extract;

import java.util.List;
import kz.iitu.medicineplatform.sample.PlantSampleRepository;
import kz.iitu.medicineplatform.activity.ActivityMeasurementRepository;
import kz.iitu.medicineplatform.activity.ActivityMeasurementResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ExtractService {
    private final ExtractRepository extracts;
    private final PlantSampleRepository samples;
    private final ActivityMeasurementRepository measurements;
    public ExtractService(ExtractRepository extracts, PlantSampleRepository samples,
                          ActivityMeasurementRepository measurements) {
        this.extracts = extracts;
        this.samples = samples;
        this.measurements = measurements;
    }
    public ExtractResponse findById(Long id) {
        return extracts.findById(id).map(ExtractResponse::from).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Extract with id " + id + " was not found"));
    }
    public List<ExtractResponse> findForSample(Long sampleId) {
        if (!samples.existsById(sampleId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Plant sample with id " + sampleId + " was not found");
        return extracts.findAllBySampleIdOrderByIdAsc(sampleId).stream().map(ExtractResponse::from).toList();
    }
    public List<ActivityMeasurementResponse> findMeasurements(Long extractId) {
        if (!extracts.existsById(extractId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Extract with id " + extractId + " was not found");
        return measurements.findAllByExtractIdOrderByCreatedAtDesc(extractId).stream()
                .map(ActivityMeasurementResponse::from).toList();
    }
}
