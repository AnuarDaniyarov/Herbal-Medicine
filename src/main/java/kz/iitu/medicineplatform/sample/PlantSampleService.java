package kz.iitu.medicineplatform.sample;

import kz.iitu.medicineplatform.api.PageResponse;
import kz.iitu.medicineplatform.plant.PlantRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class PlantSampleService {
    private final PlantSampleRepository samples;
    private final PlantRepository plants;
    public PlantSampleService(PlantSampleRepository samples, PlantRepository plants) {
        this.samples = samples;
        this.plants = plants;
    }
    public PageResponse<PlantSampleResponse> findForPlant(Long plantId, Pageable pageable) {
        if (!plants.existsById(plantId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Plant with id " + plantId + " was not found");
        return PageResponse.from(samples.findAllByPlantId(plantId, pageable).map(PlantSampleResponse::from));
    }
    public PlantSampleResponse findById(Long id) {
        return samples.findById(id).map(PlantSampleResponse::from).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant sample with id " + id + " was not found"));
    }
}
