package kz.iitu.medicineplatform.sample;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kz.iitu.medicineplatform.api.PageResponse;
import kz.iitu.medicineplatform.extract.ExtractRepository;
import kz.iitu.medicineplatform.extract.ExtractResponse;
import kz.iitu.medicineplatform.plant.PlantRepository;
import org.springframework.data.domain.Page;
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
    private final ExtractRepository extracts;

    public PlantSampleService(PlantSampleRepository samples, PlantRepository plants, ExtractRepository extracts) {
        this.samples = samples;
        this.plants = plants;
        this.extracts = extracts;
    }

    public PageResponse<PlantSampleResponse> findForPlant(Long plantId, Pageable pageable) {
        if (!plants.existsById(plantId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Plant with id " + plantId + " was not found");
        Page<PlantSample> samplePage = samples.findAllByPlantId(plantId, pageable);
        List<Long> sampleIds = samplePage.getContent().stream().map(PlantSample::getId).toList();

        Map<Long, List<ExtractResponse>> extractsBySample = sampleIds.isEmpty() ? Map.of()
                : extracts.findAllBySample_IdInOrderByIdAsc(sampleIds).stream()
                        .map(ExtractResponse::from)
                        .collect(Collectors.groupingBy(e -> e.sample().id()));

        return PageResponse.from(samplePage.map(s ->
                PlantSampleResponse.from(s, extractsBySample.getOrDefault(s.getId(), List.of()))));
    }

    public PlantSampleResponse findById(Long id) {
        return samples.findById(id)
                .map(s -> {
                    List<ExtractResponse> list = extracts.findAllBySampleIdOrderByIdAsc(s.getId()).stream()
                            .map(ExtractResponse::from).toList();
                    return PlantSampleResponse.from(s, list);
                })
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant sample with id " + id + " was not found"));
    }
}
