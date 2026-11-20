package kz.iitu.medicineplatform.assistant;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssistantMessageSourceRepository extends JpaRepository<AssistantMessageSource, Long> {
    java.util.List<AssistantMessageSource> findByMessage_Conversation_IdOrderByMessage_IdAsc(Long conversationId);
}
