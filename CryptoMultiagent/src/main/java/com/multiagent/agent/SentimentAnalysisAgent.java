package com.multiagent.agent;

import com.multiagent.model.AgentAnalysis;
import com.multiagent.util.AnalysisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
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
        log.info("Агент: Аналитик Настроений - инициализирован");
    }

    @Override
    public AgentAnalysis analyze(String cryptocurrency, String timeframe) {
        log.info("Начинаю анализ настроений криптовалюты: {} с временным интервалом: {}", cryptocurrency, timeframe);
        
        try {
            Map<String, Object> templateValues = Map.of(
                    "cryptocurrency", cryptocurrency,
                    "timeframe", timeframe
            );

            log.debug("Отправка запроса к AI для анализа настроений с параметрами: {}", templateValues);
            String analysis = getAiResponse(SENTIMENT_ANALYSIS_PROMPT, templateValues);
            
            log.debug("Получен ответ от AI, извлекаю рекомендацию и уверенность");
            String recommendation = AnalysisUtils.extractRecommendation(analysis);
            double confidence = AnalysisUtils.extractConfidence(analysis);

            log.info("Анализ настроений завершен. Рекомендация: {}, Уверенность: {}", recommendation, confidence);
            log.debug("Полный анализ: {}", analysis);
            
            return new AgentAnalysis("Аналитик Настроений", analysis, recommendation, confidence);
        } catch (Exception e) {
            log.error("Ошибка при анализе настроений криптовалюты {}: {}", cryptocurrency, e.getMessage(), e);
            throw e;
        }
    }
}
