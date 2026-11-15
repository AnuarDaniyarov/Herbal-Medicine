package kz.iitu.medicineplatform.knowledge;

import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeClaimRepository extends JpaRepository<KnowledgeClaim, Long> {
    Page<KnowledgeClaim> findByPlant_IdAndReviewStatus(Long plantId, ReviewStatus status, Pageable pageable);
    Page<KnowledgeClaim> findByPlant_IdAndReviewStatusAndClaimType(Long plantId, ReviewStatus status,
                                                                   ClaimType claimType, Pageable pageable);
    Page<KnowledgeClaim> findByPreparation_IdAndReviewStatus(Long preparationId, ReviewStatus status,
                                                             Pageable pageable);
    Page<KnowledgeClaim> findByPreparation_IdAndReviewStatusAndClaimType(Long preparationId, ReviewStatus status,
                                                                         ClaimType claimType, Pageable pageable);

    Page<KnowledgeClaim> findByReviewStatusIn(Collection<ReviewStatus> statuses, Pageable pageable);
    Page<KnowledgeClaim> findByReviewStatus(ReviewStatus reviewStatus, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("""
            select c from KnowledgeClaim c
            left join fetch c.plant p
            left join fetch c.preparation pr
            left join fetch c.source s
            where c.reviewStatus = :status
              and (
                lower(c.title) like lower(concat('%', :term, '%'))
                or lower(c.claimText) like lower(concat('%', :term, '%'))
                or lower(coalesce(c.interactionPartner, '')) like lower(concat('%', :term, '%'))
                or (p is not null and (lower(p.scientificName) like lower(concat('%', :term, '%'))
                                   or lower(p.displayName) like lower(concat('%', :term, '%'))
                                   or lower(coalesce(p.nameRu, '')) like lower(concat('%', :term, '%'))
                                   or lower(coalesce(p.nameKk, '')) like lower(concat('%', :term, '%'))
                                   or lower(coalesce(p.synonyms, '')) like lower(concat('%', :term, '%'))))
                or (pr is not null and lower(pr.name) like lower(concat('%', :term, '%')))
              )
            """)
    java.util.List<KnowledgeClaim> searchVerifiedCandidates(
            @org.springframework.data.repository.query.Param("status") ReviewStatus status,
            @org.springframework.data.repository.query.Param("term") String term,
            Pageable pageable);

    /**
     * Vector similarity search: finds the most semantically similar verified claims.
     * Uses cosine distance (<->) — lower values mean more similar (0 = identical).
     * Native query because JPQL doesn't support pgvector operators.
     */
    @org.springframework.data.jpa.repository.Query(value = """
            select *
            from knowledge_claims
            where review_status = :status
              and embedding is not null
            order by embedding <-> cast(:embedding as vector)
            """, nativeQuery = true)
    java.util.List<KnowledgeClaim> findNearestByEmbedding(
            @org.springframework.data.repository.query.Param("status") String status,
            @org.springframework.data.repository.query.Param("embedding") String embedding,
            Pageable pageable);

    @org.springframework.data.jpa.repository.Query("select c.reviewStatus, count(c) from KnowledgeClaim c group by c.reviewStatus")
    java.util.Map<ReviewStatus, Long> groupByReviewStatus();
}
