package com.cryptoagents.old.api.exception;

/**
 * Exception thrown when analysis processing fails
 */
public class AnalysisException extends RuntimeException {
    
    public AnalysisException(String message) {
        super(message);
    }
    
    public AnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
} 