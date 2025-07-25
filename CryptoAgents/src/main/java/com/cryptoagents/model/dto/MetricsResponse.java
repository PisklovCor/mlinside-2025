package com.cryptoagents.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;
import java.util.HashMap;

/**
 * DTO ответа для метрик производительности оркестратора.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricsResponse {
    
    /**
     * Общие метрики
     */
    private Long totalRequests;
    private Long successfulRequests;
    private Long failedRequests;
    private Double successRate;
    private Double failureRate;
    private Long averageExecutionTime;
    private Long uptime;
    
    /**
     * Метрики по агентам
     */
    @Builder.Default
    private Map<String, AgentMetrics> agentMetrics = new HashMap<>();
    
    /**
     * Системные метрики
     */
    private Long memoryUsage;
    private Long maxMemory;
    private Integer activeThreads;
    private Double cpuUsage;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AgentMetrics {
        private String agentName;
        private Long executionCount;
        private Long successCount;
        private Long failureCount;
        private Double successRate;
        private Long averageExecutionTime;
        private Long totalExecutionTime;
    }
} 