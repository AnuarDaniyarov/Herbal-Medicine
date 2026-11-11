package kz.iitu.medicineplatform.compound;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import kz.iitu.medicineplatform.plant.PlantCompoundRelationship;

public interface CompoundRepository extends JpaRepository<Compound, Long> {

    Page<Compound> findByIdIn(List<Long> ids, Pageable pageable);

    Optional<Compound> findFirstByCanonicalSmiles(String canonicalSmiles);

    @Query("""
            select distinct c from Compound c
            left join PlantCompoundRelationship r on r.compound = c
            left join r.plant p
            where (:query = ''
                or lower(c.name) like lower(concat('%', :query, '%'))
                or lower(c.canonicalSmiles) like lower(concat('%', :query, '%'))
                or lower(coalesce(c.molecularFormula, '')) like lower(concat('%', :query, '%'))
                or lower(coalesce(c.inchiKey, '')) like lower(concat('%', :query, '%'))
                or lower(coalesce(c.externalId, '')) like lower(concat('%', :query, '%'))
                or lower(p.scientificName) like lower(concat('%', :query, '%'))
                or lower(p.displayName) like lower(concat('%', :query, '%')))
              and (:plantId is null or p.id = :plantId)
              and (:structureStatus is null or c.structureStatus = :structureStatus)
            """)
    Page<Compound> search(@Param("query") String query,
                          @Param("plantId") Long plantId,
                          @Param("structureStatus") String structureStatus,
                          Pageable pageable);

    @Query("""
            select distinct c from Compound c
            join PlantCompoundRelationship r on r.compound = c
            where r.plant.id = :plantId
            """)
    Page<Compound> findByPlantId(@Param("plantId") Long plantId, Pageable pageable);
}
