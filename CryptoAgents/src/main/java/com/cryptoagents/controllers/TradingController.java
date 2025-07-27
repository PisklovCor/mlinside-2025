package com.cryptoagents.controllers;

import com.cryptoagents.models.AgentResponse;
import com.cryptoagents.models.TradingContext;
import com.cryptoagents.services.TradingOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/trading")
@RequiredArgsConstructor
public class TradingController {
    
    private final TradingOrchestrator tradingOrchestrator;
    
    @PostMapping("/analyze/{symbol}")
    public ResponseEntity<Map<String, Object>> analyzeTrade(
            @PathVariable String symbol,
            @RequestBody(required = false) TradingContext context) {
        
        log.info("Received trade analysis request for symbol: {}", symbol);
        
        // Use default context if not provided
        if (context == null) {
            context = createDefaultContext();
        }
        
        List<AgentResponse> responses = tradingOrchestrator.processTradingDecision(symbol, context);
        
        Map<String, Object> result = new HashMap<>();
        result.put("symbol", symbol);
        result.put("responses", responses);
        result.put("conversationId", context.getConversationId());
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/query")
    public ResponseEntity<AgentResponse> processQuery(
            @RequestParam(required = false) String agentType,
            @RequestBody Map<String, Object> request) {
        
        String query = (String) request.get("query");
        TradingContext context = extractContext(request);
        
        log.info("Processing query: {} for agent: {}", query, agentType);
        
        AgentResponse response = tradingOrchestrator.processQuery(
            query, 
            agentType != null ? agentType : "GENERAL", 
            context
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/portfolio/review")
    public ResponseEntity<Map<String, Object>> reviewPortfolio(
            @RequestBody(required = false) TradingContext context) {
        
        log.info("Performing portfolio review");
        
        if (context == null) {
            context = createDefaultContext();
        }
        
        List<AgentResponse> responses = tradingOrchestrator.performPortfolioReview(context);
        
        Map<String, Object> result = new HashMap<>();
        result.put("responses", responses);
        result.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "Spring AI Trading System");
        return ResponseEntity.ok(status);
    }
    
    private TradingContext createDefaultContext() {
        Map<String, BigDecimal> positions = new HashMap<>();
        positions.put("AAPL", new BigDecimal("10000"));
        positions.put("GOOGL", new BigDecimal("8000"));
        
        return TradingContext.builder()
                .conversationId(UUID.randomUUID().toString())
                .userId("demo-user")
                .accountBalance(new BigDecimal("100000"))
                .riskTolerance(new BigDecimal("2")) // 2% per trade
                .currentPositions(positions)
                .tradingStrategy("MODERATE")
                .build();
    }
    
    private TradingContext extractContext(Map<String, Object> request) {
        // Extract context from request or use defaults
        TradingContext context = createDefaultContext();
        
        if (request.containsKey("context")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> contextMap = (Map<String, Object>) request.get("context");
            
            if (contextMap.containsKey("accountBalance")) {
                context.setAccountBalance(new BigDecimal(contextMap.get("accountBalance").toString()));
            }
            if (contextMap.containsKey("riskTolerance")) {
                context.setRiskTolerance(new BigDecimal(contextMap.get("riskTolerance").toString()));
            }
            if (contextMap.containsKey("tradingStrategy")) {
                context.setTradingStrategy(contextMap.get("tradingStrategy").toString());
            }
        }
        
        return context;
    }
}