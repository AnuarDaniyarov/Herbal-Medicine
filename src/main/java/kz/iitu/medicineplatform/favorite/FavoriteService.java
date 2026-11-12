package kz.iitu.medicineplatform.favorite;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import kz.iitu.medicineplatform.auth.UserAccount;
import kz.iitu.medicineplatform.compound.Compound;
import kz.iitu.medicineplatform.compound.CompoundRepository;
import kz.iitu.medicineplatform.plant.Plant;
import kz.iitu.medicineplatform.plant.PlantRepository;
import kz.iitu.medicineplatform.preparation.Preparation;
import kz.iitu.medicineplatform.preparation.PreparationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class FavoriteService {

    private final UserFavoriteRepository favorites;
    private final PlantRepository plants;
    private final CompoundRepository compounds;
    private final PreparationRepository preparations;

    public FavoriteService(UserFavoriteRepository favorites,
                           PlantRepository plants,
                           CompoundRepository compounds,
                           PreparationRepository preparations) {
        this.favorites = favorites;
        this.plants = plants;
        this.compounds = compounds;
        this.preparations = preparations;
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> findAll(UserAccount user) {
        return favorites.findAllByUserIdWithSubjects(user.getId()).stream()
                .map(FavoriteResponse::from)
                .toList();
    }

    public FavoriteResponse add(UserAccount user, AddFavoriteRequest req) {
        int count = (req.plantId() != null ? 1 : 0)
                + (req.compoundId() != null ? 1 : 0)
                + (req.preparationId() != null ? 1 : 0);
        if (count != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Provide exactly one subject: plantId, compoundId, or preparationId");
        }

        if (req.plantId() != null) {
            Optional<UserFavorite> existing = favorites.findByUser_IdAndPlant_Id(user.getId(), req.plantId());
            if (existing.isPresent()) {
                return FavoriteResponse.from(existing.get());
            }
            Plant plant = plants.findById(req.plantId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant was not found"));
            return FavoriteResponse.from(favorites.save(new UserFavorite(user, plant, null, null)));
        }

        if (req.compoundId() != null) {
            Optional<UserFavorite> existing = favorites.findByUser_IdAndCompound_Id(user.getId(), req.compoundId());
            if (existing.isPresent()) {
                return FavoriteResponse.from(existing.get());
            }
            Compound compound = compounds.findById(req.compoundId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Compound was not found"));
            return FavoriteResponse.from(favorites.save(new UserFavorite(user, null, compound, null)));
        }

        Optional<UserFavorite> existing = favorites.findByUser_IdAndPreparation_Id(user.getId(), req.preparationId());
        if (existing.isPresent()) {
            return FavoriteResponse.from(existing.get());
        }
        Preparation prep = preparations.findById(req.preparationId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Preparation was not found"));
        return FavoriteResponse.from(favorites.save(new UserFavorite(user, null, null, prep)));
    }

    public void remove(UserAccount user, Long id) {
        UserFavorite fav = favorites.findByIdAndUser_Id(id, user.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite was not found"));
        favorites.delete(fav);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> check(UserAccount user, Long plantId, Long compoundId, Long preparationId) {
        if (user == null) {
            return Map.of("isFavorite", false);
        }

        Optional<UserFavorite> fav = Optional.empty();
        if (plantId != null) {
            fav = favorites.findByUser_IdAndPlant_Id(user.getId(), plantId);
        } else if (compoundId != null) {
            fav = favorites.findByUser_IdAndCompound_Id(user.getId(), compoundId);
        } else if (preparationId != null) {
            fav = favorites.findByUser_IdAndPreparation_Id(user.getId(), preparationId);
        }

        if (fav.isPresent()) {
            return Map.of("isFavorite", true, "favoriteId", fav.get().getId());
        }
        return Map.of("isFavorite", false);
    }
}
