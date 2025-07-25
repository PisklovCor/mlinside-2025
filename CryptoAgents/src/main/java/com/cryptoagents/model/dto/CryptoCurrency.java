package com.cryptoagents.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing basic cryptocurrency information.
 * 
 * This class is used to transfer cryptocurrency data between layers
 * and can be serialized/deserialized from JSON API responses.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "retrievedAt")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoCurrency {

    @JsonProperty("id")
    private String id;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("name")
    private String name;

    @JsonProperty("current_price")
    private BigDecimal currentPrice;

    @JsonProperty("market_cap")
    private BigDecimal marketCap;

    @JsonProperty("market_cap_rank")
    private Integer marketCapRank;

    @JsonProperty("total_volume")
    private BigDecimal totalVolume;

    @JsonProperty("price_change_24h")
    private BigDecimal priceChange24h;

    @JsonProperty("price_change_percentage_24h")
    private BigDecimal priceChangePercentage24h;

    @JsonProperty("last_updated")
    private String lastUpdated;

    private LocalDateTime retrievedAt = LocalDateTime.now();

    // Constructor with required fields
    public CryptoCurrency(String id, String symbol, String name, BigDecimal currentPrice) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
        this.retrievedAt = LocalDateTime.now();
    }

    // Utility methods

    /**
     * Returns the ticker symbol in uppercase.
     * 
     * @return Uppercase ticker symbol
     * @throws NullPointerException if symbol is null
     */
    public String getTickerUpperCase() {
        if (symbol == null) {
            throw new NullPointerException("Symbol cannot be null");
        }
        return symbol.toUpperCase();
    }

    /**
     * Checks if this cryptocurrency has valid price data.
     * 
     * @return true if current price is not null and positive
     */
    public boolean hasValidPrice() {
        return currentPrice != null && currentPrice.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Checks if the price has increased in the last 24 hours.
     * Uses priceChange24h if priceChangePercentage24h is not available.
     * 
     * @return true if price change is positive
     */
    public boolean isPriceIncreasing() {
        // First try percentage change, fallback to absolute change
        if (priceChangePercentage24h != null) {
            return priceChangePercentage24h.compareTo(BigDecimal.ZERO) > 0;
        }
        if (priceChange24h != null) {
            return priceChange24h.compareTo(BigDecimal.ZERO) > 0;
        }
        return false;
    }
} 