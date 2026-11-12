package kz.iitu.medicineplatform.favorite;

import java.time.OffsetDateTime;

public record FavoriteResponse(Long id, String type, Long targetId, String title, String subtitle,
                               String route, OffsetDateTime createdAt) {

    public static FavoriteResponse from(UserFavorite favorite) {
        if (favorite.getPlant() != null) {
            var p = favorite.getPlant();
            String title = p.getNameRu() != null && !p.getNameRu().isBlank() ? p.getNameRu() : p.getDisplayName();
            String subtitle = p.getScientificName() + (p.getFamily() != null ? " · " + p.getFamily() : "");
            return new FavoriteResponse(favorite.getId(), "PLANT", p.getId(), title, subtitle,
                    "/plants/" + p.getId(), favorite.getCreatedAt());
        }
        if (favorite.getPreparation() != null) {
            var prep = favorite.getPreparation();
            String subtitle = (prep.getDosageForm() != null ? prep.getDosageForm() : "")
                    + (prep.getManufacturer() != null ? " · " + prep.getManufacturer() : "");
            return new FavoriteResponse(favorite.getId(), "PREPARATION", prep.getId(), prep.getName(), subtitle.trim(),
                    "/preparations/" + prep.getId(), favorite.getCreatedAt());
        }
        if (favorite.getCompound() != null) {
            var c = favorite.getCompound();
            String subtitle = c.getCanonicalSmiles();
            return new FavoriteResponse(favorite.getId(), "COMPOUND", c.getId(), c.getName(), subtitle,
                    "/compounds/" + c.getId(), favorite.getCreatedAt());
        }
        return new FavoriteResponse(favorite.getId(), "UNKNOWN", 0L, "Неизвестный объект", "",
                "/", favorite.getCreatedAt());
    }
}
