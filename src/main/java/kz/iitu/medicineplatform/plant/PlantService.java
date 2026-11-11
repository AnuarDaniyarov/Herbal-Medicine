package kz.iitu.medicineplatform.plant;

import kz.iitu.medicineplatform.api.PageResponse;
import kz.iitu.medicineplatform.compound.CompoundRepository;
import kz.iitu.medicineplatform.compound.CompoundSummaryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class PlantService {

    private final PlantRepository plants;
    private final CompoundRepository compounds;

    public PlantService(PlantRepository plants, CompoundRepository compounds) {
        this.plants = plants;
        this.compounds = compounds;
    }

    public PageResponse<PlantResponse> findAll(String query, String family, Pageable pageable) {
        String normalizedQuery = normalize(query);
        String normalizedFamily = normalize(family);
        return PageResponse.from(plants.search(normalizedQuery, normalizedFamily, pageable).map(PlantResponse::from));
    }

    public PlantResponse findById(Long id) {
        return PlantResponse.from(plants.findById(id).orElseThrow(() -> notFound("Plant", id)));
    }

    public PageResponse<CompoundSummaryResponse> findCompounds(Long plantId, Pageable pageable) {
        if (!plants.existsById(plantId)) {
            throw notFound("Plant", plantId);
        }
        return PageResponse.from(compounds.findByPlantId(plantId, pageable).map(CompoundSummaryResponse::from));
    }

    private String normalize(String query) {
        return query == null || query.isBlank() ? "" : query.trim();
    }

    private ResponseStatusException notFound(String type, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, type + " with id " + id + " was not found");
    }
}
