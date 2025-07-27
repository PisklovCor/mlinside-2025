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

class SentimentAnalysisAgentTest extends BaseAgentTest {

    @Autowired
    private SentimentAnalysisAgent sentimentAnalysisAgent;

    @Test
    @DisplayName("Агент настроений должен корректно анализировать позитивные настроения")
    void testPositiveSentimentAnalysis() {
        // Arrange
        String mockAiResponse = "Рынок демонстрирует сильный оптимизм. Социальные медиа полны позитивных отзывов. " +
                "Рекомендация: ПОКУПАТЬ. Высокая уверенность в бычьих настроениях.";

        ChatResponse mockChatResponse = createMockChatResponse(mockAiResponse);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        AgentAnalysis result = sentimentAnalysisAgent.analyze("Bitcoin", "1 неделя");

        // Assert
        assertBasicAgentAnalysis(result, "Аналитик Настроений");
        assertBuyRecommendation(result);
        assertHighConfidence(result);
        assertTrue(result.getAnalysis().contains("оптимизм"));
    }

    @Test
    @DisplayName("Агент настроений должен корректно анализировать негативные настроения")
    void testNegativeSentimentAnalysis() {
        // Arrange
        String mockAiResponse = "Рынок охвачен страхом и неуверенностью. Преобладают негативные настроения. " +
                "Рекомендация: ПРОДАВАТЬ. Умеренная уверенность в медвежьих настроениях.";

        ChatResponse mockChatResponse = createMockChatResponse(mockAiResponse);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        AgentAnalysis result = sentimentAnalysisAgent.analyze("Ethereum", "2 недели");

        // Assert
        assertBasicAgentAnalysis(result, "Аналитик Настроений");
        assertSellRecommendation(result);
        assertModerateConfidence(result);
        assertTrue(result.getAnalysis().contains("страхом"));
    }

    @Test
    @DisplayName("Агент настроений должен корректно анализировать нейтральные настроения")
    void testNeutralSentimentAnalysis() {
        // Arrange
        String mockAiResponse = "Настроения рынка смешанные. Нет четкого направления. " +
                "Рекомендация: ДЕРЖАТЬ. Низкая уверенность в текущих настроениях.";

        ChatResponse mockChatResponse = createMockChatResponse(mockAiResponse);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        AgentAnalysis result = sentimentAnalysisAgent.analyze("Cardano", "1 месяц");

        // Assert
        assertBasicAgentAnalysis(result, "Аналитик Настроений");
        assertHoldRecommendation(result);
        assertLowConfidence(result);
    }

    @Test
    @DisplayName("Агент настроений должен обрабатывать ошибки AI модели")
    void testErrorHandling() {
        // Arrange
        when(chatModel.call(any(Prompt.class))).thenThrow(new RuntimeException("AI Service Error"));

        // Act
        AgentAnalysis result = sentimentAnalysisAgent.analyze("Polkadot", "3 дня");

        // Assert
        assertBasicAgentAnalysis(result, "Аналитик Настроений");
        assertErrorInAnalysis(result);
        assertHoldRecommendation(result); // Дефолтная рекомендация при ошибке
    }

    @Test
    @DisplayName("Агент настроений должен корректно извлекать рекомендации из анализа")
    void testRecommendationExtraction() {
        // Arrange
        String mockAiResponse = "Сильный хайп в социальных сетях. FOMO настроения. " +
                "Рекомендация: ПОКУПАТЬ. Высокая уверенность.";

        ChatResponse mockChatResponse = createMockChatResponse(mockAiResponse);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        AgentAnalysis result = sentimentAnalysisAgent.analyze("Solana", "5 дней");

        // Assert
        assertBuyRecommendation(result);
        assertHighConfidence(result);
    }

    @Test
    @DisplayName("Агент настроений должен корректно обрабатывать FUD настроения")
    void testFudSentimentAnalysis() {
        // Arrange
        String mockAiResponse = "Распространяется FUD. Страх и неуверенность в проекте. " +
                "Рекомендация: ПРОДАВАТЬ. Высокая уверенность.";

        ChatResponse mockChatResponse = createMockChatResponse(mockAiResponse);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        AgentAnalysis result = sentimentAnalysisAgent.analyze("UnknownCoin", "1 неделя");

        // Assert
        assertBasicAgentAnalysis(result, "Аналитик Настроений");
        assertSellRecommendation(result);
        assertHighConfidence(result);
        assertTrue(result.getAnalysis().contains("FUD"));
    }

    @Test
    @DisplayName("Агент настроений должен корректно обрабатывать различные временные периоды")
    void testDifferentTimeframes() {
        // Arrange
        String mockAiResponse = "Краткосрочные настроения позитивные, но есть риски. " +
                "Рекомендация: ПОКУПАТЬ. Умеренная уверенность.";

        ChatResponse mockChatResponse = createMockChatResponse(mockAiResponse);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        AgentAnalysis result = sentimentAnalysisAgent.analyze("Bitcoin", "24 часа");

        // Assert
        assertBasicAgentAnalysis(result, "Аналитик Настроений");
        assertTrue(result.getAnalysis().contains("Краткосрочные настроения"));
    }

    @Test
    @DisplayName("Агент настроений должен корректно анализировать бычьи настроения")
    void testBullishSentimentAnalysis() {
        // Arrange
        String mockAiResponse = "Преобладают бычьи настроения. Институциональный интерес растет. " +
                "Рекомендация: ПОКУПАТЬ. Высокая уверенность.";

        ChatResponse mockChatResponse = createMockChatResponse(mockAiResponse);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        AgentAnalysis result = sentimentAnalysisAgent.analyze("Ethereum", "1 месяц");

        // Assert
        assertBuyRecommendation(result);
        assertHighConfidence(result);
        assertTrue(result.getAnalysis().contains("бычьи"));
    }

    @Test
    @DisplayName("Агент настроений должен корректно анализировать медвежьи настроения")
    void testBearishSentimentAnalysis() {
        // Arrange
        String mockAiResponse = "Медвежьи настроения усиливаются. Страх доминирует. " +
                "Рекомендация: ПРОДАВАТЬ. Высокая уверенность.";

        ChatResponse mockChatResponse = createMockChatResponse(mockAiResponse);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        AgentAnalysis result = sentimentAnalysisAgent.analyze("Cardano", "2 недели");

        // Assert
        assertSellRecommendation(result);
        assertHighConfidence(result);
    }
} 