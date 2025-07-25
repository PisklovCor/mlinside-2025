package com.cryptoagents.service;

/**
 * Exception thrown when orchestration of agents fails.
 * 
 * This exception provides context about orchestration failures,
 * including the ticker, stage of failure, and underlying cause.
 */
public class OrchestrationException extends Exception {
    
    private final String ticker;
    private final String stage;
    private final String operationId;
    
    /**
     * Constructor with basic information
     */
    public OrchestrationException(String ticker, String stage, String message) {
        super(String.format("Orchestration failed for ticker '%s' at stage '%s': %s", ticker, stage, message));
        this.ticker = ticker;
        this.stage = stage;
        this.operationId = null;
    }
    
    /**
     * Constructor with cause
     */
    public OrchestrationException(String ticker, String stage, String message, Throwable cause) {
        super(String.format("Orchestration failed for ticker '%s' at stage '%s': %s", ticker, stage, message), cause);
        this.ticker = ticker;
        this.stage = stage;
        this.operationId = null;
    }
    
    /**
     * Constructor with operation tracking
     */
    public OrchestrationException(String ticker, String stage, String operationId, String message, Throwable cause) {
        super(String.format("Orchestration failed for ticker '%s' at stage '%s' [%s]: %s", ticker, stage, operationId, message), cause);
        this.ticker = ticker;
        this.stage = stage;
        this.operationId = operationId;
    }
    
    public String getTicker() {
        return ticker;
    }
    
    public String getStage() {
        return stage;
    }
    
    public String getOperationId() {
        return operationId;
    }
} 