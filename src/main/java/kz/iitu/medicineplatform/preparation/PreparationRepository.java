package kz.iitu.medicineplatform.preparation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import kz.iitu.medicineplatform.knowledge.ReviewStatus;

public interface PreparationRepository extends JpaRepository<Preparation, Long> {
    @Query("""
            select p from Preparation p
            where (:query = '' or lower(p.name) like lower(concat('%', :query, '%'))
                or lower(coalesce(p.manufacturer, '')) like lower(concat('%', :query, '%'))
                or lower(coalesce(p.registrationNumber, '')) like lower(concat('%', :query, '%')))
              and (:dosageForm = '' or lower(coalesce(p.dosageForm, '')) like lower(concat('%', :dosageForm, '%')))
              and p.reviewStatus = :status
            """)
    Page<Preparation> search(@Param("query") String query, @Param("dosageForm") String dosageForm,
                             @Param("status") ReviewStatus status, Pageable pageable);
}
