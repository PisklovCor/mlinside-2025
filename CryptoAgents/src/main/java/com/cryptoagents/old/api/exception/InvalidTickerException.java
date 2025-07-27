package com.cryptoagents.old.api.exception;

/**
 * Exception thrown when an invalid ticker is provided
 */
public class InvalidTickerException extends RuntimeException {
    
    public InvalidTickerException(String message) {
        super(message);
    }
    
    public InvalidTickerException(String message, Throwable cause) {
        super(message, cause);
    }
} 