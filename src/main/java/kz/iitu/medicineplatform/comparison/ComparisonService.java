package kz.iitu.medicineplatform.comparison;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kz.iitu.medicineplatform.activity.ActivityMeasurementRepository;
import kz.iitu.medicineplatform.activity.ActivityMeasurementResponse;
import kz.iitu.medicineplatform.activity.AssayRepository;
import kz.iitu.medicineplatform.compound.Compound;
import kz.iitu.medicineplatform.compound.CompoundRepository;
import kz.iitu.medicineplatform.compound.CompoundSummaryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ComparisonService {
    private static final String NOTICE = "Experimental measurements are shown as reported; differing assays and units are not combined.";
    private final CompoundRepository compounds;
    private final ActivityMeasurementRepository measurements;
    private final AssayRepository assays;

    public ComparisonService(CompoundRepository compounds, ActivityMeasurementRepository measurements,
                             AssayRepository assays) {
        this.compounds = compounds;
        this.measurements = measurements;
        this.assays = assays;
    }

    @Transactional(readOnly = true)
    public ComparisonResponse compare(ComparisonRequest request) {
        if (!assays.existsByActivityCode(request.activityCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unknown activityCode: " + request.activityCode());
        }
        if (request.candidateIds().stream().distinct().count() != request.candidateIds().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "candidateIds must not contain duplicates");
        }
        Map<Long, Compound> found = compounds.findAllById(request.candidateIds()).stream()
                .collect(Collectors.toMap(Compound::getId, Function.identity()));
        if (found.size() != request.candidateIds().size()) {
            List<Long> missing = request.candidateIds().stream().filter(id -> !found.containsKey(id)).toList();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown compound IDs: " + missing);
        }
        Map<Long, List<ActivityMeasurementResponse>> byCompound = measurements
                .findByCompoundIdsAndActivityCode(request.candidateIds(), request.activityCode()).stream()
                .map(ActivityMeasurementResponse::from)
                .collect(Collectors.groupingBy(item -> item.compoundId() == null ? -1L : item.compoundId()));
        List<ComparisonResponse.CandidateComparison> items = request.candidateIds().stream()
                .map(id -> new ComparisonResponse.CandidateComparison(CompoundSummaryResponse.from(found.get(id)),
                        byCompound.getOrDefault(id, List.of())))
                .toList();
        return new ComparisonResponse(request.activityCode(), NOTICE, items);
    }
}
