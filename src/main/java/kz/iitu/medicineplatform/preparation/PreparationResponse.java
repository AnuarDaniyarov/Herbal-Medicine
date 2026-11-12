package kz.iitu.medicineplatform.preparation;

import kz.iitu.medicineplatform.knowledge.ReviewStatus;
import kz.iitu.medicineplatform.source.SourceResponse;

public record PreparationResponse(Long id, String name, String dosageForm, String manufacturer,
                                  String registrationNumber, String registrationCountry, String description,
                                  ReviewStatus reviewStatus, SourceResponse source) {
    public static PreparationResponse from(Preparation preparation) {
        return new PreparationResponse(preparation.getId(), preparation.getName(), preparation.getDosageForm(),
                preparation.getManufacturer(), preparation.getRegistrationNumber(),
                preparation.getRegistrationCountry(), preparation.getDescription(), preparation.getReviewStatus(),
                SourceResponse.from(preparation.getSource()));
    }
}
