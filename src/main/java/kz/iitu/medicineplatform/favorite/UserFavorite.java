package kz.iitu.medicineplatform.favorite;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import kz.iitu.medicineplatform.auth.UserAccount;
import kz.iitu.medicineplatform.compound.Compound;
import kz.iitu.medicineplatform.plant.Plant;
import kz.iitu.medicineplatform.preparation.Preparation;

@Entity
@Table(name = "user_favorites")
public class UserFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id")
    private Plant plant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id")
    private Compound compound;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preparation_id")
    private Preparation preparation;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected UserFavorite() { }

    public UserFavorite(UserAccount user, Plant plant, Compound compound, Preparation preparation) {
        this.user = user;
        this.plant = plant;
        this.compound = compound;
        this.preparation = preparation;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    public Long getId() { return id; }
    public UserAccount getUser() { return user; }
    public Plant getPlant() { return plant; }
    public Compound getCompound() { return compound; }
    public Preparation getPreparation() { return preparation; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
