package kz.iitu.medicineplatform.activity;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityMeasurementRepository extends JpaRepository<ActivityMeasurement, Long> {
    List<ActivityMeasurement> findAllByCompoundIdOrderByCreatedAtDesc(Long compoundId);
    List<ActivityMeasurement> findAllByExtractIdOrderByCreatedAtDesc(Long extractId);

    @Query("select m from ActivityMeasurement m join fetch m.compound join fetch m.assay "
            + "join fetch m.source where m.compound.id in :compoundIds and m.assay.activityCode = :activityCode "
            + "order by m.compound.id, m.createdAt desc")
    List<ActivityMeasurement> findByCompoundIdsAndActivityCode(@Param("compoundIds") List<Long> compoundIds,
                                                               @Param("activityCode") String activityCode);

    @Query("select m from ActivityMeasurement m join fetch m.compound join fetch m.assay "
            + "join fetch m.source where m.compound.id in :compoundIds "
            + "and (:activityCode is null or m.assay.activityCode = :activityCode) "
            + "order by m.compound.id, m.createdAt desc")
    List<ActivityMeasurement> findForReport(@Param("compoundIds") List<Long> compoundIds,
                                            @Param("activityCode") String activityCode);
}
