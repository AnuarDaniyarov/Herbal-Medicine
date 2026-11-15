package kz.iitu.medicineplatform.search;

import java.util.List;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaim;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaimRepository;
import kz.iitu.medicineplatform.knowledge.ReviewStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Runs pgvector nearest-neighbour lookup in a separate transaction so SQL failures
 * on H2 or missing embeddings do not poison the outer keyword-search transaction.
 */
@Component
public class VectorClaimSearch {
    private static final Logger log = LoggerFactory.getLogger(VectorClaimSearch.class);

    private final KnowledgeClaimRepository claims;
    private final EmbeddingService embeddings;

    public VectorClaimSearch(KnowledgeClaimRepository claims, EmbeddingService embeddings) {
        this.claims = claims;
        this.embeddings = embeddings;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<KnowledgeClaim> findNearestVerified(String query, int limit) {
        try {
            float[] queryEmbedding = embeddings.embed(query);
            String vectorLiteral = toVectorLiteral(queryEmbedding);
            return claims.findNearestByEmbedding(
                    ReviewStatus.VERIFIED.name(), vectorLiteral, PageRequest.of(0, limit));
        } catch (Exception ex) {
            log.debug("Vector claim search unavailable: {}", ex.getMessage());
            return List.of();
        }
    }

    private static String toVectorLiteral(float[] embedding) {
        StringBuilder sb = new StringBuilder(embedding.length * 8);
        sb.append('[');
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(embedding[i]);
        }
        sb.append(']');
        return sb.toString();
    }
}
