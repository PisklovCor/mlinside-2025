package com.multiagent.agent;

import com.multiagent.model.AgentAnalysis;
import com.multiagent.util.AnalysisUtils;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TechnicalAnalysisAgent extends BaseAgent {

    private static final String TECHNICAL_ANALYSIS_PROMPT = """
        Ты - опытный технический аналитик криптовалют.
        
        Выполни детальный технический анализ криптовалюты {cryptocurrency} за период {timeframe}.
        
        Проанализируй следующие аспекты:
        1. Ценовые тренды и паттерны
        2. Технические индикаторы (RSI, MACD, Moving Averages)
        3. Уровни поддержки и сопротивления
        4. Объемы торгов
        5. Графические паттерны
        
        Структура ответа:
        - Краткий обзор текущей ситуации
        - Ключевые технические сигналы
        - Уровни входа/выхода
        - Рекомендация: ПОКУПАТЬ/ПРОДАВАТЬ/ДЕРЖАТЬ
        - Уровень уверенности (высокая/умеренная/низкая уверенность)
        
        Будь конкретен и обоснован в своих выводах.
        """;

    public TechnicalAnalysisAgent(ChatModel openAiChatModel) {
        super(openAiChatModel);
    }

    @Override
    public AgentAnalysis analyze(String cryptocurrency, String timeframe) {
        Map<String, Object> templateValues = Map.of(
                "cryptocurrency", cryptocurrency,
                "timeframe", timeframe
        );

        String analysis = getAiResponse(TECHNICAL_ANALYSIS_PROMPT, templateValues);
        String recommendation = AnalysisUtils.extractRecommendation(analysis);
        double confidence = AnalysisUtils.extractConfidence(analysis);

        return new AgentAnalysis("Технический Аналитик", analysis, recommendation, confidence);
    }
}
