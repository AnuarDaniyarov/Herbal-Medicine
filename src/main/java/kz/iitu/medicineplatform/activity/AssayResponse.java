package kz.iitu.medicineplatform.activity;

public record AssayResponse(
        Long id,
        String activityCode,
        String name,
        String targetName,
        String assayType,
        String description,
        String standardUnit,
        TargetResponse target
) {
    public static AssayResponse from(Assay assay) {
        return new AssayResponse(assay.getId(), assay.getActivityCode(), assay.getName(), assay.getTargetName(),
                assay.getAssayType(), assay.getDescription(), assay.getStandardUnit(),
                assay.getTarget() == null ? null : TargetResponse.from(assay.getTarget()));
    }
}
