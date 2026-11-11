package kz.iitu.medicineplatform.prediction;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelVersionRepository extends JpaRepository<ModelVersion, Long> {
    Optional<ModelVersion> findFirstByActivityCodeAndActiveTrueOrderByCreatedAtDesc(String activityCode);
}
