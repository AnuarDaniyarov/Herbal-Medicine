package kz.iitu.medicineplatform.favorite;

public record AddFavoriteRequest(Long plantId, Long compoundId, Long preparationId) {
}
