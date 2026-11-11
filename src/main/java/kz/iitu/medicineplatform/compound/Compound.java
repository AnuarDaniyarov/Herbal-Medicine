package kz.iitu.medicineplatform.compound;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "compounds")
public class Compound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String name;

    @Column(name = "canonical_smiles", nullable = false, length = 2000)
    private String canonicalSmiles;

    @Column(name = "inchi_key", unique = true, length = 100)
    private String inchiKey;

    @Column(name = "external_id", unique = true, length = 200)
    private String externalId;

    @Column(name = "molecular_formula", length = 100)
    private String molecularFormula;

    @Column(name = "structure_status", nullable = false, length = 30)
    private String structureStatus = "UNVERIFIED";

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Compound() {
    }

    public Compound(String name, String canonicalSmiles, String inchiKey, String externalId,
                    String molecularFormula, String structureStatus) {
        this.name = name;
        this.canonicalSmiles = canonicalSmiles;
        this.inchiKey = inchiKey;
        this.externalId = externalId;
        this.molecularFormula = molecularFormula;
        this.structureStatus = structureStatus;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCanonicalSmiles() { return canonicalSmiles; }
    public String getInchiKey() { return inchiKey; }
    public String getExternalId() { return externalId; }
    public String getMolecularFormula() { return molecularFormula; }
    public String getStructureStatus() { return structureStatus; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
