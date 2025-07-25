package com.cryptoagents.agent;

import com.cryptoagents.model.AnalysisResult;

/**
 * Base interface for all cryptocurrency analysis agents.
 * 
 * Each agent implements specific analysis capabilities:
 * - Analyst: Technical analysis
 * - Risk Manager: Risk assessment
 * - Trader: Trading recommendations
 * 
 * Note: Implementations should use SLF4J logger:
 * private static final Logger logger = LoggerFactory.getLogger(AgentImplementation.class);
 */
public interface Agent {
    
    /**
     * Get the unique name of this agent
     * @return agent name (e.g., "ANALYST", "RISK_MANAGER", "TRADER")
     */
    String getName();
    
    /**
     * Get the agent type for classification
     * @return agent type identifier
     */
    AgentType getType();
    
    /**
     * Perform analysis for a given cryptocurrency ticker
     * 
     * @param context the analysis context containing market data and previous agent results
     * @return analysis result specific to this agent type
     * @throws AgentAnalysisException if analysis fails
     */
    AnalysisResult analyze(AnalysisContext context) throws AgentAnalysisException;
    
    /**
     * Check if the agent can perform analysis with the given context
     * 
     * @param context the analysis context to validate
     * @return true if analysis can be performed, false otherwise
     */
    boolean canAnalyze(AnalysisContext context);
    
    /**
     * Get the priority order for this agent in the analysis pipeline
     * Lower numbers indicate higher priority (executed first)
     * 
     * @return priority order (Analyst=1, Risk Manager=2, Trader=3)
     */
    int getPriority();
    
    /**
     * Enumeration for agent types
     */
    enum AgentType {
        ANALYST("Technical Analysis Agent"),
        RISK_MANAGER("Risk Assessment Agent"), 
        TRADER("Trading Recommendation Agent");
        
        private final String description;
        
        AgentType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
} 