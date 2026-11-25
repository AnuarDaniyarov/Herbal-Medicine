package kz.iitu.medicineplatform.prediction;

import kz.iitu.medicineplatform.compound.Compound;
import kz.iitu.medicineplatform.compound.CompoundRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import kz.iitu.medicineplatform.api.RateLimitExceededException;

@Service
public class DefaultPredictionService implements PredictionService {

    private final PredictionRepository predictions;
    private final ModelVersionRepository modelVersions;
    private final CompoundRepository compounds;
    private final PredictionProvider provider;

    public DefaultPredictionService(PredictionRepository predictions,
                                   ModelVersionRepository modelVersions,
                                   CompoundRepository compounds,
                                   PredictionProvider provider) {
        this.predictions = predictions;
        this.modelVersions = modelVersions;
        this.compounds = compounds;
        this.provider = provider;
    }

    @Override
    @Transactional
    public PredictionResponse predict(PredictionRequest request) {
        return predict(request, null);
    }

    @Override
    @Transactional
    public PredictionResponse predict(PredictionRequest request, kz.iitu.medicineplatform.auth.UserAccount account) {
        if (account == null || account.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }
        long hourlyCount = predictions.countByCreatedBy_IdAndCreatedAtAfter(
                account.getId(), java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC).minusHours(1));
        if (hourlyCount >= 30) {
            throw new RateLimitExceededException(
                    "Prediction rate limit exceeded: maximum 30 predictions per hour.", 3600);
        }

        String smiles = request.smiles().trim();
        ModelVersion model = modelVersions
                .findFirstByActivityCodeAndActiveTrueOrderByCreatedAtDesc(request.activityCode())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No active model version exists for this activity code"));

        Compound compound = resolveCompound(request.compoundId(), smiles);
        PredictionResult result = provider.evaluate(smiles, request.activityCode(), model);
        Prediction prediction = new Prediction(compound, smiles, request.activityCode(), model,
                result.warning(), result.status(), account);
        prediction.setResult(result);
        prediction = predictions.save(prediction);
        return PredictionResponse.from(prediction);
    }

    @Override
    @Transactional(readOnly = true)
    public PredictionResponse findById(Long id) {
        return findById(id, null);
    }

    @Override
    @Transactional(readOnly = true)
    public PredictionResponse findById(Long id, kz.iitu.medicineplatform.auth.UserAccount account) {
        Prediction prediction = predictions.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Prediction with id " + id + " was not found"));
        if (prediction.getCreatedBy() == null || account == null
                || !prediction.getCreatedBy().getId().equals(account.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prediction with id " + id + " was not found");
        }
        return PredictionResponse.from(prediction);
    }

    private Compound resolveCompound(Long compoundId, String smiles) {
        if (compoundId != null) {
            Compound compound = compounds.findById(compoundId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Compound with id " + compoundId + " was not found"));
            if (!compound.getCanonicalSmiles().equals(smiles)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "SMILES does not match the selected compound");
            }
            return compound;
        }
        return compounds.findFirstByCanonicalSmiles(smiles).orElse(null);
    }
}
