package com.cryptoagents.old.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for cryptocurrency analysis requests
 */
@Data
public class AnalysisRequest {
    
    @NotBlank(message = "Ticker is required")
    @Size(min = 1, max = 10, message = "Ticker must be between 1 and 10 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Ticker must contain only alphanumeric characters")
    @JsonProperty("ticker")
    private String ticker;
    
    @JsonProperty("timeframe")
    private String timeframe = "24h"; // Default timeframe

} 