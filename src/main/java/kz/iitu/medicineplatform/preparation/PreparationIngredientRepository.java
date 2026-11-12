package kz.iitu.medicineplatform.preparation;

import java.util.List;
import kz.iitu.medicineplatform.knowledge.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreparationIngredientRepository extends JpaRepository<PreparationIngredient, Long> {
    List<PreparationIngredient> findByPreparation_IdAndReviewStatusOrderByIdAsc(Long preparationId,
                                                                                ReviewStatus reviewStatus);
}
