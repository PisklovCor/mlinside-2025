package com.cryptoagents.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing basic cryptocurrency information.
 * 
 * This class is used to transfer cryptocurrency data between layers
 * and can be serialized/deserialized from JSON API responses.
 */
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

    private LocalDateTime retrievedAt;

    // Default constructor
    public CryptoCurrency() {
        this.retrievedAt = LocalDateTime.now();
    }

    // Constructor with required fields
    public CryptoCurrency(String id, String symbol, String name, BigDecimal currentPrice) {
        this();
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }

    public Integer getMarketCapRank() {
        return marketCapRank;
    }

    public void setMarketCapRank(Integer marketCapRank) {
        this.marketCapRank = marketCapRank;
    }

    public BigDecimal getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(BigDecimal totalVolume) {
        this.totalVolume = totalVolume;
    }

    public BigDecimal getPriceChange24h() {
        return priceChange24h;
    }

    public void setPriceChange24h(BigDecimal priceChange24h) {
        this.priceChange24h = priceChange24h;
    }

    public BigDecimal getPriceChangePercentage24h() {
        return priceChangePercentage24h;
    }

    public void setPriceChangePercentage24h(BigDecimal priceChangePercentage24h) {
        this.priceChangePercentage24h = priceChangePercentage24h;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getRetrievedAt() {
        return retrievedAt;
    }

    public void setRetrievedAt(LocalDateTime retrievedAt) {
        this.retrievedAt = retrievedAt;
    }

    // Utility methods

    /**
     * Returns the ticker symbol in uppercase.
     * 
     * @return Uppercase ticker symbol
     */
    public String getTickerUpperCase() {
        return symbol != null ? symbol.toUpperCase() : null;
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
     * 
     * @return true if price change percentage is positive
     */
    public boolean isPriceIncreasing() {
        return priceChangePercentage24h != null && priceChangePercentage24h.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String toString() {
        return String.format("CryptoCurrency{id='%s', symbol='%s', name='%s', currentPrice=%s, marketCapRank=%d}", 
                id, symbol, name, currentPrice, marketCapRank);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CryptoCurrency)) return false;
        CryptoCurrency that = (CryptoCurrency) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 