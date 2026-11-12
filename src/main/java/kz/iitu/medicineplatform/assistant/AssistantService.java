package kz.iitu.medicineplatform.assistant;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import kz.iitu.medicineplatform.auth.UserAccount;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaim;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaimRepository;
import kz.iitu.medicineplatform.knowledge.KnowledgeClaimResponse;
import kz.iitu.medicineplatform.knowledge.ReviewStatus;
import kz.iitu.medicineplatform.api.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AssistantService {
    private static final Pattern TOKEN_SPLIT = Pattern.compile("[^\\p{L}\\p{N}]+");
    private static final Set<String> STOP_WORDS = Set.of("что", "это", "как", "для", "при", "есть", "можно",
            "какие", "какой", "какая", "про", "или", "the", "what", "with", "from", "and", "for");
    private static final List<String> SAFETY_TERMS = List.of("дозиров", "доза", "сколько принимать", "как пить",
            "беремен", "дет", "ребен", "ребён", "взаимодейств", "совместим", "антидепрессант",
            "рак", "инфаркт", "инсульт", "диабет", "диагноз", "лечить", "лечение", "вылеч");
    private static final String SAFETY_NOTICE = "Я не могу давать схемы лечения, оценивать безопасность для конкретного человека или советовать сочетания. Обратитесь к врачу или фармацевту; эта платформа предназначена только для поиска справочных сведений.";
    private static final String NO_EVIDENCE = "В проверенной базе знаний нет подходящих сведений. Я не буду дополнять ответ догадками.";

    private final AssistantConversationRepository conversations;
    private final AssistantMessageRepository messages;
    private final AssistantMessageSourceRepository messageSources;
    private final KnowledgeClaimRepository claims;
    private final AssistantAnswerGenerator generator;
    private final SourceExtractiveAnswerGenerator extractiveFallback;

    public AssistantService(AssistantConversationRepository conversations, AssistantMessageRepository messages,
                            AssistantMessageSourceRepository messageSources, KnowledgeClaimRepository claims,
                            AssistantAnswerGenerator generator, SourceExtractiveAnswerGenerator extractiveFallback) {
        this.conversations = conversations;
        this.messages = messages;
        this.messageSources = messageSources;
        this.claims = claims;
        this.generator = generator;
        this.extractiveFallback = extractiveFallback;
    }

    public AssistantResponse ask(AssistantRequest request, UserAccount account) {
        AssistantConversation conversation = request.conversationId() == null
                ? conversations.save(new AssistantConversation(account))
                : conversations.findByIdAndUser_Id(request.conversationId(), account.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation was not found"));
        conversation.touch();
        String rawQuestion = request.question().trim();
        String normalizedQuestion = Normalizer.normalize(rawQuestion.toLowerCase(Locale.ROOT), Normalizer.Form.NFKC);
        messages.save(new AssistantMessage(conversation, AssistantMessage.Role.USER, rawQuestion, null));

        if (requiresSafetyRedirect(normalizedQuestion)) {
            AssistantMessage response = messages.save(new AssistantMessage(conversation, AssistantMessage.Role.ASSISTANT,
                    SAFETY_NOTICE, "SAFETY_REDIRECT"));
            return new AssistantResponse(conversation.getId(), response.getContent(), "SAFETY_REDIRECT",
                    "SAFETY_RULE", false, SAFETY_NOTICE, List.of());
        }

        List<KnowledgeClaimResponse> evidence = retrieve(normalizedQuestion);
        if (evidence.isEmpty()) {
            AssistantMessage response = messages.save(new AssistantMessage(conversation, AssistantMessage.Role.ASSISTANT,
                    NO_EVIDENCE, "NO_EVIDENCE"));
            return new AssistantResponse(conversation.getId(), response.getContent(), "NO_EVIDENCE",
                    "SOURCE_EXTRACTIVE", false, SAFETY_NOTICE, List.of());
        }

        AssistantGeneration generated = generator.generate(rawQuestion, evidence);
        Set<Long> availableIds = evidence.stream().map(KnowledgeClaimResponse::id)
                .collect(java.util.stream.Collectors.toSet());
        if (generated.answer() == null || generated.answer().isBlank() || generated.citedClaimIds().isEmpty()
                || !availableIds.containsAll(generated.citedClaimIds())) {
            generated = extractiveFallback.generate(rawQuestion, evidence);
        }
        AssistantMessage response = messages.save(new AssistantMessage(conversation, AssistantMessage.Role.ASSISTANT,
                generated.answer(), "EVIDENCE_FOUND"));
        List<AssistantResponse.Citation> citations = new ArrayList<>();
        for (KnowledgeClaimResponse claim : evidence) {
            if (!generated.citedClaimIds().contains(claim.id())) continue;
            messageSources.save(new AssistantMessageSource(response, sourceFor(claim), claim.sourceLocator(), claim.claimText()));
            String subject = claim.plantName() != null ? claim.plantName() : claim.preparationName();
            citations.add(new AssistantResponse.Citation(claim.id(), claim.title(), claim.claimText(), subject,
                    claim.sourceLocator(), claim.source()));
        }
        return new AssistantResponse(conversation.getId(), generated.answer(), "EVIDENCE_FOUND", generated.mode(),
                generated.isAiGenerated(), SAFETY_NOTICE, citations);
    }

    @Transactional(readOnly = true)
    public PageResponse<AssistantHistoryEntry> history(UserAccount account, Pageable pageable) {
        var page = conversations.findByUser_IdOrderByUpdatedAtDesc(account.getId(), pageable)
                .map(conversation -> {
                    AssistantMessage question = messages.findFirstByConversation_IdAndRoleOrderByCreatedAtDesc(
                            conversation.getId(), AssistantMessage.Role.USER).orElse(null);
                    AssistantMessage answer = messages.findFirstByConversation_IdAndRoleOrderByCreatedAtDesc(
                            conversation.getId(), AssistantMessage.Role.ASSISTANT).orElse(null);
                    return new AssistantHistoryEntry(conversation.getId(), conversation.getUpdatedAt(),
                            question == null ? null : question.getContent(), answer == null ? null : answer.getContent(),
                            answer == null ? null : answer.getResponseStatus());
                });
        return PageResponse.from(page);
    }

    private List<KnowledgeClaimResponse> retrieve(String normalizedQuestion) {
        Set<String> terms = tokensFromNormalized(normalizedQuestion);
        if (terms.isEmpty()) return List.of();

        // Optimized candidate retrieval via targeted SQL query instead of full in-memory scan
        java.util.LinkedHashSet<KnowledgeClaim> candidates = new java.util.LinkedHashSet<>();
        for (String term : terms) {
            candidates.addAll(claims.searchVerifiedCandidates(ReviewStatus.VERIFIED, term, PageRequest.of(0, 10)));
            if (candidates.size() >= 30) break;
        }

        if (candidates.isEmpty()) {
            candidates.addAll(claims.findByReviewStatus(ReviewStatus.VERIFIED,
                    PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "reviewedAt"))).getContent());
        }

        return candidates.stream()
                .map(claim -> Map.entry(claim, score(claim, terms)))
                .filter(entry -> entry.getValue() > 0)
                .sorted(Map.Entry.<KnowledgeClaim, Integer>comparingByValue().reversed())
                .limit(5)
                .map(entry -> KnowledgeClaimResponse.from(entry.getKey()))
                .toList();
    }

    private int score(KnowledgeClaim claim, Set<String> terms) {
        String searchable = String.join(" ", claim.getTitle(), claim.getClaimText(),
                claim.getPlant() == null ? "" : claim.getPlant().getScientificName() + " " + claim.getPlant().getDisplayName(),
                claim.getPreparation() == null ? "" : claim.getPreparation().getName(),
                claim.getInteractionPartner() == null ? "" : claim.getInteractionPartner());
        String normalized = Normalizer.normalize(searchable.toLowerCase(Locale.ROOT), Normalizer.Form.NFKC);
        Set<String> fields = tokensFromNormalized(normalized);
        return (int) terms.stream().filter(fields::contains).count();
    }

    private Set<String> tokensFromNormalized(String normalized) {
        return TOKEN_SPLIT.splitAsStream(normalized).filter(token -> token.length() > 2 && !STOP_WORDS.contains(token))
                .collect(java.util.stream.Collectors.toSet());
    }

    private boolean requiresSafetyRedirect(String normalizedQuestion) {
        return SAFETY_TERMS.stream().anyMatch(normalizedQuestion::contains);
    }

    private kz.iitu.medicineplatform.source.DataSource sourceFor(KnowledgeClaimResponse response) {
        return claims.findById(response.id()).orElseThrow().getSource();
    }
}
