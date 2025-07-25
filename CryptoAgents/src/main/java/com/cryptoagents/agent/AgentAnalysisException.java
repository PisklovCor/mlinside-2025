package com.cryptoagents.agent;

/**
 * Exception thrown when an agent fails to perform analysis.
 * 
 * This exception provides detailed information about analysis failures
 * including the agent type, ticker, and specific error details.
 */
public class AgentAnalysisException extends Exception {
    
    private final String agentName;
    private final String ticker;
    private final String errorCode;
    
    /**
     * Constructor with message only
     */
    public AgentAnalysisException(String message) {
        super(message);
        this.agentName = null;
        this.ticker = null;
        this.errorCode = null;
    }
    
    /**
     * Constructor with message and cause
     */
    public AgentAnalysisException(String message, Throwable cause) {
        super(message, cause);
        this.agentName = null;
        this.ticker = null;
        this.errorCode = null;
    }
    
    /**
     * Constructor with detailed information
     */
    public AgentAnalysisException(String agentName, String ticker, String message) {
        super(String.format("Agent '%s' failed to analyze ticker '%s': %s", agentName, ticker, message));
        this.agentName = agentName;
        this.ticker = ticker;
        this.errorCode = null;
    }
    
    /**
     * Constructor with detailed information and cause
     */
    public AgentAnalysisException(String agentName, String ticker, String message, Throwable cause) {
        super(String.format("Agent '%s' failed to analyze ticker '%s': %s", agentName, ticker, message), cause);
        this.agentName = agentName;
        this.ticker = ticker;
        this.errorCode = null;
    }
    
    /**
     * Constructor with error code
     */
    public AgentAnalysisException(String agentName, String ticker, String errorCode, String message, Throwable cause) {
        super(String.format("Agent '%s' failed to analyze ticker '%s' [%s]: %s", agentName, ticker, errorCode, message), cause);
        this.agentName = agentName;
        this.ticker = ticker;
        this.errorCode = errorCode;
    }
    
    public String getAgentName() {
        return agentName;
    }
    
    public String getTicker() {
        return ticker;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
} 