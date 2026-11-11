package kz.iitu.medicineplatform.activity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssayRepository extends JpaRepository<Assay, Long> {
    boolean existsByActivityCode(String activityCode);
}
