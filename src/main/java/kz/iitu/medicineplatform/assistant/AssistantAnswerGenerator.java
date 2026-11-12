package kz.iitu.medicineplatform.assistant;

import java.util.List;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaimResponse;

/** Implementations may explain retrieved evidence, but may not introduce unsupported facts. */
public interface AssistantAnswerGenerator {
    AssistantGeneration generate(String question, List<KnowledgeClaimResponse> evidence);
}
