package com.multiagent.agent;

import com.multiagent.model.AgentAnalysis;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TechnicalAnalysisAgentTest extends BaseAgentTest {

    @Autowired
    private TechnicalAnalysisAgent technicalAnalysisAgent;

    @Test
    @DisplayName("Технический агент должен корректно анализировать бычий сигнал")
    void testBullishAnalysis() {
        // Arrange
        String mockAiResponse = "Технический анализ показывает сильный восходящий тренд. " +
                "Рекомендация: ПОКУПАТЬ. Высокая уверенность в анализе.";

        ChatResponse mockChatResponse = createMockChatResponse(mockAiResponse);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        AgentAnalysis result = technicalAnalysisAgent.analyze("Bitcoin", "1 месяц");

        // Assert
        assertBasicAgentAnalysis(result, "Технический Аналитик");
        assertBuyRecommendation(result);
        assertHighConfidence(result);
        assertTrue(result.getAnalysis().contains("восходящий тренд"));
    }

    @Test
    @DisplayName("Технический агент должен корректно анализировать медвежий сигнал")
    void testBearishAnalysis() {
        // Arrange
        String mockAiResponse = "Технические индикаторы показывают нисходящий тренд. " +
                "Рекомендация: ПРОДАВАТЬ. Умеренная уверенность.";

        ChatResponse mockChatResponse = createMockChatResponse(mockAiResponse);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        AgentAnalysis result = technicalAnalysisAgent.analyze("Ethereum", "2 недели");

        // Assert
        assertBasicAgentAnalysis(result, "Технический Аналитик");
        assertSellRecommendation(result);
        assertModerateConfidence(result);
    }

    @Test
    @DisplayName("Технический агент должен обрабатывать ошибки AI модели")
    void testErrorHandling() {
        // Arrange
        when(chatModel.call(any(Prompt.class))).thenThrow(new RuntimeException("AI Service Error"));

        // Act
        AgentAnalysis result = technicalAnalysisAgent.analyze("Cardano", "1 неделя");

        // Assert
        assertBasicAgentAnalysis(result, "Технический Аналитик");
        assertErrorInAnalysis(result);
        assertHoldRecommendation(result); // Дефолтная рекомендация при ошибке
    }
}
