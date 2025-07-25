package com.cryptoagents.api;

import com.cryptoagents.BaseSpringBootTest;
import com.cryptoagents.model.AnalysisReport;
import com.cryptoagents.model.dto.MetricsResponse;
import com.cryptoagents.service.AgentOrchestrator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for CryptoAnalysisController focusing on REST API and metrics endpoints.
 */
@WebMvcTest(CryptoAnalysisController.class)
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
        // When & Then
        mockMvc.perform(post("/api/crypto/metrics/reset"))
                .andExpect(status().isOk())
                .andExpect(content().string("Metrics reset successfully"));
    }
} 