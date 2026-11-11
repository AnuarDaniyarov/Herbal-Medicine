package kz.iitu.medicineplatform.plant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.iitu.medicineplatform.compound.Compound;
import kz.iitu.medicineplatform.source.DataSource;
import kz.iitu.medicineplatform.sample.PlantSample;
import kz.iitu.medicineplatform.extract.Extract;

@Entity
@Table(name = "plant_compound_relationships")
public class PlantCompoundRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compound_id", nullable = false)
    private Compound compound;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id")
    private PlantSample sample;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extract_id")
    private Extract extract;

    @Column(name = "plant_part", length = 250)
    private String plantPart;

    @Column(name = "sample_or_batch", length = 250)
    private String sampleOrBatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private DataSource source;

    @Column(columnDefinition = "text")
    private String notes;

    protected PlantCompoundRelationship() {
    }

    public PlantCompoundRelationship(Plant plant, Compound compound, String plantPart, String sampleOrBatch,
                                     DataSource source, String notes) {
        this.plant = plant;
        this.compound = compound;
        this.plantPart = plantPart;
        this.sampleOrBatch = sampleOrBatch;
        this.source = source;
        this.notes = notes;
    }

    public PlantCompoundRelationship(Plant plant, Compound compound, PlantSample sample, Extract extract,
                                     String plantPart, String sampleOrBatch, DataSource source, String notes) {
        this(plant, compound, plantPart, sampleOrBatch, source, notes);
        if (sample != null && sample.getPlant() != plant) {
            throw new IllegalArgumentException("sample must belong to the linked plant");
        }
        if (extract != null && (sample == null || extract.getSample() != sample)) {
            throw new IllegalArgumentException("extract must belong to the linked sample");
        }
        this.sample = sample;
        this.extract = extract;
    }

    public Long getId() { return id; }
    public Plant getPlant() { return plant; }
    public Compound getCompound() { return compound; }
    public PlantSample getSample() { return sample; }
    public Extract getExtract() { return extract; }
    public String getPlantPart() { return plantPart; }
    public String getSampleOrBatch() { return sampleOrBatch; }
    public DataSource getSource() { return source; }
    public String getNotes() { return notes; }
}
