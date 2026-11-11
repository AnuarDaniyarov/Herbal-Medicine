package kz.iitu.medicineplatform.plant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlantRepository extends JpaRepository<Plant, Long> {

    @Query("""
            select p from Plant p
            where (:query = ''
               or lower(p.scientificName) like lower(concat('%', :query, '%'))
               or lower(p.displayName) like lower(concat('%', :query, '%'))
               or lower(coalesce(p.family, '')) like lower(concat('%', :query, '%')))
              and (:family = '' or lower(p.family) like lower(concat('%', :family, '%')))
            """)
    Page<Plant> search(@Param("query") String query, @Param("family") String family, Pageable pageable);
}
