package com.cryptoagents.model.dto;

import com.cryptoagents.model.enums.TimePeriod;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoricalData {

    private String ticker;
    private TimePeriod period;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<PricePoint> pricePoints;
    private LocalDateTime retrievedAt;
    
    // Statistical data
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal averagePrice;
    private BigDecimal startPrice;
    private BigDecimal endPrice;
    private BigDecimal totalChange;
    private BigDecimal totalChangePercentage;

    // Default constructor
    public HistoricalData() {
        this.pricePoints = new ArrayList<>();
        this.retrievedAt = LocalDateTime.now();
    }

    // Constructor with basic fields
    public HistoricalData(String ticker, TimePeriod period) {
        this();
        this.ticker = ticker;
        this.period = period;
    }

    /**
     * Inner class representing a single price point in time.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PricePoint {
        private LocalDateTime timestamp;
        private BigDecimal price;
        private BigDecimal volume;

        public PricePoint() {}

        public PricePoint(LocalDateTime timestamp, BigDecimal price) {
            this.timestamp = timestamp;
            this.price = price;
        }

        public PricePoint(LocalDateTime timestamp, BigDecimal price, BigDecimal volume) {
            this.timestamp = timestamp;
            this.price = price;
            this.volume = volume;
        }

        // Getters and Setters
        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public BigDecimal getVolume() {
            return volume;
        }

        public void setVolume(BigDecimal volume) {
            this.volume = volume;
        }

        @Override
        public String toString() {
            return String.format("PricePoint{timestamp=%s, price=%s, volume=%s}", 
                    timestamp, price, volume);
        }
    }

    // Getters and Setters

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public TimePeriod getPeriod() {
        return period;
    }

    public void setPeriod(TimePeriod period) {
        this.period = period;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public List<PricePoint> getPricePoints() {
        return pricePoints;
    }

    public void setPricePoints(List<PricePoint> pricePoints) {
        this.pricePoints = pricePoints;
        calculateStatistics();
    }

    public LocalDateTime getRetrievedAt() {
        return retrievedAt;
    }

    public void setRetrievedAt(LocalDateTime retrievedAt) {
        this.retrievedAt = retrievedAt;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public BigDecimal getStartPrice() {
        return startPrice;
    }

    public BigDecimal getEndPrice() {
        return endPrice;
    }

    public BigDecimal getTotalChange() {
        return totalChange;
    }

    public BigDecimal getTotalChangePercentage() {
        return totalChangePercentage;
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

        this.minPrice = min;
        this.maxPrice = max;
        this.averagePrice = sum.divide(BigDecimal.valueOf(pricePoints.size()), 8, BigDecimal.ROUND_HALF_UP);

        if (!pricePoints.isEmpty()) {
            this.startPrice = pricePoints.get(0).getPrice();
            this.endPrice = pricePoints.get(pricePoints.size() - 1).getPrice();
            
            if (startPrice != null && endPrice != null && startPrice.compareTo(BigDecimal.ZERO) > 0) {
                this.totalChange = endPrice.subtract(startPrice);
                this.totalChangePercentage = totalChange
                        .divide(startPrice, 8, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
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
        return String.format("HistoricalData{ticker='%s', period=%s, dataPoints=%d, change=%s%%}", 
                ticker, period, getDataPointsCount(), totalChangePercentage);
    }
} 