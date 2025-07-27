//package com.cryptoagents.service;
//
//import com.cryptoagents.BaseSpringBootTest;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@DisplayName("OrchestratorMetrics Tests")
//class OrchestratorMetricsTest extends BaseSpringBootTest {
//
//    private OrchestratorMetrics metrics;
//
//    @BeforeEach
//    void setUp() {
//        metrics = new OrchestratorMetrics();
//    }
//
//    @Nested
//    @DisplayName("Analysis Tracking Tests")
//    class AnalysisTrackingTests {
//
//        @Test
//        @DisplayName("Should record analysis start")
//        void shouldRecordAnalysisStart() {
//            // Дано
//            String ticker = "BTC";
//
//            // Когда
//            metrics.recordAnalysisStart(ticker);
//
//            // Тогда
//            assertEquals(1, metrics.getTotalAnalysisRequests());
//        }
//
//        @Test
//        @DisplayName("Should record analysis success")
//        void shouldRecordAnalysisSuccess() {
//            // Дано
//            String ticker = "BTC";
//            long executionTime = 1500L;
//            metrics.recordAnalysisStart(ticker);
//
//            // Когда
//            metrics.recordAnalysisSuccess(ticker, executionTime);
//
//            // Тогда
//            assertEquals(1, metrics.getSuccessfulAnalysis());
//            assertEquals(0, metrics.getFailedAnalysis());
//        }
//
//        @Test
//        @DisplayName("Should record analysis failure")
//        void shouldRecordAnalysisFailure() {
//            // Дано
//            String ticker = "BTC";
//            String error = "Data retrieval failed";
//            metrics.recordAnalysisStart(ticker);
//
//            // Когда
//            metrics.recordAnalysisFailure(ticker, error);
//
//            // Тогда
//            assertEquals(0, metrics.getSuccessfulAnalysis());
//            assertEquals(1, metrics.getFailedAnalysis());
//        }
//    }
//
//    @Nested
//    @DisplayName("Performance Metrics Tests")
//    class PerformanceMetricsTests {
//
//        @Test
//        @DisplayName("Should track average execution time")
//        void shouldTrackAverageExecutionTime() {
//            // Дано
//            String ticker1 = "BTC";
//            String ticker2 = "ETH";
//            long time1 = 1000L;
//            long time2 = 2000L;
//
//            // Когда
//            metrics.recordAnalysisStart(ticker1);
//            metrics.recordAnalysisSuccess(ticker1, time1);
//            metrics.recordAnalysisStart(ticker2);
//            metrics.recordAnalysisSuccess(ticker2, time2);
//
//            // Тогда
//            assertEquals(2, metrics.getSuccessfulAnalysis());
//            assertTrue(metrics.getAverageExecutionTime() > 0);
//        }
//
//        @Test
//        @DisplayName("Should track success rate")
//        void shouldTrackSuccessRate() {
//            // Дано
//            String ticker1 = "BTC";
//            String ticker2 = "ETH";
//            long time1 = 1000L;
//
//            // Когда
//            metrics.recordAnalysisStart(ticker1);
//            metrics.recordAnalysisSuccess(ticker1, time1);
//            metrics.recordAnalysisStart(ticker2);
//            metrics.recordAnalysisFailure(ticker2, "Error");
//
//            // Тогда
//            assertEquals(50.0, metrics.getSuccessRate(), 0.1);
//            assertEquals(50.0, metrics.getFailureRate(), 0.1);
//        }
//    }
//
//    @Nested
//    @DisplayName("Agent Metrics Tests")
//    class AgentMetricsTests {
//
//        @Test
//        @DisplayName("Should track agent execution")
//        void shouldTrackAgentExecution() {
//            // Дано
//            String agentName = "ANALYST";
//            long executionTime = 500L;
//
//            // Когда
//            metrics.recordAgentExecution(agentName, executionTime, true);
//
//            // Тогда
//            assertEquals(1, metrics.getAgentExecutionCount(agentName));
//            assertTrue(metrics.getAgentAverageExecutionTime(agentName) > 0);
//        }
//
//        @Test
//        @DisplayName("Should track agent failure")
//        void shouldTrackAgentFailure() {
//            // Дано
//            String agentName = "RISK_MANAGER";
//            long executionTime = 300L;
//
//            // Когда
//            metrics.recordAgentExecution(agentName, executionTime, false);
//
//            // Тогда
//            assertEquals(1, metrics.getAgentExecutionCount(agentName));
//            assertEquals(100.0, metrics.getAgentFailureRate(agentName), 0.1);
//        }
//
//        @Test
//        @DisplayName("Should track agent average execution time")
//        void shouldTrackAgentAverageExecutionTime() {
//            // Дано
//            String agentName = "TRADER";
//            long time1 = 200L;
//            long time2 = 400L;
//
//            // Когда
//            metrics.recordAgentExecution(agentName, time1, true);
//            metrics.recordAgentExecution(agentName, time2, true);
//
//            // Тогда
//            assertEquals(2, metrics.getAgentExecutionCount(agentName));
//            assertTrue(metrics.getAgentAverageExecutionTime(agentName) >= 200);
//            assertTrue(metrics.getAgentAverageExecutionTime(agentName) <= 400);
//        }
//    }
//
//    @Nested
//    @DisplayName("Metrics Reset Tests")
//    class MetricsResetTests {
//
//        @Test
//        @DisplayName("Should reset all metrics")
//        void shouldResetAllMetrics() {
//            // Дано
//            metrics.recordAnalysisStart("BTC");
//            metrics.recordAnalysisSuccess("BTC", 1000L);
//            metrics.recordAgentExecution("ANALYST", 500L, true);
//
//            // Когда
//            metrics.reset();
//
//            // Тогда
//            assertEquals(0, metrics.getTotalAnalysisRequests());
//            assertEquals(0, metrics.getSuccessfulAnalysis());
//            assertEquals(0, metrics.getFailedAnalysis());
//            assertEquals(0, metrics.getAgentExecutionCount("ANALYST"));
//        }
//
//        @Test
//        @DisplayName("Should reset performance metrics")
//        void shouldResetPerformanceMetrics() {
//            // Дано
//            metrics.recordAnalysisStart("BTC");
//            metrics.recordAnalysisSuccess("BTC", 1000L);
//            long startTime = System.currentTimeMillis();
//
//            // Когда
//            metrics.reset();
//
//            // Тогда
//            assertEquals(0, metrics.getAverageExecutionTime());
//            assertTrue(metrics.getUptime() >= 0);
//        }
//    }
//
//    @Nested
//    @DisplayName("Metrics Summary Tests")
//    class MetricsSummaryTests {
//
//        @Test
//        @DisplayName("Should log metrics summary")
//        void shouldLogMetricsSummary() {
//            // Дано
//            metrics.recordAnalysisStart("BTC");
//            metrics.recordAnalysisSuccess("BTC", 1000L);
//
//            // Когда и Тогда
//            assertDoesNotThrow(() -> metrics.logMetricsSummary());
//        }
//
//        @Test
//        @DisplayName("Should get tracked agents")
//        void shouldGetTrackedAgents() {
//            // Дано
//            metrics.recordAgentExecution("ANALYST", 500L, true);
//            metrics.recordAgentExecution("RISK_MANAGER", 300L, true);
//
//            // Когда
//            var trackedAgents = metrics.getTrackedAgentNames();
//
//            // Тогда
//            assertTrue(trackedAgents.contains("ANALYST"));
//            assertTrue(trackedAgents.contains("RISK_MANAGER"));
//            assertEquals(2, trackedAgents.size());
//        }
//    }
//
//    @Nested
//    @DisplayName("Uptime Tests")
//    class UptimeTests {
//
//        @Test
//        @DisplayName("Should track uptime")
//        void shouldTrackUptime() {
//            // Дано
//            long startTime = System.currentTimeMillis();
//            metrics.recordAnalysisStart("BTC");
//
//            // Когда
//            long uptime = metrics.getUptime();
//            long endTime = System.currentTimeMillis();
//
//            // Тогда
//            // Проверяем, что время работы не превышает ожидаемое с небольшим допуском
//            assertTrue(uptime <= (endTime - startTime + 1000)); // Допускаем некоторую погрешность
//        }
//
//        @Test
//        @DisplayName("Should track last reset time")
//        void shouldTrackLastResetTime() {
//            // Дано
//            long beforeReset = System.currentTimeMillis();
//
//            // Когда
//            metrics.reset();
//            long resetTime = metrics.getLastResetTime();
//
//            // Тогда
//            assertTrue(resetTime >= beforeReset);
//        }
//    }
//}