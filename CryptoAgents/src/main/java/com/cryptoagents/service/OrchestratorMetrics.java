package com.cryptoagents.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;

/**
 * Компонент для сбора и отслеживания метрик производительности оркестратора агентов.
 * 
 * Этот класс предоставляет методы для записи времени выполнения, количества запросов,
 * ошибок и других метрик, связанных с анализом криптовалют.
 */
@Slf4j
@Component
public class OrchestratorMetrics {
    
    // Счетчики запросов
    private final AtomicLong totalAnalysisRequests = new AtomicLong(0);
    private final AtomicLong successfulAnalysisRequests = new AtomicLong(0);
    private final AtomicLong failedAnalysisRequests = new AtomicLong(0);
    
    // Время выполнения
    private final AtomicLong totalExecutionTime = new AtomicLong(0);
    private final AtomicLong averageExecutionTime = new AtomicLong(0);
    
    // Метрики по агентам
    private final Map<String, AtomicLong> agentExecutionCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> agentExecutionTimes = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> agentErrorCounts = new ConcurrentHashMap<>();
    
    // Метрики по тикерам
    private final Map<String, AtomicLong> tickerAnalysisCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> tickerExecutionTimes = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> tickerErrorCounts = new ConcurrentHashMap<>();
    
    /**
     * Записывает начало анализа для указанного тикера.
     * 
     * @param ticker тикер криптовалюты, который анализируется
     */
    public void recordAnalysisStart(String ticker) {
        totalAnalysisRequests.incrementAndGet();
        tickerAnalysisCounts.computeIfAbsent(ticker, k -> new AtomicLong(0)).incrementAndGet();
        log.debug("Начало анализа для тикера: {}", ticker);
    }
    
    /**
     * Записывает успешное завершение анализа.
     * 
     * @param ticker тикер криптовалюты
     * @param totalTime общее время выполнения в миллисекундах
     */
    public void recordAnalysisSuccess(String ticker, long totalTime) {
        successfulAnalysisRequests.incrementAndGet();
        totalExecutionTime.addAndGet(totalTime);
        tickerExecutionTimes.computeIfAbsent(ticker, k -> new AtomicLong(0)).addAndGet(totalTime);
        
        // Обновляем среднее время выполнения
        long totalRequests = totalAnalysisRequests.get();
        long totalTimeSum = totalExecutionTime.get();
        averageExecutionTime.set(totalTimeSum / totalRequests);
        
        log.debug("Успешный анализ для тикера {} завершен за {} мс", ticker, totalTime);
    }
    
    /**
     * Записывает неудачное завершение анализа.
     * 
     * @param ticker тикер криптовалюты
     * @param error сообщение об ошибке
     */
    public void recordAnalysisFailure(String ticker, String error) {
        failedAnalysisRequests.incrementAndGet();
        tickerErrorCounts.computeIfAbsent(ticker, k -> new AtomicLong(0)).incrementAndGet();
        log.warn("Неудачный анализ для тикера {}: {}", ticker, error);
    }
    
    /**
     * Записывает выполнение агента.
     * 
     * @param agentName имя агента
     * @param executionTime время выполнения в миллисекундах
     * @param success было ли выполнение успешным
     */
    public void recordAgentExecution(String agentName, long executionTime, boolean success) {
        agentExecutionCounts.computeIfAbsent(agentName, k -> new AtomicLong(0)).incrementAndGet();
        agentExecutionTimes.computeIfAbsent(agentName, k -> new AtomicLong(0)).addAndGet(executionTime);
        
        if (!success) {
            agentErrorCounts.computeIfAbsent(agentName, k -> new AtomicLong(0)).incrementAndGet();
        }
        
        log.debug("Выполнение агента {}: {} мс, успех: {}", agentName, executionTime, success);
    }
    
    /**
     * Получает общее количество запросов анализа.
     * 
     * @return общее количество запросов
     */
    public long getTotalAnalysisRequests() {
        return totalAnalysisRequests.get();
    }
    
    /**
     * Получает количество успешных запросов анализа.
     * 
     * @return количество успешных запросов
     */
    public long getSuccessfulAnalysisRequests() {
        return successfulAnalysisRequests.get();
    }
    
    /**
     * Получает количество неудачных запросов анализа.
     * 
     * @return количество неудачных запросов
     */
    public long getFailedAnalysisRequests() {
        return failedAnalysisRequests.get();
    }
    
    /**
     * Получает среднее время выполнения анализа.
     * 
     * @return среднее время выполнения в миллисекундах
     */
    public long getAverageExecutionTime() {
        return averageExecutionTime.get();
    }
    
    /**
     * Получает общее время выполнения всех анализов.
     * 
     * @return общее время выполнения в миллисекундах
     */
    public long getTotalExecutionTime() {
        return totalExecutionTime.get();
    }
    
    /**
     * Получает количество выполненных анализов для указанного тикера.
     * 
     * @param ticker тикер криптовалюты
     * @return количество анализов
     */
    public long getTickerAnalysisCount(String ticker) {
        return tickerAnalysisCounts.getOrDefault(ticker, new AtomicLong(0)).get();
    }
    
    /**
     * Получает общее время выполнения анализов для указанного тикера.
     * 
     * @param ticker тикер криптовалюты
     * @return общее время выполнения в миллисекундах
     */
    public long getTickerExecutionTime(String ticker) {
        return tickerExecutionTimes.getOrDefault(ticker, new AtomicLong(0)).get();
    }
    
    /**
     * Получает количество ошибок для указанного тикера.
     * 
     * @param ticker тикер криптовалюты
     * @return количество ошибок
     */
    public long getTickerErrorCount(String ticker) {
        return tickerErrorCounts.getOrDefault(ticker, new AtomicLong(0)).get();
    }
    
    /**
     * Получает количество выполненных агентов указанного типа.
     * 
     * @param agentName имя агента
     * @return количество выполнений
     */
    public long getAgentExecutionCount(String agentName) {
        return agentExecutionCounts.getOrDefault(agentName, new AtomicLong(0)).get();
    }
    
    /**
     * Получает общее время выполнения агента указанного типа.
     * 
     * @param agentName имя агента
     * @return общее время выполнения в миллисекундах
     */
    public long getAgentExecutionTime(String agentName) {
        return agentExecutionTimes.getOrDefault(agentName, new AtomicLong(0)).get();
    }
    
    /**
     * Получает количество ошибок агента указанного типа.
     * 
     * @param agentName имя агента
     * @return количество ошибок
     */
    public long getAgentErrorCount(String agentName) {
        return agentErrorCounts.getOrDefault(agentName, new AtomicLong(0)).get();
    }
    
    /**
     * Получает процент успешных запросов.
     * 
     * @return процент успешных запросов (0.0 - 100.0)
     */
    public double getSuccessRate() {
        long total = totalAnalysisRequests.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) successfulAnalysisRequests.get() / total * 100.0;
    }
    
    /**
     * Получает полную статистику метрик.
     * 
     * @return карта со всеми метриками
     */
    public Map<String, Object> getAllMetrics() {
        Map<String, Object> metrics = new ConcurrentHashMap<>();
        
        // Общие метрики
        metrics.put("totalAnalysisRequests", getTotalAnalysisRequests());
        metrics.put("successfulAnalysisRequests", getSuccessfulAnalysisRequests());
        metrics.put("failedAnalysisRequests", getFailedAnalysisRequests());
        metrics.put("successRate", getSuccessRate());
        metrics.put("averageExecutionTime", getAverageExecutionTime());
        metrics.put("totalExecutionTime", getTotalExecutionTime());
        
        // Метрики по агентам
        Map<String, Object> agentMetrics = new ConcurrentHashMap<>();
        for (String agentName : agentExecutionCounts.keySet()) {
            Map<String, Object> agentStats = new ConcurrentHashMap<>();
            agentStats.put("executionCount", getAgentExecutionCount(agentName));
            agentStats.put("executionTime", getAgentExecutionTime(agentName));
            agentStats.put("errorCount", getAgentErrorCount(agentName));
            agentMetrics.put(agentName, agentStats);
        }
        metrics.put("agentMetrics", agentMetrics);
        
        // Метрики по тикерам
        Map<String, Object> tickerMetrics = new ConcurrentHashMap<>();
        for (String ticker : tickerAnalysisCounts.keySet()) {
            Map<String, Object> tickerStats = new ConcurrentHashMap<>();
            tickerStats.put("analysisCount", getTickerAnalysisCount(ticker));
            tickerStats.put("executionTime", getTickerExecutionTime(ticker));
            tickerStats.put("errorCount", getTickerErrorCount(ticker));
            tickerMetrics.put(ticker, tickerStats);
        }
        metrics.put("tickerMetrics", tickerMetrics);
        
        return metrics;
    }
} 