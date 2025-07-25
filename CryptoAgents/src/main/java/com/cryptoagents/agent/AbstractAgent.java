package com.cryptoagents.agent;

import com.cryptoagents.model.AnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Abstract base class for all cryptocurrency analysis agents.
 * 
 * Provides common functionality and validation logic that all agents need,
 * including logging, timing, and basic validation.
 */
public abstract class AbstractAgent implements Agent {
    
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * Template method for analysis with common pre/post processing
     */
    @Override
    public final AnalysisResult analyze(AnalysisContext context) throws AgentAnalysisException {
        logger.info("Starting analysis for agent '{}' with ticker '{}'", getName(), context.getTicker());
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Validate context
            validateContext(context);
            
            // Perform agent-specific analysis
            AnalysisResult result = performAnalysis(context);
            
            // Set common result fields
            setupCommonResultFields(result, context, startTime);
            
            logger.info("Completed analysis for agent '{}' with ticker '{}' in {}ms", 
                       getName(), context.getTicker(), result.getProcessingTimeMs());
            
            return result;
            
        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            logger.error("Analysis failed for agent '{}' with ticker '{}' after {}ms", 
                        getName(), context.getTicker(), processingTime, e);
            
            if (e instanceof AgentAnalysisException) {
                throw e;
            } else {
                throw new AgentAnalysisException(getName(), context.getTicker(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * Agent-specific analysis implementation to be overridden by concrete agents
     */
    protected abstract AnalysisResult performAnalysis(AnalysisContext context) throws AgentAnalysisException;
    
    /**
     * Basic context validation
     */
    protected void validateContext(AnalysisContext context) throws AgentAnalysisException {
        if (context == null) {
            throw new AgentAnalysisException(getName(), "unknown", "Analysis context cannot be null");
        }
        
        if (!StringUtils.hasText(context.getTicker())) {
            throw new AgentAnalysisException(getName(), context.getTicker(), "Ticker cannot be null or empty");
        }
        
        if (context.getMarketData() == null) {
            throw new AgentAnalysisException(getName(), context.getTicker(), "Market data is required for analysis");
        }
        
        // Allow subclasses to add additional validation
        performAdditionalValidation(context);
    }
    
    /**
     * Additional validation hook for subclasses
     */
    protected void performAdditionalValidation(AnalysisContext context) throws AgentAnalysisException {
        // Default implementation does nothing
    }
    
    /**
     * Default implementation of canAnalyze based on context validation
     */
    @Override
    public boolean canAnalyze(AnalysisContext context) {
        try {
            validateContext(context);
            return true;
        } catch (AgentAnalysisException e) {
            logger.debug("Agent '{}' cannot analyze context: {}", getName(), e.getMessage());
            return false;
        }
    }
    
    /**
     * Set up common fields in the analysis result
     */
    private void setupCommonResultFields(AnalysisResult result, AnalysisContext context, long startTime) {
        if (result != null) {
            result.setAgentName(getName());
            result.setTicker(context.getTicker());
            result.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            result.setStatus(AnalysisResult.AnalysisStatus.COMPLETED);
        }
    }
    
    /**
     * Utility method to create analysis failure result
     */
    protected AnalysisResult createFailureResult(AnalysisContext context, String errorMessage, long startTime) {
        // This would need to be implemented by concrete agents since AnalysisResult is abstract
        // Each agent would create their specific result type
        throw new UnsupportedOperationException("Concrete agents must implement failure result creation");
    }
} 