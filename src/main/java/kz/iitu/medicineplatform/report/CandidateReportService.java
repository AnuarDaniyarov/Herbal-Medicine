package kz.iitu.medicineplatform.report;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kz.iitu.medicineplatform.activity.ActivityMeasurement;
import kz.iitu.medicineplatform.activity.ActivityMeasurementRepository;
import kz.iitu.medicineplatform.activity.AssayRepository;
import kz.iitu.medicineplatform.compound.Compound;
import kz.iitu.medicineplatform.compound.CompoundRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CandidateReportService {
    private static final int PAGE_SIZE = 500;
    private final CompoundRepository compounds;
    private final ActivityMeasurementRepository measurements;
    private final AssayRepository assays;

    public CandidateReportService(CompoundRepository compounds, ActivityMeasurementRepository measurements,
                                  AssayRepository assays) {
        this.compounds = compounds;
        this.measurements = measurements;
        this.assays = assays;
    }

    public void validate(String activityCode, List<Long> candidateIds) {
        if (activityCode != null && !activityCode.isBlank() && !assays.existsByActivityCode(activityCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown activityCode: " + activityCode);
        }
        if (candidateIds != null && candidateIds.size() > 1000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At most 1000 candidateIds may be exported at once");
        }
        if (candidateIds != null && !candidateIds.isEmpty()) {
            if (candidateIds.stream().anyMatch(id -> id == null || id <= 0)
                    || candidateIds.stream().distinct().count() != candidateIds.size()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "candidateIds must be positive and must not contain duplicates");
            }
            if (compounds.findAllById(candidateIds).size() != candidateIds.size()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or more candidateIds do not exist");
            }
        }
    }

    @Transactional(readOnly = true)
    public void writeCsv(OutputStream output, String activityCode, List<Long> candidateIds) throws IOException {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8))) {
            writer.println("compoundId,compoundName,canonicalSmiles,molecularFormula,structureStatus,activityCode,assayName,value,relation,unit,qualityStatus,sourceTitle,doi");
            int pageNumber = 0;
            Page<Compound> page;
            do {
                PageRequest request = PageRequest.of(pageNumber++, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "id"));
                page = candidateIds == null || candidateIds.isEmpty()
                        ? compounds.search("", null, null, request)
                        : compounds.findByIdIn(candidateIds, request);
                List<Compound> content = page.getContent();
                if (content.isEmpty()) continue;
                List<Long> ids = content.stream().map(Compound::getId).toList();
                Map<Long, List<ActivityMeasurement>> byCompound = new HashMap<>();
                measurements.findForReport(ids, activityCode == null || activityCode.isBlank() ? null : activityCode)
                        .forEach(measurement -> byCompound.computeIfAbsent(measurement.getCompound().getId(), key -> new ArrayList<>())
                                .add(measurement));
                for (Compound compound : content) {
                    List<ActivityMeasurement> compoundMeasurements = byCompound.getOrDefault(compound.getId(), List.of());
                    if (compoundMeasurements.isEmpty()) {
                        row(writer, compound, null);
                    } else {
                        for (ActivityMeasurement measurement : compoundMeasurements) row(writer, compound, measurement);
                    }
                }
                writer.flush();
            } while (page.hasNext());
            if (writer.checkError()) throw new IOException("Failed to write candidate CSV");
        }
    }

    private void row(PrintWriter writer, Compound compound, ActivityMeasurement measurement) {
        List<String> cells = new ArrayList<>();
        cells.add(String.valueOf(compound.getId()));
        cells.add(compound.getName());
        cells.add(compound.getCanonicalSmiles());
        cells.add(compound.getMolecularFormula());
        cells.add(compound.getStructureStatus());
        cells.add(measurement == null ? null : measurement.getAssay().getActivityCode());
        cells.add(measurement == null ? null : measurement.getAssay().getName());
        cells.add(measurement == null ? null : measurement.getValue().toPlainString());
        cells.add(measurement == null ? null : measurement.getRelation());
        cells.add(measurement == null ? null : measurement.getUnit());
        cells.add(measurement == null ? null : measurement.getQualityStatus());
        cells.add(measurement == null ? null : measurement.getSource().getTitle());
        cells.add(measurement == null ? null : measurement.getSource().getDoi());
        writer.println(cells.stream().map(this::escape).reduce((left, right) -> left + "," + right).orElse(""));
    }

    private String escape(String value) {
        if (value == null) return "";
        String safe = value.stripLeading();
        boolean numericNegative = safe.matches("-\\d+(?:\\.\\d+)?");
        if (!safe.isEmpty() && "=+-@\t\r".indexOf(safe.charAt(0)) >= 0 && !numericNegative) value = "'" + value;
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
