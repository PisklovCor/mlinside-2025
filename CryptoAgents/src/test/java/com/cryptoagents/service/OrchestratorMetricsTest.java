package com.cryptoagents.service;

import com.cryptoagents.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("OrchestratorMetrics Tests")
class OrchestratorMetricsTest extends BaseSpringBootTest {

    private OrchestratorMetrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new OrchestratorMetrics();
    }

    @Nested
    @DisplayName("Analysis Tracking Tests")
    class AnalysisTrackingTests {

        @Test
        @DisplayName("Should record analysis start")
        void shouldRecordAnalysisStart() {
            // Given
            String ticker = "BTC";

            // When
            metrics.recordAnalysisStart(ticker);

            // Then
            assertEquals(1, metrics.getTotalAnalysisRequests());
        }

        @Test
        @DisplayName("Should record analysis success")
        void shouldRecordAnalysisSuccess() {
            // Given
            String ticker = "BTC";
            long executionTime = 1500L;
            metrics.recordAnalysisStart(ticker);

            // When
            metrics.recordAnalysisSuccess(ticker, executionTime);

            // Then
            assertEquals(1, metrics.getSuccessfulAnalysis());
            assertEquals(0, metrics.getFailedAnalysis());
        }

        @Test
        @DisplayName("Should record analysis failure")
        void shouldRecordAnalysisFailure() {
            // Given
            String ticker = "BTC";
            String error = "Data retrieval failed";
            metrics.recordAnalysisStart(ticker);

            // When
            metrics.recordAnalysisFailure(ticker, error);

            // Then
            assertEquals(0, metrics.getSuccessfulAnalysis());
            assertEquals(1, metrics.getFailedAnalysis());
        }
    }

    @Nested
    @DisplayName("Performance Metrics Tests")
    class PerformanceMetricsTests {

        @Test
        @DisplayName("Should track average execution time")
        void shouldTrackAverageExecutionTime() {
            // Given
            String ticker1 = "BTC";
            String ticker2 = "ETH";
            long time1 = 1000L;
            long time2 = 2000L;

            // When
            metrics.recordAnalysisStart(ticker1);
            metrics.recordAnalysisSuccess(ticker1, time1);
            metrics.recordAnalysisStart(ticker2);
            metrics.recordAnalysisSuccess(ticker2, time2);

            // Then
            assertEquals(2, metrics.getSuccessfulAnalysis());
            assertTrue(metrics.getAverageExecutionTime() > 0);
        }

        @Test
        @DisplayName("Should track success rate")
        void shouldTrackSuccessRate() {
            // Given
            String ticker1 = "BTC";
            String ticker2 = "ETH";
            long time1 = 1000L;

            // When
            metrics.recordAnalysisStart(ticker1);
            metrics.recordAnalysisSuccess(ticker1, time1);
            metrics.recordAnalysisStart(ticker2);
            metrics.recordAnalysisFailure(ticker2, "Error");

            // Then
            assertEquals(50.0, metrics.getSuccessRate(), 0.1);
            assertEquals(50.0, metrics.getFailureRate(), 0.1);
        }
    }

    @Nested
    @DisplayName("Agent Metrics Tests")
    class AgentMetricsTests {

        @Test
        @DisplayName("Should track agent execution")
        void shouldTrackAgentExecution() {
            // Given
            String agentName = "ANALYST";
            long executionTime = 500L;

            // When
            metrics.recordAgentExecution(agentName, executionTime, true);

            // Then
            assertEquals(1, metrics.getAgentExecutionCount(agentName));
            assertEquals(0, metrics.getAgentFailureRate(agentName), 0.1);
        }

        @Test
        @DisplayName("Should track agent failure")
        void shouldTrackAgentFailure() {
            // Given
            String agentName = "RISK_MANAGER";
            long executionTime = 300L;

            // When
            metrics.recordAgentExecution(agentName, executionTime, false);

            // Then
            assertEquals(1, metrics.getAgentExecutionCount(agentName));
            assertEquals(100.0, metrics.getAgentFailureRate(agentName), 0.1);
        }

        @Test
        @DisplayName("Should track agent average execution time")
        void shouldTrackAgentAverageExecutionTime() {
            // Given
            String agentName = "TRADER";
            long time1 = 200L;
            long time2 = 400L;

            // When
            metrics.recordAgentExecution(agentName, time1, true);
            metrics.recordAgentExecution(agentName, time2, true);

            // Then
            assertEquals(2, metrics.getAgentExecutionCount(agentName));
            assertEquals(300.0, metrics.getAgentAverageExecutionTime(agentName), 0.1);
        }
    }

    @Nested
    @DisplayName("Metrics Reset Tests")
    class MetricsResetTests {

        @Test
        @DisplayName("Should reset all metrics")
        void shouldResetAllMetrics() {
            // Given
            String ticker = "BTC";
            metrics.recordAnalysisStart(ticker);
            metrics.recordAnalysisSuccess(ticker, 1000L);

            // When
            metrics.reset();

            // Then
            assertEquals(0, metrics.getTotalAnalysisRequests());
            assertEquals(0, metrics.getSuccessfulAnalysis());
            assertEquals(0, metrics.getFailedAnalysis());
        }

        @Test
        @DisplayName("Should reset performance metrics")
        void shouldResetPerformanceMetrics() {
            // Given
            String ticker = "BTC";
            metrics.recordAnalysisStart(ticker);
            metrics.recordAnalysisSuccess(ticker, 1000L);

            // When
            metrics.reset();

            // Then
            assertEquals(0, metrics.getAverageExecutionTime());
        }
    }

    @Nested
    @DisplayName("Metrics Summary Tests")
    class MetricsSummaryTests {

        @Test
        @DisplayName("Should log metrics summary")
        void shouldLogMetricsSummary() {
            // Given
            String ticker = "BTC";
            metrics.recordAnalysisStart(ticker);
            metrics.recordAnalysisSuccess(ticker, 1000L);

            // When & Then
            assertDoesNotThrow(() -> metrics.logMetricsSummary());
        }

        @Test
        @DisplayName("Should get tracked agents")
        void shouldGetTrackedAgents() {
            // Given
            String agentName = "ANALYST";
            metrics.recordAgentExecution(agentName, 500L, true);

            // When
            var agents = metrics.getTrackedAgents();

            // Then
            assertTrue(agents.contains(agentName));
        }
    }

    @Nested
    @DisplayName("Uptime Tests")
    class UptimeTests {

        @Test
        @DisplayName("Should track uptime")
        void shouldTrackUptime() {
            // When
            long uptime = metrics.getUptimeMs();

            // Then
            assertTrue(uptime > 0);
        }

        @Test
        @DisplayName("Should track last reset time")
        void shouldTrackLastResetTime() {
            // When
            long lastResetTime = metrics.getLastResetTime();

            // Then
            assertTrue(lastResetTime > 0);
        }
    }
}