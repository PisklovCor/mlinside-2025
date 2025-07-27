package com.cryptoagents.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object для ответов с метриками системы.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsResponse {
    
    private long totalAnalysisRequests;
    private long successfulAnalysisRequests;
    private long failedAnalysisRequests;
    private double successRate;
    private long averageExecutionTime;
    private long totalExecutionTime;
    private long lastResetTime;
} 