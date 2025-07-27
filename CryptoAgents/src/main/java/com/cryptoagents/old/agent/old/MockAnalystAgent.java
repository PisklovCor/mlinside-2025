package com.cryptoagents.old.agent.old;

import com.cryptoagents.old.model.AnalysisResult;
import com.cryptoagents.old.model.AnalystReport;
import com.cryptoagents.old.model.enums.MarketTrend;
import com.cryptoagents.old.model.enums.SignalStrength;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of Analyst agent for testing purposes.
 * 
 * This agent performs basic technical analysis simulation by generating
 * sample recommendations based on simple price patterns.
 */
@Component
public class MockAnalystAgent extends AbstractAgent {
    
    @Override
    public String getName() {
        return "ANALYST";
    }
    
    @Override
    public AgentType getType() {
        return AgentType.ANALYST;
    }
    
    @Override
    public boolean canAnalyze(AnalysisContext context) {
        return context != null 
            && context.getTicker() != null 
            && !context.getTicker().trim().isEmpty()
            && context.getMarketData() != null;
    }
    
    @Override
    public int getPriority() {
        return 1; // Highest priority - executes first
    }
    
    @Override
    protected AnalysisResult performAnalysis(AnalysisContext context) throws AgentAnalysisException {
        logger.info("Mock Analyst performing technical analysis for {}", context.getTicker());
        
        // Simulate analysis processing time
        simulateProcessingTime(500, 1000);
        
        // Create mock analysis result
        AnalystReport report = new AnalystReport();
        report.setAgentName(getName());
        report.setTicker(context.getTicker());
        report.setAnalysisTime(java.time.LocalDateTime.now());
        
        // Mock technical analysis based on price
        var marketData = context.getMarketData();
        if (marketData.getCurrentPrice() != null) {
            double price = marketData.getCurrentPrice().doubleValue();
            
            // Simple mock logic for demonstration
            if (price > 50000) {
                report.setMarketTrend(MarketTrend.BULLISH);
                report.setSignalStrength(SignalStrength.STRONG_BUY);
                report.setConfidenceScore(0.75);
                report.setResultSummary("Strong bullish trend detected above $50k resistance");
            } else if (price > 30000) {
                report.setMarketTrend(MarketTrend.SIDEWAYS);
                report.setSignalStrength(SignalStrength.NEUTRAL);
                report.setConfidenceScore(0.60);
                report.setResultSummary("Consolidation phase, wait for breakout");
            } else {
                report.setMarketTrend(MarketTrend.BEARISH);
                report.setSignalStrength(SignalStrength.STRONG_SELL);
                report.setConfidenceScore(0.80);
                report.setResultSummary("Bearish trend, price below key support levels");
            }
            
            // Set price levels
            report.setCurrentPrice(java.math.BigDecimal.valueOf(price));
            report.setSupportLevel(java.math.BigDecimal.valueOf(price * 0.9));
            report.setResistanceLevel(java.math.BigDecimal.valueOf(price * 1.1));
            report.setPriceTarget(java.math.BigDecimal.valueOf(price * 1.15));
        } else {
            report.setMarketTrend(MarketTrend.UNKNOWN);
            report.setSignalStrength(SignalStrength.NEUTRAL);
            report.setConfidenceScore(0.50);
            report.setResultSummary("Insufficient data for analysis");
        }
        
        // Add technical indicators
        report.setTechnicalIndicators("RSI: 65.2, MACD: Bullish crossover, SMA_50: " + 
                (marketData.getCurrentPrice() != null ? 
                String.valueOf(marketData.getCurrentPrice().doubleValue() * 0.95) : "N/A"));
        
        report.setTimeHorizonDays(30);
        report.setStatus(AnalysisResult.AnalysisStatus.COMPLETED);
        
        logger.info("Mock Analyst analysis completed for {} with trend: {}", 
                context.getTicker(), report.getMarketTrend());
        
        return report;
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