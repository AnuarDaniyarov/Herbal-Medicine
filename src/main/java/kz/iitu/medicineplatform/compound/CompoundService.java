package kz.iitu.medicineplatform.compound;

import java.util.List;
import kz.iitu.medicineplatform.activity.ActivityMeasurementRepository;
import kz.iitu.medicineplatform.activity.ActivityMeasurementResponse;
import kz.iitu.medicineplatform.api.PageResponse;
import kz.iitu.medicineplatform.plant.PlantCompoundRelationshipRepository;
import kz.iitu.medicineplatform.plant.PlantOccurrenceResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class CompoundService {

    private final CompoundRepository compounds;
    private final PlantCompoundRelationshipRepository relationships;
    private final ActivityMeasurementRepository measurements;

    public CompoundService(CompoundRepository compounds,
                           PlantCompoundRelationshipRepository relationships,
                           ActivityMeasurementRepository measurements) {
        this.compounds = compounds;
        this.relationships = relationships;
        this.measurements = measurements;
    }

    public PageResponse<CompoundSummaryResponse> findAll(String query, Long plantId,
                                                         StructureStatus structureStatus, Pageable pageable) {
        String normalizedQuery = query == null || query.isBlank() ? "" : query.trim();
        String status = structureStatus == null ? null : structureStatus.name();
        return PageResponse.from(compounds.search(normalizedQuery, plantId, status, pageable)
                .map(CompoundSummaryResponse::from));
    }

    public CompoundDetailsResponse findById(Long id) {
        Compound compound = compounds.findById(id).orElseThrow(() -> notFound("Compound", id));
        List<PlantOccurrenceResponse> occurrences = relationships.findByCompoundId(id).stream()
                .map(PlantOccurrenceResponse::from)
                .toList();
        return new CompoundDetailsResponse(
                compound.getId(), compound.getName(), compound.getCanonicalSmiles(), compound.getInchiKey(),
                compound.getExternalId(), compound.getMolecularFormula(), compound.getStructureStatus(),
                compound.getCreatedAt(), compound.getUpdatedAt(), occurrences);
    }

    public List<ActivityMeasurementResponse> findMeasurements(Long compoundId) {
        if (!compounds.existsById(compoundId)) {
            throw notFound("Compound", compoundId);
        }
        return measurements.findAllByCompoundIdOrderByCreatedAtDesc(compoundId).stream()
                .map(ActivityMeasurementResponse::from)
                .toList();
    }

    private ResponseStatusException notFound(String type, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, type + " with id " + id + " was not found");
    }
}
