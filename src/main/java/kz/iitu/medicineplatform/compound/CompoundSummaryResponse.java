package kz.iitu.medicineplatform.compound;

public record CompoundSummaryResponse(
        Long id,
        String name,
        String canonicalSmiles,
        String inchiKey,
        String externalId,
        String molecularFormula,
        String structureStatus
) {
    public static CompoundSummaryResponse from(Compound compound) {
        return new CompoundSummaryResponse(compound.getId(), compound.getName(), compound.getCanonicalSmiles(),
                compound.getInchiKey(), compound.getExternalId(), compound.getMolecularFormula(),
                compound.getStructureStatus());
    }
}
