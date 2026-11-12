package kz.iitu.medicineplatform.assistant;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssistantConversationRepository extends JpaRepository<AssistantConversation, Long> {
    Optional<AssistantConversation> findByIdAndUser_Id(Long id, Long userId);
    Page<AssistantConversation> findByUser_IdOrderByUpdatedAtDesc(Long userId, Pageable pageable);
}
