package com.multiagent.agent;

import com.multiagent.model.AgentAnalysis;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FundamentalAnalysisAgent extends BaseAgent {

    private static final String FUNDAMENTAL_ANALYSIS_PROMPT = """
        Ты - эксперт по фундаментальному анализу криптовалют и блокчейн проектов.
        
        Проведи глубокий фундаментальный анализ криптовалюты {cryptocurrency} с учетом временного горизонта {timeframe}.
        
        Проанализируй следующие аспекты:
        1. Технология и инновации проекта
        2. Команда разработчиков и их опыт
        3. Партнерства и экосистема
        4. Tokenomics и механизмы стоимости
        5. Конкурентные преимущества
        6. Дорожная карта и будущие обновления
        7. Регуляторные риски
        8. Принятие и использование
        
        Структура ответа:
        - Сильные стороны проекта
        - Слабые стороны и риски
        - Долгосрочные перспективы
        - Рекомендация: ПОКУПАТЬ/ПРОДАВАТЬ/ДЕРЖАТЬ
        - Уровень уверенности (высокая/умеренная/низкая уверенность)
        
        Основывай выводы на фактах и логическом анализе.
        """;

    public FundamentalAnalysisAgent(ChatModel openAiChatModel) {
        super(openAiChatModel);
    }

    @Override
    public AgentAnalysis analyze(String cryptocurrency, String timeframe) {
        Map<String, Object> templateValues = Map.of(
                "cryptocurrency", cryptocurrency,
                "timeframe", timeframe
        );

        String analysis = getAiResponse(FUNDAMENTAL_ANALYSIS_PROMPT, templateValues);
        String recommendation = extractRecommendation(analysis);
        double confidence = extractConfidence(analysis);

        return new AgentAnalysis("Фундаментальный Аналитик", analysis, recommendation, confidence);
    }

    private String extractRecommendation(String analysis) {
        String lowerAnalysis = analysis.toLowerCase();
        if (lowerAnalysis.contains("покупать") || lowerAnalysis.contains("buy") ||
                lowerAnalysis.contains("перспективн") || lowerAnalysis.contains("сильн") ||
                lowerAnalysis.contains("инновацион")) {
            return "ПОКУПАТЬ";
        } else if (lowerAnalysis.contains("продавать") || lowerAnalysis.contains("sell") ||
                lowerAnalysis.contains("рискованн") || lowerAnalysis.contains("слаб") ||
                lowerAnalysis.contains("проблем")) {
            return "ПРОДАВАТЬ";
        } else {
            return "ДЕРЖАТЬ";
        }
    }
}
