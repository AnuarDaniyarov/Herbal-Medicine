package kz.iitu.medicineplatform.knowledge;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeClaimRevisionRepository extends JpaRepository<KnowledgeClaimRevision, Long> {
    long countByClaim_Id(Long claimId);
}
