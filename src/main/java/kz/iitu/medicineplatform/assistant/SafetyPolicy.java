package kz.iitu.medicineplatform.assistant;

import java.text.Normalizer;
import java.util.Locale;
import java.util.List;
import java.util.regex.Pattern;

public final class SafetyPolicy {

    public static final String SAFETY_NOTICE =
            "Я не могу давать схемы лечения, оценивать безопасность для конкретного человека или советовать сочетания. " +
            "Обратитесь к врачу или фармацевту; эта платформа предназначена только для поиска справочных сведений.";

    private static final List<Pattern> SAFETY_PATTERNS = List.of(
            // 1. Дозировка и схемы применения (RU / KZ / EN)
            Pattern.compile("(?<![\\p{L}\\p{N}])(дозиров\\p{L}*|доза|дозы|дозу|дозе|дозой|дозам|дозах)(?![\\p{L}\\p{N}])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            Pattern.compile("(?<![\\p{L}\\p{N}])(сколько (?:принимать|пить|капать|ложек|грамм|мг|таблеток))(?![\\p{L}\\p{N}])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            Pattern.compile("(?<![\\p{L}\\p{N}])(как (?:принимать|пить|капать|заваривать|употреблять))(?![\\p{L}\\p{N}])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            Pattern.compile("(?<![\\p{L}\\p{N}])(схема (?:лечения|приема|приёма)|курс (?:лечения|приема|приёма))(?![\\p{L}\\p{N}])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            Pattern.compile("(?<![\\p{L}\\p{N}])(мөлшер\\p{L}*|қанша (?:ішу|қабылдау)|қалай (?:ішу|қабылдау|қолдану))(?![\\p{L}\\p{N}])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            Pattern.compile("\\b(dosage|dosing|dose|how much to take|how to take|how many drops|daily dose)\\b", Pattern.CASE_INSENSITIVE),

            // 2. Дети / педиатрия (RU / KZ / EN)
            Pattern.compile("(?<![\\p{L}\\p{N}])(детям|детей|детский\\p{L}*|детская|детское|ребёнку|ребенку|ребёнка|ребенка|ребёнок|ребенок|младен\\p{L}*|груднич\\p{L}*)(?![\\p{L}\\p{N}])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            Pattern.compile("(?<![\\p{L}\\p{N}])(что дать (?:ребёнку|ребенку|детям))(?![\\p{L}\\p{N}])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            Pattern.compile("(?<![\\p{L}\\p{N}])(бала\\p{L}*|балалар\\p{L}*|сәби\\p{L}*)(?![\\p{L}\\p{N}])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            Pattern.compile("\\b(child|children|pediatric|paediatric|infant|toddler|baby|babies)\\b", Pattern.CASE_INSENSITIVE),

            // 3. Беременность и лактация (RU / KZ / EN)
            Pattern.compile("(?<![\\p{L}\\p{N}])(беремен\\p{L}*|лактац\\p{L}*|кормящ\\p{L}*|грудн\\p{L}* вскармливан\\p{L}*)(?![\\p{L}\\p{N}])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            Pattern.compile("(?<![\\p{L}\\p{N}])(жүкті\\p{L}*|жүктілік|емізу\\p{L}*)(?![\\p{L}\\p{N}])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            Pattern.compile("\\b(pregnan\\w*|breastfeed\\w*|lactat\\w*)\\b", Pattern.CASE_INSENSITIVE),

            // 4. Острые и жизнеугрожающие состояния, онкология (RU / KZ / EN)
            Pattern.compile("(?<![\\p{L}\\p{N}])(рак|рака|раку|раком|раке|онколог\\p{L}*|инфаркт\\p{L}*|инсульт\\p{L}*|диабет\\p{L}*|опухол\\p{L}*)(?![\\p{L}\\p{N}])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            Pattern.compile("(?<![\\p{L}\\p{N}])(қатерлі ісік|инфаркт|инсульт|диабет)(?![\\p{L}\\p{N}])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            Pattern.compile("\\b(cancer|carcinoma|tumour|tumor|stroke|myocardial infarction|heart attack|diabetes)\\b", Pattern.CASE_INSENSITIVE),

            // 5. Диагностика и попытка назначения лечения (RU / KZ / EN)
            Pattern.compile("(?<![\\p{L}\\p{N}])(как лечить|чем лечить|вылечить|излечить|диагноз\\p{L}*|диагностик\\p{L}*)(?![\\p{L}\\p{N}])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            Pattern.compile("(?<![\\p{L}\\p{N}])(қалай емдеу|емдеу әдісі|диагноз қою|диагноз)(?![\\p{L}\\p{N}])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            Pattern.compile("\\b(how to treat|how to cure|cure for|diagnos\\w*|self-medication|treatment of (?!data\\b))\\b", Pattern.CASE_INSENSITIVE)
    );

    private SafetyPolicy() {
    }

    public static boolean requiresSafetyRedirect(String question) {
        if (question == null || question.isBlank()) {
            return false;
        }
        String normalized = Normalizer.normalize(question.toLowerCase(Locale.ROOT), Normalizer.Form.NFKC);
        for (Pattern pattern : SAFETY_PATTERNS) {
            if (pattern.matcher(normalized).find()) {
                return true;
            }
        }
        return false;
    }
}
