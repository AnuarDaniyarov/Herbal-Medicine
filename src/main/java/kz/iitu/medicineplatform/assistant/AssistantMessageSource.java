package kz.iitu.medicineplatform.assistant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.iitu.medicineplatform.source.DataSource;

@Entity
@Table(name = "assistant_message_sources")
public class AssistantMessageSource {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private AssistantMessage message;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private DataSource source;

    @Column(name = "source_locator", length = 500)
    private String sourceLocator;

    @Column(columnDefinition = "text")
    private String excerpt;

    protected AssistantMessageSource() { }
    public AssistantMessageSource(AssistantMessage message, DataSource source, String sourceLocator, String excerpt) {
        this.message = message;
        this.source = source;
        this.sourceLocator = sourceLocator;
        this.excerpt = excerpt;
    }
}
