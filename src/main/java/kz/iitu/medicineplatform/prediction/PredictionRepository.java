package kz.iitu.medicineplatform.prediction;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    long countByCreatedBy_IdAndCreatedAtAfter(Long userId, java.time.OffsetDateTime after);
}
