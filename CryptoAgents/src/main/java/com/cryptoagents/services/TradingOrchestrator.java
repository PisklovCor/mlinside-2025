package com.cryptoagents.services;

import com.cryptoagents.agents.AnalystAgent;
import com.cryptoagents.agents.RiskManagerAgent;
import com.cryptoagents.agents.ExecutorAgent;
import com.cryptoagents.models.AgentResponse;
import com.cryptoagents.models.TradingContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradingOrchestrator {
    
    private final AnalystAgent analystAgent;
    private final RiskManagerAgent riskManagerAgent;
    private final ExecutorAgent executorAgent;
    private final SimpMessagingTemplate messagingTemplate;
    
    public List<AgentResponse> processTradingDecision(String symbol, TradingContext context) {
        log.info("Orchestrating trading decision for symbol: {}", symbol);
        
        List<AgentResponse> responses = new ArrayList<>();
        
        try {
            // Step 1: Analyst analyzes the market
            sendAgentUpdate("ANALYST", "Starting market analysis for " + symbol, "WORKING");
            AgentResponse analystResponse = analystAgent.analyzeMarket(symbol, context);
            responses.add(analystResponse);
            sendAgentUpdate("ANALYST", analystResponse.getMessage(), "COMPLETED");
            log.info("Analyst completed analysis: {}", analystResponse.getRecommendations());
            
            // Step 2: Risk Manager evaluates the recommendation
            sendAgentUpdate("RISK_MANAGER", "Evaluating trade risks...", "WORKING");
            AgentResponse riskResponse = riskManagerAgent.evaluateTradeRisk(analystResponse, context);
            responses.add(riskResponse);
            sendAgentUpdate("RISK_MANAGER", riskResponse.getMessage(), 
                "APPROVE".equals(riskResponse.getDecision()) ? "APPROVED" : "REJECTED");
            log.info("Risk Manager decision: {}", riskResponse.getDecision());
            
            // Step 3: Executor implements the approved trade
            if ("APPROVE".equals(riskResponse.getDecision())) {
                sendAgentUpdate("EXECUTOR", "Executing trade...", "WORKING");
                AgentResponse executionResponse = executorAgent.executeTrade(
                    analystResponse, riskResponse, context);
                responses.add(executionResponse);
                sendAgentUpdate("EXECUTOR", executionResponse.getMessage(), "EXECUTED");
                log.info("Executor status: {}", executionResponse.getDecision());
            } else {
                sendAgentUpdate("EXECUTOR", "Trade rejected by Risk Manager", "BLOCKED");
            }
            
        } catch (Exception e) {
            log.error("Error in trading orchestration", e);
            responses.add(AgentResponse.builder()
                    .agentType("ORCHESTRATOR")
                    .message("Error occurred: " + e.getMessage())
                    .decision("ERROR")
                    .build());
        }
        
        return responses;
    }
    
    public AgentResponse processQuery(String query, String agentType, TradingContext context) {
        log.info("Processing query for agent: {}", agentType);
        
        switch (agentType.toUpperCase()) {
            case "ANALYST":
                return analystAgent.evaluateOpportunity(query, context);
            case "RISK_MANAGER":
                return riskManagerAgent.assessPortfolioRisk(context);
            case "EXECUTOR":
                return executorAgent.getExecutionStatus(query, context);
            default:
                // Route to all agents and aggregate responses
                return processGeneralQuery(query, context);
        }
    }
    
    private AgentResponse processGeneralQuery(String query, TradingContext context) {
        // For general queries, we can route to the most appropriate agent
        // or create a coordinator response
        
        if (query.toLowerCase().contains("risk") || 
            query.toLowerCase().contains("position size")) {
            return riskManagerAgent.assessPortfolioRisk(context);
        } else if (query.toLowerCase().contains("execute") || 
                   query.toLowerCase().contains("order")) {
            return executorAgent.getExecutionStatus(query, context);
        } else {
            return analystAgent.evaluateOpportunity(query, context);
        }
    }
    
    public List<AgentResponse> performPortfolioReview(TradingContext context) {
        log.info("Performing comprehensive portfolio review");
        
        List<AgentResponse> responses = new ArrayList<>();
        
        // Parallel execution of portfolio review tasks
        CompletableFuture<AgentResponse> riskAssessment = 
            CompletableFuture.supplyAsync(() -> 
                riskManagerAgent.assessPortfolioRisk(context));
        
        CompletableFuture<AgentResponse> positionManagement = 
            CompletableFuture.supplyAsync(() -> 
                executorAgent.managePositions(context));
        
        // Wait for all assessments to complete
        CompletableFuture.allOf(riskAssessment, positionManagement).join();
        
        try {
            responses.add(riskAssessment.get());
            responses.add(positionManagement.get());
        } catch (Exception e) {
            log.error("Error in portfolio review", e);
        }
        
        return responses;
    }
    
    private void sendAgentUpdate(String agentType, String message, String status) {
        Map<String, Object> update = new HashMap<>();
        update.put("agentType", agentType);
        update.put("message", message);
        update.put("status", status);
        update.put("timestamp", System.currentTimeMillis());
        
        messagingTemplate.convertAndSend("/topic/agent-updates", update);
    }
}