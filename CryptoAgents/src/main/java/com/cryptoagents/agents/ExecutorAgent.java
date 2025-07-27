package com.cryptoagents.agents;

import com.cryptoagents.models.AgentResponse;
import com.cryptoagents.models.TradingContext;
import com.cryptoagents.tools.TradingExecutionTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ExecutorAgent {
    
    private final ChatClient chatClient;
    private final TradingExecutionTools executionTools;
    
    @Value("${trading.agents.executor.system-prompt}")
    private String systemPrompt;
    
    public ExecutorAgent(ChatClient chatClient, TradingExecutionTools executionTools) {
        this.chatClient = chatClient;
        this.executionTools = executionTools;
    }
    
    public AgentResponse executeTrade(AgentResponse analystRecommendation,
                                     AgentResponse riskApproval,
                                     TradingContext context) {
        log.info("Executor agent processing trade execution");
        
        if (!"APPROVE".equals(riskApproval.getDecision())) {
            return AgentResponse.builder()
                    .agentType("EXECUTOR")
                    .message("Trade execution cancelled - Risk Manager did not approve")
                    .decision("CANCELLED")
                    .timestamp(LocalDateTime.now())
                    .build();
        }
        
        String prompt = String.format("""
            Execute the following approved trade:
            
            Analyst Recommendation:
            %s
            
            Risk Manager Approval:
            %s
            
            Account Balance: $%s
            
            Please:
            1. Place the appropriate order(s)
            2. Set stop loss and take profit levels as recommended
            3. Confirm execution details
            4. Report any issues encountered
            
            Use trading execution tools to place orders.
            """, analystRecommendation.getMessage(),
            riskApproval.getMessage(),
            context.getAccountBalance());
        
        String fullPrompt = systemPrompt + "\n\n" + prompt;
        String execution = chatClient.prompt()
                .user(fullPrompt)
                .call()
                .content();
        
        return AgentResponse.builder()
                .agentType("EXECUTOR")
                .message(execution)
                .timestamp(LocalDateTime.now())
                .decision("EXECUTED")
                .metadata(extractExecutionDetails(execution))
                .build();
    }
    
    public AgentResponse managePositions(TradingContext context) {
        log.info("Executor agent managing existing positions");
        
        String prompt = String.format("""
            Review and manage existing positions:
            Current Positions: %s
            
            Please:
            1. Check current position status
            2. Update stop loss/take profit if needed
            3. Identify positions that need attention
            4. Report on overall portfolio status
            """, context.getCurrentPositions());
        
        // Get current positions first
        Map<String, Object> positions = executionTools.getOpenPositions();
        
        String fullPrompt = systemPrompt + "\n\nCurrent Positions:\n" +
                positions.toString() + "\n\n" + prompt;
        
        String management = chatClient.prompt()
                .user(fullPrompt)
                .call()
                .content();
        
        return AgentResponse.builder()
                .agentType("EXECUTOR")
                .message(management)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public AgentResponse getExecutionStatus(String query, TradingContext context) {
        log.info("Executor agent checking execution status");
        
        String fullPrompt = systemPrompt + "\n\n" + query;
        String status = chatClient.prompt()
                .user(fullPrompt)
                .call()
                .content();
        
        return AgentResponse.builder()
                .agentType("EXECUTOR")
                .message(status)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    private Map<String, Object> extractExecutionDetails(String execution) {
        Map<String, Object> details = new HashMap<>();
        details.put("raw_execution", execution);
        
        // In a real implementation, you might parse order IDs and execution prices
        if (execution.toLowerCase().contains("order")) {
            details.put("has_orders", true);
        }
        
        return details;
    }
}