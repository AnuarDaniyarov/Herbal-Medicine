package kz.iitu.medicineplatform;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import kz.iitu.medicineplatform.activity.ActivityMeasurement;
import kz.iitu.medicineplatform.activity.ActivityMeasurementRepository;
import kz.iitu.medicineplatform.activity.Assay;
import kz.iitu.medicineplatform.activity.AssayRepository;
import kz.iitu.medicineplatform.extract.Extract;
import kz.iitu.medicineplatform.extract.ExtractRepository;
import kz.iitu.medicineplatform.compound.Compound;
import kz.iitu.medicineplatform.compound.CompoundRepository;
import kz.iitu.medicineplatform.plant.Plant;
import kz.iitu.medicineplatform.plant.PlantCompoundRelationship;
import kz.iitu.medicineplatform.plant.PlantCompoundRelationshipRepository;
import kz.iitu.medicineplatform.plant.PlantRepository;
import kz.iitu.medicineplatform.sample.PlantSample;
import kz.iitu.medicineplatform.sample.PlantSampleRepository;
import kz.iitu.medicineplatform.prediction.PredictionRepository;
import kz.iitu.medicineplatform.prediction.ModelVersionRepository;
import kz.iitu.medicineplatform.prediction.ModelVersion;
import kz.iitu.medicineplatform.source.DataSource;
import kz.iitu.medicineplatform.source.DataSourceRepository;
import kz.iitu.medicineplatform.knowledge.ClaimType;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaim;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaimRepository;
import kz.iitu.medicineplatform.knowledge.ReviewStatus;
import kz.iitu.medicineplatform.auth.UserAccount;
import kz.iitu.medicineplatform.auth.UserAccountRepository;
import kz.iitu.medicineplatform.auth.UserRole;
import kz.iitu.medicineplatform.assistant.AssistantConversationRepository;
import kz.iitu.medicineplatform.assistant.AssistantMessageRepository;
import kz.iitu.medicineplatform.assistant.AssistantMessageSourceRepository;
import kz.iitu.medicineplatform.assistant.AssistantRequest;
import kz.iitu.medicineplatform.assistant.AssistantService;
import kz.iitu.medicineplatform.preparation.Preparation;
import kz.iitu.medicineplatform.preparation.PreparationRepository;
import kz.iitu.medicineplatform.preparation.PreparationIngredientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MedicinePlatformApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlantRepository plants;

    @Autowired
    private CompoundRepository compounds;

    @Autowired
    private PlantCompoundRelationshipRepository relationships;

    @Autowired
    private DataSourceRepository sources;

    @Autowired
    private AssayRepository assays;

    @Autowired
    private ActivityMeasurementRepository measurements;

    @Autowired
    private PlantSampleRepository samples;

    @Autowired
    private ExtractRepository extracts;

    @Autowired
    private PredictionRepository predictions;

    @Autowired
    private ModelVersionRepository modelVersions;

    @Autowired
    private KnowledgeClaimRepository knowledgeClaims;

    @Autowired
    private PreparationRepository preparations;

    @Autowired
    private PreparationIngredientRepository preparationIngredients;

    @Autowired
    private AssistantConversationRepository assistantConversations;

    @Autowired
    private AssistantMessageRepository assistantMessages;

    @Autowired
    private AssistantMessageSourceRepository assistantMessageSources;

    @Autowired
    private UserAccountRepository userAccounts;

    @Autowired
    private AssistantService assistantService;

    @BeforeEach
    void clearDatabase() {
        assistantMessageSources.deleteAll();
        assistantMessages.deleteAll();
        assistantConversations.deleteAll();
        knowledgeClaims.deleteAll();
        preparationIngredients.deleteAll();
        preparations.deleteAll();
        predictions.deleteAll();
        measurements.deleteAll();
        relationships.deleteAll();
        compounds.deleteAll();
        plants.deleteAll();
        sources.deleteAll();
    }

    @Test
    void assistantRetrievesOnlyVerifiedClaimsAndReturnsTheirSources() {
        Plant plant = plants.save(new Plant("Assistant species", "Assistant plant", null, null));
        DataSource source = sources.save(new DataSource("Verified research paper", "Research group", 2025,
                "10.0000/assistant", "https://example.org/assistant", "ARTICLE", OffsetDateTime.now(ZoneOffset.UTC)));
        var claim = knowledgeClaims.save(new KnowledgeClaim(plant, null, ClaimType.INDICATION,
                "Research topic evidence", "This verified statement discusses the research topic in a laboratory assay.",
                null, source, "p. 4", null));
        claim.review(null, ReviewStatus.VERIFIED, null);
        knowledgeClaims.save(claim);
        UserAccount user = userAccounts.save(new UserAccount("assistant-" + UUID.randomUUID() + "@example.test", "Assistant Test",
                "not-a-real-login-hash", UserRole.USER));

        var response = assistantService.ask(new AssistantRequest("research topic", null), user);

        assertEquals("EVIDENCE_FOUND", response.status());
        assertEquals("SOURCE_EXTRACTIVE", response.answerMode());
        assertTrue(!response.isAiGenerated());
        assertEquals(1, response.citations().size());
        assertEquals("Verified research paper", response.citations().getFirst().source().title());
    }

    @Test
    void unverifiedPreparationsNeverAppearInPublicCatalogOrDetails() throws Exception {
        DataSource source = sources.save(new DataSource("Registry source", "Agency", 2025,
                "10.0000/registry", "https://example.org/registry", "REGISTRY", OffsetDateTime.now(ZoneOffset.UTC)));
        Preparation preparation = preparations.save(new Preparation("Unreviewed product", "capsule",
                "Example manufacturer", "REG-TEST", "KZ", "Unverified fixture", source));

        mockMvc.perform(get("/api/preparations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
        mockMvc.perform(get("/api/preparations/{id}", preparation.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void unreviewedClaimsStayOutOfPublicKnowledgeEndpointAndExpertQueueIsProtected() throws Exception {
        Plant plant = plants.save(new Plant("Review species", "Review plant", null, null));
        DataSource source = sources.save(new DataSource("Source for review", "Researcher", 2025,
                "10.0000/review", "https://example.org/review", "ARTICLE", OffsetDateTime.now(ZoneOffset.UTC)));
        knowledgeClaims.save(new KnowledgeClaim(plant, null, ClaimType.INDICATION,
                "Research finding", "A source-backed research statement.", null, source, "p. 12", null));

        mockMvc.perform(get("/api/expert/claims/queue"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/knowledge/claims").param("plantId", plant.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void catalogSearchReturnsPlantCompoundAndSourceProvenance() throws Exception {
        Plant plant = plants.save(new Plant("Example species", "Example plant", "Example family", "Test plant"));
        Compound compound = compounds.save(new Compound(
                "Example compound", "CCO", null, "TEST-1", "C2H6O", "UNVERIFIED"));
        DataSource source = sources.save(new DataSource(
                "Example paper", "A. Author", 2024, "10.0000/example", "https://example.org/paper",
                "ARTICLE", OffsetDateTime.now(ZoneOffset.UTC)));
        relationships.save(new PlantCompoundRelationship(
                plant, compound, "leaf", "batch-1", source, "Example provenance record"));
        Assay assay = assays.save(new Assay(
                "test-assay", "Test assay", "Test target", "IN_VITRO", "Test description", "uM"));
        measurements.save(new ActivityMeasurement(
                compound, assay, source, new BigDecimal("1.25000000"), "uM", "=", "Test conditions", "UNVERIFIED"));

        mockMvc.perform(get("/api/plants").param("query", "example"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].scientificName").value("Example species"));

        mockMvc.perform(get("/api/plants").param("family", "example family")
                        .param("sort", "scientificName,desc").param("page", "0").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].family").value("Example family"))
                .andExpect(jsonPath("$.size").value(1));

        mockMvc.perform(get("/api/plants/{id}/compounds", plant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Example compound"));

        mockMvc.perform(get("/api/compounds").param("query", "example").param("plantId", plant.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].structureStatus").value("UNVERIFIED"));

        mockMvc.perform(get("/api/compounds/{id}", compound.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plantOccurrences[0].source.doi").value("10.0000/example"));

        mockMvc.perform(get("/api/compounds/{id}/measurements", compound.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assay.activityCode").value("test-assay"))
                .andExpect(jsonPath("$[0].source.id").value(source.getId()));

        mockMvc.perform(get("/api/sources/{id}", source.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Example paper"));
    }

    @Test
    void predictionStubIsExplicitlyMarkedAndSavedToHistory() throws Exception {
        ModelVersion demoVersion = modelVersions.findAll().stream()
                .filter(version -> "demo".equals(version.getActivityCode()))
                .findFirst().orElseThrow();
        assertTrue(demoVersion.isActive());

        String request = """
                {
                  "smiles": "CCO",
                  "activityCode": "demo"
                }
                """;

        mockMvc.perform(post("/api/predictions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DEMO"))
                .andExpect(jsonPath("$.isDemo").value(true))
                .andExpect(jsonPath("$.probability").doesNotExist())
                .andExpect(jsonPath("$.warning").value(org.hamcrest.Matchers.containsString("no inference model")))
                .andExpect(jsonPath("$.predictionId").exists());

        long id = predictions.findAll().getFirst().getId();
        mockMvc.perform(get("/api/predictions/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelName").value("prediction-api-placeholder"));
    }

    @Test
    void invalidPredictionAndMissingPlantReturnStructuredErrors() throws Exception {
        mockMvc.perform(post("/api/predictions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"smiles\":\" \",\"activityCode\":\"demo\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("smiles"));

        mockMvc.perform(get("/api/plants/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.path").value("/api/plants/999999"))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
    }

    @Test
    void comparisonReturnsPerCandidateFactsAndRejectsUnknownActivity() throws Exception {
        Compound first = compounds.save(new Compound("First compound", "CCO", null, "C-1", null, "UNVERIFIED"));
        Compound second = compounds.save(new Compound("Second compound", "CCN", null, "C-2", null, "UNVERIFIED"));
        String request = "{\"candidateIds\":[" + first.getId() + "," + second.getId()
                + "],\"activityCode\":\"demo\"}";

        mockMvc.perform(post("/api/comparisons").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.candidates.length()").value(2))
                .andExpect(jsonPath("$.candidates[0].measurements").isArray())
                .andExpect(jsonPath("$.notice").exists());

        mockMvc.perform(post("/api/comparisons").contentType(MediaType.APPLICATION_JSON)
                        .content(request.replace("demo", "unknown")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"));

        mockMvc.perform(post("/api/comparisons").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"candidateIds\":[987654,987655],\"activityCode\":\"demo\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
    }

    @Test
    void candidateReportStreamsCsvAndCorsAllowsLocalAngularOrigin() throws Exception {
        compounds.save(new Compound("CSV compound", "CCO", null, "CSV-1", "C2H6O", "UNVERIFIED"));

        var csvRequest = mockMvc.perform(get("/api/reports/candidates").param("activityCode", "demo"))
                .andExpect(request().asyncStarted())
                .andReturn();
        mockMvc.perform(asyncDispatch(csvRequest))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string("Content-Disposition", org.hamcrest.Matchers.containsString("plant-candidates.csv")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.containsString("CSV compound")));

        mockMvc.perform(get("/api/assays").header("Origin", "http://localhost:4200"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    void plantSampleExtractAndExtractMeasurementKeepTheirProvenance() throws Exception {
        Plant plant = plants.save(new Plant("Sample species", "Sample plant", "Family", "Description"));
        PlantSample sample = samples.save(new PlantSample(plant, "leaf", "Kazakhstan", "LOT-1",
                java.time.LocalDate.of(2025, 5, 1), "field notes"));
        Extract extract = extracts.save(new Extract(sample, "maceration", "ethanol", "room temperature", "EXT-1"));
        Assay assay = assays.findAll().getFirst();
        DataSource source = sources.save(new DataSource("Extract paper", null, null, null, null, "ARTICLE",
                OffsetDateTime.now(ZoneOffset.UTC)));
        measurements.save(new ActivityMeasurement(extract, assay, source, new BigDecimal("2.5"), "mg/mL", "=",
                "extract assay", "UNVERIFIED"));

        mockMvc.perform(get("/api/plants/{id}/samples", plant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].plantPart").value("leaf"));
        mockMvc.perform(get("/api/samples/{id}/extracts", sample.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].solvent").value("ethanol"));
        mockMvc.perform(get("/api/extracts/{id}/measurements", extract.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].compoundId").doesNotExist())
                .andExpect(jsonPath("$[0].extract.sample.plantId").value(plant.getId()))
                .andExpect(jsonPath("$[0].source.title").value("Extract paper"));
    }

    @Test
    void activityMeasurementsPreserveLargeReportedValues() {
        Compound compound = compounds.save(new Compound("Large value compound", "CCO", null, "LARGE-1", null,
                "UNVERIFIED"));
        Assay assay = assays.findAll().getFirst();
        DataSource source = sources.save(new DataSource("Large value source", null, null, null, null, "ARTICLE",
                OffsetDateTime.now(ZoneOffset.UTC)));
        BigDecimal reportedValue = new BigDecimal("2818382931264450.123456");

        ActivityMeasurement stored = measurements.save(new ActivityMeasurement(compound, assay, source, reportedValue,
                "nM", "=", null, "UNVERIFIED"));

        assertEquals(0, stored.getValue().compareTo(reportedValue));
    }

    @Test
    void openApiDocumentIsAvailable() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.paths['/api/plants']").exists())
                .andExpect(jsonPath("$.paths['/api/predictions']").exists())
                .andExpect(jsonPath("$.paths['/api/comparisons']").exists())
                .andExpect(jsonPath("$.paths['/api/reports/candidates']").exists());
    }
}
