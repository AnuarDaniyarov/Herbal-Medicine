package kz.iitu.medicineplatform.knowledge;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import kz.iitu.medicineplatform.auth.UserAccount;
import kz.iitu.medicineplatform.auth.UserAccountRepository;
import kz.iitu.medicineplatform.auth.UserRole;
import kz.iitu.medicineplatform.plant.Plant;
import kz.iitu.medicineplatform.plant.PlantRepository;
import kz.iitu.medicineplatform.source.DataSource;
import kz.iitu.medicineplatform.source.DataSourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class PostgresVectorMigrationTest {
    private static final String VECTOR_MODEL_ID = "pg-vector-migration-test";

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("pgvector/pgvector:pg17")
                    .withDatabaseName("medicine_platform")
                    .withUsername("postgres")
                    .withPassword("test-password")
                    .withInitScript("init-pgvector.sql");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.locations",
                () -> "classpath:db/migration,classpath:db/postgres-migration");
        registry.add("search.vector.enabled", () -> "true");
    }

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private KnowledgeClaimRepository claims;
    @Autowired private KnowledgeClaimService claimService;
    @Autowired private PlantRepository plants;
    @Autowired private DataSourceRepository sources;
    @Autowired private UserAccountRepository users;

    @Test
    void appliesPostgresMigrationsAndRunsCosineVectorRetrieval() {
        Integer latestMigration = jdbcTemplate.queryForObject(
                "SELECT MAX(version::integer) FROM flyway_schema_history WHERE success = TRUE",
                Integer.class);
        assertEquals(16, latestMigration);

        Integer invalidVerifiedClaims = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM knowledge_claims claim
                WHERE claim.review_status = 'VERIFIED'
                  AND (claim.reviewed_by IS NULL
                       OR claim.reviewed_at IS NULL
                       OR NOT EXISTS (
                           SELECT 1
                           FROM knowledge_claim_reviews review_event
                           WHERE review_event.claim_id = claim.id
                             AND review_event.reviewer_id = claim.reviewed_by
                             AND review_event.new_status = claim.review_status
                       ))
                """, Integer.class);
        assertEquals(0, invalidVerifiedClaims);

        Integer duplicateSourceTitles = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM (
                    SELECT lower(trim(title))
                    FROM sources
                    GROUP BY lower(trim(title))
                    HAVING COUNT(*) > 1
                ) duplicate_titles
                """, Integer.class);
        assertEquals(0, duplicateSourceTitles);

        Plant plant = plants.save(new Plant("Testus plantus", "Test plant", null, null));
        DataSource source = sources.save(new DataSource("Vector test source", "Test", 2026,
                "10.0000/vector-test", "https://example.test", "ARTICLE", OffsetDateTime.now(ZoneOffset.UTC)));
        UserAccount author = users.save(new UserAccount(
                "author-" + UUID.randomUUID() + "@example.test", "Author", "hash", UserRole.EXPERT));
        UserAccount reviewer = users.save(new UserAccount(
                "reviewer-" + UUID.randomUUID() + "@example.test", "Reviewer", "hash", UserRole.EXPERT));
        KnowledgeClaim claim = claims.save(new KnowledgeClaim(plant, null, ClaimType.INDICATION,
                "Vector test claim", "Test claim text", null, source, "p. 1", author));
        claimService.review(claim.getId(),
                new ClaimReviewRequest(ReviewStatus.VERIFIED, "Test-only review"), reviewer);

        String vector = vector(0);
        jdbcTemplate.update("""
                UPDATE knowledge_claims
                SET embedding = CAST(? AS vector), embedding_model = ?
                WHERE id = ?
                """, vector, VECTOR_MODEL_ID, claim.getId());

        var matches = claims.findNearestByEmbedding(
                ReviewStatus.VERIFIED.name(), VECTOR_MODEL_ID, vector, PageRequest.of(0, 5));

        assertFalse(matches.isEmpty());
        assertEquals(claim.getId(), matches.get(0).getId());
    }

    private static String vector(int hotIndex) {
        StringBuilder value = new StringBuilder("[");
        for (int i = 0; i < 384; i++) {
            if (i > 0) value.append(',');
            value.append(i == hotIndex ? '1' : '0');
        }
        return value.append(']').toString();
    }
}
