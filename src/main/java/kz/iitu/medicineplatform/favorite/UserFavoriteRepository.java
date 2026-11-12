package kz.iitu.medicineplatform.favorite;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {

    @Query("""
            select f from UserFavorite f
            left join fetch f.plant
            left join fetch f.compound
            left join fetch f.preparation
            where f.user.id = :userId
            order by f.createdAt desc
            """)
    List<UserFavorite> findAllByUserIdWithSubjects(@Param("userId") Long userId);

    Optional<UserFavorite> findByIdAndUser_Id(Long id, Long userId);

    Optional<UserFavorite> findByUser_IdAndPlant_Id(Long userId, Long plantId);

    Optional<UserFavorite> findByUser_IdAndCompound_Id(Long userId, Long compoundId);

    Optional<UserFavorite> findByUser_IdAndPreparation_Id(Long userId, Long preparationId);

    boolean existsByUser_IdAndPlant_Id(Long userId, Long plantId);

    boolean existsByUser_IdAndCompound_Id(Long userId, Long compoundId);

    boolean existsByUser_IdAndPreparation_Id(Long userId, Long preparationId);

    void deleteByUser_IdAndPlant_Id(Long userId, Long plantId);

    void deleteByUser_IdAndCompound_Id(Long userId, Long compoundId);

    void deleteByUser_IdAndPreparation_Id(Long userId, Long preparationId);
}
