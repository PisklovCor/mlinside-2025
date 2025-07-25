package com.cryptoagents.service;

import com.cryptoagents.BaseSpringBootTest;
import com.cryptoagents.agent.DefaultAgentFactory;
import com.cryptoagents.model.AnalysisReport;
import com.cryptoagents.model.dto.MarketData;
import com.cryptoagents.model.dto.HistoricalData;
import com.cryptoagents.model.enums.TimePeriod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("AgentOrchestrator Integration Tests")
class AgentOrchestratorIntegrationTest extends BaseSpringBootTest {

    @Autowired
    private AgentOrchestrator orchestrator;

    @MockBean
    private CryptoDataService cryptoDataService;

    @BeforeEach
    void setUp() {
        reset(cryptoDataService);
    }

    @Test
    @DisplayName("Should perform complete analysis with real agents")
    void shouldPerformCompleteAnalysisWithRealAgents() {
        // Given
        String ticker = "BTC";
        MarketData marketData = new MarketData(ticker, "Bitcoin", BigDecimal.valueOf(50000));
        HistoricalData historicalData = new HistoricalData(ticker, TimePeriod.ONE_WEEK);
        
        when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
        when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(marketData));
        when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_WEEK)).thenReturn(Optional.of(historicalData));

        // When
        AnalysisReport result = orchestrator.analyze(ticker);

        // Then
        assertNotNull(result);
        assertEquals(ticker.toUpperCase(), result.getTicker());
        assertTrue(result.isSuccessful());
        assertEquals(3, result.getAgentResults().size());
        
        verify(cryptoDataService).isTickerSupported(ticker);
    }

    @Test
    @DisplayName("Should handle unsupported ticker gracefully")
    void shouldHandleUnsupportedTickerGracefully() {
        // Given
        String ticker = "INVALID";
        when(cryptoDataService.isTickerSupported(ticker)).thenReturn(false);

        // When
        AnalysisReport result = orchestrator.analyze(ticker);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccessful());
        assertTrue(result.hasErrors());
        
        verify(cryptoDataService).isTickerSupported(ticker);
    }

    @Test
    @DisplayName("Should handle missing market data")
    void shouldHandleMissingMarketData() {
        // Given
        String ticker = "BTC";
        when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
        when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(OrchestrationException.class, () -> {
            orchestrator.analyze(ticker);
        });
        
        verify(cryptoDataService).isTickerSupported(ticker);
    }

    @Test
    @DisplayName("Should handle missing historical data")
    void shouldHandleMissingHistoricalData() {
        // Given
        String ticker = "BTC";
        MarketData marketData = new MarketData(ticker, "Bitcoin", BigDecimal.valueOf(50000));
        
        when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
        when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(marketData));
        when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_WEEK)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(OrchestrationException.class, () -> {
            orchestrator.analyze(ticker);
        });
        
        verify(cryptoDataService).isTickerSupported(ticker);
    }

    @Test
    @DisplayName("Should analyze multiple tickers successfully")
    void shouldAnalyzeMultipleTickersSuccessfully() {
        // Given
        String ticker1 = "BTC";
        String ticker2 = "ETH";
        MarketData marketData = new MarketData("BTC", "Bitcoin", BigDecimal.valueOf(50000));
        HistoricalData historicalData = new HistoricalData("BTC", TimePeriod.ONE_WEEK);
        
        when(cryptoDataService.isTickerSupported(anyString())).thenReturn(true);
        when(cryptoDataService.getMarketData(anyString())).thenReturn(Optional.of(marketData));
        when(cryptoDataService.getHistoricalData(anyString(), any(TimePeriod.class))).thenReturn(Optional.of(historicalData));

        // When
        var results = orchestrator.analyzeMultiple(java.util.Arrays.asList(ticker1, ticker2));

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        results.values().forEach(report -> {
            assertTrue(report.isSuccessful());
            assertEquals(3, report.getAgentResults().size());
        });
        
        verify(cryptoDataService, times(2)).isTickerSupported(anyString());
    }

    @Test
    @DisplayName("Should handle mixed valid and invalid tickers")
    void shouldHandleMixedValidAndInvalidTickers() {
        // Given
        String validTicker = "BTC";
        String invalidTicker = "INVALID";
        MarketData marketData = new MarketData(validTicker, "Bitcoin", BigDecimal.valueOf(50000));
        HistoricalData historicalData = new HistoricalData(validTicker, TimePeriod.ONE_WEEK);
        
        when(cryptoDataService.isTickerSupported(validTicker)).thenReturn(true);
        when(cryptoDataService.isTickerSupported(invalidTicker)).thenReturn(false);
        when(cryptoDataService.getMarketData(validTicker)).thenReturn(Optional.of(marketData));
        when(cryptoDataService.getHistoricalData(validTicker, TimePeriod.ONE_WEEK)).thenReturn(Optional.of(historicalData));

        // When
        var results = orchestrator.analyzeMultiple(java.util.Arrays.asList(validTicker, invalidTicker));

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        
        AnalysisReport validReport = results.get(validTicker);
        assertTrue(validReport.isSuccessful());
        
        AnalysisReport invalidReport = results.get(invalidTicker);
        assertFalse(invalidReport.isSuccessful());
        
        verify(cryptoDataService, times(2)).isTickerSupported(anyString());
    }

    @Test
    @DisplayName("Should handle null ticker in multiple analysis")
    void shouldHandleNullTickerInMultipleAnalysis() {
        // Given
        String validTicker = "BTC";
        MarketData marketData = new MarketData(validTicker, "Bitcoin", BigDecimal.valueOf(50000));
        HistoricalData historicalData = new HistoricalData(validTicker, TimePeriod.ONE_WEEK);
        
        when(cryptoDataService.isTickerSupported(validTicker)).thenReturn(true);
        when(cryptoDataService.getMarketData(validTicker)).thenReturn(Optional.of(marketData));
        when(cryptoDataService.getHistoricalData(validTicker, TimePeriod.ONE_WEEK)).thenReturn(Optional.of(historicalData));

        // When & Then
        assertThrows(OrchestrationException.class, () -> {
            orchestrator.analyze(null);
        });
    }

    @Test
    @DisplayName("Should handle empty ticker in multiple analysis")
    void shouldHandleEmptyTickerInMultipleAnalysis() {
        // Given
        String validTicker = "BTC";
        MarketData marketData = new MarketData(validTicker, "Bitcoin", BigDecimal.valueOf(50000));
        HistoricalData historicalData = new HistoricalData(validTicker, TimePeriod.ONE_WEEK);
        
        when(cryptoDataService.isTickerSupported(validTicker)).thenReturn(true);
        when(cryptoDataService.getMarketData(validTicker)).thenReturn(Optional.of(marketData));
        when(cryptoDataService.getHistoricalData(validTicker, TimePeriod.ONE_WEEK)).thenReturn(Optional.of(historicalData));

        // When & Then
        assertThrows(OrchestrationException.class, () -> {
            orchestrator.analyze("");
        });
    }

    @Test
    @DisplayName("Should check orchestrator readiness")
    void shouldCheckOrchestratorReadiness() {
        // When
        boolean isReady = orchestrator.isReady();

        // Then
        assertTrue(isReady);
    }

    @Test
    @DisplayName("Should get available agents")
    void shouldGetAvailableAgents() {
        // When
        var agents = orchestrator.getAvailableAgents();

        // Then
        assertNotNull(agents);
        assertEquals(3, agents.size());
    }
} 