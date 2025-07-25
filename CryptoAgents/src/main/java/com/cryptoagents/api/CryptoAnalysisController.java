package com.cryptoagents.api;

import com.cryptoagents.model.AnalysisReport;
import com.cryptoagents.model.dto.MetricsResponse;
import com.cryptoagents.service.AgentOrchestrator;
import com.cryptoagents.service.OrchestratorMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for cryptocurrency analysis endpoints.
 * 
 * This controller handles HTTP requests for crypto analysis operations and performance metrics.
 */
@Slf4j
@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoAnalysisController {
    
    private final AgentOrchestrator orchestrator;
    
    /**
     * Analyze a single cryptocurrency ticker.
     * 
     * @param ticker the cryptocurrency ticker (e.g., "BTC", "ETH")
     * @return analysis report for the ticker
     */
    @GetMapping("/analyze/{ticker}")
    public ResponseEntity<AnalysisReport> analyzeSingle(@PathVariable String ticker) {
        log.info("Received analysis request for ticker: {}", ticker);
        
        try {
            AnalysisReport report = orchestrator.analyze(ticker);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Analysis failed for ticker: {}", ticker, e);
            AnalysisReport errorReport = AnalysisReport.builder()
                    .ticker(ticker)
                    .analysisStartTime(LocalDateTime.now())
                    .analysisEndTime(LocalDateTime.now())
                    .successful(false)
                    .build();
            errorReport.addError("Analysis failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorReport);
        }
    }
    
    /**
     * Analyze multiple cryptocurrency tickers in parallel.
     * 
     * @param tickers list of cryptocurrency tickers
     * @return map of ticker to analysis report
     */
    @PostMapping("/analyze/batch")
    public ResponseEntity<Map<String, AnalysisReport>> analyzeMultiple(@RequestBody List<String> tickers) {
        log.info("Received batch analysis request for {} tickers", tickers != null ? tickers.size() : 0);
        
        try {
            Map<String, AnalysisReport> reports = orchestrator.analyzeMultiple(tickers);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            log.error("Batch analysis failed for tickers: {}", tickers, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get current performance metrics.
     * 
     * @return current orchestrator performance metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<MetricsResponse> getMetrics() {
        log.debug("Metrics requested");
        
        OrchestratorMetrics metrics = orchestrator.getMetrics();
        
        // Build agent metrics map
        Map<String, MetricsResponse.AgentMetrics> agentMetricsMap = new HashMap<>();
        for (String agentName : metrics.getTrackedAgents()) {
            agentMetricsMap.put(agentName, MetricsResponse.AgentMetrics.builder()
                    .executionCount(metrics.getAgentExecutionCount(agentName))
                    .failureRate(metrics.getAgentFailureRate(agentName))
                    .averageExecutionTime(metrics.getAgentAverageExecutionTime(agentName))
                    .build());
        }
        
        // Convert timestamp to readable format
        String lastResetTime = Instant.ofEpochMilli(metrics.getLastResetTime())
                .atZone(ZoneId.systemDefault())
                .toString();
        
        MetricsResponse response = MetricsResponse.builder()
                .totalRequests(metrics.getTotalAnalysisRequests())
                .successfulAnalyses(metrics.getSuccessfulAnalysis())
                .failedAnalyses(metrics.getFailedAnalysis())
                .successRate(metrics.getSuccessRate())
                .failureRate(metrics.getFailureRate())
                .averageExecutionTime(metrics.getAverageExecutionTime())
                .agentMetrics(agentMetricsMap)
                .uptimeMs(metrics.getUptimeMs())
                .lastResetTime(lastResetTime)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Reset performance metrics.
     * 
     * @return confirmation of reset
     */
    @PostMapping("/metrics/reset")
    public ResponseEntity<String> resetMetrics() {
        log.info("Metrics reset requested");
        orchestrator.getMetrics().reset();
        return ResponseEntity.ok("Metrics reset successfully");
    }
    
    /**
     * Get orchestrator health status.
     * 
     * @return health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", orchestrator.isReady() ? "UP" : "DOWN");
        health.put("timestamp", LocalDateTime.now());
        health.put("availableAgents", orchestrator.getAvailableAgents().size());
        
        return ResponseEntity.ok(health);
    }
} 