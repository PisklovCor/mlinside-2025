package com.cryptoagents.api;

import com.cryptoagents.api.exception.AnalysisException;
import com.cryptoagents.api.exception.InvalidTickerException;
import com.cryptoagents.model.AnalysisReport;
import com.cryptoagents.model.dto.AnalysisRequest;
import com.cryptoagents.model.dto.MetricsResponse;
import com.cryptoagents.service.AgentOrchestrator;
import com.cryptoagents.service.OrchestrationException;
import com.cryptoagents.service.OrchestratorMetrics;
import com.cryptoagents.util.InputValidator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REST API Controller for cryptocurrency analysis operations
 */
@RestController
@RequestMapping("/api/crypto")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CryptoAnalysisController {
    
    private static final Logger logger = LoggerFactory.getLogger(CryptoAnalysisController.class);
    
    private final AgentOrchestrator orchestrator;
    private final OrchestratorMetrics metrics;
    private final InputValidator inputValidator;
    
    public CryptoAnalysisController(AgentOrchestrator orchestrator, OrchestratorMetrics metrics, 
                                   InputValidator inputValidator) {
        this.orchestrator = orchestrator;
        this.metrics = metrics;
        this.inputValidator = inputValidator;
    }
    
    /**
     * Analyze a single cryptocurrency token
     */
    @PostMapping("/analyze")
    public ResponseEntity<AnalysisReport> analyzeToken(@Valid @RequestBody AnalysisRequest request) {
        logger.info("Received analysis request for ticker: {}", request.getTicker());
        
        try {
            validateTicker(request.getTicker());
            AnalysisReport report = orchestrator.analyze(request.getTicker());
            logger.info("Analysis completed successfully for ticker: {}", request.getTicker());
            return ResponseEntity.ok(report);
        } catch (OrchestrationException e) {
            logger.error("Orchestration error analyzing ticker {}: {}", request.getTicker(), e.getMessage(), e);
            throw new AnalysisException("Analysis failed: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error analyzing ticker {}: {}", request.getTicker(), e.getMessage(), e);
            throw new AnalysisException("Unexpected error during analysis", e);
        }
    }
    
    /**
     * Analyze a single cryptocurrency token via GET request
     */
    @GetMapping("/analyze/{ticker}")
    public ResponseEntity<AnalysisReport> analyzeTokenGet(@PathVariable String ticker) {
        logger.info("Received GET analysis request for ticker: {}", ticker);
        
        try {
            validateTicker(ticker);
            AnalysisReport report = orchestrator.analyze(ticker);
            logger.info("Analysis completed successfully for ticker: {}", ticker);
            return ResponseEntity.ok(report);
        } catch (OrchestrationException e) {
            logger.error("Orchestration error analyzing ticker {}: {}", ticker, e.getMessage(), e);
            throw new AnalysisException("Analysis failed: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error analyzing ticker {}: {}", ticker, e.getMessage(), e);
            throw new AnalysisException("Unexpected error during analysis", e);
        }
    }
    
    /**
     * Analyze multiple cryptocurrency tokens
     */
    @PostMapping("/analyze/batch")
    public ResponseEntity<Map<String, AnalysisReport>> analyzeMultipleTokens(
            @RequestBody List<String> tickers) {
        logger.info("Received batch analysis request for {} tickers", tickers.size());
        
        try {
            // Validate all tickers
            tickers.forEach(this::validateTicker);
            
            // Perform parallel analysis
            Map<String, AnalysisReport> results = orchestrator.analyzeMultiple(tickers);
            logger.info("Batch analysis completed successfully for {} tickers", tickers.size());
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error in batch analysis: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Get system metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<MetricsResponse> getMetrics() {
        logger.debug("Metrics request received");
        
        MetricsResponse response = metrics.toMetricsResponse();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Reset system metrics
     */
    @PostMapping("/metrics/reset")
    public ResponseEntity<Map<String, String>> resetMetrics() {
        logger.info("Metrics reset request received");
        
        metrics.reset();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Metrics reset successfully");
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "Crypto Analysis API");
        health.put("version", "1.0.0");
        health.put("ready", orchestrator.isReady());
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * Validate ticker format
     */
    private void validateTicker(String ticker) {
        try {
            inputValidator.validateTicker(ticker);
        } catch (IllegalArgumentException e) {
            throw new InvalidTickerException(e.getMessage());
        }
    }
    
    // Exception handling is now centralized in GlobalExceptionHandler
} 