package com.multiagent.service;

import com.multiagent.BaseTestConfiguration;
import com.multiagent.model.AgentAnalysis;
import com.multiagent.model.CryptoAnalysisResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
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

        when(technicalAnalysisAgent.analyze(anyString(), anyString()))
                .thenReturn(new AgentAnalysis("Технический Аналитик",
                        "Технический анализ показывает восходящий тренд", "ПОКУПАТЬ", 0.8));

        when(fundamentalAnalysisAgent.analyze(anyString(), anyString()))
                .thenReturn(new AgentAnalysis("Фундаментальный Аналитик",
                        "Сильные фундаментальные показатели", "ПОКУПАТЬ", 0.9));

        when(sentimentAnalysisAgent.analyze(anyString(), anyString()))
                .thenReturn(new AgentAnalysis("Аналитик Настроений",
                        "Позитивные настроения в сообществе", "ПОКУПАТЬ", 0.7));

        // Act
        CryptoAnalysisResponse response = cryptoAnalysisService.analyzeCryptocurrency(cryptocurrency, timeframe);

        // Assert
        assertNotNull(response);
        assertEquals(cryptocurrency, response.getCryptocurrency());
        assertEquals(3, response.getAgentAnalyses().size());
        assertEquals("ПОКУПАТЬ", response.getFinalRecommendation());
        assertEquals(0.8, response.getAverageConfidence(), 0.1);
    }

    @Test
    @DisplayName("Смешанные рекомендации должны приводить к взвешенному решению")
    void testMixedRecommendations() {
        // Arrange
        String cryptocurrency = "Ethereum";
        String timeframe = "2 недели";

        when(technicalAnalysisAgent.analyze(anyString(), anyString()))
                .thenReturn(new AgentAnalysis("Технический Аналитик",
                        "Технические индикаторы показывают неопределенность", "ДЕРЖАТЬ", 0.5));

        when(fundamentalAnalysisAgent.analyze(anyString(), anyString()))
                .thenReturn(new AgentAnalysis("Фундаментальный Аналитик",
                        "Высокие риски в краткосрочной перспективе", "ПРОДАВАТЬ", 0.8));

        when(sentimentAnalysisAgent.analyze(anyString(), anyString()))
                .thenReturn(new AgentAnalysis("Аналитик Настроений",
                        "Умеренно позитивные настроения", "ПОКУПАТЬ", 0.6));

        // Act
        CryptoAnalysisResponse response = cryptoAnalysisService.analyzeCryptocurrency(cryptocurrency, timeframe);

        // Assert
        assertNotNull(response);
        assertEquals("ПРОДАВАТЬ", response.getFinalRecommendation()); // Наибольший вес у ПРОДАВАТЬ (0.8)
        assertTrue(response.getAverageConfidence() > 0.6);
    }

    @Test
    @DisplayName("Асинхронный анализ должен завершаться успешно")
    void testAsynchronousAnalysis() throws Exception {
        // Arrange
        String cryptocurrency = "Cardano";
        String timeframe = "3 месяца";

        when(technicalAnalysisAgent.analyze(anyString(), anyString()))
                .thenReturn(new AgentAnalysis("Технический Аналитик",
                        "Консолидация в диапазоне", "ДЕРЖАТЬ", 0.6));

        when(fundamentalAnalysisAgent.analyze(anyString(), anyString()))
                .thenReturn(new AgentAnalysis("Фундаментальный Аналитик",
                        "Перспективные обновления протокола", "ПОКУПАТЬ", 0.7));

        when(sentimentAnalysisAgent.analyze(anyString(), anyString()))
                .thenReturn(new AgentAnalysis("Аналитик Настроений",
                        "Нейтральные настроения", "ДЕРЖАТЬ", 0.5));

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
}
