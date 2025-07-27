package com.cryptoagents.agents;

import com.cryptoagents.models.AgentResponse;
import com.cryptoagents.models.TradingContext;
import com.cryptoagents.tools.RiskManagementTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RiskManagerAgent {
    
    private final ChatClient chatClient;
    private final RiskManagementTools riskTools;
    
    @Value("${trading.agents.risk-manager.system-prompt}")
    private String systemPrompt;
    
    public RiskManagerAgent(ChatClient chatClient, RiskManagementTools riskTools) {
        this.chatClient = chatClient;
        this.riskTools = riskTools;
    }
    
    public AgentResponse evaluateTradeRisk(AgentResponse analystRecommendation, 
                                          TradingContext context) {
        log.info("Risk Manager evaluating trade recommendation");
        
        String prompt = String.format("""
            Evaluate the following trading recommendation from the analyst:
            %s
            
            Account Balance: $%s
            Current Positions: %s
            Risk Tolerance: %s%%
            
            Please assess:
            1. Position sizing recommendations
            2. Risk/reward ratio
            3. Portfolio impact
            4. Stop loss and take profit levels
            5. Overall risk assessment (APPROVE/REJECT with reasoning)
            
            Use risk management tools to calculate appropriate metrics.
            """, analystRecommendation.getMessage(), 
            context.getAccountBalance(),
            context.getCurrentPositions(),
            context.getRiskTolerance());
        
        // Calculate some risk metrics
        Map<String, Object> positionSize = riskTools.calculatePositionSize(
                context.getAccountBalance(),
                context.getRiskTolerance(),
                new BigDecimal("100"), // Example entry price
                new BigDecimal("95")   // Example stop loss
        );
        
        String fullPrompt = systemPrompt + "\n\nCalculated Risk Metrics:\n" +
                "Recommended Position Size: " + positionSize.get("positionSize") + "\n" +
                "Risk Amount: $" + positionSize.get("riskAmount") + "\n\n" + prompt;
        
        String evaluation = chatClient.prompt()
                .user(fullPrompt)
                .call()
                .content();
        
        return AgentResponse.builder()
                .agentType("RISK_MANAGER")
                .message(evaluation)
                .timestamp(LocalDateTime.now())
                .decision(extractDecision(evaluation))
                .analysis(extractRiskMetrics(evaluation))
                .build();
    }
    
    public AgentResponse assessPortfolioRisk(TradingContext context) {
        log.info("Risk Manager assessing overall portfolio risk");
        
        String prompt = String.format("""
            Assess the current portfolio risk profile:
            Account Balance: $%s
            Current Positions: %s
            Risk Tolerance: %s%%
            
            Provide comprehensive risk analysis including:
            1. Current exposure levels
            2. Diversification assessment
            3. Value at Risk (VaR)
            4. Recommendations for risk reduction if needed
            """, context.getAccountBalance(),
            context.getCurrentPositions(),
            context.getRiskTolerance());
        
        // Evaluate portfolio risk
        Map<String, Object> portfolioRisk = riskTools.evaluatePortfolioRisk(
                context.getCurrentPositions(), context.getAccountBalance());
        
        String fullPrompt = systemPrompt + "\n\nPortfolio Risk Metrics:\n" +
                "Total Exposure: $" + portfolioRisk.get("totalExposure") + "\n" +
                "Risk Level: " + portfolioRisk.get("riskLevel") + "\n\n" + prompt;
        
        String assessment = chatClient.prompt()
                .user(fullPrompt)
                .call()
                .content();
        
        return AgentResponse.builder()
                .agentType("RISK_MANAGER")
                .message(assessment)
                .timestamp(LocalDateTime.now())
                .analysis(extractRiskMetrics(assessment))
                .build();
    }
    
    private String extractDecision(String evaluation) {
        // Simple pattern matching for demonstration
        if (evaluation.toLowerCase().contains("approve")) {
            return "APPROVE";
        } else if (evaluation.toLowerCase().contains("reject")) {
            return "REJECT";
        }
        return "REVIEW_REQUIRED";
    }
    
    private Map<String, Object> extractRiskMetrics(String evaluation) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("raw_evaluation", evaluation);
        
        // In a real implementation, you might parse specific metrics
        // For now, we'll add some placeholder data
        if (evaluation.toLowerCase().contains("position size")) {
            metrics.put("has_position_sizing", true);
        }
        if (evaluation.toLowerCase().contains("stop loss")) {
            metrics.put("has_stop_loss", true);
        }
        
        return metrics;
    }
}