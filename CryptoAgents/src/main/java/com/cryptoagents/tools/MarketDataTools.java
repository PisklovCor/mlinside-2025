package com.cryptoagents.tools;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
public class MarketDataTools {
    
    private final Random random = new Random();
    private final Map<String, BigDecimal> mockPrices = new HashMap<>();
    
    public MarketDataTools() {
        // Initialize with some mock data
        mockPrices.put("AAPL", new BigDecimal("150.00"));
        mockPrices.put("GOOGL", new BigDecimal("140.00"));
        mockPrices.put("MSFT", new BigDecimal("380.00"));
        mockPrices.put("TSLA", new BigDecimal("250.00"));
        mockPrices.put("AMZN", new BigDecimal("170.00"));
    }
    
    @Description("Get current market price for a given stock symbol")
    public Map<String, Object> getCurrentPrice(String symbol) {
        log.info("Getting current price for symbol: {}", symbol);
        
        BigDecimal basePrice = mockPrices.getOrDefault(symbol.toUpperCase(), new BigDecimal("100.00"));
        
        // Simulate price fluctuation
        double fluctuation = (random.nextDouble() - 0.5) * 0.02; // +/- 1%
        BigDecimal currentPrice = basePrice.multiply(BigDecimal.valueOf(1 + fluctuation))
                .setScale(2, RoundingMode.HALF_UP);
        
        Map<String, Object> result = new HashMap<>();
        result.put("symbol", symbol.toUpperCase());
        result.put("price", currentPrice);
        result.put("currency", "USD");
        result.put("timestamp", LocalDateTime.now().toString());
        result.put("volume", random.nextInt(1000000) + 100000);
        
        return result;
    }
    
    @Description("Get market trend analysis for a given stock symbol over a specified period")
    public Map<String, Object> getMarketTrend(String symbol, String period) {
        log.info("Analyzing market trend for {} over period: {}", symbol, period);
        
        Map<String, Object> trend = new HashMap<>();
        trend.put("symbol", symbol.toUpperCase());
        trend.put("period", period);
        trend.put("trend", random.nextBoolean() ? "BULLISH" : "BEARISH");
        trend.put("strength", random.nextInt(100));
        trend.put("support", mockPrices.getOrDefault(symbol.toUpperCase(), new BigDecimal("100")).multiply(new BigDecimal("0.95")));
        trend.put("resistance", mockPrices.getOrDefault(symbol.toUpperCase(), new BigDecimal("100")).multiply(new BigDecimal("1.05")));
        trend.put("rsi", 30 + random.nextInt(40)); // RSI between 30-70
        trend.put("movingAverage50", mockPrices.getOrDefault(symbol.toUpperCase(), new BigDecimal("100")).multiply(new BigDecimal("0.98")));
        trend.put("movingAverage200", mockPrices.getOrDefault(symbol.toUpperCase(), new BigDecimal("100")).multiply(new BigDecimal("0.96")));
        
        return trend;
    }
    
    @Description("Calculate technical indicators for a given stock")
    public Map<String, Object> calculateTechnicalIndicators(String symbol) {
        log.info("Calculating technical indicators for: {}", symbol);
        
        Map<String, Object> indicators = new HashMap<>();
        indicators.put("symbol", symbol.toUpperCase());
        indicators.put("macd", new HashMap<String, Object>() {{
            put("value", random.nextDouble() * 2 - 1);
            put("signal", random.nextDouble() * 2 - 1);
            put("histogram", random.nextDouble() * 0.5 - 0.25);
        }});
        indicators.put("bollingerBands", new HashMap<String, Object>() {{
            BigDecimal price = mockPrices.getOrDefault(symbol.toUpperCase(), new BigDecimal("100"));
            put("upper", price.multiply(new BigDecimal("1.02")));
            put("middle", price);
            put("lower", price.multiply(new BigDecimal("0.98")));
        }});
        indicators.put("volume", new HashMap<String, Object>() {{
            put("current", random.nextInt(1000000) + 500000);
            put("average", 750000);
            put("trend", random.nextBoolean() ? "INCREASING" : "DECREASING");
        }});
        
        return indicators;
    }
}