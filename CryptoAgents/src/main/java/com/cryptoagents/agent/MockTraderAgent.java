package com.cryptoagents.agent;

import com.cryptoagents.model.AnalysisResult;
import com.cryptoagents.model.AnalystReport;
import com.cryptoagents.model.enums.MarketTrend;
import com.cryptoagents.model.enums.SignalStrength;
import com.cryptoagents.model.RiskManagerReport;
import com.cryptoagents.model.TraderReport;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of Trader agent for testing purposes.
 * 
 * This agent makes final trading decisions based on analyst recommendations
 * and risk manager assessments to provide actionable trading signals.
 */
@Component
public class MockTraderAgent extends AbstractAgent {
    
    @Override
    public String getName() {
        return "TRADER";
    }
    
    @Override
    public AgentType getType() {
        return AgentType.TRADER;
    }
    
    @Override
    public boolean canAnalyze(AnalysisContext context) {
        return context != null 
            && context.getTicker() != null 
            && !context.getTicker().trim().isEmpty()
            && context.getMarketData() != null
            && hasAnalystRecommendation(context)
            && hasRiskManagerAssessment(context);
    }
    
    @Override
    public int getPriority() {
        return 3; // Lowest priority - executes last
    }
    
    @Override
    protected AnalysisResult performAnalysis(AnalysisContext context) throws AgentAnalysisException {
        logger.info("Mock Trader making trading decision for {}", context.getTicker());
        
        // Simulate analysis processing time
        simulateProcessingTime(200, 600);
        
        // Get previous agent reports
        AnalystReport analystReport = getAnalystReport(context);
        RiskManagerReport riskReport = getRiskManagerReport(context);
        
        if (analystReport == null || riskReport == null) {
            throw new AgentAnalysisException(getName(), context.getTicker(), "Missing required reports for trading decision");
        }
        
        // Create trading decision
        TraderReport report = new TraderReport();
        report.setAgentName(getName());
        report.setTicker(context.getTicker());
        report.setAnalysisTime(java.time.LocalDateTime.now());
        
        // Make trading decision based on analyst and risk manager inputs
        makeTradingDecision(analystReport, riskReport, context, report);
        
        logger.info("Mock Trader decision completed for {} with action: {}", 
                context.getTicker(), report.getActionRecommendation());
        
        return report;
    }
    
    /**
     * Check if analyst recommendation is available
     */
    private boolean hasAnalystRecommendation(AnalysisContext context) {
        return context.getAgentResults().containsKey("ANALYST");
    }
    
    /**
     * Check if risk manager assessment is available
     */
    private boolean hasRiskManagerAssessment(AnalysisContext context) {
        return context.getAgentResults().containsKey("RISK_MANAGER");
    }
    
    /**
     * Get analyst report from context
     */
    private AnalystReport getAnalystReport(AnalysisContext context) {
        AnalysisResult result = context.getAgentResults().get("ANALYST");
        return (result instanceof AnalystReport) ? (AnalystReport) result : null;
    }
    
    /**
     * Get risk manager report from context
     */
    private RiskManagerReport getRiskManagerReport(AnalysisContext context) {
        AnalysisResult result = context.getAgentResults().get("RISK_MANAGER");
        return (result instanceof RiskManagerReport) ? (RiskManagerReport) result : null;
    }
    
    /**
     * Make trading decision based on analyst and risk manager inputs
     */
    private void makeTradingDecision(AnalystReport analystReport, RiskManagerReport riskReport, 
                                   AnalysisContext context, TraderReport report) {
        
        MarketTrend trend = analystReport.getMarketTrend();
        RiskManagerReport.RiskLevel riskLevel = riskReport.getRiskLevel();
        double maxPosition = riskReport.getRecommendedPositionSize().doubleValue();
        double currentPrice = context.getMarketData().getCurrentPrice().doubleValue();
        
        TraderReport.TradingAction tradingAction;
        double positionSize = 0.0;
        String reasoning;
        
        // Trading decision logic based on trend and risk
        if (trend == MarketTrend.BULLISH) {
            if (riskLevel == RiskManagerReport.RiskLevel.LOW) {
                tradingAction = TraderReport.TradingAction.BUY;
                positionSize = maxPosition;
                reasoning = "Strong bullish signal with manageable risk";
            } else if (riskLevel == RiskManagerReport.RiskLevel.MODERATE) {
                tradingAction = TraderReport.TradingAction.BUY;
                positionSize = maxPosition * 0.7; // Reduce position due to risk
                reasoning = "Bullish signal but reduced position due to moderate risk";
            } else { // HIGH risk
                tradingAction = TraderReport.TradingAction.WAIT;
                positionSize = 0.0;
                reasoning = "Bullish signal rejected due to high risk level";
            }
        } else if (trend == MarketTrend.BEARISH) {
            tradingAction = TraderReport.TradingAction.SELL;
            positionSize = 1.0; // Sell full position
            reasoning = "Bearish signal confirmed by risk assessment";
        } else { // SIDEWAYS, VOLATILE, UNCERTAIN
            if (riskLevel == RiskManagerReport.RiskLevel.HIGH) {
                tradingAction = TraderReport.TradingAction.SELL;
                positionSize = 0.5; // Reduce position by half
                reasoning = "High risk detected, reducing exposure";
            } else {
                tradingAction = TraderReport.TradingAction.HOLD;
                positionSize = 0.0;
                reasoning = "Maintain current position";
            }
        }
        
        // Set trading decision results
        report.setActionRecommendation(tradingAction);
        report.setPositionSize(java.math.BigDecimal.valueOf(positionSize));
        report.setTradingRationale(reasoning);
        
        // Set order details if action is BUY or SELL
        if (tradingAction == TraderReport.TradingAction.BUY) {
            report.setOrderType(TraderReport.OrderType.MARKET);
            report.setEntryPrice(java.math.BigDecimal.valueOf(currentPrice));
            report.setStopLoss(riskReport.getStopLossLevel());
            // Set take profit target at 15% above entry
            report.setTakeProfit(java.math.BigDecimal.valueOf(currentPrice * 1.15));
        } else if (tradingAction == TraderReport.TradingAction.SELL) {
            report.setOrderType(TraderReport.OrderType.MARKET);
            report.setExitPrice(java.math.BigDecimal.valueOf(currentPrice));
        }
        
        // Set urgency level
        if (riskLevel == RiskManagerReport.RiskLevel.HIGH && 
            (tradingAction == TraderReport.TradingAction.SELL)) {
            report.setUrgencyLevel(3); // HIGH
        } else if (tradingAction == TraderReport.TradingAction.BUY && 
                   riskLevel == RiskManagerReport.RiskLevel.LOW) {
            report.setUrgencyLevel(2); // MEDIUM
        } else {
            report.setUrgencyLevel(1); // LOW
        }
        
        // Summary based on all inputs
        String summary = String.format(
            "Trading decision: %s (%.1f%% position) - %s. " +
            "Based on analyst %s trend (%.0f%% confidence) and %s risk level.",
            tradingAction, 
            positionSize * 100,
            reasoning,
            trend.toString().toLowerCase(),
            analystReport.getConfidenceScore() * 100,
            riskLevel.toString().toLowerCase()
        );
        
        report.setResultSummary(summary);
        report.setStatus(AnalysisResult.AnalysisStatus.COMPLETED);
    }
    
    /**
     * Simulate processing time to make testing more realistic
     */
    private void simulateProcessingTime(int minMs, int maxMs) {
        try {
            int delay = minMs + (int) (Math.random() * (maxMs - minMs));
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Processing simulation interrupted");
        }
    }
} 