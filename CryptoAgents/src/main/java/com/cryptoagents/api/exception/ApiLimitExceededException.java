package com.cryptoagents.api.exception;

/**
 * Exception thrown when external API rate limit is exceeded
 */
public class ApiLimitExceededException extends RuntimeException {
    
    public ApiLimitExceededException(String message) {
        super(message);
    }
    
    public ApiLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
} 