package com.multiagent.agent;

import com.multiagent.model.AgentAnalysis;
import com.multiagent.util.AnalysisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
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
        log.info("Агент: Технический Аналитик - инициализирован");
    }

    @Override
    public AgentAnalysis analyze(String cryptocurrency, String timeframe) {
        log.info("Начинаю технический анализ криптовалюты: {} с временным интервалом: {}", cryptocurrency, timeframe);
        
        try {
            Map<String, Object> templateValues = Map.of(
                    "cryptocurrency", cryptocurrency,
                    "timeframe", timeframe
            );

            log.debug("Отправка запроса к AI для технического анализа с параметрами: {}", templateValues);
            String analysis = getAiResponse(TECHNICAL_ANALYSIS_PROMPT, templateValues);
            
            log.debug("Получен ответ от AI, извлекаю рекомендацию и уверенность");
            String recommendation = AnalysisUtils.extractRecommendation(analysis);
            double confidence = AnalysisUtils.extractConfidence(analysis);

            log.info("Технический анализ завершен. Рекомендация: {}, Уверенность: {}", recommendation, confidence);
            log.debug("Полный анализ: {}", analysis);
            
            return new AgentAnalysis("Технический Аналитик", analysis, recommendation, confidence);
        } catch (Exception e) {
            log.error("Ошибка при техническом анализе криптовалюты {}: {}", cryptocurrency, e.getMessage(), e);
            throw e;
        }
    }
}
