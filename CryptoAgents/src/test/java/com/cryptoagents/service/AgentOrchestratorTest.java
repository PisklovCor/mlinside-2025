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
        }

        @Test
        @DisplayName("Should handle unsupported ticker")
        void shouldHandleUnsupportedTicker() throws Exception {
            // Given
            String ticker = "INVALID";
            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(false);

            // When
            AnalysisReport result = orchestrator.analyze(ticker);

            // Then
            assertNotNull(result);
            assertEquals(ticker, result.getTicker());
            assertFalse(result.isSuccessful());
            assertTrue(result.hasErrors());
            assertTrue(result.getErrors().stream().anyMatch(error -> error.contains("not supported")));
        }

        @Test
        @DisplayName("Should handle null ticker")
        void shouldHandleNullTicker() {
            // When & Then
            assertThrows(OrchestrationException.class, () -> orchestrator.analyze(null));
        }

        @Test
        @DisplayName("Should handle empty ticker")
        void shouldHandleEmptyTicker() {
            // When & Then
            assertThrows(OrchestrationException.class, () -> orchestrator.analyze(""));
        }
    }

    @Nested
    @DisplayName("Parallel Analysis Tests")
    class ParallelAnalysisTests {

        @Test
        @DisplayName("Should analyze multiple tickers in parallel")
        void shouldAnalyzeMultipleTickersInParallel() throws Exception {
            // Given
            List<String> tickers = Arrays.asList("BTC", "ETH", "ADA");
            
            for (String ticker : tickers) {
                when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
                when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(new MarketData(ticker, ticker, BigDecimal.valueOf(100))));
                when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_MONTH)).thenReturn(Optional.of(new HistoricalData(ticker, TimePeriod.ONE_MONTH)));
            }
            
            AnalystReport analystResult = new AnalystReport("BTC");
            RiskManagerReport riskResult = new RiskManagerReport("BTC");
            TraderReport traderResult = new TraderReport("BTC");
            
            when(analystAgent.analyze(any())).thenReturn(analystResult);
            when(riskManagerAgent.analyze(any())).thenReturn(riskResult);
            when(traderAgent.analyze(any())).thenReturn(traderResult);

            // When
            var results = orchestrator.analyzeMultiple(tickers);

            // Then
            assertNotNull(results);
            assertEquals(3, results.size());
            results.values().forEach(report -> {
                assertTrue(report.isSuccessful());
                assertEquals(3, report.getAgentResults().size());
            });
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very long ticker names")
        void shouldHandleVeryLongTickerNames() throws Exception {
            // Given
            String longTicker = "A".repeat(100);
            MarketData marketData = new MarketData(longTicker, "Long Ticker", BigDecimal.valueOf(100));
            HistoricalData historicalData = new HistoricalData(longTicker, TimePeriod.ONE_MONTH);
            
            when(cryptoDataService.isTickerSupported(longTicker)).thenReturn(true);
            when(cryptoDataService.getMarketData(longTicker)).thenReturn(Optional.of(marketData));
            when(cryptoDataService.getHistoricalData(longTicker, TimePeriod.ONE_MONTH)).thenReturn(Optional.of(historicalData));
            
            AnalystReport analystResult = new AnalystReport(longTicker);
            RiskManagerReport riskResult = new RiskManagerReport(longTicker);
            TraderReport traderResult = new TraderReport(longTicker);
            
            when(analystAgent.analyze(any())).thenReturn(analystResult);
            when(riskManagerAgent.analyze(any())).thenReturn(riskResult);
            when(traderAgent.analyze(any())).thenReturn(traderResult);

            // When
            AnalysisReport result = orchestrator.analyze(longTicker);

            // Then
            assertNotNull(result);
            assertEquals(longTicker, result.getTicker());
            assertTrue(result.isSuccessful());
        }

        @Test
        @DisplayName("Should handle special characters in ticker")
        void shouldHandleSpecialCharactersInTicker() throws Exception {
            // Given
            String specialTicker = "BTC-USD";
            MarketData marketData = new MarketData(specialTicker, "Bitcoin USD", BigDecimal.valueOf(50000));
            HistoricalData historicalData = new HistoricalData(specialTicker, TimePeriod.ONE_MONTH);
            
            when(cryptoDataService.isTickerSupported(specialTicker)).thenReturn(true);
            when(cryptoDataService.getMarketData(specialTicker)).thenReturn(Optional.of(marketData));
            when(cryptoDataService.getHistoricalData(specialTicker, TimePeriod.ONE_MONTH)).thenReturn(Optional.of(historicalData));
            
            AnalystReport analystResult = new AnalystReport(specialTicker);
            RiskManagerReport riskResult = new RiskManagerReport(specialTicker);
            TraderReport traderResult = new TraderReport(specialTicker);
            
            when(analystAgent.analyze(any())).thenReturn(analystResult);
            when(riskManagerAgent.analyze(any())).thenReturn(riskResult);
            when(traderAgent.analyze(any())).thenReturn(traderResult);

            // When
            AnalysisReport result = orchestrator.analyze(specialTicker);

            // Then
            assertNotNull(result);
            assertEquals(specialTicker, result.getTicker());
            assertTrue(result.isSuccessful());
        }

        @Test
        @DisplayName("Should handle whitespace in ticker")
        void shouldHandleWhitespaceInTicker() {
            // When & Then
            assertThrows(OrchestrationException.class, () -> orchestrator.analyze(" BTC "));
            assertThrows(OrchestrationException.class, () -> orchestrator.analyze("  "));
        }

        @Test
        @DisplayName("Should handle case sensitivity")
        void shouldHandleCaseSensitivity() throws Exception {
            // Given
            String lowerTicker = "btc";
            String upperTicker = "BTC";
            MarketData marketData = new MarketData(upperTicker, "Bitcoin", BigDecimal.valueOf(50000));
            HistoricalData historicalData = new HistoricalData(upperTicker, TimePeriod.ONE_MONTH);
            
            when(cryptoDataService.isTickerSupported(lowerTicker)).thenReturn(true);
            when(cryptoDataService.getMarketData(lowerTicker)).thenReturn(Optional.of(marketData));
            when(cryptoDataService.getHistoricalData(lowerTicker, TimePeriod.ONE_MONTH)).thenReturn(Optional.of(historicalData));
            
            AnalystReport analystResult = new AnalystReport(upperTicker);
            RiskManagerReport riskResult = new RiskManagerReport(upperTicker);
            TraderReport traderResult = new TraderReport(upperTicker);
            
            when(analystAgent.analyze(any())).thenReturn(analystResult);
            when(riskManagerAgent.analyze(any())).thenReturn(riskResult);
            when(traderAgent.analyze(any())).thenReturn(traderResult);

            // When
            AnalysisReport result = orchestrator.analyze(lowerTicker);

            // Then
            assertNotNull(result);
            assertEquals(upperTicker, result.getTicker());
            assertTrue(result.isSuccessful());
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should complete analysis within reasonable time")
        void shouldCompleteAnalysisWithinReasonableTime() throws Exception {
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
            long startTime = System.currentTimeMillis();
            AnalysisReport result = orchestrator.analyze(ticker);
            long endTime = System.currentTimeMillis();

            // Then
            assertNotNull(result);
            assertTrue(result.isSuccessful());
            assertTrue(endTime - startTime < 5000); // Should complete within 5 seconds
            assertNotNull(result.getExecutionTimeMs());
            assertTrue(result.getExecutionTimeMs() > 0);
        }

        @Test
        @DisplayName("Should handle concurrent analysis efficiently")
        void shouldHandleConcurrentAnalysisEfficiently() throws Exception {
            // Given
            List<String> tickers = Arrays.asList("BTC", "ETH", "ADA", "DOT", "LINK");
            
            for (String ticker : tickers) {
                when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
                when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(new MarketData(ticker, ticker, BigDecimal.valueOf(100))));
                when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_MONTH)).thenReturn(Optional.of(new HistoricalData(ticker, TimePeriod.ONE_MONTH)));
            }
            
            AnalystReport analystResult = new AnalystReport("BTC");
            RiskManagerReport riskResult = new RiskManagerReport("BTC");
            TraderReport traderResult = new TraderReport("BTC");
            
            when(analystAgent.analyze(any())).thenReturn(analystResult);
            when(riskManagerAgent.analyze(any())).thenReturn(riskResult);
            when(traderAgent.analyze(any())).thenReturn(traderResult);

            // When
            long startTime = System.currentTimeMillis();
            var results = orchestrator.analyzeMultiple(tickers);
            long endTime = System.currentTimeMillis();

            // Then
            assertNotNull(results);
            assertEquals(5, results.size());
            assertTrue(endTime - startTime < 10000); // Should complete within 10 seconds for 5 tickers
            
            results.values().forEach(report -> {
                assertTrue(report.isSuccessful());
                assertEquals(3, report.getAgentResults().size());
            });
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle agent analysis failure")
        void shouldHandleAgentAnalysisFailure() throws Exception {
            // Given
            String ticker = "BTC";
            MarketData marketData = new MarketData(ticker, "Bitcoin", BigDecimal.valueOf(50000));
            HistoricalData historicalData = new HistoricalData(ticker, TimePeriod.ONE_MONTH);
            
            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
            when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(marketData));
            when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_MONTH)).thenReturn(Optional.of(historicalData));
            
            when(analystAgent.analyze(any())).thenThrow(new RuntimeException("Agent failed"));

            // When & Then
            assertThrows(OrchestrationException.class, () -> orchestrator.analyze(ticker));
        }

        @Test
        @DisplayName("Should handle data service failure")
        void shouldHandleDataServiceFailure() throws Exception {
            // Given
            String ticker = "BTC";
            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
            when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.empty());

            // When
            AnalysisReport result = orchestrator.analyze(ticker);

            // Then
            assertNotNull(result);
            assertFalse(result.isSuccessful());
            assertTrue(result.hasErrors());
        }

        @Test
        @DisplayName("Should handle partial agent failures")
        void shouldHandlePartialAgentFailures() throws Exception {
            // Given
            String ticker = "BTC";
            MarketData marketData = new MarketData(ticker, "Bitcoin", BigDecimal.valueOf(50000));
            HistoricalData historicalData = new HistoricalData(ticker, TimePeriod.ONE_MONTH);
            
            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
            when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(marketData));
            when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_MONTH)).thenReturn(Optional.of(historicalData));
            
            AnalystReport analystResult = new AnalystReport(ticker);
            when(analystAgent.analyze(any())).thenReturn(analystResult);
            when(riskManagerAgent.analyze(any())).thenThrow(new RuntimeException("Risk manager failed"));
            when(traderAgent.analyze(any())).thenReturn(new TraderReport(ticker));

            // When & Then
            assertThrows(OrchestrationException.class, () -> orchestrator.analyze(ticker));
        }

        @Test
        @DisplayName("Should handle timeout scenarios")
        void shouldHandleTimeoutScenarios() throws Exception {
            // Given
            String ticker = "BTC";
            MarketData marketData = new MarketData(ticker, "Bitcoin", BigDecimal.valueOf(50000));
            HistoricalData historicalData = new HistoricalData(ticker, TimePeriod.ONE_MONTH);
            
            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
            when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(marketData));
            when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_MONTH)).thenReturn(Optional.of(historicalData));
            
            when(analystAgent.analyze(any())).thenAnswer(invocation -> {
                Thread.sleep(6000); // Simulate timeout
                return new AnalystReport(ticker);
            });

            // When & Then
            assertThrows(OrchestrationException.class, () -> orchestrator.analyze(ticker));
        }
    }

    @Nested
    @DisplayName("System Readiness Tests")
    class SystemReadinessTests {

        @Test
        @DisplayName("Should check system readiness")
        void shouldCheckSystemReadiness() {
            // Given
            when(cryptoDataService.isServiceAvailable()).thenReturn(true);

            // When
            boolean isReady = orchestrator.isReady();

            // Then
            assertTrue(isReady);
        }

        @Test
        @DisplayName("Should return available agents")
        void shouldReturnAvailableAgents() {
            // Given
            List<Agent> expectedAgents = Arrays.asList(analystAgent, riskManagerAgent, traderAgent);

            // When
            List<Agent> actualAgents = orchestrator.getAvailableAgents();

            // Then
            assertEquals(3, actualAgents.size());
        }

        @Test
        @DisplayName("Should handle data service unavailability")
        void shouldHandleDataServiceUnavailability() {
            // Given
            when(cryptoDataService.isServiceAvailable()).thenReturn(false);

            // When
            boolean isReady = orchestrator.isReady();

            // Then
            assertFalse(isReady);
        }

        @Test
        @DisplayName("Should handle missing agents")
        void shouldHandleMissingAgents() {
            // Given
            when(cryptoDataService.isServiceAvailable()).thenReturn(true);
            when(agentFactory.createAgent(any())).thenThrow(new RuntimeException("Agent creation failed"));

            // When
            boolean isReady = orchestrator.isReady();

            // Then
            assertFalse(isReady);
        }
    }

    @Nested
    @DisplayName("Metrics Tests")
    class MetricsTests {

        @Test
        @DisplayName("Should record metrics for successful analysis")
        void shouldRecordMetricsForSuccessfulAnalysis() throws Exception {
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
            assertTrue(result.isSuccessful());
            
            // Verify metrics were recorded
            OrchestratorMetrics metrics = orchestrator.getMetrics();
            assertNotNull(metrics);
        }

        @Test
        @DisplayName("Should record metrics for failed analysis")
        void shouldRecordMetricsForFailedAnalysis() throws Exception {
            // Given
            String ticker = "BTC";
            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(false);

            // When
            AnalysisReport result = orchestrator.analyze(ticker);

            // Then
            assertNotNull(result);
            assertFalse(result.isSuccessful());
            
            // Verify metrics were recorded
            OrchestratorMetrics metrics = orchestrator.getMetrics();
            assertNotNull(metrics);
        }
    }

    @Nested
    @DisplayName("Input Validation Tests")
    class InputValidationTests {

        @Test
        @DisplayName("Should validate ticker format")
        void shouldValidateTickerFormat() {
            // When & Then
            assertThrows(OrchestrationException.class, () -> orchestrator.analyze("123"));
            assertThrows(OrchestrationException.class, () -> orchestrator.analyze("BTC@"));
            assertThrows(OrchestrationException.class, () -> orchestrator.analyze("BTC/USD"));
        }

        @Test
        @DisplayName("Should validate ticker length")
        void shouldValidateTickerLength() {
            // When & Then
            assertThrows(OrchestrationException.class, () -> orchestrator.analyze("A"));
            assertThrows(OrchestrationException.class, () -> orchestrator.analyze("A".repeat(101)));
        }

        @Test
        @DisplayName("Should validate multiple tickers")
        void shouldValidateMultipleTickers() {
            // When & Then
            assertThrows(OrchestrationException.class, () -> orchestrator.analyzeMultiple(Arrays.asList("BTC", null)));
            assertThrows(OrchestrationException.class, () -> orchestrator.analyzeMultiple(Arrays.asList("BTC", "")));
            assertThrows(OrchestrationException.class, () -> orchestrator.analyzeMultiple(Arrays.asList("BTC", "INVALID@")));
        }
    }
} 