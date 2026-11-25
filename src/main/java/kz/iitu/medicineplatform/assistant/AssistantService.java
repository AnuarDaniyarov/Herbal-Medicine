package kz.iitu.medicineplatform.assistant;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AssistantService {
    private static final Logger log = LoggerFactory.getLogger(AssistantService.class);
    private static final Pattern TOKEN_SPLIT = Pattern.compile("[^\\p{L}\\p{N}]+");
    private static final Set<String> STOP_WORDS = Set.of("что", "это", "как", "для", "при", "есть", "можно",
            "какие", "какой", "какая", "про", "или", "the", "what", "with", "from", "and", "for");
    private static final String SAFETY_NOTICE = SafetyPolicy.SAFETY_NOTICE;
    private static final String NO_EVIDENCE = "В проверенной базе знаний нет подходящих сведений. Я не буду дополнять ответ догадками.";

    private final AssistantConversationRepository conversations;
    private final AssistantMessageRepository messages;
    private final AssistantMessageSourceRepository messageSources;
    private final KnowledgeClaimRepository claims;
    private final AssistantAnswerGenerator generator;
    private final SourceExtractiveAnswerGenerator extractiveFallback;
    private final EmbeddingService embeddingService;
    private final EmbeddingIndexer embeddingIndexer;
    private final TransactionTemplate transactionTemplate;

    @Value("${search.vector.enabled:false}")
    private boolean vectorSearchEnabled;

    public AssistantService(AssistantConversationRepository conversations, AssistantMessageRepository messages,
                            AssistantMessageSourceRepository messageSources, KnowledgeClaimRepository claims,
                            AssistantAnswerGenerator generator, SourceExtractiveAnswerGenerator extractiveFallback,
                            EmbeddingService embeddingService,
                            org.springframework.beans.factory.ObjectProvider<EmbeddingIndexer> indexerProvider,
                            PlatformTransactionManager transactionManager) {
        this.conversations = conversations;
        this.messages = messages;
        this.messageSources = messageSources;
        this.claims = claims;
        this.generator = generator;
        this.extractiveFallback = extractiveFallback;
        this.embeddingService = embeddingService;
        this.embeddingIndexer = indexerProvider.getIfAvailable();
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    private record PreparedTurn(Long conversationId, List<KnowledgeClaimResponse> evidence, AssistantResponse earlyResponse) {}

    public AssistantResponse ask(AssistantRequest request, UserAccount account) {
        String rawQuestion = request.question().trim();

        // Шаг 1 (короткая транзакция): проверка прав, сохранение вопроса, поиск доказательств
        PreparedTurn prepared = transactionTemplate.execute(status -> {
            AssistantConversation conversation = request.conversationId() == null
                    ? conversations.save(new AssistantConversation(account))
                    : conversations.findByIdAndUser_Id(request.conversationId(), account.getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation was not found"));
            conversation.touch();

            messages.save(new AssistantMessage(conversation, AssistantMessage.Role.USER, rawQuestion, null));

            // Фильтр безопасности (R5: использует SafetyPolicy с границами слов)
            if (SafetyPolicy.requiresSafetyRedirect(rawQuestion)) {
                AssistantMessage responseMsg = messages.save(new AssistantMessage(conversation, AssistantMessage.Role.ASSISTANT,
                        SAFETY_NOTICE, "SAFETY_REDIRECT"));
                return new PreparedTurn(conversation.getId(), List.of(),
                        new AssistantResponse(conversation.getId(), responseMsg.getContent(), "SAFETY_REDIRECT",
                                "SAFETY_RULE", false, SAFETY_NOTICE, List.of()));
            }

            String normalizedQuestion = Normalizer.normalize(rawQuestion.toLowerCase(Locale.ROOT), Normalizer.Form.NFKC);
            List<KnowledgeClaimResponse> evidence = retrieve(normalizedQuestion);

            if (evidence.isEmpty()) {
                AssistantMessage responseMsg = messages.save(new AssistantMessage(conversation, AssistantMessage.Role.ASSISTANT,
                        NO_EVIDENCE, "NO_EVIDENCE"));
                return new PreparedTurn(conversation.getId(), List.of(),
                        new AssistantResponse(conversation.getId(), responseMsg.getContent(), "NO_EVIDENCE",
                                "SOURCE_EXTRACTIVE", false, SAFETY_NOTICE, List.of()));
            }

            return new PreparedTurn(conversation.getId(), evidence, null);
        });

        if (prepared.earlyResponse() != null) {
            return prepared.earlyResponse();
        }

        // Шаг 2 (ВНЕ транзакции, R16): обращение к LLM не держит соединение с базой данных
        List<KnowledgeClaimResponse> evidence = prepared.evidence();
        AssistantGeneration generated = generator.generate(rawQuestion, evidence);
        Set<Long> availableIds = evidence.stream().map(KnowledgeClaimResponse::id)
                .collect(java.util.stream.Collectors.toSet());
        if (generated.answer() == null || generated.answer().isBlank() || generated.citedClaimIds().isEmpty()
                || !availableIds.containsAll(generated.citedClaimIds())) {
            generated = extractiveFallback.generate(rawQuestion, evidence);
        }

        final AssistantGeneration finalGenerated = generated;

        // Шаг 3 (короткая транзакция): сохранение ответа и источников
        return transactionTemplate.execute(status -> {
            AssistantConversation conversation = conversations.findById(prepared.conversationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation was not found"));
            AssistantMessage responseMsg = messages.save(new AssistantMessage(conversation, AssistantMessage.Role.ASSISTANT,
                    finalGenerated.answer(), "EVIDENCE_FOUND"));

            List<AssistantResponse.Citation> citations = new ArrayList<>();
            for (KnowledgeClaimResponse claim : evidence) {
                if (!finalGenerated.citedClaimIds().contains(claim.id())) continue;
                messageSources.save(new AssistantMessageSource(responseMsg, sourceFor(claim), claim.sourceLocator(), claim.claimText()));
                String subject = claim.plantName() != null ? claim.plantName() : claim.preparationName();
                String claimTypeStr = claim.claimType() != null ? claim.claimType().name() : null;
                citations.add(new AssistantResponse.Citation(claim.id(), claim.title(), claim.claimText(), subject,
                        claim.sourceLocator(), claim.source(), claimTypeStr));
            }
            return new AssistantResponse(conversation.getId(), finalGenerated.answer(), "EVIDENCE_FOUND", finalGenerated.mode(),
                    finalGenerated.isAiGenerated(), SAFETY_NOTICE, citations);
        });
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

    @Transactional(readOnly = true)
    public List<AssistantHistoryMessage> conversationHistory(long conversationId, UserAccount account) {
        conversations.findByIdAndUser_Id(conversationId, account.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation was not found"));
        List<AssistantMessage> conversationMessages = messages.findByConversation_IdOrderByIdAsc(conversationId);
        Map<Long, List<AssistantMessageSource>> sourcesByMessage = messageSources
                .findByMessage_Conversation_IdOrderByMessage_IdAsc(conversationId).stream()
                .collect(java.util.stream.Collectors.groupingBy(source -> source.getMessage().getId()));

        return conversationMessages.stream().map(message -> new AssistantHistoryMessage(
                message.getId(), message.getRole().name(), message.getContent(), message.getResponseStatus(),
                message.getCreatedAt(), sourcesByMessage.getOrDefault(message.getId(), List.of()).stream()
                        .map(source -> new AssistantHistoryMessage.SourceReference(source.getSourceLocator(),
                                source.getExcerpt(), kz.iitu.medicineplatform.source.SourceResponse.from(source.getSource())))
                        .toList())).toList();
    }

    private List<KnowledgeClaimResponse> retrieve(String normalizedQuestion) {
        Set<String> terms = tokensFromNormalized(normalizedQuestion);
        if (terms.isEmpty()) return List.of();

        // 1. Полнотекстовый / ключевой поиск
        LinkedHashSet<KnowledgeClaim> keywordCandidates = new LinkedHashSet<>();
        for (String term : terms) {
            keywordCandidates.addAll(claims.searchVerifiedCandidates(ReviewStatus.VERIFIED, term, PageRequest.of(0, 15)));
            if (keywordCandidates.size() >= 30) break;
        }

        // 2. Векторный семантический поиск
        List<KnowledgeClaim> vectorCandidates = new ArrayList<>();
        if (vectorSearchEnabled) {
            try {
                float[] queryEmbedding = embeddingService.embedQuery(normalizedQuestion);
                StringBuilder sb = new StringBuilder();
                sb.append('[');
                for (int i = 0; i < queryEmbedding.length; i++) {
                    if (i > 0) sb.append(',');
                    sb.append(queryEmbedding[i]);
                }
                sb.append(']');
                vectorCandidates = claims.findNearestByEmbedding(
                        ReviewStatus.VERIFIED.name(), embeddingService.modelId(), sb.toString(), PageRequest.of(0, 15));
            } catch (RuntimeException exception) {
                log.warn("Assistant vector search unavailable; returning keyword matches: {}", exception.getMessage());
            }
        }

        // Фильтрация ключевых кандидатов:
        // Утверждение проходит, если совпало >= 2 токенов или 1 токен совпал с названием растения/препарата
        List<KnowledgeClaim> qualifiedKeywordCandidates = keywordCandidates.stream()
                .filter(claim -> isKeywordMatchQualified(claim, terms))
                .toList();

        if (qualifiedKeywordCandidates.isEmpty() && vectorCandidates.isEmpty()) {
            return List.of();
        }

        // 3. Reciprocal Rank Fusion (RRF, R6/B2):
        // Объединение рангов ключевого и векторного поиска: score = Σ 1 / (60 + rank)
        Map<Long, Double> rrfScores = new HashMap<>();
        Map<Long, KnowledgeClaim> claimById = new LinkedHashMap<>();

        for (int rank = 0; rank < qualifiedKeywordCandidates.size(); rank++) {
            KnowledgeClaim claim = qualifiedKeywordCandidates.get(rank);
            claimById.putIfAbsent(claim.getId(), claim);
            rrfScores.merge(claim.getId(), 1.0 / (60.0 + rank), Double::sum);
        }

        for (int rank = 0; rank < vectorCandidates.size(); rank++) {
            KnowledgeClaim claim = vectorCandidates.get(rank);
            claimById.putIfAbsent(claim.getId(), claim);
            rrfScores.merge(claim.getId(), 1.0 / (60.0 + rank), Double::sum);
        }

        // Ограничиваем топ-3 наиболее релевантных утверждений (B5)
        return rrfScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(3)
                .map(e -> KnowledgeClaimResponse.from(claimById.get(e.getKey())))
                .toList();
    }

    private boolean isKeywordMatchQualified(KnowledgeClaim claim, Set<String> queryTerms) {
        String plantNames = claim.getPlant() == null ? "" : String.join(" ",
                nullToEmpty(claim.getPlant().getScientificName()),
                nullToEmpty(claim.getPlant().getDisplayName()),
                nullToEmpty(claim.getPlant().getNameRu()),
                nullToEmpty(claim.getPlant().getNameKk()),
                nullToEmpty(claim.getPlant().getSynonyms()));
        String preparationName = claim.getPreparation() == null ? "" : nullToEmpty(claim.getPreparation().getName());
        String nameText = Normalizer.normalize((plantNames + " " + preparationName).toLowerCase(Locale.ROOT), Normalizer.Form.NFKC);
        Set<String> nameTokens = tokensFromNormalized(nameText);

        boolean matchesName = queryTerms.stream().anyMatch(nameTokens::contains);
        if (matchesName) {
            return true;
        }

        String allSearchable = String.join(" ", nullToEmpty(claim.getTitle()), nullToEmpty(claim.getClaimText()),
                nullToEmpty(claim.getInteractionPartner()), plantNames, preparationName);
        String normalized = Normalizer.normalize(allSearchable.toLowerCase(Locale.ROOT), Normalizer.Form.NFKC);
        Set<String> allTokens = tokensFromNormalized(normalized);

        long matchCount = queryTerms.stream().filter(allTokens::contains).count();
        return matchCount >= 2;
    }

    private Set<String> tokensFromNormalized(String normalized) {
        return TOKEN_SPLIT.splitAsStream(normalized).filter(token -> token.length() > 2 && !STOP_WORDS.contains(token))
                .collect(java.util.stream.Collectors.toSet());
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private kz.iitu.medicineplatform.source.DataSource sourceFor(KnowledgeClaimResponse response) {
        return claims.findById(response.id()).orElseThrow().getSource();
    }
}
