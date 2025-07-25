package com.cryptoagents.model.dto;

import com.cryptoagents.model.enums.TimePeriod;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object representing historical cryptocurrency price data.
 * 
 * This class contains time-series data for a cryptocurrency over a specified period,
 * including price points, timestamps, and statistical information.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoricalData {

    private String ticker;
    private TimePeriod period;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<PricePoint> pricePoints = new ArrayList<>();
    private LocalDateTime retrievedAt = LocalDateTime.now();
    
    // Statistical data (calculated, no setters)
    @Setter(lombok.AccessLevel.NONE)
    private BigDecimal minPrice;
    @Setter(lombok.AccessLevel.NONE)
    private BigDecimal maxPrice;
    @Setter(lombok.AccessLevel.NONE)
    private BigDecimal averagePrice;
    @Setter(lombok.AccessLevel.NONE)
    private BigDecimal startPrice;
    @Setter(lombok.AccessLevel.NONE)
    private BigDecimal endPrice;
    @Setter(lombok.AccessLevel.NONE)
    private BigDecimal totalChange;
    @Setter(lombok.AccessLevel.NONE)
    private BigDecimal totalChangePercentage;

    // Constructor with basic fields
    public HistoricalData(String ticker, TimePeriod period) {
        this.ticker = ticker;
        this.period = period;
        this.pricePoints = new ArrayList<>();
        this.retrievedAt = LocalDateTime.now();
    }

    /**
     * Inner class representing a single price point in time.
     */
    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PricePoint {
        private LocalDateTime timestamp;
        private BigDecimal price;
        private BigDecimal volume;

        public PricePoint(LocalDateTime timestamp, BigDecimal price) {
            this.timestamp = timestamp;
            this.price = price;
        }

        public PricePoint(LocalDateTime timestamp, BigDecimal price, BigDecimal volume) {
            this.timestamp = timestamp;
            this.price = price;
            this.volume = volume;
        }
    }

    // Custom setter for pricePoints to trigger statistics calculation
    public void setPricePoints(List<PricePoint> pricePoints) {
        this.pricePoints = pricePoints;
        calculateStatistics();
    }

    // Utility methods

    /**
     * Adds a price point to the historical data and recalculates statistics.
     * 
     * @param pricePoint The price point to add
     */
    public void addPricePoint(PricePoint pricePoint) {
        if (pricePoints == null) {
            pricePoints = new ArrayList<>();
        }
        pricePoints.add(pricePoint);
        calculateStatistics();
    }

    /**
     * Calculates statistical data from the price points.
     */
    private void calculateStatistics() {
        if (pricePoints == null || pricePoints.isEmpty()) {
            return;
        }

        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal min = null;
        BigDecimal max = null;

        for (PricePoint point : pricePoints) {
            if (point.getPrice() != null) {
                sum = sum.add(point.getPrice());
                
                if (min == null || point.getPrice().compareTo(min) < 0) {
                    min = point.getPrice();
                }
                
                if (max == null || point.getPrice().compareTo(max) > 0) {
                    max = point.getPrice();
                }
            }
        }

        // Normalize all values to same scale for consistency
        this.minPrice = min != null ? min.setScale(2, BigDecimal.ROUND_HALF_UP) : null;
        this.maxPrice = max != null ? max.setScale(2, BigDecimal.ROUND_HALF_UP) : null;
        this.averagePrice = sum.divide(BigDecimal.valueOf(pricePoints.size()), 2, BigDecimal.ROUND_HALF_UP);

        if (!pricePoints.isEmpty()) {
            this.startPrice = pricePoints.get(0).getPrice() != null ? 
                pricePoints.get(0).getPrice().setScale(2, BigDecimal.ROUND_HALF_UP) : null;
            this.endPrice = pricePoints.get(pricePoints.size() - 1).getPrice() != null ?
                pricePoints.get(pricePoints.size() - 1).getPrice().setScale(2, BigDecimal.ROUND_HALF_UP) : null;
            
            if (startPrice != null && endPrice != null && startPrice.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal change = endPrice.subtract(startPrice);
                this.totalChange = change.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : change.setScale(2, BigDecimal.ROUND_HALF_UP);
                
                BigDecimal percentage = change.divide(startPrice, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
                this.totalChangePercentage = percentage.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : percentage;
            }
        }
    }

    /**
     * Returns the number of data points in this historical data.
     * 
     * @return Number of price points
     */
    public int getDataPointsCount() {
        return pricePoints != null ? pricePoints.size() : 0;
    }

    /**
     * Checks if this historical data contains valid price information.
     * 
     * @return true if there are price points with valid data
     */
    public boolean hasValidData() {
        return pricePoints != null && !pricePoints.isEmpty() && 
               pricePoints.stream().anyMatch(p -> p.getPrice() != null);
    }

    /**
     * Returns true if the price has increased over the period.
     * 
     * @return true if end price is higher than start price
     */
    public boolean isPriceIncreasing() {
        return totalChangePercentage != null && totalChangePercentage.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String toString() {
        return String.format("HistoricalData{ticker='%s', period=%s, dataPoints=%d, minPrice=%s, maxPrice=%s}",
                ticker, period != null ? period.name() : null, getDataPointsCount(), minPrice, maxPrice);
    }
} 