package com.cryptoagents.agent;

import com.cryptoagents.model.AnalysisResult;
import com.cryptoagents.model.AnalystReport;
import com.cryptoagents.model.RiskManagerReport;
import com.cryptoagents.model.enums.MarketTrend;
import com.cryptoagents.model.enums.SignalStrength;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of Risk Manager agent for testing purposes.
 * 
 * This agent evaluates risk factors based on analyst recommendations and
 * market conditions to provide risk-adjusted guidance.
 */
@Component
public class MockRiskManagerAgent extends AbstractAgent {
    
    @Override
    public String getName() {
        return "RISK_MANAGER";
    }
    
    @Override
    public AgentType getType() {
        return AgentType.RISK_MANAGER;
    }
    
    @Override
    public boolean canAnalyze(AnalysisContext context) {
        return context != null 
            && context.getTicker() != null 
            && !context.getTicker().trim().isEmpty()
            && context.getMarketData() != null
            && hasAnalystRecommendation(context);
    }
    
    @Override
    public int getPriority() {
        return 2; // Second priority - executes after analyst
    }
    
    @Override
    protected AnalysisResult performAnalysis(AnalysisContext context) throws AgentAnalysisException {
        logger.info("Mock Risk Manager performing risk assessment for {}", context.getTicker());
        
        // Simulate analysis processing time
        simulateProcessingTime(300, 800);
        
        // Get analyst recommendation
        AnalystReport analystReport = getAnalystReport(context);
        if (analystReport == null) {
            throw new AgentAnalysisException(getName(), context.getTicker(), "No analyst report available for risk assessment");
        }
        
        // Create mock risk assessment
        RiskManagerReport report = new RiskManagerReport();
        report.setAgentName(getName());
        report.setTicker(context.getTicker());
        report.setAnalysisTime(java.time.LocalDateTime.now());
        
        // Perform risk assessment based on analyst recommendation
        assessRisk(analystReport, context, report);
        
        logger.info("Mock Risk Manager assessment completed for {} with risk level: {}", 
                context.getTicker(), report.getRiskLevel());
        
        return report;
    }
    
    /**
     * Check if analyst recommendation is available
     */
    private boolean hasAnalystRecommendation(AnalysisContext context) {
        return context.getAgentResults().containsKey("ANALYST");
    }
    
    /**
     * Get analyst report from context
     */
    private AnalystReport getAnalystReport(AnalysisContext context) {
        AnalysisResult result = context.getAgentResults().get("ANALYST");
        return (result instanceof AnalystReport) ? (AnalystReport) result : null;
    }
    
    /**
     * Perform risk assessment based on analyst recommendation
     */
    private void assessRisk(AnalystReport analystReport, AnalysisContext context, RiskManagerReport report) {
        MarketTrend trend = analystReport.getMarketTrend();
        SignalStrength signalStrength = analystReport.getSignalStrength();
        double confidence = analystReport.getConfidenceScore();
        double price = context.getMarketData().getCurrentPrice().doubleValue();
        
        // Mock risk assessment logic based on trend and signal strength
        RiskManagerReport.RiskLevel riskLevel;
        double riskScore;
        String riskSummary;
        
        if (trend == MarketTrend.BULLISH) {
            if (confidence > 0.7 && price < 60000) {
                riskLevel = RiskManagerReport.RiskLevel.MODERATE;
                riskScore = 0.6;
                riskSummary = "Moderate risk - strong bullish signal but consider market volatility";
            } else if (confidence > 0.7) {
                riskLevel = RiskManagerReport.RiskLevel.HIGH;
                riskScore = 0.8;
                riskSummary = "High risk - buying at elevated prices";
            } else {
                riskLevel = RiskManagerReport.RiskLevel.HIGH;
                riskScore = 0.75;
                riskSummary = "High risk - low confidence bullish signal";
            }
        } else if (trend == MarketTrend.BEARISH) {
            if (confidence > 0.7) {
                riskLevel = RiskManagerReport.RiskLevel.LOW;
                riskScore = 0.3;
                riskSummary = "Low risk - strong bearish signal protects capital";
            } else {
                riskLevel = RiskManagerReport.RiskLevel.MODERATE;
                riskScore = 0.5;
                riskSummary = "Moderate risk - uncertain bearish signal";
            }
        } else { // SIDEWAYS, VOLATILE, UNCERTAIN
            riskLevel = RiskManagerReport.RiskLevel.LOW;
            riskScore = 0.4;
            riskSummary = "Low risk - maintaining current position";
        }
        
        // Set risk assessment results
        report.setRiskLevel(riskLevel);
        report.setRiskScore(java.math.BigDecimal.valueOf(riskScore));
        report.setResultSummary(riskSummary);
        
        // Set volatility and other risk metrics
        report.setVolatilityScore(java.math.BigDecimal.valueOf(price > 50000 ? 0.8 : 0.5));
        report.setLiquidityRisk(java.math.BigDecimal.valueOf(0.3));
        report.setMarketCapRisk(java.math.BigDecimal.valueOf(0.4));
        report.setRegulatoryRisk(java.math.BigDecimal.valueOf(0.6));
        report.setTechnicalRisk(java.math.BigDecimal.valueOf(0.35));
        report.setConcentrationRisk(java.math.BigDecimal.valueOf(0.25));
        
        // Set position recommendations based on risk level
        if (trend == MarketTrend.BULLISH && riskLevel == RiskManagerReport.RiskLevel.LOW) {
            report.setRecommendedPositionSize(java.math.BigDecimal.valueOf(0.1)); // 10% max position
        } else if (trend == MarketTrend.BULLISH && riskLevel == RiskManagerReport.RiskLevel.MODERATE) {
            report.setRecommendedPositionSize(java.math.BigDecimal.valueOf(0.05)); // 5% max position
        } else if (trend == MarketTrend.BULLISH && riskLevel == RiskManagerReport.RiskLevel.HIGH) {
            report.setRecommendedPositionSize(java.math.BigDecimal.valueOf(0.02)); // 2% max position
        } else {
            report.setRecommendedPositionSize(java.math.BigDecimal.valueOf(0.0)); // No new position
        }
        
        report.setStopLossLevel(java.math.BigDecimal.valueOf(price * 0.95)); // 5% stop loss
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