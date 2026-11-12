package kz.iitu.medicineplatform.extract;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtractRepository extends JpaRepository<Extract, Long> {
    List<Extract> findAllBySampleIdOrderByIdAsc(Long sampleId);
    List<Extract> findAllBySample_IdInOrderByIdAsc(java.util.Collection<Long> sampleIds);
}
