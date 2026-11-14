package kz.iitu.medicineplatform.assistant;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
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
import kz.iitu.medicineplatform.search.EmbeddingIndexer;
import kz.iitu.medicineplatform.search.EmbeddingService;
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
    private static final List<String> SAFETY_TERMS = List.of("дозиров", "доза", "дозу", "дозы", "сколько принимать", "как пить",
            "беремен", "жүктілік", "жүкті", "дет", "ребен", "ребён", "бала", "балалар", "емізу",
            "взаимодейств", "совместим", "антидепрессант", "өзара әрекеттесу", "дәрімен үйлес",
            "рак", "инфаркт", "инсульт", "диабет", "диагноз", "лечить", "лечение", "вылеч", "емдеу",
            "dose", "dosage", "pregnan", "breastfeed", "child", "children", "interact", "treat", "diagnos");
    private static final String SAFETY_NOTICE = "Я не могу давать схемы лечения, оценивать безопасность для конкретного человека или советовать сочетания. Обратитесь к врачу или фармацевту; эта платформа предназначена только для поиска справочных сведений.";
    private static final String NO_EVIDENCE = "В проверенной базе знаний нет подходящих сведений. Я не буду дополнять ответ догадками.";

    private final AssistantConversationRepository conversations;
    private final AssistantMessageRepository messages;
    private final AssistantMessageSourceRepository messageSources;
    private final KnowledgeClaimRepository claims;
    private final AssistantAnswerGenerator generator;
    private final SourceExtractiveAnswerGenerator extractiveFallback;
    private final EmbeddingService embeddingService;
    private final EmbeddingIndexer embeddingIndexer;

    public AssistantService(AssistantConversationRepository conversations, AssistantMessageRepository messages,
                            AssistantMessageSourceRepository messageSources, KnowledgeClaimRepository claims,
                            AssistantAnswerGenerator generator, SourceExtractiveAnswerGenerator extractiveFallback,
                            EmbeddingService embeddingService,
                            org.springframework.beans.factory.ObjectProvider<EmbeddingIndexer> indexerProvider) {
        this.conversations = conversations;
        this.messages = messages;
        this.messageSources = messageSources;
        this.claims = claims;
        this.generator = generator;
        this.extractiveFallback = extractiveFallback;
        this.embeddingService = embeddingService;
        this.embeddingIndexer = indexerProvider.getIfAvailable();
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

        // Hybrid search: combine keyword-based and vector-based results
        java.util.LinkedHashMap<Long, KnowledgeClaim> merged = new java.util.LinkedHashMap<>();

        // 1. Keyword search
        java.util.LinkedHashSet<KnowledgeClaim> keywordCandidates = new java.util.LinkedHashSet<>();
        for (String term : terms) {
            keywordCandidates.addAll(claims.searchVerifiedCandidates(ReviewStatus.VERIFIED, term, PageRequest.of(0, 10)));
            if (keywordCandidates.size() >= 30) break;
        }

        // 2. Vector similarity search
        try {
            float[] queryEmbedding = embeddingService.embed(normalizedQuestion);
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (int i = 0; i < queryEmbedding.length; i++) {
                if (i > 0) sb.append(',');
                sb.append(queryEmbedding[i]);
            }
            sb.append(']');
            String vectorStr = sb.toString();
            List<KnowledgeClaim> vectorCandidates = claims.findNearestByEmbedding(
                    ReviewStatus.VERIFIED.name(), vectorStr, PageRequest.of(0, 10));
            for (KnowledgeClaim claim : vectorCandidates) {
                keywordCandidates.add(claim);
            }
        } catch (Exception e) {
            // Vector search failed — fall back to keyword-only
        }

        if (keywordCandidates.isEmpty()) {
            keywordCandidates.addAll(claims.findByReviewStatus(ReviewStatus.VERIFIED,
                    PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "reviewedAt"))).getContent());
        }

        // Score and rank: keyword matches get higher priority, vector matches supplement
        return keywordCandidates.stream()
                .map(claim -> Map.entry(claim, score(claim, terms)))
                .filter(entry -> entry.getValue() > 0)
                .sorted(Map.Entry.<KnowledgeClaim, Integer>comparingByValue().reversed())
                .limit(10)  // Increased from 5 to 10 for hybrid search
                .map(entry -> KnowledgeClaimResponse.from(entry.getKey()))
                .toList();
    }

    private int score(KnowledgeClaim claim, Set<String> terms) {
        String searchable = String.join(" ", claim.getTitle(), claim.getClaimText(),
                claim.getPlant() == null ? "" : String.join(" ", claim.getPlant().getScientificName(),
                        claim.getPlant().getDisplayName(), nullToEmpty(claim.getPlant().getNameRu()),
                        nullToEmpty(claim.getPlant().getNameKk()), nullToEmpty(claim.getPlant().getSynonyms())),
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

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private kz.iitu.medicineplatform.source.DataSource sourceFor(KnowledgeClaimResponse response) {
        return claims.findById(response.id()).orElseThrow().getSource();
    }
}
