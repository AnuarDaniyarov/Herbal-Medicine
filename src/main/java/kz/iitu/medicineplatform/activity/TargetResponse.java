package kz.iitu.medicineplatform.activity;

public record TargetResponse(Long id, String normalizedIdentifier, String name, String organism) {
    public static TargetResponse from(Target target) {
        return new TargetResponse(target.getId(), target.getNormalizedIdentifier(), target.getName(), target.getOrganism());
    }
}
