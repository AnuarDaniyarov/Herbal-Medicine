package kz.iitu.medicineplatform.assistant;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "assistant_messages")
public class AssistantMessage {
    public enum Role { USER, ASSISTANT }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private AssistantConversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_role", nullable = false, length = 20)
    private Role role;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "response_status", length = 40)
    private String responseStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected AssistantMessage() { }
    public AssistantMessage(AssistantConversation conversation, Role role, String content, String responseStatus) {
        this.conversation = conversation;
        this.role = role;
        this.content = content;
        this.responseStatus = responseStatus;
    }
    public Long getId() { return id; }
    public AssistantConversation getConversation() { return conversation; }
    public Role getRole() { return role; }
    public String getContent() { return content; }
    public String getResponseStatus() { return responseStatus; }

    @PrePersist
    void onCreate() { createdAt = OffsetDateTime.now(ZoneOffset.UTC); }
}
