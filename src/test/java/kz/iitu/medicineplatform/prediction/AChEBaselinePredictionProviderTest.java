package kz.iitu.medicineplatform.prediction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.server.ResponseStatusException;

class AChEBaselinePredictionProviderTest {
    private static final String ACTIVITY = "imppat_ic50_e662faf14ac2";
    @TempDir Path temporaryDirectory;
    private Path modelPath;
    private AChEBaselinePredictionProvider provider;

    @BeforeEach
    void setUp() throws Exception {
        modelPath = temporaryDirectory.resolve("model.json");
        double[] weights = new double[256];
        new ObjectMapper().writeValue(modelPath.toFile(), Map.of(
                "schemaVersion", 1,
                "activityCode", ACTIVITY,
                "bias", 0.2,
                "weights", weights));
        provider = new AChEBaselinePredictionProvider(new ObjectMapper(), modelPath.toString());
    }

    @Test
    void returnsLearnedProbabilityAndExploratoryStatus() {
        PredictionResult result = provider.evaluate("C1CCCCC1", ACTIVITY, modelVersion());
        assertThat(result.status()).isEqualTo("ML_BASELINE");
        assertThat(result.probability().doubleValue()).isBetween(0.549, 0.550);
        assertThat(result.warning()).contains("not scaffold split");
        assertThat(result.label()).contains("IC50_10UM_THRESHOLD");
    }

    @Test
    void inferenceFeatureHashMatchesTheTrainerForSimpleSmiles() {
        double[] features = AChEBaselinePredictionProvider.features("CCO");
        Map<Integer, Double> expected = Map.of(
                4, -1.0 / Math.sqrt(7),
                10, 1.0 / Math.sqrt(7),
                91, -1.0 / Math.sqrt(7),
                116, -1.0 / Math.sqrt(7),
                171, 1.0 / Math.sqrt(7),
                215, -1.0 / Math.sqrt(7),
                236, -1.0 / Math.sqrt(7));
        assertThat(features).hasSize(256);
        expected.forEach((index, value) -> assertThat(features[index]).isCloseTo(value,
                org.assertj.core.data.Offset.offset(0.000001)));
        assertThat(java.util.stream.IntStream.range(0, features.length)
                .filter(index -> features[index] != 0.0).count()).isEqualTo(7);
    }

    @Test
    void rejectsUnbalancedSmilesRingNumbers() {
        assertThatThrownBy(() -> provider.evaluate("C1CC", ACTIVITY, modelVersion()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("format check failed");
    }

    @Test
    void reportsMissingModelArtifactAsUnavailable() throws Exception {
        Files.delete(modelPath);
        assertThatThrownBy(() -> provider.evaluate("CCO", ACTIVITY, modelVersion()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("model artifact is missing");
    }

    private static ModelVersion modelVersion() {
        return new ModelVersion("AChE baseline", "0.1.0", ACTIVITY, "ML_BASELINE", "local test", "{}", true);
    }
}
