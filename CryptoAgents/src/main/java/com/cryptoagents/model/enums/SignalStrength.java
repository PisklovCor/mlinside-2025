package com.cryptoagents.model.enums;

/**
 * Перечисление, представляющее силу торговых сигналов
 */
public enum SignalStrength {
    STRONG_BUY("Сильная покупка"),
    BUY("Покупка"),
    NEUTRAL("Нейтральный"),
    SELL("Продажа"),
    STRONG_SELL("Сильная продажа"),
    VERY_WEAK("Очень слабый"),
    WEAK("Слабый"),
    MODERATE("Умеренный"),
    STRONG("Сильный"),
    VERY_STRONG("Очень сильный");

    private final String description;

    SignalStrength(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 