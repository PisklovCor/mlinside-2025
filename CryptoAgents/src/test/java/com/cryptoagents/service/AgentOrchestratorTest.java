package com.cryptoagents.service;

import com.cryptoagents.BaseSpringBootTest;
import com.cryptoagents.agent.*;
import com.cryptoagents.model.AnalysisReport;
import com.cryptoagents.model.AnalystReport;
import com.cryptoagents.model.RiskManagerReport;
import com.cryptoagents.model.TraderReport;
import com.cryptoagents.model.dto.MarketData;
import com.cryptoagents.model.dto.HistoricalData;
import com.cryptoagents.model.enums.TimePeriod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("AgentOrchestrator Tests")
class AgentOrchestratorTest extends BaseSpringBootTest {

    @MockBean
    private AgentFactory agentFactory;

    @MockBean
    private CryptoDataService cryptoDataService;

    @MockBean
    private Agent analystAgent;

    @MockBean
    private Agent riskManagerAgent;

    @MockBean
    private Agent traderAgent;

    private AgentOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        orchestrator = new AgentOrchestrator(agentFactory, cryptoDataService);

        // Setup default agent factory behavior
        when(agentFactory.createAgent(Agent.AgentType.ANALYST)).thenReturn(analystAgent);
        when(agentFactory.createAgent(Agent.AgentType.RISK_MANAGER)).thenReturn(riskManagerAgent);
        when(agentFactory.createAgent(Agent.AgentType.TRADER)).thenReturn(traderAgent);

        // Setup default agent behavior
        when(analystAgent.getType()).thenReturn(Agent.AgentType.ANALYST);
        when(riskManagerAgent.getType()).thenReturn(Agent.AgentType.RISK_MANAGER);
        when(traderAgent.getType()).thenReturn(Agent.AgentType.TRADER);
    }

    @Nested
    @DisplayName("Sequential Analysis Tests")
    class SequentialAnalysisTests {

        @Test
        @DisplayName("Should analyze single ticker successfully")
        void shouldAnalyzeSingleTickerSuccessfully() throws Exception {
            logTestStart("shouldAnalyzeSingleTickerSuccessfully");
            
            // Given
            String ticker = "BTC";
            MarketData marketData = new MarketData(ticker, "Bitcoin", BigDecimal.valueOf(50000));
            HistoricalData historicalData = new HistoricalData(ticker, TimePeriod.ONE_MONTH);

            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
            when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(marketData));
            when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_MONTH)).thenReturn(Optional.of(historicalData));

            AnalystReport analystResult = new AnalystReport(ticker);
            RiskManagerReport riskResult = new RiskManagerReport(ticker);
            TraderReport traderResult = new TraderReport(ticker);

            when(analystAgent.analyze(any())).thenReturn(analystResult);
            when(riskManagerAgent.analyze(any())).thenReturn(riskResult);
            when(traderAgent.analyze(any())).thenReturn(traderResult);

            // When
            AnalysisReport result = orchestrator.analyze(ticker);

            // Then
            assertNotNull(result);
            assertEquals(ticker, result.getTicker());
            assertTrue(result.isSuccessful());
            assertEquals(3, result.getAgentResults().size());
            assertNotNull(result.getAnalysisStartTime());
            assertNotNull(result.getAnalysisEndTime());
            assertNotNull(result.getExecutionTimeMs());
            
            logTestEnd("shouldAnalyzeSingleTickerSuccessfully");
        }

        @Test
        @DisplayName("Should handle unsupported ticker")
        void shouldHandleUnsupportedTicker() {
            logTestStart("shouldHandleUnsupportedTicker");
            
            // Given
            String ticker = "INVALID";

            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(false);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> orchestrator.analyze(ticker));
            
            logTestEnd("shouldHandleUnsupportedTicker");
        }

        @Test
        @DisplayName("Should handle market data retrieval failure")
        void shouldHandleMarketDataRetrievalFailure() {
            logTestStart("shouldHandleMarketDataRetrievalFailure");
            
            // Given
            String ticker = "BTC";

            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
            when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(RuntimeException.class, () -> orchestrator.analyze(ticker));
            
            logTestEnd("shouldHandleMarketDataRetrievalFailure");
        }

        @Test
        @DisplayName("Should handle historical data retrieval failure")
        void shouldHandleHistoricalDataRetrievalFailure() {
            logTestStart("shouldHandleHistoricalDataRetrievalFailure");
            
            // Given
            String ticker = "BTC";
            MarketData marketData = new MarketData(ticker, "Bitcoin", BigDecimal.valueOf(50000));

            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
            when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(marketData));
            when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_MONTH)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(RuntimeException.class, () -> orchestrator.analyze(ticker));
            
            logTestEnd("shouldHandleHistoricalDataRetrievalFailure");
        }
    }

    @Nested
    @DisplayName("Batch Analysis Tests")
    class BatchAnalysisTests {

        @Test
        @DisplayName("Should analyze multiple tickers successfully")
        void shouldAnalyzeMultipleTickersSuccessfully() throws Exception {
            logTestStart("shouldAnalyzeMultipleTickersSuccessfully");
            
            // Given
            List<String> tickers = Arrays.asList("BTC", "ETH", "ADA");
            MarketData marketData = new MarketData("BTC", "Bitcoin", BigDecimal.valueOf(50000));
            HistoricalData historicalData = new HistoricalData("BTC", TimePeriod.ONE_MONTH);

            when(cryptoDataService.isTickerSupported(anyString())).thenReturn(true);
            when(cryptoDataService.getMarketData(anyString())).thenReturn(Optional.of(marketData));
            when(cryptoDataService.getHistoricalData(anyString(), any(TimePeriod.class))).thenReturn(Optional.of(historicalData));

            AnalystReport analystResult = new AnalystReport("BTC");
            RiskManagerReport riskResult = new RiskManagerReport("BTC");
            TraderReport traderResult = new TraderReport("BTC");

            when(analystAgent.analyze(any())).thenReturn(analystResult);
            when(riskManagerAgent.analyze(any())).thenReturn(riskResult);
            when(traderAgent.analyze(any())).thenReturn(traderResult);

            // When
            List<AnalysisReport> results = orchestrator.analyzeMultiple(tickers);

            // Then
            assertNotNull(results);
            assertEquals(3, results.size());
            results.forEach(result -> {
                assertTrue(result.isSuccessful());
                assertEquals(3, result.getAgentResults().size());
            });
            
            logTestEnd("shouldAnalyzeMultipleTickersSuccessfully");
        }

        @Test
        @DisplayName("Should handle empty ticker list")
        void shouldHandleEmptyTickerList() {
            logTestStart("shouldHandleEmptyTickerList");
            
            // Given
            List<String> tickers = Arrays.asList();

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> orchestrator.analyzeMultiple(tickers));
            
            logTestEnd("shouldHandleEmptyTickerList");
        }
    }

    @Nested
    @DisplayName("Agent Error Handling Tests")
    class AgentErrorHandlingTests {

        @Test
        @DisplayName("Should handle analyst agent failure")
        void shouldHandleAnalystAgentFailure() throws Exception {
            logTestStart("shouldHandleAnalystAgentFailure");
            
            // Given
            String ticker = "BTC";
            MarketData marketData = new MarketData(ticker, "Bitcoin", BigDecimal.valueOf(50000));
            HistoricalData historicalData = new HistoricalData(ticker, TimePeriod.ONE_MONTH);

            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
            when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(marketData));
            when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_MONTH)).thenReturn(Optional.of(historicalData));

            when(analystAgent.analyze(any())).thenThrow(new RuntimeException("Analyst failed"));
            when(riskManagerAgent.analyze(any())).thenReturn(new RiskManagerReport(ticker));
            when(traderAgent.analyze(any())).thenReturn(new TraderReport(ticker));

            // When
            AnalysisReport result = orchestrator.analyze(ticker);

            // Then
            assertNotNull(result);
            assertFalse(result.isSuccessful());
            assertEquals(2, result.getAgentResults().size()); // Only risk manager and trader succeeded
            
            logTestEnd("shouldHandleAnalystAgentFailure");
        }

        @Test
        @DisplayName("Should handle all agents failure")
        void shouldHandleAllAgentsFailure() throws Exception {
            logTestStart("shouldHandleAllAgentsFailure");
            
            // Given
            String ticker = "BTC";
            MarketData marketData = new MarketData(ticker, "Bitcoin", BigDecimal.valueOf(50000));
            HistoricalData historicalData = new HistoricalData(ticker, TimePeriod.ONE_MONTH);

            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
            when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(marketData));
            when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_MONTH)).thenReturn(Optional.of(historicalData));

            when(analystAgent.analyze(any())).thenThrow(new RuntimeException("Analyst failed"));
            when(riskManagerAgent.analyze(any())).thenThrow(new RuntimeException("Risk manager failed"));
            when(traderAgent.analyze(any())).thenThrow(new RuntimeException("Trader failed"));

            // When
            AnalysisReport result = orchestrator.analyze(ticker);

            // Then
            assertNotNull(result);
            assertFalse(result.isSuccessful());
            assertEquals(0, result.getAgentResults().size());
            
            logTestEnd("shouldHandleAllAgentsFailure");
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should measure execution time")
        void shouldMeasureExecutionTime() throws Exception {
            logTestStart("shouldMeasureExecutionTime");
            
            // Given
            String ticker = "BTC";
            MarketData marketData = new MarketData(ticker, "Bitcoin", BigDecimal.valueOf(50000));
            HistoricalData historicalData = new HistoricalData(ticker, TimePeriod.ONE_MONTH);

            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
            when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(marketData));
            when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_MONTH)).thenReturn(Optional.of(historicalData));

            AnalystReport analystResult = new AnalystReport(ticker);
            RiskManagerReport riskResult = new RiskManagerReport(ticker);
            TraderReport traderResult = new TraderReport(ticker);

            when(analystAgent.analyze(any())).thenReturn(analystResult);
            when(riskManagerAgent.analyze(any())).thenReturn(riskResult);
            when(traderAgent.analyze(any())).thenReturn(traderResult);

            // When
            AnalysisReport result = orchestrator.analyze(ticker);

            // Then
            assertNotNull(result.getExecutionTimeMs());
            assertTrue(result.getExecutionTimeMs() >= 0);
            
            logTestEnd("shouldMeasureExecutionTime");
        }
    }
}