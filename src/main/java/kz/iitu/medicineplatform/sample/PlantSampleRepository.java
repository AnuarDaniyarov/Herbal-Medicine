package kz.iitu.medicineplatform.sample;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantSampleRepository extends JpaRepository<PlantSample, Long> {
    Page<PlantSample> findAllByPlantId(Long plantId, Pageable pageable);
}
