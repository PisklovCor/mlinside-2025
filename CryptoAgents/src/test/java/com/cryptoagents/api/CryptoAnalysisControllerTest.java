package com.cryptoagents.api;

import com.cryptoagents.BaseSpringBootTest;
import com.cryptoagents.model.AnalysisReport;
import com.cryptoagents.model.dto.MetricsResponse;
import com.cryptoagents.service.AgentOrchestrator;
import com.cryptoagents.service.OrchestratorMetrics;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for CryptoAnalysisController focusing on REST API and metrics endpoints.
 */
@ActiveProfiles("test")
class CryptoAnalysisControllerTest extends BaseSpringBootTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AgentOrchestrator orchestrator;

    @Test
    void testAnalyzeSingle_Success() throws Exception {
        // Given
        String ticker = "BTC";
        AnalysisReport mockReport = AnalysisReport.builder()
                .ticker(ticker)
                .analysisStartTime(LocalDateTime.now())
                .analysisEndTime(LocalDateTime.now())
                .successful(true)
                .build();

        when(orchestrator.analyze(ticker)).thenReturn(mockReport);

        // When & Then
        mockMvc.perform(get("/api/crypto/analyze/{ticker}", ticker))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticker").value(ticker))
                .andExpect(jsonPath("$.successful").value(true));
    }

    @Test
    void testAnalyzeMultiple_Success() throws Exception {
        // Given
        String[] tickers = {"BTC", "ETH"};
        Map<String, AnalysisReport> mockReports = new HashMap<>();
        
        for (String ticker : tickers) {
            mockReports.put(ticker, AnalysisReport.builder()
                    .ticker(ticker)
                    .analysisStartTime(LocalDateTime.now())
                    .analysisEndTime(LocalDateTime.now())
                    .successful(true)
                    .build());
        }

        when(orchestrator.analyzeMultiple(Arrays.asList(tickers))).thenReturn(mockReports);

        // When & Then
        mockMvc.perform(post("/api/crypto/analyze/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(tickers))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.BTC.ticker").value("BTC"))
                .andExpect(jsonPath("$.ETH.ticker").value("ETH"))
                .andExpect(jsonPath("$.BTC.successful").value(true))
                .andExpect(jsonPath("$.ETH.successful").value(true));
    }

    @Test
    void testGetMetrics() throws Exception {
        // Given
        OrchestratorMetrics mockMetrics = mock(OrchestratorMetrics.class);
        when(orchestrator.getMetrics()).thenReturn(mockMetrics);
        when(mockMetrics.getTrackedAgents()).thenReturn(Set.of("Analyst", "RiskManager"));
        when(mockMetrics.getAgentExecutionCount("Analyst")).thenReturn(10L);
        when(mockMetrics.getAgentExecutionCount("RiskManager")).thenReturn(8L);
        when(mockMetrics.getAgentFailureRate("Analyst")).thenReturn(5.0);
        when(mockMetrics.getAgentFailureRate("RiskManager")).thenReturn(2.5);
        when(mockMetrics.getAgentAverageExecutionTime("Analyst")).thenReturn(150.0);
        when(mockMetrics.getAgentAverageExecutionTime("RiskManager")).thenReturn(200.0);
        when(mockMetrics.getTotalAnalysisRequests()).thenReturn(20L);
        when(mockMetrics.getSuccessfulAnalysis()).thenReturn(18L);
        when(mockMetrics.getFailedAnalysis()).thenReturn(2L);
        when(mockMetrics.getSuccessRate()).thenReturn(90.0);
        when(mockMetrics.getFailureRate()).thenReturn(10.0);
        when(mockMetrics.getAverageExecutionTime()).thenReturn(175.0);
        when(mockMetrics.getUptimeMs()).thenReturn(3600000L);
        when(mockMetrics.getLastResetTime()).thenReturn(System.currentTimeMillis());

        // When & Then
        mockMvc.perform(get("/api/crypto/metrics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalRequests").value(20))
                .andExpect(jsonPath("$.successfulAnalyses").value(18))
                .andExpect(jsonPath("$.failedAnalyses").value(2))
                .andExpect(jsonPath("$.successRate").value(90.0))
                .andExpect(jsonPath("$.failureRate").value(10.0))
                .andExpect(jsonPath("$.averageExecutionTime").value(175.0))
                .andExpect(jsonPath("$.uptimeMs").value(3600000))
                .andExpect(jsonPath("$.lastResetTime").exists())
                .andExpect(jsonPath("$.agentMetrics.Analyst.executionCount").value(10))
                .andExpect(jsonPath("$.agentMetrics.Analyst.failureRate").value(5.0))
                .andExpect(jsonPath("$.agentMetrics.Analyst.averageExecutionTime").value(150.0))
                .andExpect(jsonPath("$.agentMetrics.RiskManager.executionCount").value(8))
                .andExpect(jsonPath("$.agentMetrics.RiskManager.failureRate").value(2.5))
                .andExpect(jsonPath("$.agentMetrics.RiskManager.averageExecutionTime").value(200.0));
    }

    @Test
    void testGetHealth() throws Exception {
        // Given
        when(orchestrator.isReady()).thenReturn(true);
        when(orchestrator.getAvailableAgents()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/crypto/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.availableAgents").value(0))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testResetMetrics() throws Exception {
        // Given
        OrchestratorMetrics mockMetrics = mock(OrchestratorMetrics.class);
        when(orchestrator.getMetrics()).thenReturn(mockMetrics);
        
        // When & Then
        mockMvc.perform(post("/api/crypto/metrics/reset"))
                .andExpect(status().isOk())
                .andExpect(content().string("Metrics reset successfully"));
        
        verify(mockMetrics).reset();
    }
} 