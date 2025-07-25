package com.cryptoagents.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

/**
 * Response DTO for orchestrator performance metrics.
 */
@Data
@Builder
@Jacksonized
public class MetricsResponse {
    
    /**
     * Overall metrics
     */
    private long totalRequests;
    private long successfulAnalyses;
    private long failedAnalyses;
    private double successRate;
    private double failureRate;
    private double averageExecutionTime;
    
    /**
     * Agent-specific metrics
     */
    private Map<String, AgentMetrics> agentMetrics;
    
    /**
     * System metrics
     */
    private long uptimeMs;
    private String lastResetTime;
    
    @Data
    @Builder
    @Jacksonized
    public static class AgentMetrics {
        private long executionCount;
        private double failureRate;
        private double averageExecutionTime;
    }
} 