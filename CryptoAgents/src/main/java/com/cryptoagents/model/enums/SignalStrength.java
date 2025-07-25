package com.cryptoagents.model.enums;

/**
 * Enum representing the strength of trading signals
 */
public enum SignalStrength {
    STRONG_BUY("Strong Buy"),
    BUY("Buy"),
    NEUTRAL("Neutral"),
    SELL("Sell"),
    STRONG_SELL("Strong Sell"),
    UNKNOWN("Unknown");
    
    private final String displayName;
    
    SignalStrength(String displayName) {
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