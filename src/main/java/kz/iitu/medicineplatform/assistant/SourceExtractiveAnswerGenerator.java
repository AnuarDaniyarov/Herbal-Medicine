package kz.iitu.medicineplatform.assistant;

import java.util.List;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaimResponse;
import java.util.List;
import org.springframework.stereotype.Component;

/** Safe initial provider: quotes verified database claims and does not claim to be an LLM. */
@Component
public class SourceExtractiveAnswerGenerator implements AssistantAnswerGenerator {
    @Override
    public AssistantGeneration generate(String question, List<KnowledgeClaimResponse> evidence) {
        return new AssistantGeneration(
                "В проверенной базе найдены связанные утверждения. Они приведены ниже по источникам без медицинской интерпретации.",
                "SOURCE_EXTRACTIVE", false, evidence.stream().map(KnowledgeClaimResponse::id).toList());
    }
}
