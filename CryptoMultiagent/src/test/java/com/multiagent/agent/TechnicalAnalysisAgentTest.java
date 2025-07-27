package com.multiagent.agent;

import com.multiagent.BaseTestConfiguration;
import com.multiagent.model.AgentAnalysis;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TechnicalAnalysisAgentTest extends BaseTestConfiguration {

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
        assertNotNull(result);
        assertEquals("Технический Аналитик", result.getAgentName());
        assertEquals("ПОКУПАТЬ", result.getRecommendation());
        assertTrue(result.getConfidence() > 0.8);
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
        assertNotNull(result);
        assertEquals("ПРОДАВАТЬ", result.getRecommendation());
        assertTrue(result.getConfidence() >= 0.6 && result.getConfidence() <= 0.8);
    }

    @Test
    @DisplayName("Технический агент должен обрабатывать ошибки AI модели")
    void testErrorHandling() {
        // Arrange
        when(chatModel.call(any(Prompt.class))).thenThrow(new RuntimeException("AI Service Error"));

        // Act
        AgentAnalysis result = technicalAnalysisAgent.analyze("Cardano", "1 неделя");

        // Assert
        assertNotNull(result);
        assertTrue(result.getAnalysis().contains("Ошибка при получении анализа"));
        assertEquals("ДЕРЖАТЬ", result.getRecommendation()); // Дефолтная рекомендация при ошибке
    }

    private ChatResponse createMockChatResponse(String content) {
        AssistantMessage message = new AssistantMessage(content);
        Generation generation = new Generation(message);
        return new ChatResponse(java.util.List.of(generation));
    }
}
