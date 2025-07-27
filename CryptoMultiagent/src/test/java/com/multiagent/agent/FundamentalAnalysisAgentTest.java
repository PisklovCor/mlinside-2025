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

class FundamentalAnalysisAgentTest extends BaseAgentTest {

    @Autowired
    private FundamentalAnalysisAgent fundamentalAnalysisAgent;

    @Test
    @DisplayName("Фундаментальный агент должен корректно анализировать сильный проект")
    void testStrongProjectAnalysis() {
        // Arrange
        String mockAiResponse = "Bitcoin демонстрирует отличную технологическую базу и сильную команду. " +
                "Рекомендация: ПОКУПАТЬ. Высокая уверенность в долгосрочных перспективах.";

        ChatResponse mockChatResponse = createMockChatResponse(mockAiResponse);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        AgentAnalysis result = fundamentalAnalysisAgent.analyze("Bitcoin", "6 месяцев");

        // Assert
        assertBasicAgentAnalysis(result, "Фундаментальный Аналитик");
        assertBuyRecommendation(result);
        assertHighConfidence(result);
        assertTrue(result.getAnalysis().contains("Bitcoin"));
    }

    @Test
    @DisplayName("Фундаментальный агент должен корректно анализировать слабый проект")
    void testWeakProjectAnalysis() {
        // Arrange
        String mockAiResponse = "Проект имеет слабую технологическую базу и неопределенные перспективы. " +
                "Рекомендация: ПРОДАВАТЬ. Умеренная уверенность.";

        ChatResponse mockChatResponse = createMockChatResponse(mockAiResponse);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        AgentAnalysis result = fundamentalAnalysisAgent.analyze("UnknownCoin", "3 месяца");

        // Assert
        assertBasicAgentAnalysis(result, "Фундаментальный Аналитик");
        assertSellRecommendation(result);
        assertModerateConfidence(result);
        assertTrue(result.getAnalysis().contains("слабую"));
    }

    @Test
    @DisplayName("Фундаментальный агент должен корректно анализировать нейтральный проект")
    void testNeutralProjectAnalysis() {
        // Arrange
        String mockAiResponse = "Проект имеет смешанные показатели. Технология интересная, но есть риски. " +
                "Рекомендация: ДЕРЖАТЬ. Низкая уверенность.";

        ChatResponse mockChatResponse = createMockChatResponse(mockAiResponse);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        AgentAnalysis result = fundamentalAnalysisAgent.analyze("Ethereum", "1 месяц");

        // Assert
        assertBasicAgentAnalysis(result, "Фундаментальный Аналитик");
        assertHoldRecommendation(result);
        assertLowConfidence(result);
    }

    @Test
    @DisplayName("Фундаментальный агент должен обрабатывать ошибки AI модели")
    void testErrorHandling() {
        // Arrange
        when(chatModel.call(any(Prompt.class))).thenThrow(new RuntimeException("AI Service Error"));

        // Act
        AgentAnalysis result = fundamentalAnalysisAgent.analyze("Cardano", "2 месяца");

        // Assert
        assertBasicAgentAnalysis(result, "Фундаментальный Аналитик");
        assertErrorInAnalysis(result);
        assertHoldRecommendation(result); // Дефолтная рекомендация при ошибке
    }

    @Test
    @DisplayName("Фундаментальный агент должен корректно извлекать рекомендации из анализа")
    void testRecommendationExtraction() {
        // Arrange
        String mockAiResponse = "Инновационная технология и сильная команда. Перспективный проект. " +
                "Рекомендация: ПОКУПАТЬ. Высокая уверенность.";

        ChatResponse mockChatResponse = createMockChatResponse(mockAiResponse);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        AgentAnalysis result = fundamentalAnalysisAgent.analyze("Polkadot", "1 год");

        // Assert
        assertBuyRecommendation(result);
        assertHighConfidence(result);
    }

    @Test
    @DisplayName("Фундаментальный агент должен корректно обрабатывать различные временные горизонты")
    void testDifferentTimeframes() {
        // Arrange
        String mockAiResponse = "Долгосрочный анализ показывает стабильные перспективы. " +
                "Рекомендация: ПОКУПАТЬ. Высокая уверенность.";

        ChatResponse mockChatResponse = createMockChatResponse(mockAiResponse);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        AgentAnalysis result = fundamentalAnalysisAgent.analyze("Solana", "2 года");

        // Assert
        assertBasicAgentAnalysis(result, "Фундаментальный Аналитик");
        assertTrue(result.getAnalysis().contains("Долгосрочный анализ"));
    }
} 