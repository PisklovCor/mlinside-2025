package com.cryptoagents.service;

import com.cryptoagents.model.dto.MetricsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сборщик метрик для мониторинга производительности AgentOrchestrator.
 * 
 * Этот класс отслеживает различные метрики производительности и частоту ошибок
 * для помощи в мониторинге состояния и эффективности процесса оркестрации.
 */
public class OrchestratorMetrics {
    
    private static final Logger logger = LoggerFactory.getLogger(OrchestratorMetrics.class);
    
    // Счетчики операций
    private final AtomicLong totalAnalysisRequests = new AtomicLong(0);
    private final AtomicLong successfulAnalysis = new AtomicLong(0);
    private final AtomicLong failedAnalysis = new AtomicLong(0);
    
    // Агент-специфичные метрики
    private final Map<String, AtomicLong> agentExecutionCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> agentFailureCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> agentExecutionTimes = new ConcurrentHashMap<>();
    
    // Метрики производительности
    private final AtomicLong totalExecutionTime = new AtomicLong(0);
    private volatile long lastResetTime = System.currentTimeMillis();
    
    /**
     * Записывает начало операции анализа
     * 
     * @param ticker тикер криптовалюты, который анализируется
     */
    public void recordAnalysisStart(String ticker) {
        totalAnalysisRequests.incrementAndGet();
        logger.debug("Analysis started for ticker: {} (total requests: {})", 
                ticker, totalAnalysisRequests.get());
    }
    
    /**
     * Записывает успешное завершение анализа
     * 
     * @param ticker тикер криптовалюты
     * @param totalTime общее время выполнения в миллисекундах
     */
    public void recordAnalysisSuccess(String ticker, long totalTime) {
        successfulAnalysis.incrementAndGet();
        totalExecutionTime.addAndGet(totalTime);
        logger.info("Analysis completed successfully for ticker: {} in {}ms (success rate: {:.2f}%)", 
                ticker, totalTime, getSuccessRate());
    }
    
    /**
     * Записывает неудачный анализ
     * 
     * @param ticker тикер криптовалюты
     * @param error сообщение об ошибке
     */
    public void recordAnalysisFailure(String ticker, String error) {
        failedAnalysis.incrementAndGet();
        logger.warn("Analysis failed for ticker: {} - Error: {} (failure rate: {:.2f}%)", 
                ticker, error, getFailureRate());
    }
    
    /**
     * Записывает выполнение агента
     * 
     * @param agentName имя агента
     * @param executionTime время выполнения в миллисекундах
     * @param success было ли выполнение успешным
     */
    public void recordAgentExecution(String agentName, long executionTime, boolean success) {
        agentExecutionCounts.computeIfAbsent(agentName, k -> new AtomicLong(0)).incrementAndGet();
        agentExecutionTimes.computeIfAbsent(agentName, k -> new AtomicLong(0)).addAndGet(executionTime);
        
        if (!success) {
            agentFailureCounts.computeIfAbsent(agentName, k -> new AtomicLong(0)).incrementAndGet();
        }
        
        logger.debug("Agent {} executed in {}ms (success: {})", agentName, executionTime, success);
    }
    
    /**
     * Получает текущую частоту успеха в процентах
     */
    public double getSuccessRate() {
        long total = totalAnalysisRequests.get();
        if (total == 0) return 0.0;
        return (successfulAnalysis.get() * 100.0) / total;
    }
    
    /**
     * Получает текущую частоту неудач в процентах
     */
    public double getFailureRate() {
        long total = totalAnalysisRequests.get();
        if (total == 0) return 0.0;
        return (failedAnalysis.get() * 100.0) / total;
    }
    
    /**
     * Получает среднее время выполнения в миллисекундах
     */
    public double getAverageExecutionTime() {
        long successful = successfulAnalysis.get();
        if (successful == 0) return 0.0;
        return totalExecutionTime.get() / (double) successful;
    }
    
    /**
     * Get agent-specific failure rate
     */
    public double getAgentFailureRate(String agentName) {
        AtomicLong executions = agentExecutionCounts.get(agentName);
        AtomicLong failures = agentFailureCounts.get(agentName);
        
        if (executions == null || executions.get() == 0) return 0.0;
        if (failures == null) return 0.0;
        
        return (failures.get() * 100.0) / executions.get();
    }
    
    /**
     * Получает среднее время выполнения для конкретного агента
     */
    public double getAgentAverageExecutionTime(String agentName) {
        AtomicLong executions = agentExecutionCounts.get(agentName);
        AtomicLong totalTime = agentExecutionTimes.get(agentName);
        
        if (executions == null || executions.get() == 0) return 0.0;
        if (totalTime == null) return 0.0;
        
        return totalTime.get() / (double) executions.get();
    }
    
    /**
     * Логирует комплексную сводку метрик
     */
    public void logMetricsSummary() {
        logger.info("=== Orchestrator Metrics Summary ===");
        logger.info("Total requests: {}, Successful: {}, Failed: {}", 
                totalAnalysisRequests.get(), successfulAnalysis.get(), failedAnalysis.get());
        logger.info("Success rate: {:.2f}%, Average execution time: {:.2f}ms", 
                getSuccessRate(), getAverageExecutionTime());
        
        agentExecutionCounts.forEach((agent, count) -> {
            logger.info("Agent {}: {} executions, {:.2f}% failure rate, {:.2f}ms avg time",
                    agent, count.get(), getAgentFailureRate(agent), getAgentAverageExecutionTime(agent));
        });
        
        long uptime = System.currentTimeMillis() - lastResetTime;
        logger.info("Metrics collected over {}ms", uptime);
    }
    
    /**
     * Получает все отслеживаемые имена агентов
     */
    public java.util.Set<String> getTrackedAgents() {
        return agentExecutionCounts.keySet();
    }
    
    /**
     * Получает общее количество запросов анализа
     */
    public long getTotalAnalysisRequests() {
        return totalAnalysisRequests.get();
    }
    
    /**
     * Получает количество успешных анализов
     */
    public long getSuccessfulAnalysis() {
        return successfulAnalysis.get();
    }
    
    /**
     * Получает количество неудачных анализов
     */
    public long getFailedAnalysis() {
        return failedAnalysis.get();
    }
    
    /**
     * Получает количество выполнений для конкретного агента
     */
    public long getAgentExecutionCount(String agentName) {
        AtomicLong count = agentExecutionCounts.get(agentName);
        return count != null ? count.get() : 0;
    }
    
    /**
     * Получает время работы с момента последнего сброса в миллисекундах
     */
    public long getUptimeMs() {
        return System.currentTimeMillis() - lastResetTime;
    }
    
    /**
     * Получает время последнего сброса
     */
    public long getLastResetTime() {
        return lastResetTime;
    }
    
    /**
     * Сбрасывает все метрики
     */
    public void reset() {
        totalAnalysisRequests.set(0);
        successfulAnalysis.set(0);
        failedAnalysis.set(0);
        totalExecutionTime.set(0);
        agentExecutionCounts.clear();
        agentFailureCounts.clear();
        agentExecutionTimes.clear();
        lastResetTime = System.currentTimeMillis();
        logger.info("Orchestrator metrics reset");
    }
    
    /**
     * Convert metrics to DTO for API response
     */
    public MetricsResponse toMetricsResponse() {
        Map<String, MetricsResponse.AgentMetrics> agentMetricsMap = new HashMap<>();
        
        for (String agentName : getTrackedAgents()) {
            MetricsResponse.AgentMetrics agentMetrics = MetricsResponse.AgentMetrics.builder()
                    .executionCount(getAgentExecutionCount(agentName))
                    .failureRate(getAgentFailureRate(agentName))
                    .averageExecutionTime(getAgentAverageExecutionTime(agentName))
                    .build();
            agentMetricsMap.put(agentName, agentMetrics);
        }
        
        return MetricsResponse.builder()
                .totalRequests(getTotalAnalysisRequests())
                .successfulAnalyses(getSuccessfulAnalysis())
                .failedAnalyses(getFailedAnalysis())
                .successRate(getSuccessRate())
                .failureRate(getFailureRate())
                .averageExecutionTime(getAverageExecutionTime())
                .agentMetrics(agentMetricsMap)
                .uptimeMs(getUptimeMs())
                .lastResetTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new java.util.Date(getLastResetTime())))
                .build();
    }
} 