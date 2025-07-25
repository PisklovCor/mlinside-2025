package com.cryptoagents.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for cryptocurrency analysis requests
 */
public class AnalysisRequest {
    
    @NotBlank(message = "Ticker is required")
    @Size(min = 1, max = 10, message = "Ticker must be between 1 and 10 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Ticker must contain only alphanumeric characters")
    @JsonProperty("ticker")
    private String ticker;
    
    @JsonProperty("timeframe")
    private String timeframe = "24h"; // Default timeframe
    
    @JsonProperty("include_metrics")
    private boolean includeMetrics = true;
    
    public AnalysisRequest() {}
    
    public AnalysisRequest(String ticker) {
        this.ticker = ticker;
    }
    
    public AnalysisRequest(String ticker, String timeframe, boolean includeMetrics) {
        this.ticker = ticker;
        this.timeframe = timeframe;
        this.includeMetrics = includeMetrics;
    }
    
    // Getters and Setters
    public String getTicker() {
        return ticker;
    }
    
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
    
    public String getTimeframe() {
        return timeframe;
    }
    
    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }
    
    public boolean isIncludeMetrics() {
        return includeMetrics;
    }
    
    public void setIncludeMetrics(boolean includeMetrics) {
        this.includeMetrics = includeMetrics;
    }
    
    @Override
    public String toString() {
        return "AnalysisRequest{" +
                "ticker='" + ticker + '\'' +
                ", timeframe='" + timeframe + '\'' +
                ", includeMetrics=" + includeMetrics +
                '}';
    }
} 