package com.multiagent.service;

import com.multiagent.BaseTestConfiguration;
import com.multiagent.model.CryptoAnalysisResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CryptoAnalysisServiceTest extends BaseTestConfiguration {

    @Autowired
    private CryptoAnalysisService cryptoAnalysisService;

    @Test
    @DisplayName("Синхронный анализ должен возвращать корректный результат")
    void testSynchronousAnalysis() {
        // Arrange
        String cryptocurrency = "Bitcoin";
        String timeframe = "1 месяц";

        String technicalResponse = "Технический анализ показывает восходящий тренд. Рекомендация: ПОКУПАТЬ. Высокая уверенность.";
        String fundamentalResponse = "Сильные фундаментальные показатели. Рекомендация: ПОКУПАТЬ. Очень высокая уверенность.";
        String sentimentResponse = "Позитивные настроения в сообществе. Рекомендация: ПОКУПАТЬ. Умеренная уверенность.";

        when(chatModel.call(any(Prompt.class)))
                .thenReturn(createMockChatResponse(technicalResponse))
                .thenReturn(createMockChatResponse(fundamentalResponse))
                .thenReturn(createMockChatResponse(sentimentResponse));

        // Act
        CryptoAnalysisResponse response = cryptoAnalysisService.analyzeCryptocurrency(cryptocurrency, timeframe);

        // Assert
        assertNotNull(response);
        assertEquals(cryptocurrency, response.getCryptocurrency());
        assertEquals(3, response.getAgentAnalyses().size());
        assertEquals("ПОКУПАТЬ", response.getFinalRecommendation());
        assertTrue(response.getAverageConfidence() > 0.7);
    }

    @Test
    @DisplayName("Смешанные рекомендации должны приводить к взвешенному решению")
    void testMixedRecommendations() {
        // Arrange
        String cryptocurrency = "Ethereum";
        String timeframe = "2 недели";

        String technicalResponse = "Технические индикаторы показывают неопределенность. Рекомендация: ДЕРЖАТЬ. Низкая уверенность.";
        String fundamentalResponse = "Высокие риски в краткосрочной перспективе. Рекомендация: ПРОДАВАТЬ. Высокая уверенность.";
        String sentimentResponse = "Умеренно позитивные настроения. Рекомендация: ПОКУПАТЬ. Умеренная уверенность.";

        when(chatModel.call(any(Prompt.class)))
                .thenReturn(createMockChatResponse(technicalResponse))
                .thenReturn(createMockChatResponse(fundamentalResponse))
                .thenReturn(createMockChatResponse(sentimentResponse));

        // Act
        CryptoAnalysisResponse response = cryptoAnalysisService.analyzeCryptocurrency(cryptocurrency, timeframe);

        // Assert
        assertNotNull(response);
        assertEquals("ПРОДАВАТЬ", response.getFinalRecommendation()); // Наибольший вес у ПРОДАВАТЬ
        assertTrue(response.getAverageConfidence() > 0.5);
    }

    @Test
    @DisplayName("Асинхронный анализ должен завершаться успешно")
    void testAsynchronousAnalysis() throws Exception {
        // Arrange
        String cryptocurrency = "Cardano";
        String timeframe = "3 месяца";

        String technicalResponse = "Консолидация в диапазоне. Рекомендация: ДЕРЖАТЬ. Умеренная уверенность.";
        String fundamentalResponse = "Перспективные обновления протокола. Рекомендация: ПОКУПАТЬ. Высокая уверенность.";
        String sentimentResponse = "Нейтральные настроения. Рекомендация: ДЕРЖАТЬ. Низкая уверенность.";

        when(chatModel.call(any(Prompt.class)))
                .thenReturn(createMockChatResponse(technicalResponse))
                .thenReturn(createMockChatResponse(fundamentalResponse))
                .thenReturn(createMockChatResponse(sentimentResponse));

        // Act
        CompletableFuture<CryptoAnalysisResponse> futureResponse =
                cryptoAnalysisService.analyzeCryptocurrencyAsync(cryptocurrency, timeframe);

        CryptoAnalysisResponse response = futureResponse.get(); // Ждем завершения

        // Assert
        assertNotNull(response);
        assertEquals(cryptocurrency, response.getCryptocurrency());
        assertEquals(3, response.getAgentAnalyses().size());
        assertNotNull(response.getFinalRecommendation());
        assertTrue(response.getAverageConfidence() >= 0.0);
    }

    private ChatResponse createMockChatResponse(String content) {
        AssistantMessage message = new AssistantMessage(content);
        Generation generation = new Generation(message);
        return new ChatResponse(java.util.List.of(generation));
    }
}
