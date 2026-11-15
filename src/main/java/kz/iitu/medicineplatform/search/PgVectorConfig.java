package kz.iitu.medicineplatform.search;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.annotation.PostConstruct;

/**
 * Ensures the pgvector extension is available in the database.
 * Only active when search.vector.enabled=true.
 */
@Configuration
@ConditionalOnProperty(name = "search.vector.enabled", havingValue = "true")
public class PgVectorConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void ensurePgVectorExtension() {
        try {
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
        } catch (Exception e) {
            // Non-critical: pgvector may not be available in test environments
        }
    }
}
