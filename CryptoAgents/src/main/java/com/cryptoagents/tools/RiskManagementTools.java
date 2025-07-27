package com.cryptoagents.tools;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RiskManagementTools {
    
    @Description("Calculate position size based on account balance, risk percentage, and stop loss")
    public Map<String, Object> calculatePositionSize(
            BigDecimal accountBalance, 
            BigDecimal riskPercentage, 
            BigDecimal entryPrice, 
            BigDecimal stopLoss) {
        
        log.info("Calculating position size - Balance: {}, Risk: {}%, Entry: {}, Stop: {}", 
                accountBalance, riskPercentage, entryPrice, stopLoss);
        
        // Calculate risk amount
        BigDecimal riskAmount = accountBalance.multiply(riskPercentage.divide(new BigDecimal("100")));
        
        // Calculate price difference
        BigDecimal priceDifference = entryPrice.subtract(stopLoss).abs();
        
        // Calculate position size
        BigDecimal positionSize = riskAmount.divide(priceDifference, 0, RoundingMode.DOWN);
        
        Map<String, Object> result = new HashMap<>();
        result.put("positionSize", positionSize);
        result.put("riskAmount", riskAmount.setScale(2, RoundingMode.HALF_UP));
        result.put("totalPositionValue", positionSize.multiply(entryPrice).setScale(2, RoundingMode.HALF_UP));
        result.put("riskRewardRatio", calculateRiskRewardRatio(entryPrice, stopLoss, entryPrice.multiply(new BigDecimal("1.02"))));
        
        return result;
    }
    
    @Description("Evaluate portfolio risk metrics and provide risk assessment")
    public Map<String, Object> evaluatePortfolioRisk(Map<String, BigDecimal> positions, BigDecimal accountBalance) {
        log.info("Evaluating portfolio risk for {} positions", positions.size());
        
        BigDecimal totalValue = positions.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal exposurePercentage = totalValue.divide(accountBalance, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        
        Map<String, Object> riskMetrics = new HashMap<>();
        riskMetrics.put("totalExposure", totalValue.setScale(2, RoundingMode.HALF_UP));
        riskMetrics.put("exposurePercentage", exposurePercentage.setScale(2, RoundingMode.HALF_UP));
        riskMetrics.put("numberOfPositions", positions.size());
        riskMetrics.put("averagePositionSize", positions.isEmpty() ? BigDecimal.ZERO : 
                totalValue.divide(new BigDecimal(positions.size()), 2, RoundingMode.HALF_UP));
        
        // Risk assessment
        String riskLevel;
        if (exposurePercentage.compareTo(new BigDecimal("80")) > 0) {
            riskLevel = "HIGH";
        } else if (exposurePercentage.compareTo(new BigDecimal("50")) > 0) {
            riskLevel = "MODERATE";
        } else {
            riskLevel = "LOW";
        }
        riskMetrics.put("riskLevel", riskLevel);
        
        // Diversification assessment
        String diversification;
        if (positions.size() < 3) {
            diversification = "POOR";
        } else if (positions.size() < 7) {
            diversification = "MODERATE";
        } else {
            diversification = "GOOD";
        }
        riskMetrics.put("diversification", diversification);
        
        return riskMetrics;
    }
    
    @Description("Calculate Value at Risk (VaR) for a position or portfolio")
    public Map<String, Object> calculateVaR(BigDecimal positionValue, BigDecimal volatility, int confidenceLevel) {
        log.info("Calculating VaR - Position: {}, Volatility: {}%, Confidence: {}%", 
                positionValue, volatility, confidenceLevel);
        
        // Simplified VaR calculation
        double zScore;
        switch (confidenceLevel) {
            case 95:
                zScore = 1.645;
                break;
            case 99:
                zScore = 2.326;
                break;
            default:
                zScore = 1.645; // Default to 95%
        }
        
        BigDecimal var = positionValue.multiply(volatility.divide(new BigDecimal("100")))
                .multiply(BigDecimal.valueOf(zScore))
                .setScale(2, RoundingMode.HALF_UP);
        
        Map<String, Object> result = new HashMap<>();
        result.put("valueAtRisk", var);
        result.put("confidenceLevel", confidenceLevel);
        result.put("timeHorizon", "1 day");
        result.put("interpretation", String.format("There is a %d%% probability that the position will not lose more than $%.2f in one day", 
                confidenceLevel, var));
        
        return result;
    }
    
    private BigDecimal calculateRiskRewardRatio(BigDecimal entry, BigDecimal stopLoss, BigDecimal target) {
        BigDecimal risk = entry.subtract(stopLoss).abs();
        BigDecimal reward = target.subtract(entry).abs();
        
        if (risk.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return reward.divide(risk, 2, RoundingMode.HALF_UP);
    }
}