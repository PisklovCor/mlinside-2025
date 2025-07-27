package com.cryptoagents.old.model.enums;

/**
 * Перечисление, представляющее различные рыночные тренды
 */
public enum MarketTrend {
    BULLISH("Бычий"),
    BEARISH("Медвежий"),
    SIDEWAYS("Боковой"),
    VOLATILE("Волатильный"),
    UNKNOWN("Неизвестно");

    private final String description;

    MarketTrend(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 