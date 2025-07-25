package com.cryptoagents.model.enums;

/**
 * Enum representing different market trends
 */
public enum MarketTrend {
    BULLISH("Bullish"),
    BEARISH("Bearish"),
    SIDEWAYS("Sideways"),
    UNKNOWN("Unknown");
    
    private final String displayName;
    
    MarketTrend(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
} 