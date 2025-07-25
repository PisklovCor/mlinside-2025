package com.cryptoagents.api;

import com.cryptoagents.api.exception.InvalidTickerException;
import com.cryptoagents.model.AnalysisReport;
import com.cryptoagents.model.dto.AnalysisRequest;
import com.cryptoagents.model.dto.MetricsResponse;
import com.cryptoagents.service.AgentOrchestrator;
import com.cryptoagents.service.OrchestratorMetrics;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CryptoAnalysisControllerTest {
    
    @Mock
    private AgentOrchestrator orchestrator;
    
    @Mock
    private OrchestratorMetrics metrics;
    
    @InjectMocks
    private CryptoAnalysisController controller;
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void analyzeToken_ValidRequest_ReturnsAnalysisReport() throws Exception {
        // Given
        AnalysisRequest request = new AnalysisRequest("BTC");
        AnalysisReport report = createMockAnalysisReport("BTC");
        
        when(orchestrator.analyze("BTC")).thenReturn(report);
        
        // When & Then
        mockMvc.perform(post("/api/crypto/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("BTC"))
                .andExpect(jsonPath("$.successful").value(true));
        
        verify(orchestrator).analyze("BTC");
    }
    
    @Test
    void analyzeToken_InvalidTicker_ReturnsBadRequest() throws Exception {
        // Given
        AnalysisRequest request = new AnalysisRequest("INVALID@TICKER");
        
        // When & Then
        mockMvc.perform(post("/api/crypto/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid ticker"));
        
        verify(orchestrator, never()).analyze(anyString());
    }
    
    @Test
    void analyzeToken_EmptyTicker_ReturnsBadRequest() throws Exception {
        // Given
        AnalysisRequest request = new AnalysisRequest("");
        
        // When & Then
        mockMvc.perform(post("/api/crypto/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(orchestrator, never()).analyze(anyString());
    }
    
    @Test
    void analyzeTokenGet_ValidTicker_ReturnsAnalysisReport() throws Exception {
        // Given
        AnalysisReport report = createMockAnalysisReport("ETH");
        
        when(orchestrator.analyze("ETH")).thenReturn(report);
        
        // When & Then
        mockMvc.perform(get("/api/crypto/analyze/ETH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("ETH"))
                .andExpect(jsonPath("$.successful").value(true));
        
        verify(orchestrator).analyze("ETH");
    }
    
    @Test
    void analyzeTokenGet_InvalidTicker_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/crypto/analyze/INVALID@TICKER"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid ticker"));
        
        verify(orchestrator, never()).analyze(anyString());
    }
    
    @Test
    void analyzeMultipleTokens_ValidTickers_ReturnsAnalysisReports() throws Exception {
        // Given
        List<String> tickers = Arrays.asList("BTC", "ETH", "ADA");
        Map<String, AnalysisReport> reports = new HashMap<>();
        reports.put("BTC", createMockAnalysisReport("BTC"));
        reports.put("ETH", createMockAnalysisReport("ETH"));
        reports.put("ADA", createMockAnalysisReport("ADA"));
        
        when(orchestrator.analyzeMultiple(tickers)).thenReturn(reports);
        
        // When & Then
        mockMvc.perform(post("/api/crypto/analyze/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tickers)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.BTC.ticker").value("BTC"))
                .andExpect(jsonPath("$.ETH.ticker").value("ETH"))
                .andExpect(jsonPath("$.ADA.ticker").value("ADA"));
        
        verify(orchestrator).analyzeMultiple(tickers);
    }
    
    @Test
    void analyzeMultipleTokens_InvalidTicker_ReturnsBadRequest() throws Exception {
        // Given
        List<String> tickers = Arrays.asList("BTC", "INVALID@TICKER", "ETH");
        
        // When & Then
        mockMvc.perform(post("/api/crypto/analyze/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tickers)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid ticker"));
        
        verify(orchestrator, never()).analyzeMultiple(any());
    }
    
    @Test
    void getMetrics_ReturnsMetricsResponse() throws Exception {
        // Given
        when(metrics.getTotalRequests()).thenReturn(100L);
        when(metrics.getSuccessfulAnalyses()).thenReturn(90L);
        when(metrics.getFailedAnalyses()).thenReturn(10L);
        when(metrics.getSuccessRate()).thenReturn(0.9);
        when(metrics.getAnalystExecutions()).thenReturn(100L);
        when(metrics.getRiskManagerExecutions()).thenReturn(100L);
        when(metrics.getTraderExecutions()).thenReturn(100L);
        when(metrics.getAnalystErrors()).thenReturn(5L);
        when(metrics.getRiskManagerErrors()).thenReturn(3L);
        when(metrics.getTraderErrors()).thenReturn(2L);
        when(metrics.getAverageExecutionTime()).thenReturn(1500.0);
        when(metrics.getUptime()).thenReturn(3600000L);
        when(metrics.getLastResetTime()).thenReturn(LocalDateTime.now());
        
        // When & Then
        mockMvc.perform(get("/api/crypto/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRequests").value(100))
                .andExpect(jsonPath("$.successfulAnalyses").value(90))
                .andExpect(jsonPath("$.failedAnalyses").value(10))
                .andExpect(jsonPath("$.successRate").value(0.9));
    }
    
    @Test
    void resetMetrics_ReturnsSuccessMessage() throws Exception {
        // Given
        doNothing().when(metrics).reset();
        
        // When & Then
        mockMvc.perform(post("/api/crypto/metrics/reset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Metrics reset successfully"))
                .andExpect(jsonPath("$.timestamp").exists());
        
        verify(metrics).reset();
    }
    
    @Test
    void healthCheck_ReturnsHealthStatus() throws Exception {
        // Given
        when(orchestrator.isReady()).thenReturn(true);
        
        // When & Then
        mockMvc.perform(get("/api/crypto/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Crypto Analysis API"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.ready").value(true))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void analyzeToken_OrchestratorException_ReturnsInternalServerError() throws Exception {
        // Given
        AnalysisRequest request = new AnalysisRequest("BTC");
        
        when(orchestrator.analyze("BTC")).thenThrow(new RuntimeException("Orchestrator error"));
        
        // When & Then
        mockMvc.perform(post("/api/crypto/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"));
    }
    
    @Test
    void analyzeToken_MissingTicker_ReturnsBadRequest() throws Exception {
        // Given
        AnalysisRequest request = new AnalysisRequest();
        request.setTimeframe("24h");
        request.setIncludeMetrics(true);
        
        // When & Then
        mockMvc.perform(post("/api/crypto/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void analyzeToken_TickerTooLong_ReturnsBadRequest() throws Exception {
        // Given
        AnalysisRequest request = new AnalysisRequest("VERYLONGTICKERNAME");
        
        // When & Then
        mockMvc.perform(post("/api/crypto/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid ticker"));
    }
    
    private AnalysisReport createMockAnalysisReport(String ticker) {
        AnalysisReport report = new AnalysisReport();
        report.setTicker(ticker);
        report.setAnalysisStartTime(LocalDateTime.now());
        report.setAnalysisEndTime(LocalDateTime.now());
        report.setSuccessful(true);
        report.setConfidenceScore(0.85);
        return report;
    }
} 