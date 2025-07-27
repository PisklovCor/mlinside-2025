//package com.cryptoagents.api;
//
//import com.cryptoagents.api.exception.InvalidTickerException;
//import com.cryptoagents.model.AnalysisReport;
//import com.cryptoagents.model.dto.AnalysisRequest;
//import com.cryptoagents.model.dto.MetricsResponse;
//import com.cryptoagents.service.AgentOrchestrator;
//import com.cryptoagents.service.OrchestratorMetrics;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("CryptoAnalysisController Tests")
//class CryptoAnalysisControllerTest {
//
//    @Mock
//    private AgentOrchestrator orchestrator;
//
//    @Mock
//    private OrchestratorMetrics metrics;
//
//    @InjectMocks
//    private CryptoAnalysisController controller;
//
//    private MockMvc mockMvc;
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
//        objectMapper = new ObjectMapper();
//    }
//
//    @Nested
//    @DisplayName("Single Token Analysis Tests")
//    class SingleTokenAnalysisTests {
//
//        @Test
//        @DisplayName("Should analyze valid token successfully")
//        void analyzeToken_ValidRequest_ReturnsAnalysisReport() throws Exception {
//            // Given
//            AnalysisRequest request = new AnalysisRequest("BTC");
//            AnalysisReport report = createMockAnalysisReport("BTC");
//
//            when(orchestrator.analyze("BTC")).thenReturn(report);
//
//            // When & Then
//            String response = mockMvc.perform(post("/api/crypto/analyze")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(request)))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.ticker").value("BTC"))
//                    .andExpect(jsonPath("$.successful").value(true))
//                    .andExpect(jsonPath("$.analysisStartTime").exists())
//                    .andExpect(jsonPath("$.analysisEndTime").exists())
//                    .andReturn()
//                    .getResponse()
//                    .getContentAsString();
//
//            verify(orchestrator).analyze("BTC");
//            assertThat(response).contains("BTC");
//        }
//
//        @ParameterizedTest
//        @ValueSource(strings = {"", " ", "INVALID@TICKER", "VERYLONGTICKERNAME", "123", "BTC/USD"})
//        @DisplayName("Should reject invalid tickers")
//        void analyzeToken_InvalidTicker_ReturnsBadRequest(String invalidTicker) throws Exception {
//            // Given
//            AnalysisRequest request = new AnalysisRequest(invalidTicker);
//
//            // When & Then
//            mockMvc.perform(post("/api/crypto/analyze")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(request)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.error").exists());
//
//            verify(orchestrator, never()).analyze(anyString());
//        }
//
//        @Test
//        @DisplayName("Should handle orchestrator exception gracefully")
//        void analyzeToken_OrchestratorException_ReturnsInternalServerError() throws Exception {
//            // Given
//            AnalysisRequest request = new AnalysisRequest("BTC");
//
//            when(orchestrator.analyze("BTC")).thenThrow(new RuntimeException("Orchestrator error"));
//
//            // When & Then
//            mockMvc.perform(post("/api/crypto/analyze")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(request)))
//                    .andExpect(status().isInternalServerError())
//                    .andExpect(jsonPath("$.error").value("Internal server error"))
//                    .andExpect(jsonPath("$.timestamp").exists());
//        }
//
//        @Test
//        @DisplayName("Should handle missing ticker in request")
//        void analyzeToken_MissingTicker_ReturnsBadRequest() throws Exception {
//            // Given
//            AnalysisRequest request = new AnalysisRequest();
//            request.setTimeframe("24h");
//            request.setIncludeMetrics(true);
//
//            // When & Then
//            mockMvc.perform(post("/api/crypto/analyze")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(request)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.error").exists());
//        }
//    }
//
//    @Nested
//    @DisplayName("GET Analysis Tests")
//    class GetAnalysisTests {
//
//        @Test
//        @DisplayName("Should analyze token via GET request")
//        void analyzeTokenGet_ValidTicker_ReturnsAnalysisReport() throws Exception {
//            // Given
//            AnalysisReport report = createMockAnalysisReport("ETH");
//
//            when(orchestrator.analyze("ETH")).thenReturn(report);
//
//            // When & Then
//            mockMvc.perform(get("/api/crypto/analyze/ETH"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.ticker").value("ETH"))
//                    .andExpect(jsonPath("$.successful").value(true))
//                    .andExpect(jsonPath("$.confidenceScore").value(0.85));
//
//            verify(orchestrator).analyze("ETH");
//        }
//
//        @ParameterizedTest
//        @ValueSource(strings = {"INVALID@TICKER", "VERYLONGTICKERNAME", "123"})
//        @DisplayName("Should reject invalid tickers in GET request")
//        void analyzeTokenGet_InvalidTicker_ReturnsBadRequest(String invalidTicker) throws Exception {
//            // When & Then
//            mockMvc.perform(get("/api/crypto/analyze/" + invalidTicker))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.error").value("Invalid ticker"));
//
//            verify(orchestrator, never()).analyze(anyString());
//        }
//    }
//
//    @Nested
//    @DisplayName("Batch Analysis Tests")
//    class BatchAnalysisTests {
//
//        @Test
//        @DisplayName("Should analyze multiple tokens successfully")
//        void analyzeMultipleTokens_ValidTickers_ReturnsAnalysisReports() throws Exception {
//            // Given
//            List<String> tickers = Arrays.asList("BTC", "ETH", "ADA");
//            Map<String, AnalysisReport> reports = new HashMap<>();
//            reports.put("BTC", createMockAnalysisReport("BTC"));
//            reports.put("ETH", createMockAnalysisReport("ETH"));
//            reports.put("ADA", createMockAnalysisReport("ADA"));
//
//            when(orchestrator.analyzeMultiple(tickers)).thenReturn(reports);
//
//            // When & Then
//            String response = mockMvc.perform(post("/api/crypto/analyze/batch")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(tickers)))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.BTC.ticker").value("BTC"))
//                    .andExpect(jsonPath("$.ETH.ticker").value("ETH"))
//                    .andExpect(jsonPath("$.ADA.ticker").value("ADA"))
//                    .andExpect(jsonPath("$.BTC.successful").value(true))
//                    .andExpect(jsonPath("$.ETH.successful").value(true))
//                    .andExpect(jsonPath("$.ADA.successful").value(true))
//                    .andReturn()
//                    .getResponse()
//                    .getContentAsString();
//
//            verify(orchestrator).analyzeMultiple(tickers);
//            assertThat(response).contains("BTC", "ETH", "ADA");
//        }
//
//        @Test
//        @DisplayName("Should reject batch with invalid ticker")
//        void analyzeMultipleTokens_InvalidTicker_ReturnsBadRequest() throws Exception {
//            // Given
//            List<String> tickers = Arrays.asList("BTC", "INVALID@TICKER", "ETH");
//
//            // When & Then
//            mockMvc.perform(post("/api/crypto/analyze/batch")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(tickers)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.error").value("Invalid ticker"));
//
//            verify(orchestrator, never()).analyzeMultiple(any());
//        }
//
//        @Test
//        @DisplayName("Should handle empty batch request")
//        void analyzeMultipleTokens_EmptyList_ReturnsBadRequest() throws Exception {
//            // Given
//            List<String> tickers = Arrays.asList();
//
//            // When & Then
//            mockMvc.perform(post("/api/crypto/analyze/batch")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(tickers)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.error").exists());
//        }
//    }
//
//    @Nested
//    @DisplayName("Metrics Tests")
//    class MetricsTests {
//
//        @Test
//        @DisplayName("Should return metrics successfully")
//        void getMetrics_ReturnsMetricsResponse() throws Exception {
//            // Given
//            LocalDateTime lastResetTime = LocalDateTime.now();
//            when(metrics.getTotalAnalysisRequests()).thenReturn(100L);
//            when(metrics.getSuccessfulAnalysis()).thenReturn(90L);
//            when(metrics.getFailedAnalysis()).thenReturn(10L);
//            when(metrics.getSuccessRate()).thenReturn(0.9);
//            when(metrics.getAgentExecutionCount("ANALYST")).thenReturn(100L);
//            when(metrics.getAgentExecutionCount("RISK_MANAGER")).thenReturn(100L);
//            when(metrics.getAgentExecutionCount("TRADER")).thenReturn(100L);
//            when(metrics.getAgentFailureRate("ANALYST")).thenReturn(0.05);
//            when(metrics.getAgentFailureRate("RISK_MANAGER")).thenReturn(0.03);
//            when(metrics.getAgentFailureRate("TRADER")).thenReturn(0.02);
//            when(metrics.getAverageExecutionTime()).thenReturn(1500.0);
//            when(metrics.getUptimeMs()).thenReturn(3600000L);
//            when(metrics.getLastResetTime()).thenReturn(System.currentTimeMillis());
//
//            // When & Then
//            mockMvc.perform(get("/api/crypto/metrics"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.totalRequests").value(100))
//                    .andExpect(jsonPath("$.successfulAnalyses").value(90))
//                    .andExpect(jsonPath("$.failedAnalyses").value(10))
//                    .andExpect(jsonPath("$.successRate").value(0.9))
//                    .andExpect(jsonPath("$.analystExecutions").value(100))
//                    .andExpect(jsonPath("$.riskManagerExecutions").value(100))
//                    .andExpect(jsonPath("$.traderExecutions").value(100))
//                    .andExpect(jsonPath("$.analystErrors").exists())
//                    .andExpect(jsonPath("$.riskManagerErrors").exists())
//                    .andExpect(jsonPath("$.traderErrors").exists())
//                    .andExpect(jsonPath("$.averageExecutionTime").value(1500.0))
//                    .andExpect(jsonPath("$.uptime").value(3600000))
//                    .andExpect(jsonPath("$.lastResetTime").exists());
//        }
//
//        @Test
//        @DisplayName("Should reset metrics successfully")
//        void resetMetrics_ReturnsSuccessMessage() throws Exception {
//            // Given
//            doNothing().when(metrics).reset();
//
//            // When & Then
//            mockMvc.perform(post("/api/crypto/metrics/reset"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.message").value("Metrics reset successfully"))
//                    .andExpect(jsonPath("$.timestamp").exists());
//
//            verify(metrics).reset();
//        }
//    }
//
//    @Nested
//    @DisplayName("Health Check Tests")
//    class HealthCheckTests {
//
//        @Test
//        @DisplayName("Should return health status when service is ready")
//        void healthCheck_ServiceReady_ReturnsHealthStatus() throws Exception {
//            // Given
//            when(orchestrator.isReady()).thenReturn(true);
//
//            // When & Then
//            mockMvc.perform(get("/api/crypto/health"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.status").value("UP"))
//                    .andExpect(jsonPath("$.service").value("Crypto Analysis API"))
//                    .andExpect(jsonPath("$.version").value("1.0.0"))
//                    .andExpect(jsonPath("$.ready").value(true))
//                    .andExpect(jsonPath("$.timestamp").exists());
//        }
//
//        @Test
//        @DisplayName("Should return health status when service is not ready")
//        void healthCheck_ServiceNotReady_ReturnsHealthStatus() throws Exception {
//            // Given
//            when(orchestrator.isReady()).thenReturn(false);
//
//            // When & Then
//            mockMvc.perform(get("/api/crypto/health"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.status").value("UP"))
//                    .andExpect(jsonPath("$.service").value("Crypto Analysis API"))
//                    .andExpect(jsonPath("$.version").value("1.0.0"))
//                    .andExpect(jsonPath("$.ready").value(false))
//                    .andExpect(jsonPath("$.timestamp").exists());
//        }
//    }
//
//    private AnalysisReport createMockAnalysisReport(String ticker) {
//        AnalysisReport report = new AnalysisReport();
//        report.setTicker(ticker);
//        report.setAnalysisStartTime(LocalDateTime.now());
//        report.setAnalysisEndTime(LocalDateTime.now());
//        report.setSuccessful(true);
//        return report;
//    }
//}