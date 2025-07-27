//package com.cryptoagents.service;
//
//import com.cryptoagents.BaseSpringBootTest;
//import com.cryptoagents.agent.old.DefaultAgentFactory;
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
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//import java.util.Optional;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@DisplayName("AgentOrchestrator Load Tests")
//class AgentOrchestratorLoadTest extends BaseSpringBootTest {
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
//    @DisplayName("Concurrent Load Tests")
//    class ConcurrentLoadTests {
//
//        @Test
//        @DisplayName("Should handle 10 concurrent analyses")
//        void shouldHandle10ConcurrentAnalyses() throws Exception {
//            // Given
//            List<String> tickers = generateTickers(10);
//            setupMockDataForTickers(tickers);
//
//            // When
//            long startTime = System.currentTimeMillis();
//            var results = orchestrator.analyzeMultiple(tickers);
//            long endTime = System.currentTimeMillis();
//
//            // Then
//            assertNotNull(results);
//            assertEquals(10, results.size());
//            assertTrue(endTime - startTime < 15000); // Should complete within 15 seconds
//
//            results.values().forEach(report -> {
//                assertTrue(report.isSuccessful());
//                assertEquals(3, report.getAgentResults().size());
//            });
//        }
//
//        @Test
//        @DisplayName("Should handle 50 concurrent analyses")
//        void shouldHandle50ConcurrentAnalyses() throws Exception {
//            // Given
//            List<String> tickers = generateTickers(50);
//            setupMockDataForTickers(tickers);
//
//            // When
//            long startTime = System.currentTimeMillis();
//            var results = orchestrator.analyzeMultiple(tickers);
//            long endTime = System.currentTimeMillis();
//
//            // Then
//            assertNotNull(results);
//            assertEquals(50, results.size());
//            assertTrue(endTime - startTime < 30000); // Should complete within 30 seconds
//
//            results.values().forEach(report -> {
//                assertTrue(report.isSuccessful());
//                assertEquals(3, report.getAgentResults().size());
//            });
//        }
//
//        @Test
//        @DisplayName("Should handle mixed load with failures")
//        void shouldHandleMixedLoadWithFailures() throws Exception {
//            // Given
//            List<String> tickers = generateTickers(20);
//
//            // Setup some tickers to fail
//            for (int i = 0; i < tickers.size(); i++) {
//                String ticker = tickers.get(i);
//                if (i % 5 == 0) { // Every 5th ticker fails
//                    when(cryptoDataService.isTickerSupported(ticker)).thenReturn(false);
//                } else {
//                    setupMockDataForTicker(ticker);
//                }
//            }
//
//            // When
//            var results = orchestrator.analyzeMultiple(tickers);
//
//            // Then
//            assertNotNull(results);
//            assertEquals(20, results.size());
//
//            int successCount = 0;
//            int failureCount = 0;
//
//            for (AnalysisReport report : results.values()) {
//                if (report.isSuccessful()) {
//                    successCount++;
//                } else {
//                    failureCount++;
//                }
//            }
//
//            assertTrue(successCount > 0);
//            assertTrue(failureCount > 0);
//            assertEquals(20, successCount + failureCount);
//        }
//    }
//
//    // @Nested
//    // @DisplayName("Stress Tests")
//    // class StressTests {
//
//    //     @Test
//    //     @DisplayName("Should handle rapid successive requests")
//    //     void shouldHandleRapidSuccessiveRequests() throws Exception {
//    //         // Given
//    //         String ticker = "BTC";
//    //         setupMockDataForTicker(ticker);
//    //         when(cryptoDataService.isServiceAvailable()).thenReturn(true);
//
//    //         // When
//    //         long startTime = System.currentTimeMillis();
//    //         List<AnalysisReport> results = new ArrayList<>();
//
//    //         for (int i = 0; i < 20; i++) {
//    //             results.add(orchestrator.analyze(ticker));
//    //         }
//
//    //         long endTime = System.currentTimeMillis();
//
//    //         // Then
//    //         assertEquals(20, results.size());
//    //         assertTrue(endTime - startTime < 20000); // Should complete within 20 seconds
//
//    //         results.forEach(report -> {
//    //             assertTrue(report.isSuccessful());
//    //             assertEquals(3, report.getAgentResults().size());
//    //         });
//    //     }
//
//    //     @Test
//    //     @DisplayName("Should handle memory pressure under load")
//    //     void shouldHandleMemoryPressureUnderLoad() throws Exception {
//    //         // Given
//    //         List<String> tickers = generateTickers(100);
//    //         setupMockDataForTickers(tickers);
//
//    //         // When
//    //         long startTime = System.currentTimeMillis();
//    //         var results = orchestrator.analyzeMultiple(tickers);
//    //         long endTime = System.currentTimeMillis();
//
//    //         // Then
//    //         assertNotNull(results);
//    //         assertEquals(100, results.size());
//    //         assertTrue(endTime - startTime < 60000); // Should complete within 60 seconds
//
//    //         // Check memory usage is reasonable
//    //         Runtime runtime = Runtime.getRuntime();
//    //         long usedMemory = runtime.totalMemory() - runtime.freeMemory();
//    //         long maxMemory = runtime.maxMemory();
//
//    //         assertTrue(usedMemory < maxMemory * 0.8); // Should use less than 80% of max memory
//    //     }
//    // }
//
//    @Nested
//    @DisplayName("Performance Benchmark Tests")
//    class PerformanceBenchmarkTests {
//
//        @Test
//        @DisplayName("Should maintain performance under sustained load")
//        void shouldMaintainPerformanceUnderSustainedLoad() throws Exception {
//            // Given
//            List<String> tickers = generateTickers(30);
//            setupMockDataForTickers(tickers);
//
//            // When
//            List<Long> executionTimes = new ArrayList<>();
//
//            for (int round = 0; round < 3; round++) {
//                long startTime = System.currentTimeMillis();
//                var results = orchestrator.analyzeMultiple(tickers);
//                long endTime = System.currentTimeMillis();
//
//                executionTimes.add(endTime - startTime);
//
//                assertNotNull(results);
//                assertEquals(30, results.size());
//            }
//
//            // Then
//            assertEquals(3, executionTimes.size());
//
//            // Performance should not degrade significantly
//            long firstExecution = executionTimes.get(0);
//            long lastExecution = executionTimes.get(2);
//
//            assertTrue(lastExecution <= firstExecution * 1.5); // Should not degrade more than 50%
//        }
//
//        @Test
//        @DisplayName("Should handle burst traffic")
//        void shouldHandleBurstTraffic() throws Exception {
//            // Given
//            List<String> tickers = generateTickers(25);
//            setupMockDataForTickers(tickers);
//
//            // When
//            ExecutorService executor = Executors.newFixedThreadPool(10);
//            List<CompletableFuture<AnalysisReport>> futures = new ArrayList<>();
//
//            long startTime = System.currentTimeMillis();
//
//            for (String ticker : tickers) {
//                CompletableFuture<AnalysisReport> future = CompletableFuture.supplyAsync(() -> {
//                    try {
//                        return orchestrator.analyze(ticker);
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                }, executor);
//                futures.add(future);
//            }
//
//            // Wait for all to complete
//            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(30, TimeUnit.SECONDS);
//
//            long endTime = System.currentTimeMillis();
//
//            // Then
//            assertEquals(25, futures.size());
//            assertTrue(endTime - startTime < 25000); // Should complete within 25 seconds
//
//            futures.forEach(future -> {
//                try {
//                    AnalysisReport report = future.get();
//                    assertTrue(report.isSuccessful());
//                    assertEquals(3, report.getAgentResults().size());
//                } catch (Exception e) {
//                    fail("Future should complete successfully");
//                }
//            });
//
//            executor.shutdown();
//        }
//    }
//
//    @Nested
//    @DisplayName("Resource Management Tests")
//    class ResourceManagementTests {
//
//        @Test
//        @DisplayName("Should not leak memory under load")
//        void shouldNotLeakMemoryUnderLoad() throws Exception {
//            // Дано
//            List<String> tickers = generateTickers(50);
//            setupMockDataForTickers(tickers);
//
//            // Когда
//            Runtime runtime = Runtime.getRuntime();
//            long initialMemory = runtime.totalMemory() - runtime.freeMemory();
//
//            for (int i = 0; i < 5; i++) {
//                var results = orchestrator.analyzeMultiple(tickers);
//                assertNotNull(results);
//                assertEquals(50, results.size());
//
//                // Принудительная сборка мусора
//                System.gc();
//                Thread.sleep(100);
//            }
//
//            long finalMemory = runtime.totalMemory() - runtime.freeMemory();
//
//            // Тогда
//            // Использование памяти не должно значительно увеличиваться
//            assertTrue(finalMemory <= initialMemory * 1.2); // Не должно увеличиваться более чем на 20%
//        }
//
//        @Test
//        @DisplayName("Should handle thread pool exhaustion gracefully")
//        void shouldHandleThreadPoolExhaustionGracefully() throws Exception {
//            // Given
//            List<String> tickers = generateTickers(200); // Very large number
//            setupMockDataForTickers(tickers);
//
//            // When
//            long startTime = System.currentTimeMillis();
//            var results = orchestrator.analyzeMultiple(tickers);
//            long endTime = System.currentTimeMillis();
//
//            // Then
//            assertNotNull(results);
//            assertEquals(200, results.size());
//            assertTrue(endTime - startTime < 120000); // Should complete within 2 minutes
//
//            results.values().forEach(report -> {
//                assertTrue(report.isSuccessful());
//                assertEquals(3, report.getAgentResults().size());
//            });
//        }
//    }
//
//    // Helper methods
//    private List<String> generateTickers(int count) {
//        List<String> tickers = new ArrayList<>();
//        String[] baseTickers = {"BTC", "ETH", "ADA", "DOT", "LINK", "UNI", "AAVE", "COMP", "MKR", "SNX"};
//
//        for (int i = 0; i < count; i++) {
//            String baseTicker = baseTickers[i % baseTickers.length];
//            tickers.add(baseTicker + (i / baseTickers.length + 1));
//        }
//
//        return tickers;
//    }
//
//    private void setupMockDataForTickers(List<String> tickers) {
//        for (String ticker : tickers) {
//            setupMockDataForTicker(ticker);
//        }
//    }
//
//    private void setupMockDataForTicker(String ticker) {
//        when(cryptoDataService.isTickerSupported(ticker)).thenReturn(true);
//        when(cryptoDataService.getMarketData(ticker)).thenReturn(Optional.of(new MarketData(ticker, ticker, BigDecimal.valueOf(100))));
//        when(cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_WEEK)).thenReturn(Optional.of(new HistoricalData(ticker, TimePeriod.ONE_WEEK)));
//    }
//}