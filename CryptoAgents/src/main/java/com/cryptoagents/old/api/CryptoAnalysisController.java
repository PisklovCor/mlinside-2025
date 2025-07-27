package com.cryptoagents.old.api;

import com.cryptoagents.old.api.exception.AnalysisException;
import com.cryptoagents.old.api.exception.InvalidTickerException;
import com.cryptoagents.old.model.AnalysisReport;
import com.cryptoagents.old.model.dto.AnalysisRequest;
import com.cryptoagents.old.util.InputValidator;
import com.cryptoagents.old.service.AgentOrchestrator;
import com.cryptoagents.old.service.OrchestrationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

/**
 * REST API Controller for cryptocurrency analysis operations
 */
@Slf4j
@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoAnalysisController {

    private final AgentOrchestrator orchestrator;

    /**
     * Analyze a single cryptocurrency token
     */
    @PostMapping("/analyze")
    public ResponseEntity<AnalysisReport> analyzeToken(@Valid @RequestBody AnalysisRequest request) {
        log.info("Received analysis request for ticker: {}", request.getTicker());
        
        try {
            validateTicker(request.getTicker());
            AnalysisReport report = orchestrator.analyze(request.getTicker());
            log.info("Analysis completed successfully for ticker: {}", request.getTicker());
            return ResponseEntity.ok(report);
        } catch (OrchestrationException e) {
            log.error("Orchestration error analyzing ticker {}: {}", request.getTicker(), e.getMessage(), e);
            throw new AnalysisException("Analysis failed: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error analyzing ticker {}: {}", request.getTicker(), e.getMessage(), e);
            throw new AnalysisException("Unexpected error during analysis", e);
        }
    }

    /**
     * Validate ticker format
     */
    private void validateTicker(String ticker) {
        try {
            InputValidator.isValidTicker(ticker);
        } catch (IllegalArgumentException e) {
            throw new InvalidTickerException(e.getMessage());
        }
    }
} 