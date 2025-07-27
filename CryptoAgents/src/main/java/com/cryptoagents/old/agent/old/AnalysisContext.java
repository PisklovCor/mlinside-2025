package com.cryptoagents.old.agent.old;

import com.cryptoagents.old.model.dto.MarketData;
import com.cryptoagents.old.model.AnalysisResult;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.HashMap;

/**
 * Context class for sharing data and results between agents during analysis.
 * <p>
 * This class serves as a container for all data that needs to be passed
 * between agents in the analysis pipeline (Analyst → Risk Manager → Trader).
 */
@Data
@NoArgsConstructor
public class AnalysisContext {

    // Core data
    private String ticker;
    private MarketData marketData;

    // Agent results
    private final Map<String, AnalysisResult> agentResults = new HashMap<>();

} 