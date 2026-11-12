package kz.iitu.medicineplatform.preparation;

import kz.iitu.medicineplatform.compound.CompoundSummaryResponse;
import kz.iitu.medicineplatform.knowledge.ReviewStatus;
import kz.iitu.medicineplatform.plant.PlantResponse;
import kz.iitu.medicineplatform.source.SourceResponse;

public record PreparationIngredientResponse(Long id, String ingredientName, String quantity,
                                            PlantResponse plant, CompoundSummaryResponse compound,
                                            ReviewStatus reviewStatus, SourceResponse source) {
    public static PreparationIngredientResponse from(PreparationIngredient ingredient) {
        return new PreparationIngredientResponse(ingredient.getId(), ingredient.getIngredientName(),
                ingredient.getQuantity(), ingredient.getPlant() == null ? null : PlantResponse.from(ingredient.getPlant()),
                ingredient.getCompound() == null ? null : CompoundSummaryResponse.from(ingredient.getCompound()),
                ingredient.getReviewStatus(), SourceResponse.from(ingredient.getSource()));
    }
}
