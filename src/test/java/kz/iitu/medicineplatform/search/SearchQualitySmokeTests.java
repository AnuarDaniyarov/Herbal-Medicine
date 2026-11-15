package kz.iitu.medicineplatform.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import kz.iitu.medicineplatform.knowledge.ClaimType;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaim;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaimRepository;
import kz.iitu.medicineplatform.knowledge.ReviewStatus;
import kz.iitu.medicineplatform.plant.Plant;
import kz.iitu.medicineplatform.plant.PlantRepository;
import kz.iitu.medicineplatform.source.DataSource;
import kz.iitu.medicineplatform.source.DataSourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
class SearchQualitySmokeTests {

    @Autowired WebApplicationContext context;
    @Autowired PlantRepository plants;
    @Autowired DataSourceRepository sources;
    @Autowired KnowledgeClaimRepository claims;
    @Autowired SearchService searchService;

    private MockMvc mockMvc;
    private String marker;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        marker = "insommarker-" + UUID.randomUUID();

        Plant plant = plants.save(new Plant("Valeriana officinalis " + marker, "Valerian " + marker,
                "Caprifoliaceae", null, "Валериана", "Дәрілік шүйгіншөп", "garden valerian",
                null, null, "VERIFIED"));
        DataSource source = sources.save(new DataSource("WHO Monograph " + marker, "WHO", 2002,
                "10.0000/" + marker, "https://example.org/" + marker, "MONOGRAPH",
                OffsetDateTime.now(ZoneOffset.UTC)));
        KnowledgeClaim claim = claims.save(new KnowledgeClaim(plant, null, ClaimType.INDICATION,
                "Бессонница и нарушения сна " + marker,
                "Традиционно применяется при нарушениях сна и бессоннице. Marker " + marker,
                null, source, "Vol. II", null));
        claim.review(null, ReviewStatus.VERIFIED, null);
        claims.save(claim);
    }

    @Test
    void searchReturnsVerifiedClaimForInsomniaQuery() {
        SearchDtos.SearchResponse response = searchService.search(marker, null, 0, 10);

        assertFalse(response.results().isEmpty());
        assertTrue(response.claimsCount() >= 1);
        assertTrue(response.results().stream().anyMatch(r -> r.verified() && "claim".equals(r.type())));
        assertEquals("keyword", response.searchMode());
    }

    @Test
    void searchEndpointExposesVerifiedBadgeFields() throws Exception {
        mockMvc.perform(get("/api/search").param("q", marker))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.claimsCount").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.results[0].verified").value(true))
                .andExpect(jsonPath("$.results[0].reviewStatus").value("VERIFIED"));
    }

    @Test
    void unknownQueryDoesNotInventClaims() {
        SearchDtos.SearchResponse response = searchService.search("unicornium-zzzunknown-" + UUID.randomUUID(), null, 0, 10);

        assertEquals(0, response.claimsCount());
        assertTrue(response.results().stream().noneMatch(r -> "claim".equals(r.type())));
    }
}
