//package com.cryptoagents.service;
//
//import com.cryptoagents.BaseSpringBootTest;
//import com.cryptoagents.agent.DefaultAgentFactory;
//import com.cryptoagents.model.AnalysisReport;
//import com.cryptoagents.model.dto.MarketData;
//import com.cryptoagents.model.dto.HistoricalData;
//import com.cryptoagents.model.enums.TimePeriod;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.math.BigDecimal;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@DisplayName("AgentOrchestrator CryptoData Integration Tests")
//class AgentOrchestratorCryptoDataIntegrationTest extends BaseSpringBootTest {
//
//    @Autowired
//    private AgentOrchestrator orchestrator;
//
//    @MockBean
//    private CryptoDataService cryptoDataService;
//
//    @BeforeEach
//    void setUp() {
//        reset(cryptoDataService);
//    }
//
//    @Nested
//    @DisplayName("Data Service Integration Tests")
//    class DataServiceIntegrationTests {
//
//        @Test
//        @DisplayName("Should integrate with CryptoDataService for market data")
//        void shouldIntegrateWithCryptoDataServiceForMarketData() {
//            // Given
//            String ticker = "BTC";
//            MarketData expectedMarketData = new MarketData(ticker, "Bitcoin", BigDecimal.valueOf(50000));
//            HistoricalData expectedHistoricalData = new HistoricalData(ticker, TimePeriod.ONE_WEEK);
//
//            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
//            when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(expectedMarketData));
//            when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_WEEK)).thenReturn(Optional.of(expectedHistoricalData));
//
//            // When
//            AnalysisReport result = orchestrator.analyze(ticker);
//
//            // Then
//            assertNotNull(result);
//            assertTrue(result.isSuccessful());
//
//            verify(cryptoDataService).isTickerSupported(ticker);
//            verify(cryptoDataService).getMarketData(ticker);
//            verify(cryptoDataService).getHistoricalData(ticker, TimePeriod.ONE_WEEK);
//        }
//
//        @Test
//        @DisplayName("Should handle CryptoDataService unavailability")
//        void shouldHandleCryptoDataServiceUnavailability() {
//            // Given
//            when(cryptoDataService.isServiceAvailable()).thenReturn(false);
//
//            // When
//            boolean isReady = orchestrator.isReady();
//
//            // Then
//            assertFalse(isReady);
//            verify(cryptoDataService).isServiceAvailable();
//        }
//
//        @Test
//        @DisplayName("Should handle CryptoDataService timeout")
//        void shouldHandleCryptoDataServiceTimeout() {
//            // Given
//            String ticker = "BTC";
//            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
//            when(cryptoDataService.getMarketData(ticker)).thenAnswer(invocation -> {
//                Thread.sleep(6000); // Simulate timeout
//                return Optional.of(new MarketData(ticker, "Bitcoin", BigDecimal.valueOf(50000)));
//            });
//
//            // When & Then
//            assertThrows(OrchestrationException.class, () -> orchestrator.analyze(ticker));
//        }
//
//        @Test
//        @DisplayName("Should handle CryptoDataService rate limiting")
//        void shouldHandleCryptoDataServiceRateLimiting() {
//            // Given
//            String ticker = "BTC";
//            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
//            when(cryptoDataService.getMarketData(ticker)).thenThrow(new RuntimeException("Rate limit exceeded"));
//
//            // When
//            AnalysisReport result = orchestrator.analyze(ticker);
//
//            // Then
//            assertNotNull(result);
//            assertFalse(result.isSuccessful());
//            assertTrue(result.hasErrors());
//        }
//    }
//
//    @Nested
//    @DisplayName("Data Validation Tests")
//    class DataValidationTests {
//
//        @Test
//        @DisplayName("Should validate market data quality")
//        void shouldValidateMarketDataQuality() {
//            // Given
//            String ticker = "BTC";
//            MarketData invalidMarketData = new MarketData(ticker, "", BigDecimal.ZERO); // Invalid data
//            HistoricalData historicalData = new HistoricalData(ticker, TimePeriod.ONE_WEEK);
//
//            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
//            when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(invalidMarketData));
//            when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_WEEK)).thenReturn(Optional.of(historicalData));
//
//            // When
//            AnalysisReport result = orchestrator.analyze(ticker);
//
//            // Then
//            assertNotNull(result);
//            assertFalse(result.isSuccessful());
//            assertTrue(result.hasErrors());
//        }
//
//        @Test
//        @DisplayName("Should handle missing historical data")
//        void shouldHandleMissingHistoricalData() {
//            // Given
//            String ticker = "BTC";
//            MarketData marketData = new MarketData(ticker, "Bitcoin", BigDecimal.valueOf(50000));
//
//            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
//            when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(marketData));
//            when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_WEEK)).thenReturn(Optional.empty());
//
//            // When & Then
//            assertThrows(OrchestrationException.class, () -> orchestrator.analyze(ticker));
//        }
//
//        @Test
//        @DisplayName("Should handle corrupted market data")
//        void shouldHandleCorruptedMarketData() {
//            // Given
//            String ticker = "BTC";
//            MarketData corruptedMarketData = new MarketData(null, null, null); // Corrupted data
//            HistoricalData historicalData = new HistoricalData(ticker, TimePeriod.ONE_WEEK);
//
//            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
//            when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(corruptedMarketData));
//            when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_WEEK)).thenReturn(Optional.of(historicalData));
//
//            // When
//            AnalysisReport result = orchestrator.analyze(ticker);
//
//            // Then
//            assertNotNull(result);
//            assertFalse(result.isSuccessful());
//            assertTrue(result.hasErrors());
//        }
//    }
//
//    @Nested
//    @DisplayName("Multiple Ticker Integration Tests")
//    class MultipleTickerIntegrationTests {
//
//        @Test
//        @DisplayName("Should handle multiple tickers with different data availability")
//        void shouldHandleMultipleTickersWithDifferentDataAvailability() {
//            // Given
//            List<String> tickers = Arrays.asList("BTC", "ETH", "INVALID");
//
//            // BTC - full data
//            when(cryptoDataService.isTickerSupported("BTC")).thenReturn(true);
//            when(cryptoDataService.getMarketData("BTC")).thenReturn(Optional.of(new MarketData("BTC", "Bitcoin", BigDecimal.valueOf(50000))));
//            when(cryptoDataService.getHistoricalData("BTC", TimePeriod.ONE_WEEK)).thenReturn(Optional.of(new HistoricalData("BTC", TimePeriod.ONE_WEEK)));
//
//            // ETH - full data
//            when(cryptoDataService.isTickerSupported("ETH")).thenReturn(true);
//            when(cryptoDataService.getMarketData("ETH")).thenReturn(Optional.of(new MarketData("ETH", "Ethereum", BigDecimal.valueOf(3000))));
//            when(cryptoDataService.getHistoricalData("ETH", TimePeriod.ONE_WEEK)).thenReturn(Optional.of(new HistoricalData("ETH", TimePeriod.ONE_WEEK)));
//
//            // INVALID - not supported
//            when(cryptoDataService.isTickerSupported("INVALID")).thenReturn(false);
//
//            // When
//            var results = orchestrator.analyzeMultiple(tickers);
//
//            // Then
//            assertNotNull(results);
//            assertEquals(3, results.size());
//
//            assertTrue(results.get("BTC").isSuccessful());
//            assertTrue(results.get("ETH").isSuccessful());
//            assertFalse(results.get("INVALID").isSuccessful());
//        }
//
//        @Test
//        @DisplayName("Should handle concurrent data service calls")
//        void shouldHandleConcurrentDataServiceCalls() {
//            // Given
//            List<String> tickers = Arrays.asList("BTC", "ETH", "ADA", "DOT");
//
//            for (String ticker : tickers) {
//                when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
//                when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(new MarketData(ticker, ticker, BigDecimal.valueOf(100))));
//                when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_WEEK)).thenReturn(Optional.of(new HistoricalData(ticker, TimePeriod.ONE_WEEK)));
//            }
//
//            // When
//            var results = orchestrator.analyzeMultiple(tickers);
//
//            // Then
//            assertNotNull(results);
//            assertEquals(4, results.size());
//
//            results.values().forEach(report -> {
//                assertTrue(report.isSuccessful());
//                assertEquals(3, report.getAgentResults().size());
//            });
//
//            // Verify all data service calls were made
//            verify(cryptoDataService, times(4)).isTickerSupported(anyString());
//            verify(cryptoDataService, times(4)).getMarketData(anyString());
//            verify(cryptoDataService, times(4)).getHistoricalData(anyString(), any(TimePeriod.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("Error Recovery Tests")
//    class ErrorRecoveryTests {
//
//        @Test
//        @DisplayName("Should recover from temporary data service failure")
//        void shouldRecoverFromTemporaryDataServiceFailure() {
//            // Given
//            String ticker = "BTC";
//            MarketData marketData = new MarketData(ticker, "Bitcoin", BigDecimal.valueOf(50000));
//            HistoricalData historicalData = new HistoricalData(ticker, TimePeriod.ONE_WEEK);
//
//            // First call fails, second succeeds
//            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
//            when(cryptoDataService.getMarketData(ticker))
//                .thenReturn(Optional.empty()) // First call fails
//                .thenReturn(Optional.of(marketData)); // Second call succeeds
//            when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_WEEK))
//                .thenReturn(Optional.empty()) // First call fails
//                .thenReturn(Optional.of(historicalData)); // Second call succeeds
//
//            // When
//            AnalysisReport result = orchestrator.analyze(ticker);
//
//            // Then
//            assertNotNull(result);
//            assertFalse(result.isSuccessful());
//            assertTrue(result.hasErrors());
//        }
//
//        @Test
//        @DisplayName("Should handle data service partial failure")
//        void shouldHandleDataServicePartialFailure() {
//            // Given
//            String ticker = "BTC";
//            MarketData marketData = new MarketData(ticker, "Bitcoin", BigDecimal.valueOf(50000));
//
//            when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
//            when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(marketData));
//            when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_WEEK)).thenReturn(Optional.empty());
//
//            // When & Then
//            assertThrows(OrchestrationException.class, () -> orchestrator.analyze(ticker));
//        }
//    }
//}