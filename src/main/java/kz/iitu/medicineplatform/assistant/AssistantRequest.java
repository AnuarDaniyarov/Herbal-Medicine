package kz.iitu.medicineplatform.assistant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AssistantRequest(@NotBlank @Size(max = 2000) String question,
                               @Positive Long conversationId) { }
