package com.cryptoagents.old.agent.old;

import com.cryptoagents.old.model.AnalystReport;
import com.cryptoagents.old.model.AnalysisResult;
import com.cryptoagents.old.model.enums.TimePeriod;
import com.cryptoagents.old.model.dto.MarketData;
import com.cryptoagents.old.model.enums.MarketTrend;
import com.cryptoagents.old.model.enums.SignalStrength;
import com.cryptoagents.old.service.CryptoDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Analyst Agent responsible for technical analysis of cryptocurrency tokens.
 * Performs calculations for various technical indicators and provides market insights.
 */
@Component
public class AnalystAgent extends AbstractAgent {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalystAgent.class);
    
    private final CryptoDataService cryptoDataService;
    
    // Technical analysis constants
    private static final int SMA_SHORT_PERIOD = 20;
    private static final int SMA_LONG_PERIOD = 50;
    private static final int RSI_PERIOD = 14;
    private static final int MACD_FAST = 12;
    private static final int MACD_SLOW = 26;
    private static final int MACD_SIGNAL = 9;
    
    // RSI thresholds
    private static final BigDecimal RSI_OVERSOLD = new BigDecimal("30");
    private static final BigDecimal RSI_OVERBOUGHT = new BigDecimal("70");
    
    public AnalystAgent(CryptoDataService cryptoDataService) {
        this.cryptoDataService = cryptoDataService;
    }
    
    @Override
    public String getName() {
        return "ANALYST";
    }
    
    @Override
    public AgentType getType() {
        return AgentType.ANALYST;
    }
    
    @Override
    public int getPriority() {
        return 1;
    }
    
    @Override
    protected AnalysisResult performAnalysis(AnalysisContext context) throws AgentAnalysisException {
        String ticker = context.getTicker();
        logger.info("Starting technical analysis for ticker: {}", ticker);
        
        try {
            // Get current market data
            Optional<MarketData> marketDataOpt = cryptoDataService.getMarketData(ticker);
            if (marketDataOpt.isEmpty()) {
                throw new AgentAnalysisException(getName(), ticker, "Failed to retrieve market data");
            }
            
            MarketData marketData = marketDataOpt.get();
            
            // Get historical data for technical indicators
            Optional<HistoricalData> historicalDataOpt = cryptoDataService.getHistoricalData(ticker, TimePeriod.ONE_MONTH);
            if (historicalDataOpt.isEmpty()) {
                throw new AgentAnalysisException(getName(), ticker, "Failed to retrieve historical data");
            }
            
            HistoricalData historicalData = historicalDataOpt.get();
            
            // Calculate technical indicators
            BigDecimal currentPrice = marketData.getCurrentPrice();
            BigDecimal smaShort = calculateSMA(historicalData.getPrices(), SMA_SHORT_PERIOD);
            BigDecimal smaLong = calculateSMA(historicalData.getPrices(), SMA_LONG_PERIOD);
            BigDecimal rsi = calculateRSI(historicalData.getPrices(), RSI_PERIOD);
            BigDecimal macd = calculateMACD(historicalData.getPrices());
            BigDecimal macdSignal = calculateMACDSignal(historicalData.getPrices());
            
            // Analyze market trend
            MarketTrend marketTrend = analyzeMarketTrend(currentPrice, smaShort, smaLong, rsi);
            
            // Calculate support and resistance levels
            BigDecimal supportLevel = calculateSupportLevel(historicalData.getPrices());
            BigDecimal resistanceLevel = calculateResistanceLevel(historicalData.getPrices());
            
            // Calculate price target
            BigDecimal priceTarget = calculatePriceTarget(currentPrice, marketTrend, rsi);
            
            // Determine signal strength
            SignalStrength signalStrength = determineSignalStrength(rsi, macd, marketTrend);
            
            // Analyze volume
            String volumeAnalysis = analyzeVolume(marketData);
            
            // Calculate momentum indicators
            String momentumIndicators = calculateMomentumIndicators(historicalData.getPrices());
            
            // Pattern recognition
            String patternRecognition = identifyPatterns(historicalData.getPrices());
            
            // Determine time horizon
            int timeHorizonDays = determineTimeHorizon(marketTrend, signalStrength);
            
            // Create analyst report
            AnalystReport report = new AnalystReport();
            report.setTicker(ticker);
            report.setAnalysisTime(LocalDateTime.now());
            report.setAgentName(getName());
            report.setStatus(AnalysisResult.AnalysisStatus.COMPLETED);
            
            // Set technical analysis results
            report.setMarketTrend(marketTrend);
            report.setTechnicalIndicators(String.format(
                "SMA(20): %s, SMA(50): %s, RSI: %s, MACD: %s, Signal: %s",
                smaShort.setScale(8, RoundingMode.HALF_UP),
                smaLong.setScale(8, RoundingMode.HALF_UP),
                rsi.setScale(2, RoundingMode.HALF_UP),
                macd.setScale(8, RoundingMode.HALF_UP),
                macdSignal.setScale(8, RoundingMode.HALF_UP)
            ));
            report.setSupportLevel(supportLevel);
            report.setResistanceLevel(resistanceLevel);
            report.setCurrentPrice(currentPrice);
            report.setPriceTarget(priceTarget);
            report.setSignalStrength(signalStrength);
            report.setVolumeAnalysis(volumeAnalysis);
            report.setMomentumIndicators(momentumIndicators);
            report.setPatternRecognition(patternRecognition);
            report.setTimeHorizonDays(timeHorizonDays);
            
            // Calculate confidence score based on data quality and indicator consistency
            double confidenceScore = calculateConfidenceScore(historicalData, marketData, rsi, macd);
            report.setConfidenceScore(confidenceScore);
            
            // Generate result summary
            String summary = generateAnalysisSummary(ticker, marketTrend, signalStrength, confidenceScore, priceTarget);
            report.setResultSummary(summary);
            
            logger.info("Technical analysis completed for {}: Trend={}, Signal={}, Confidence={}%", 
                ticker, marketTrend, signalStrength, Math.round(confidenceScore * 100));
            
            return report;
            
        } catch (Exception e) {
            logger.error("Error during technical analysis for ticker {}: {}", ticker, e.getMessage(), e);
            throw new AgentAnalysisException(getName(), ticker, "Technical analysis failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Calculate Simple Moving Average (SMA)
     */
    protected BigDecimal calculateSMA(List<BigDecimal> prices, int period) {
        if (prices.size() < period) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = prices.size() - period; i < prices.size(); i++) {
            sum = sum.add(prices.get(i));
        }
        
        return sum.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate Relative Strength Index (RSI)
     */
    protected BigDecimal calculateRSI(List<BigDecimal> prices, int period) {
        if (prices.size() < period + 1) {
            return BigDecimal.valueOf(50); // Neutral RSI
        }
        
        BigDecimal gains = BigDecimal.ZERO;
        BigDecimal losses = BigDecimal.ZERO;
        
        for (int i = prices.size() - period; i < prices.size(); i++) {
            BigDecimal change = prices.get(i).subtract(prices.get(i - 1));
            if (change.compareTo(BigDecimal.ZERO) > 0) {
                gains = gains.add(change);
            } else {
                losses = losses.add(change.abs());
            }
        }
        
        BigDecimal avgGain = gains.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
        BigDecimal avgLoss = losses.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
        
        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100);
        }
        
        BigDecimal rs = avgGain.divide(avgLoss, 8, RoundingMode.HALF_UP);
        BigDecimal rsi = BigDecimal.valueOf(100).subtract(
            BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), 2, RoundingMode.HALF_UP)
        );
        
        return rsi;
    }
    
    /**
     * Calculate MACD (Moving Average Convergence Divergence)
     */
    protected BigDecimal calculateMACD(List<BigDecimal> prices) {
        if (prices.size() < MACD_SLOW) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal emaFast = calculateEMA(prices, MACD_FAST);
        BigDecimal emaSlow = calculateEMA(prices, MACD_SLOW);
        
        return emaFast.subtract(emaSlow);
    }
    
    /**
     * Calculate MACD Signal Line
     */
    protected BigDecimal calculateMACDSignal(List<BigDecimal> prices) {
        // Simplified implementation - in practice would need to track MACD values over time
        BigDecimal macd = calculateMACD(prices);
        return macd.multiply(BigDecimal.valueOf(0.8)); // Simplified signal line
    }
    
    /**
     * Calculate Exponential Moving Average (EMA)
     */
    protected BigDecimal calculateEMA(List<BigDecimal> prices, int period) {
        if (prices.size() < period) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal multiplier = BigDecimal.valueOf(2.0 / (period + 1));
        BigDecimal ema = prices.get(prices.size() - period);
        
        for (int i = prices.size() - period + 1; i < prices.size(); i++) {
            ema = prices.get(i).multiply(multiplier)
                .add(ema.multiply(BigDecimal.ONE.subtract(multiplier)));
        }
        
        return ema;
    }
    
    /**
     * Analyze market trend based on technical indicators
     */
    protected MarketTrend analyzeMarketTrend(BigDecimal currentPrice, BigDecimal smaShort, 
                                          BigDecimal smaLong, BigDecimal rsi) {
        int bullishSignals = 0;
        int bearishSignals = 0;
        
        // Price vs SMA analysis
        if (currentPrice.compareTo(smaShort) > 0) bullishSignals++;
        else bearishSignals++;
        
        if (smaShort.compareTo(smaLong) > 0) bullishSignals++;
        else bearishSignals++;
        
        // RSI analysis
        if (rsi.compareTo(RSI_OVERSOLD) < 0) bullishSignals++; // Oversold
        else if (rsi.compareTo(RSI_OVERBOUGHT) > 0) bearishSignals++; // Overbought
        else if (rsi.compareTo(BigDecimal.valueOf(50)) > 0) bullishSignals++; // Above neutral
        else bearishSignals++; // Below neutral
        
        // Determine trend
        if (bullishSignals > bearishSignals + 1) {
            return MarketTrend.BULLISH;
        } else if (bearishSignals > bullishSignals + 1) {
            return MarketTrend.BEARISH;
        } else if (Math.abs(bullishSignals - bearishSignals) <= 1) {
            return MarketTrend.SIDEWAYS;
        } else {
            return MarketTrend.UNKNOWN;
        }
    }
    
    /**
     * Calculate support level (simplified)
     */
    protected BigDecimal calculateSupportLevel(List<BigDecimal> prices) {
        if (prices.isEmpty()) return BigDecimal.ZERO;
        
        BigDecimal minPrice = prices.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal currentPrice = prices.get(prices.size() - 1);
        
        // Support level is typically 5-10% below current price
        return currentPrice.multiply(BigDecimal.valueOf(0.92));
    }
    
    /**
     * Calculate resistance level (simplified)
     */
    protected BigDecimal calculateResistanceLevel(List<BigDecimal> prices) {
        if (prices.isEmpty()) return BigDecimal.ZERO;
        
        BigDecimal maxPrice = prices.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal currentPrice = prices.get(prices.size() - 1);
        
        // Resistance level is typically 5-10% above current price
        return currentPrice.multiply(BigDecimal.valueOf(1.08));
    }
    
    /**
     * Calculate price target based on trend and indicators
     */
    protected BigDecimal calculatePriceTarget(BigDecimal currentPrice, MarketTrend trend, BigDecimal rsi) {
        BigDecimal multiplier;
        
        switch (trend) {
            case BULLISH:
                multiplier = BigDecimal.valueOf(1.15); // 15% upside
                break;
            case BEARISH:
                multiplier = BigDecimal.valueOf(0.85); // 15% downside
                break;
            case SIDEWAYS:
                multiplier = BigDecimal.valueOf(1.05); // 5% upside
                break;
            default:
                multiplier = BigDecimal.valueOf(1.02); // 2% upside
        }
        
        return currentPrice.multiply(multiplier);
    }
    
    /**
     * Determine signal strength based on indicators
     */
    protected SignalStrength determineSignalStrength(BigDecimal rsi, BigDecimal macd, MarketTrend trend) {
        int strength = 0;
        
        // RSI contribution
        if (rsi.compareTo(RSI_OVERSOLD) < 0 || rsi.compareTo(RSI_OVERBOUGHT) > 0) {
            strength += 2; // Strong signal
        } else if (rsi.compareTo(BigDecimal.valueOf(40)) < 0 || rsi.compareTo(BigDecimal.valueOf(60)) > 0) {
            strength += 1; // Moderate signal
        }
        
        // MACD contribution
        if (macd.compareTo(BigDecimal.ZERO) > 0) {
            strength += 1;
        } else {
            strength -= 1;
        }
        
        // Trend contribution
        switch (trend) {
            case BULLISH:
                strength += 2;
                break;
            case BEARISH:
                strength -= 2;
                break;
            case SIDEWAYS:
                // Neutral
                break;
            default:
                strength -= 1;
        }
        
        // Map to SignalStrength enum
        if (strength >= 4) return SignalStrength.STRONG_BUY;
        if (strength >= 2) return SignalStrength.BUY;
        if (strength >= 0) return SignalStrength.NEUTRAL;
        if (strength >= -2) return SignalStrength.SELL;
        return SignalStrength.STRONG_SELL;
    }
    
    /**
     * Analyze trading volume
     */
    protected String analyzeVolume(MarketData marketData) {
        BigDecimal volume24h = marketData.getVolume24h();
        BigDecimal marketCap = marketData.getMarketCap();
        
        if (volume24h.compareTo(BigDecimal.ZERO) == 0 || marketCap.compareTo(BigDecimal.ZERO) == 0) {
            return "Insufficient volume data";
        }
        
        BigDecimal volumeToMarketCapRatio = volume24h.divide(marketCap, 4, RoundingMode.HALF_UP);
        
        if (volumeToMarketCapRatio.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            return "High volume relative to market cap - strong liquidity";
        } else if (volumeToMarketCapRatio.compareTo(BigDecimal.valueOf(0.05)) > 0) {
            return "Moderate volume - adequate liquidity";
        } else {
            return "Low volume - limited liquidity, higher risk";
        }
    }
    
    /**
     * Calculate momentum indicators
     */
    protected String calculateMomentumIndicators(List<BigDecimal> prices) {
        if (prices.size() < 10) {
            return "Insufficient data for momentum analysis";
        }
        
        BigDecimal currentPrice = prices.get(prices.size() - 1);
        BigDecimal price10DaysAgo = prices.get(prices.size() - 10);
        
        BigDecimal momentum = currentPrice.subtract(price10DaysAgo)
            .divide(price10DaysAgo, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
        
        if (momentum.compareTo(BigDecimal.valueOf(10)) > 0) {
            return "Strong positive momentum (+" + momentum.setScale(2, RoundingMode.HALF_UP) + "%)";
        } else if (momentum.compareTo(BigDecimal.valueOf(5)) > 0) {
            return "Moderate positive momentum (+" + momentum.setScale(2, RoundingMode.HALF_UP) + "%)";
        } else if (momentum.compareTo(BigDecimal.valueOf(-5)) < 0) {
            return "Negative momentum (" + momentum.setScale(2, RoundingMode.HALF_UP) + "%)";
        } else {
            return "Neutral momentum (" + momentum.setScale(2, RoundingMode.HALF_UP) + "%)";
        }
    }
    
    /**
     * Identify price patterns (simplified)
     */
    protected String identifyPatterns(List<BigDecimal> prices) {
        if (prices.size() < 20) {
            return "Insufficient data for pattern recognition";
        }
        
        // Simple pattern detection
        BigDecimal recentHigh = prices.subList(prices.size() - 5, prices.size()).stream()
            .max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal recentLow = prices.subList(prices.size() - 5, prices.size()).stream()
            .min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal currentPrice = prices.get(prices.size() - 1);
        
        if (currentPrice.compareTo(recentHigh) == 0) {
            return "Potential resistance at current level";
        } else if (currentPrice.compareTo(recentLow) == 0) {
            return "Potential support at current level";
        } else {
            return "No clear pattern identified";
        }
    }
    
    /**
     * Determine analysis time horizon
     */
    protected int determineTimeHorizon(MarketTrend trend, SignalStrength strength) {
        switch (strength) {
            case STRONG_BUY:
                return trend == MarketTrend.BULLISH ? 30 : 7; // Longer for strong bullish, shorter for bearish
            case BUY:
                return 14;
            case NEUTRAL:
                return 7;
            case SELL:
            case STRONG_SELL:
                return 3;
            default:
                return 7;
        }
    }
    
    /**
     * Calculate confidence score based on data quality and indicator consistency
     */
    protected double calculateConfidenceScore(HistoricalData historicalData, MarketData marketData, 
                                          BigDecimal rsi, BigDecimal macd) {
        double score = 0.5; // Base score
        
        // Data quality contribution
        if (historicalData.getPrices().size() >= 50) score += 0.2;
        if (marketData.getVolume24h().compareTo(BigDecimal.ZERO) > 0) score += 0.1;
        
        // Indicator consistency
        if (rsi.compareTo(BigDecimal.valueOf(20)) > 0 && rsi.compareTo(BigDecimal.valueOf(80)) < 0) {
            score += 0.1; // RSI in reasonable range
        }
        
        if (macd.abs().compareTo(BigDecimal.valueOf(0.001)) > 0) {
            score += 0.1; // MACD shows some movement
        }
        
        return Math.min(score, 1.0); // Cap at 1.0
    }
    
    /**
     * Generate analysis summary
     */
    protected String generateAnalysisSummary(String ticker, MarketTrend trend, SignalStrength strength, 
                                         double confidence, BigDecimal priceTarget) {
        return String.format(
            "%s Analysis: %s trend with %s signal strength. " +
            "Confidence: %.1f%%. Price target: $%s",
            ticker.toUpperCase(),
            trend.toString().toLowerCase(),
            strength.toString().toLowerCase().replace("_", " "),
            confidence * 100,
            priceTarget.setScale(8, RoundingMode.HALF_UP)
        );
    }
} 