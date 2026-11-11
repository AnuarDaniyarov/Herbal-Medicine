package kz.iitu.medicineplatform.plant;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantCompoundRelationshipRepository extends JpaRepository<PlantCompoundRelationship, Long> {
    List<PlantCompoundRelationship> findByCompoundId(Long compoundId);
}
