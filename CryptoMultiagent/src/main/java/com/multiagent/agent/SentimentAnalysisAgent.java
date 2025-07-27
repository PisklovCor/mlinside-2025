package com.multiagent.agent;

import com.multiagent.model.AgentAnalysis;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SentimentAnalysisAgent extends BaseAgent {

    private static final String SENTIMENT_ANALYSIS_PROMPT = """
        Ты - специалист по анализу настроений криптовалютного рынка и социальных медиа.
        
        Проанализируй текущие настроения рынка относительно криптовалюты {cryptocurrency} в период {timeframe}.
        
        Рассмотри следующие факторы:
        1. Новости и медиа освещение
        2. Настроения в социальных сетях (Twitter, Reddit, Telegram)
        3. Мнения влиятельных лиц и аналитиков
        4. Общий страх и жадность рынка (Fear & Greed Index)
        5. Институциональный интерес
        6. Общественное принятие и FOMO/FUD тенденции
        7. Корреляция с общим криптовалютным рынком
        8. Медийные события и хайп
        
        Структура ответа:
        - Текущие настроения рынка
        - Ключевые драйверы настроений
        - Социальные медиа индикаторы
        - Потенциальные катализаторы изменений
        - Рекомендация: ПОКУПАТЬ/ПРОДАВАТЬ/ДЕРЖАТЬ
        - Уровень уверенности (высокая/умеренная/низкая уверенность)
        
        Учитывай психологические аспекты рынка и поведение толпы.
        """;

    public SentimentAnalysisAgent(ChatModel openAiChatModel) {
        super(openAiChatModel);
    }

    @Override
    public AgentAnalysis analyze(String cryptocurrency, String timeframe) {
        Map<String, Object> templateValues = Map.of(
                "cryptocurrency", cryptocurrency,
                "timeframe", timeframe
        );

        String analysis = getAiResponse(SENTIMENT_ANALYSIS_PROMPT, templateValues);
        String recommendation = extractRecommendation(analysis);
        double confidence = extractConfidence(analysis);

        return new AgentAnalysis("Аналитик Настроений", analysis, recommendation, confidence);
    }

    private String extractRecommendation(String analysis) {
        String lowerAnalysis = analysis.toLowerCase();
        if (lowerAnalysis.contains("покупать") || lowerAnalysis.contains("buy") ||
                lowerAnalysis.contains("позитивн") || lowerAnalysis.contains("оптимизм") ||
                lowerAnalysis.contains("бычий") || lowerAnalysis.contains("хайп")) {
            return "ПОКУПАТЬ";
        } else if (lowerAnalysis.contains("продавать") || lowerAnalysis.contains("sell") ||
                lowerAnalysis.contains("негативн") || lowerAnalysis.contains("пессимизм") ||
                lowerAnalysis.contains("медвежий") || lowerAnalysis.contains("fud")) {
            return "ПРОДАВАТЬ";
        } else {
            return "ДЕРЖАТЬ";
        }
    }
}
