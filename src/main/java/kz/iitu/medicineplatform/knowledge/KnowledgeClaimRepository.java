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
    long countByReviewStatus(ReviewStatus reviewStatus);

    @org.springframework.data.jpa.repository.Query("""
            select c.reviewStatus, count(c)
            from KnowledgeClaim c
            group by c.reviewStatus
            """)
    java.util.List<Object[]> countGroupedByReviewStatus();

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
     * Uses cosine distance (<=>) matching the vector_cosine_ops HNSW index.
     * Native query selects IDs, and default method loads full entities with joins.
     */
    @org.springframework.data.jpa.repository.Query(value = """
            select kc.id from knowledge_claims kc
            where kc.review_status = :status
              and kc.embedding IS NOT NULL
              and kc.embedding_model = :modelId
            order by kc.embedding <=> CAST(:embedding AS vector)
            limit :limit
            """, nativeQuery = true)
    java.util.List<Long> findNearestIdsByEmbedding(
            @org.springframework.data.repository.query.Param("status") String status,
            @org.springframework.data.repository.query.Param("modelId") String modelId,
            @org.springframework.data.repository.query.Param("embedding") String embedding,
            @org.springframework.data.repository.query.Param("limit") int limit);

    @org.springframework.data.jpa.repository.Query("""
            select c from KnowledgeClaim c
            left join fetch c.plant
            left join fetch c.preparation
            left join fetch c.source
            where c.id in :ids
            """)
    java.util.List<KnowledgeClaim> findAllByIdWithDetails(
            @org.springframework.data.repository.query.Param("ids") java.util.Collection<Long> ids);

    default java.util.List<KnowledgeClaim> findNearestByEmbedding(
            String status, String modelId, String embedding, Pageable pageable) {
        int limit = pageable.isPaged() ? pageable.getPageSize() : 10;
        java.util.List<Long> ids = findNearestIdsByEmbedding(status, modelId, embedding, limit);
        if (ids.isEmpty()) {
            return java.util.List.of();
        }
        java.util.Map<Long, KnowledgeClaim> map = findAllByIdWithDetails(ids).stream()
                .collect(java.util.stream.Collectors.toMap(KnowledgeClaim::getId, c -> c));
        return ids.stream().map(map::get).filter(java.util.Objects::nonNull).toList();
    }
}
