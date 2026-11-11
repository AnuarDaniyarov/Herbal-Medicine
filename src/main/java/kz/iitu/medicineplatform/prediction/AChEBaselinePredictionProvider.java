package kz.iitu.medicineplatform.prediction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AChEBaselinePredictionProvider implements PredictionProvider {
    private static final String AChE_ACTIVITY = "imppat_ic50_e662faf14ac2";
    private static final int FEATURE_COUNT = 256;
    private static final String DEMO_WARNING =
            "DEMO ONLY: no inference model is connected. This is not a biological prediction or medical advice.";
    private static final String BASELINE_WARNING =
            "Exploratory ML baseline only: trained on 74 unverified IMPPAT AChE compounds; test set 16; " +
            "random compound split (not scaffold split). Original assay conditions are not fully harmonized. " +
            "SMILES receives format checks only. Not evidence of efficacy or safety and not medical advice.";

    private final ObjectMapper objectMapper;
    private final Path modelPath;
    private volatile JsonNode artifact;

    public AChEBaselinePredictionProvider(ObjectMapper objectMapper, String modelPath) {
        this.objectMapper = objectMapper;
        this.modelPath = Path.of(modelPath);
    }

    @Override
    public PredictionResult evaluate(String smiles, String activityCode, ModelVersion modelVersion) {
        if (!"ML_BASELINE".equalsIgnoreCase(modelVersion.getExecutionType())) {
            return new PredictionResult(null, null, null, null, null, "DEMO", DEMO_WARNING);
        }
        if (!AChE_ACTIVITY.equals(activityCode) || !AChE_ACTIVITY.equals(modelVersion.getActivityCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The exploratory model supports human AChE IC50 only");
        }
        validateSmilesFormat(smiles);
        JsonNode model = loadArtifact();
        double[] features = features(smiles);
        JsonNode weightsNode = model.path("weights");
        if (!weightsNode.isArray() || weightsNode.size() != FEATURE_COUNT) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The local AChE model artifact has an invalid format");
        }
        double score = model.path("bias").asDouble();
        for (int i = 0; i < FEATURE_COUNT; i++) {
            score += features[i] * weightsNode.get(i).asDouble();
        }
        score = Math.max(-30.0, Math.min(30.0, score));
        double probability = 1.0 / (1.0 + Math.exp(-score));
        double uncertainty = 4.0 * probability * (1.0 - probability);
        String label = probability >= 0.5 ? "LIKELY_ACTIVE_AT_IC50_10UM_THRESHOLD" : "LIKELY_BELOW_IC50_10UM_THRESHOLD";
        return new PredictionResult(label, null, BigDecimal.valueOf(probability), BigDecimal.valueOf(uncertainty),
                "EXPLORATORY", "ML_BASELINE", BASELINE_WARNING);
    }

    private JsonNode loadArtifact() {
        JsonNode current = artifact;
        if (current != null) return current;
        synchronized (this) {
            if (artifact != null) return artifact;
            if (!Files.isRegularFile(modelPath)) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "AChE model artifact is missing. Run scripts/Train-AcheBaseline.ps1 using the local processed dataset.");
            }
            try {
                JsonNode loaded = objectMapper.readTree(modelPath.toFile());
                if (loaded.path("schemaVersion").asInt() != 1 ||
                        !AChE_ACTIVITY.equals(loaded.path("activityCode").asText())) {
                    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The local AChE model version is unsupported");
                }
                artifact = loaded;
                return loaded;
            } catch (IOException exception) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Could not read the local AChE model artifact", exception);
            }
        }
    }

    private static void validateSmilesFormat(String smiles) {
        if (smiles == null || smiles.isBlank() || !smiles.matches("[A-Za-z0-9@+\\-\\[\\]()=:#$/\\\\.%*]+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "SMILES format check failed; this is not full chemical structure validation");
        }
        int branches = 0;
        boolean inBracket = false;
        Map<String, Integer> rings = new HashMap<>();
        for (int i = 0; i < smiles.length(); i++) {
            char token = smiles.charAt(i);
            if (token == '[') {
                if (inBracket) throw invalidSmiles();
                inBracket = true;
                continue;
            }
            if (token == ']') {
                if (!inBracket) throw invalidSmiles();
                inBracket = false;
                continue;
            }
            if (inBracket) continue;
            if (token == '(') branches++;
            if (token == ')' && --branches < 0) throw invalidSmiles();
            if (Character.isDigit(token)) {
                String ring = String.valueOf(token);
                rings.merge(ring, 1, Integer::sum);
            } else if (token == '%') {
                if (i + 2 >= smiles.length() || !Character.isDigit(smiles.charAt(i + 1)) || !Character.isDigit(smiles.charAt(i + 2))) {
                    throw invalidSmiles();
                }
                String ring = smiles.substring(i + 1, i + 3);
                rings.merge(ring, 1, Integer::sum);
                i += 2;
            }
        }
        if (inBracket || branches != 0 || rings.values().stream().anyMatch(count -> count != 2)) throw invalidSmiles();
    }

    private static ResponseStatusException invalidSmiles() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "SMILES format check failed; this is not full chemical structure validation");
    }

    static double[] features(String smiles) {
        double[] vector = new double[FEATURE_COUNT];
        String padded = "^" + smiles.trim() + "$";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (int width : new int[]{2, 3}) {
                for (int i = 0; i <= padded.length() - width; i++) {
                    byte[] hash = digest.digest(padded.substring(i, i + width).getBytes(StandardCharsets.UTF_8));
                    int value = ByteBuffer.wrap(hash, 0, Integer.BYTES).order(ByteOrder.LITTLE_ENDIAN).getInt();
                    int index = Math.abs(value % FEATURE_COUNT);
                    vector[index] += (value & Integer.MIN_VALUE) == 0 ? 1.0 : -1.0;
                }
            }
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
        double squaredNorm = 0.0;
        for (double value : vector) squaredNorm += value * value;
        double norm = Math.sqrt(squaredNorm);
        if (norm > 0.0) for (int i = 0; i < vector.length; i++) vector[i] /= norm;
        return vector;
    }
}
