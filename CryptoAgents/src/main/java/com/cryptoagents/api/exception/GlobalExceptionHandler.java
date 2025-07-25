package com.cryptoagents.api.exception;

import com.cryptoagents.config.RateLimitInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for centralized error processing
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_ERROR",
            "Input validation failed",
            fieldErrors.toString(),
            LocalDateTime.now()
        );
        
        logger.warn("Validation error for request {}: {}", request.getDescription(false), fieldErrors);
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle invalid ticker exceptions
     */
    @ExceptionHandler(InvalidTickerException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTickerException(
            InvalidTickerException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INVALID_TICKER",
            "Invalid cryptocurrency ticker provided",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        logger.warn("Invalid ticker error for request {}: {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle rate limit exceptions
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitException(
            RateLimitExceededException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "RATE_LIMIT_EXCEEDED",
            "Too many requests. Please try again later.",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        logger.warn("Rate limit exceeded for request {}: {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
    }
    
    /**
     * Handle API limit exceptions
     */
    @ExceptionHandler(ApiLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleApiLimitException(
            ApiLimitExceededException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "API_LIMIT_EXCEEDED",
            "External API rate limit exceeded",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        logger.warn("API limit exceeded for request {}: {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
    
    /**
     * Handle external service exceptions
     */
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceException(
            ExternalServiceException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "EXTERNAL_SERVICE_ERROR",
            "External service temporarily unavailable",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        logger.error("External service error for request {}: {}", request.getDescription(false), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
    
    /**
     * Handle analysis exceptions
     */
    @ExceptionHandler(AnalysisException.class)
    public ResponseEntity<ErrorResponse> handleAnalysisException(
            AnalysisException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "ANALYSIS_ERROR",
            "Analysis processing failed",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        logger.error("Analysis error for request {}: {}", request.getDescription(false), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Handle general exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred",
            "Please try again later or contact support if the problem persists",
            LocalDateTime.now()
        );
        
        logger.error("Unexpected error for request {}: {}", request.getDescription(false), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
} 