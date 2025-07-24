package com.cryptoagents.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing comprehensive market data for a cryptocurrency.
 * 
 * This class contains detailed market information including prices, volumes,
 * market capitalization, and various trading metrics.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketData {

    // Basic identification
    private String ticker;
    private String name;
    private String id;

    // Price data
    @JsonProperty("current_price")
    private BigDecimal currentPrice;

    @JsonProperty("price_change_24h")
    private BigDecimal priceChange24h;

    @JsonProperty("price_change_percentage_24h")
    private BigDecimal priceChangePercentage24h;

    @JsonProperty("price_change_percentage_7d")
    private BigDecimal priceChangePercentage7d;

    @JsonProperty("price_change_percentage_30d")
    private BigDecimal priceChangePercentage30d;

    // Market cap data
    @JsonProperty("market_cap")
    private BigDecimal marketCap;

    @JsonProperty("market_cap_rank")
    private Integer marketCapRank;

    @JsonProperty("market_cap_change_24h")
    private BigDecimal marketCapChange24h;

    @JsonProperty("market_cap_change_percentage_24h")
    private BigDecimal marketCapChangePercentage24h;

    // Volume data
    @JsonProperty("total_volume")
    private BigDecimal totalVolume;

    @JsonProperty("volume_change_24h")
    private BigDecimal volumeChange24h;

    // High/Low data
    @JsonProperty("high_24h")
    private BigDecimal high24h;

    @JsonProperty("low_24h")
    private BigDecimal low24h;

    @JsonProperty("ath")
    private BigDecimal allTimeHigh;

    @JsonProperty("ath_change_percentage")
    private BigDecimal athChangePercentage;

    @JsonProperty("ath_date")
    private String athDate;

    @JsonProperty("atl")
    private BigDecimal allTimeLow;

    @JsonProperty("atl_change_percentage")
    private BigDecimal atlChangePercentage;

    @JsonProperty("atl_date")
    private String atlDate;

    // Supply data
    @JsonProperty("circulating_supply")
    private BigDecimal circulatingSupply;

    @JsonProperty("total_supply")
    private BigDecimal totalSupply;

    @JsonProperty("max_supply")
    private BigDecimal maxSupply;

    // Additional metrics
    @JsonProperty("fully_diluted_valuation")
    private BigDecimal fullyDilutedValuation;

    @JsonProperty("last_updated")
    private String lastUpdated;

    private LocalDateTime retrievedAt;

    // Default constructor
    public MarketData() {
        this.retrievedAt = LocalDateTime.now();
    }

    // Constructor with basic fields
    public MarketData(String ticker, String name, BigDecimal currentPrice) {
        this();
        this.ticker = ticker;
        this.name = name;
        this.currentPrice = currentPrice;
    }

    // Getters and Setters

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
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

    public BigDecimal getPriceChangePercentage7d() {
        return priceChangePercentage7d;
    }

    public void setPriceChangePercentage7d(BigDecimal priceChangePercentage7d) {
        this.priceChangePercentage7d = priceChangePercentage7d;
    }

    public BigDecimal getPriceChangePercentage30d() {
        return priceChangePercentage30d;
    }

    public void setPriceChangePercentage30d(BigDecimal priceChangePercentage30d) {
        this.priceChangePercentage30d = priceChangePercentage30d;
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

    public BigDecimal getMarketCapChange24h() {
        return marketCapChange24h;
    }

    public void setMarketCapChange24h(BigDecimal marketCapChange24h) {
        this.marketCapChange24h = marketCapChange24h;
    }

    public BigDecimal getMarketCapChangePercentage24h() {
        return marketCapChangePercentage24h;
    }

    public void setMarketCapChangePercentage24h(BigDecimal marketCapChangePercentage24h) {
        this.marketCapChangePercentage24h = marketCapChangePercentage24h;
    }

    public BigDecimal getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(BigDecimal totalVolume) {
        this.totalVolume = totalVolume;
    }

    public BigDecimal getVolumeChange24h() {
        return volumeChange24h;
    }

    public void setVolumeChange24h(BigDecimal volumeChange24h) {
        this.volumeChange24h = volumeChange24h;
    }

    public BigDecimal getHigh24h() {
        return high24h;
    }

    public void setHigh24h(BigDecimal high24h) {
        this.high24h = high24h;
    }

    public BigDecimal getLow24h() {
        return low24h;
    }

    public void setLow24h(BigDecimal low24h) {
        this.low24h = low24h;
    }

    public BigDecimal getAllTimeHigh() {
        return allTimeHigh;
    }

    public void setAllTimeHigh(BigDecimal allTimeHigh) {
        this.allTimeHigh = allTimeHigh;
    }

    public BigDecimal getAthChangePercentage() {
        return athChangePercentage;
    }

    public void setAthChangePercentage(BigDecimal athChangePercentage) {
        this.athChangePercentage = athChangePercentage;
    }

    public String getAthDate() {
        return athDate;
    }

    public void setAthDate(String athDate) {
        this.athDate = athDate;
    }

    public BigDecimal getAllTimeLow() {
        return allTimeLow;
    }

    public void setAllTimeLow(BigDecimal allTimeLow) {
        this.allTimeLow = allTimeLow;
    }

    public BigDecimal getAtlChangePercentage() {
        return atlChangePercentage;
    }

    public void setAtlChangePercentage(BigDecimal atlChangePercentage) {
        this.atlChangePercentage = atlChangePercentage;
    }

    public String getAtlDate() {
        return atlDate;
    }

    public void setAtlDate(String atlDate) {
        this.atlDate = atlDate;
    }

    public BigDecimal getCirculatingSupply() {
        return circulatingSupply;
    }

    public void setCirculatingSupply(BigDecimal circulatingSupply) {
        this.circulatingSupply = circulatingSupply;
    }

    public BigDecimal getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(BigDecimal totalSupply) {
        this.totalSupply = totalSupply;
    }

    public BigDecimal getMaxSupply() {
        return maxSupply;
    }

    public void setMaxSupply(BigDecimal maxSupply) {
        this.maxSupply = maxSupply;
    }

    public BigDecimal getFullyDilutedValuation() {
        return fullyDilutedValuation;
    }

    public void setFullyDilutedValuation(BigDecimal fullyDilutedValuation) {
        this.fullyDilutedValuation = fullyDilutedValuation;
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
     * Calculates the price volatility based on 24h high/low range.
     * 
     * @return Volatility percentage, or null if data is not available
     */
    public BigDecimal getVolatility24h() {
        if (high24h != null && low24h != null && currentPrice != null && 
            currentPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal range = high24h.subtract(low24h);
            return range.divide(currentPrice, 4, BigDecimal.ROUND_HALF_UP)
                       .multiply(BigDecimal.valueOf(100));
        }
        return null;
    }

    /**
     * Checks if this cryptocurrency is currently bullish (price increasing).
     * 
     * @return true if 24h price change is positive
     */
    public boolean isBullish() {
        return priceChangePercentage24h != null && 
               priceChangePercentage24h.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Checks if this cryptocurrency has high trading volume.
     * 
     * @return true if volume/market cap ratio is above 5%
     */
    public boolean hasHighVolume() {
        if (totalVolume != null && marketCap != null && 
            marketCap.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal volumeRatio = totalVolume.divide(marketCap, 4, BigDecimal.ROUND_HALF_UP);
            return volumeRatio.compareTo(BigDecimal.valueOf(0.05)) > 0;
        }
        return false;
    }

    /**
     * Checks if the current price is near all-time high (within 10%).
     * 
     * @return true if current price is within 10% of ATH
     */
    public boolean isNearAllTimeHigh() {
        if (currentPrice != null && allTimeHigh != null && 
            allTimeHigh.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal ratio = currentPrice.divide(allTimeHigh, 4, BigDecimal.ROUND_HALF_UP);
            return ratio.compareTo(BigDecimal.valueOf(0.9)) >= 0;
        }
        return false;
    }

    /**
     * Checks if this cryptocurrency has a limited supply.
     * 
     * @return true if max supply is defined and finite
     */
    public boolean hasLimitedSupply() {
        return maxSupply != null && maxSupply.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Calculates the supply utilization percentage.
     * 
     * @return Percentage of supply in circulation, or null if data unavailable
     */
    public BigDecimal getSupplyUtilization() {
        if (circulatingSupply != null && maxSupply != null && 
            maxSupply.compareTo(BigDecimal.ZERO) > 0) {
            return circulatingSupply.divide(maxSupply, 4, BigDecimal.ROUND_HALF_UP)
                                  .multiply(BigDecimal.valueOf(100));
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("MarketData{ticker='%s', name='%s', price=%s, marketCap=%s, rank=%d}", 
                ticker, name, currentPrice, marketCap, marketCapRank);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarketData)) return false;
        MarketData that = (MarketData) o;
        return ticker != null ? ticker.equals(that.ticker) : that.ticker == null;
    }

    @Override
    public int hashCode() {
        return ticker != null ? ticker.hashCode() : 0;
    }
} 