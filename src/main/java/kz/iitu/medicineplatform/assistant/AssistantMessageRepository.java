package kz.iitu.medicineplatform.assistant;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssistantMessageRepository extends JpaRepository<AssistantMessage, Long> {
    List<AssistantMessage> findByConversation_IdOrderByCreatedAtAsc(Long conversationId);
    java.util.Optional<AssistantMessage> findFirstByConversation_IdAndRoleOrderByCreatedAtDesc(
            Long conversationId, AssistantMessage.Role role);
}
