package com.cryptoagents.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing comprehensive market data for a cryptocurrency.
 * 
 * This class contains detailed market information including prices, volumes,
 * market capitalization, and various trading metrics.
 */
@Data
@NoArgsConstructor
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

    private LocalDateTime retrievedAt = LocalDateTime.now();

    // Constructor with basic fields
    public MarketData(String ticker, String name, BigDecimal currentPrice) {
        this.ticker = ticker;
        this.name = name;
        this.currentPrice = currentPrice;
        this.retrievedAt = LocalDateTime.now();
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
} 