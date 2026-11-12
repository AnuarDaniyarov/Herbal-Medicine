package kz.iitu.medicineplatform.preparation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.iitu.medicineplatform.compound.Compound;
import kz.iitu.medicineplatform.knowledge.ReviewStatus;
import kz.iitu.medicineplatform.plant.Plant;
import kz.iitu.medicineplatform.source.DataSource;
import org.springframework.util.Assert;

@Entity
@Table(name = "preparation_ingredients")
public class PreparationIngredient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "preparation_id", nullable = false)
    private Preparation preparation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id")
    private Plant plant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id")
    private Compound compound;

    @Column(name = "ingredient_name", length = 500)
    private String ingredientName;

    @Column(length = 250)
    private String quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private DataSource source;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", nullable = false, length = 30)
    private ReviewStatus reviewStatus = ReviewStatus.UNVERIFIED;

    protected PreparationIngredient() { }

    public PreparationIngredient(Preparation preparation, Plant plant, Compound compound, String ingredientName,
                                 String quantity, DataSource source, ReviewStatus reviewStatus) {
        Assert.isTrue((plant == null) != (compound == null), "Exactly one plant or compound is required");
        this.preparation = preparation;
        this.plant = plant;
        this.compound = compound;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.source = source;
        this.reviewStatus = reviewStatus == null ? ReviewStatus.UNVERIFIED : reviewStatus;
    }

    public Long getId() { return id; }
    public Preparation getPreparation() { return preparation; }
    public Plant getPlant() { return plant; }
    public Compound getCompound() { return compound; }
    public String getIngredientName() { return ingredientName; }
    public String getQuantity() { return quantity; }
    public DataSource getSource() { return source; }
    public ReviewStatus getReviewStatus() { return reviewStatus; }
}
