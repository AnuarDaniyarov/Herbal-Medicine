package kz.iitu.medicineplatform.preparation;

import kz.iitu.medicineplatform.api.PageResponse;
import kz.iitu.medicineplatform.knowledge.ReviewStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class PreparationService {
    private final PreparationRepository preparations;
    private final PreparationIngredientRepository ingredients;

    public PreparationService(PreparationRepository preparations, PreparationIngredientRepository ingredients) {
        this.preparations = preparations;
        this.ingredients = ingredients;
    }

    public PageResponse<PreparationResponse> search(String query, String dosageForm, Pageable page) {
        return PageResponse.from(preparations.search(normalize(query), normalize(dosageForm), ReviewStatus.VERIFIED, page)
                .map(PreparationResponse::from));
    }

    public PreparationResponse get(long id) {
        Preparation preparation = preparations.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Preparation was not found"));
        if (preparation.getReviewStatus() != ReviewStatus.VERIFIED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Preparation was not found");
        }
        return PreparationResponse.from(preparation);
    }

    public java.util.List<PreparationIngredientResponse> ingredients(long id) {
        get(id);
        return ingredients.findByPreparation_IdAndReviewStatusOrderByIdAsc(id, ReviewStatus.VERIFIED).stream()
                .map(PreparationIngredientResponse::from).toList();
    }

    private String normalize(String value) { return value == null || value.isBlank() ? "" : value.trim(); }
}
