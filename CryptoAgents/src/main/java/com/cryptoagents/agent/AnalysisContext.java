package com.cryptoagents.agent;

import com.cryptoagents.model.dto.MarketData;
import com.cryptoagents.model.dto.HistoricalData;
import com.cryptoagents.model.AnalysisResult;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Context class for sharing data and results between agents during analysis.
 * 
 * This class serves as a container for all data that needs to be passed
 * between agents in the analysis pipeline (Analyst → Risk Manager → Trader).
 */
@Data
@NoArgsConstructor
public class AnalysisContext {
    
    // Core data
    private String ticker;
    private MarketData marketData;
    private List<HistoricalData> historicalData = new ArrayList<>();
    
    // Agent results
    private final Map<String, AnalysisResult> agentResults = new HashMap<>();
    
    // Additional context data
    private final Map<String, Object> additionalData = new HashMap<>();
    
    // Analysis metadata
    private long startTime;
    private String requestId;
    
    /**
     * Constructor with ticker
     */
    public AnalysisContext(String ticker) {
        this.ticker = ticker;
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * Add an agent's analysis result to the context
     */
    public void addAgentResult(String agentName, AnalysisResult result) {
        agentResults.put(agentName, result);
    }
    
    /**
     * Get analysis result from a specific agent
     */
    public <T extends AnalysisResult> T getAgentResult(String agentName, Class<T> resultType) {
        AnalysisResult result = agentResults.get(agentName);
        if (result != null && resultType.isInstance(result)) {
            return resultType.cast(result);
        }
        return null;
    }
    
    /**
     * Add additional data to the context
     */
    public void put(String key, Object value) {
        additionalData.put(key, value);
    }
    
    /**
     * Get additional data from the context
     */
    public <T> T get(String key, Class<T> type) {
        Object value = additionalData.get(key);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }
    
    /**
     * Check if specific agent result exists
     */
    public boolean hasAgentResult(String agentName) {
        return agentResults.containsKey(agentName);
    }
    
    /**
     * Get elapsed analysis time in milliseconds
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }
} 