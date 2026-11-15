package kz.iitu.medicineplatform.search;

import com.pgvector.PGvector;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaimRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Indexes knowledge claims by computing and storing vector embeddings.
 * Runs on application startup for all verified claims that lack embeddings.
 * Active when search.vector.enabled=true (default true in application.yaml; false in tests).
 */
@Service
@ConditionalOnProperty(name = "search.vector.enabled", havingValue = "true")
public class EmbeddingIndexer {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingIndexer.class);

    @Autowired
    private KnowledgeClaimRepository claimRepository;

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void indexUnembeddedClaims() {
        log.info("Starting embedding index for verified knowledge claims...");
        int totalIndexed = 0;
        int batchSize = 50;

        try {
            int page = 0;
            while (true) {
                List<Long> ids = jdbcTemplate.queryForList(
                        "SELECT id FROM knowledge_claims " +
                        "WHERE review_status = 'VERIFIED' AND embedding IS NULL " +
                        "LIMIT ? OFFSET ?",
                        Long.class,
                        batchSize, page * batchSize
                );

                if (ids.isEmpty()) break;

                for (Long id : ids) {
                    try {
                        Map<String, Object> row = jdbcTemplate.queryForMap(
                                "SELECT id, title, claim_text, interaction_partner, plant_id " +
                                "FROM knowledge_claims WHERE id = ?",
                                id
                        );

                        if (row.isEmpty()) continue;

                        String title = (String) row.get("title");
                        String claimText = (String) row.get("claim_text");
                        String interactionPartner = (String) row.get("interaction_partner");
                        Number plantIdNum = (Number) row.get("plant_id");
                        Long plantId = plantIdNum != null ? plantIdNum.longValue() : null;

                        String text = title + " " + claimText;
                        if (interactionPartner != null) {
                            text += " " + interactionPartner;
                        }

                        // Add plant info if available
                        if (plantId != null) {
                            Map<String, Object> plantRow = jdbcTemplate.queryForMap(
                                    "SELECT scientific_name, display_name, name_ru, name_kk, synonyms FROM plants WHERE id = ?",
                                    plantId
                            );
                            if (!plantRow.isEmpty()) {
                                text += " " + (plantRow.get("scientific_name") != null ? plantRow.get("scientific_name") : "");
                                text += " " + (plantRow.get("display_name") != null ? plantRow.get("display_name") : "");
                                text += " " + (plantRow.get("name_ru") != null ? plantRow.get("name_ru") : "");
                                text += " " + (plantRow.get("name_kk") != null ? plantRow.get("name_kk") : "");
                                text += " " + (plantRow.get("synonyms") != null ? plantRow.get("synonyms") : "");
                            }
                        }

                        float[] embedding = embeddingService.embed(text);
                        PGvector pgVector = embeddingService.toPgVector(embedding);

                        jdbcTemplate.update(
                                "UPDATE knowledge_claims SET embedding = ? WHERE id = ?",
                                pgVector, id
                        );
                        totalIndexed++;
                    } catch (Exception e) {
                        log.warn("Failed to index claim {}: {}", id, e.getMessage());
                    }
                }
                page++;
            }

            log.info("Embedding index complete: {} claims indexed", totalIndexed);
        } catch (Exception e) {
            log.warn("Embedding index failed (pgvector may not be available): {}", e.getMessage());
        }
    }
}
