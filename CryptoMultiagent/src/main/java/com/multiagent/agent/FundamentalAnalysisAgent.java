package com.multiagent.agent;

import com.multiagent.model.AgentAnalysis;
import com.multiagent.util.AnalysisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
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
        log.info("Агент: Фундаментальный Аналитик - инициализирован");
    }

    @Override
    public AgentAnalysis analyze(String cryptocurrency, String timeframe) {
        log.info("Начинаю фундаментальный анализ криптовалюты: {} с временным интервалом: {}", cryptocurrency, timeframe);
        
        try {
            Map<String, Object> templateValues = Map.of(
                    "cryptocurrency", cryptocurrency,
                    "timeframe", timeframe
            );

            log.debug("Отправка запроса к AI для фундаментального анализа с параметрами: {}", templateValues);
            String analysis = getAiResponse(FUNDAMENTAL_ANALYSIS_PROMPT, templateValues);
            
            log.debug("Получен ответ от AI, извлекаю рекомендацию и уверенность");
            String recommendation = AnalysisUtils.extractRecommendation(analysis);
            double confidence = AnalysisUtils.extractConfidence(analysis);

            log.info("Фундаментальный анализ завершен. Рекомендация: {}, Уверенность: {}", recommendation, confidence);
            log.debug("Полный анализ: {}", analysis);
            
            return new AgentAnalysis("Фундаментальный Аналитик", analysis, recommendation, confidence);
        } catch (Exception e) {
            log.error("Ошибка при фундаментальном анализе криптовалюты {}: {}", cryptocurrency, e.getMessage(), e);
            throw e;
        }
    }
}
