package com.cryptoagents.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Metrics collector for AgentOrchestrator performance monitoring.
 * 
 * This class tracks various performance metrics and error rates
 * to help monitor the health and efficiency of the orchestration process.
 */
public class OrchestratorMetrics {
    
    private static final Logger logger = LoggerFactory.getLogger(OrchestratorMetrics.class);
    
    // Operation counters
    private final AtomicLong totalAnalysisRequests = new AtomicLong(0);
    private final AtomicLong successfulAnalysis = new AtomicLong(0);
    private final AtomicLong failedAnalysis = new AtomicLong(0);
    
    // Agent-specific metrics
    private final Map<String, AtomicLong> agentExecutionCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> agentFailureCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> agentExecutionTimes = new ConcurrentHashMap<>();
    
    // Performance metrics
    private final AtomicLong totalExecutionTime = new AtomicLong(0);
    private volatile long lastResetTime = System.currentTimeMillis();
    
    /**
     * Record the start of an analysis operation
     * 
     * @param ticker the cryptocurrency ticker being analyzed
     */
    public void recordAnalysisStart(String ticker) {
        totalAnalysisRequests.incrementAndGet();
        logger.debug("Analysis started for ticker: {} (total requests: {})", 
                ticker, totalAnalysisRequests.get());
    }
    
    /**
     * Record successful completion of analysis
     * 
     * @param ticker the cryptocurrency ticker
     * @param totalTime total execution time in milliseconds
     */
    public void recordAnalysisSuccess(String ticker, long totalTime) {
        successfulAnalysis.incrementAndGet();
        totalExecutionTime.addAndGet(totalTime);
        logger.info("Analysis completed successfully for ticker: {} in {}ms (success rate: {:.2f}%)", 
                ticker, totalTime, getSuccessRate());
    }
    
    /**
     * Record failed analysis
     * 
     * @param ticker the cryptocurrency ticker
     * @param error the error message
     */
    public void recordAnalysisFailure(String ticker, String error) {
        failedAnalysis.incrementAndGet();
        logger.warn("Analysis failed for ticker: {} - Error: {} (failure rate: {:.2f}%)", 
                ticker, error, getFailureRate());
    }
    
    /**
     * Record agent execution
     * 
     * @param agentName the name of the agent
     * @param executionTime execution time in milliseconds
     * @param success whether the execution was successful
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
     * Get current success rate as percentage
     */
    public double getSuccessRate() {
        long total = totalAnalysisRequests.get();
        if (total == 0) return 0.0;
        return (successfulAnalysis.get() * 100.0) / total;
    }
    
    /**
     * Get current failure rate as percentage
     */
    public double getFailureRate() {
        long total = totalAnalysisRequests.get();
        if (total == 0) return 0.0;
        return (failedAnalysis.get() * 100.0) / total;
    }
    
    /**
     * Get average execution time in milliseconds
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
     * Get agent-specific average execution time
     */
    public double getAgentAverageExecutionTime(String agentName) {
        AtomicLong executions = agentExecutionCounts.get(agentName);
        AtomicLong totalTime = agentExecutionTimes.get(agentName);
        
        if (executions == null || executions.get() == 0) return 0.0;
        if (totalTime == null) return 0.0;
        
        return totalTime.get() / (double) executions.get();
    }
    
    /**
     * Log comprehensive metrics summary
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
     * Get all tracked agent names
     */
    public java.util.Set<String> getTrackedAgents() {
        return agentExecutionCounts.keySet();
    }
    
    /**
     * Get total analysis requests count
     */
    public long getTotalAnalysisRequests() {
        return totalAnalysisRequests.get();
    }
    
    /**
     * Get successful analysis count
     */
    public long getSuccessfulAnalysis() {
        return successfulAnalysis.get();
    }
    
    /**
     * Get failed analysis count
     */
    public long getFailedAnalysis() {
        return failedAnalysis.get();
    }
    
    /**
     * Get agent execution count for specific agent
     */
    public long getAgentExecutionCount(String agentName) {
        AtomicLong count = agentExecutionCounts.get(agentName);
        return count != null ? count.get() : 0;
    }
    
    /**
     * Get uptime since last reset in milliseconds
     */
    public long getUptimeMs() {
        return System.currentTimeMillis() - lastResetTime;
    }
    
    /**
     * Get last reset time
     */
    public long getLastResetTime() {
        return lastResetTime;
    }
    
    /**
     * Reset all metrics
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
} 