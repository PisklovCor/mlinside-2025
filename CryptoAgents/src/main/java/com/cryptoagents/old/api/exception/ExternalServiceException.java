package com.cryptoagents.old.api.exception;

/**
 * Exception thrown when external service is unavailable or returns an error
 */
public class ExternalServiceException extends RuntimeException {
    
    public ExternalServiceException(String message) {
        super(message);
    }
    
    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
} 