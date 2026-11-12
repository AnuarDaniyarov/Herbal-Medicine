package kz.iitu.medicineplatform.assistant;

import java.util.List;

public record AssistantGeneration(String answer, String mode, boolean isAiGenerated, List<Long> citedClaimIds) { }
