package com.cryptoagents.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Utility class for input validation and sanitization
 */
@Component
public class InputValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(InputValidator.class);
    
    // Pattern for valid ticker symbols (alphanumeric, 1-10 characters)
    private static final Pattern TICKER_PATTERN = Pattern.compile("^[A-Za-z0-9]{1,10}$");
    
    // Pattern for valid timeframes
    private static final Pattern TIMEFRAME_PATTERN = Pattern.compile("^(1h|4h|24h|7d|30d)$");
    
    private final InputSanitizer inputSanitizer;
    
    public InputValidator(InputSanitizer inputSanitizer) {
        this.inputSanitizer = inputSanitizer;
    }
    
    /**
     * Validate and sanitize ticker symbol
     */
    public String validateTicker(String ticker) {
        try {
            return inputSanitizer.sanitizeTicker(ticker);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid ticker format: {}", ticker);
            throw e;
        }
    }
    
    /**
     * Validate timeframe parameter
     */
    public String validateTimeframe(String timeframe) {
        if (timeframe == null || timeframe.trim().isEmpty()) {
            return "24h"; // Default timeframe
        }
        
        String sanitizedTimeframe = timeframe.trim().toLowerCase();
        
        if (!TIMEFRAME_PATTERN.matcher(sanitizedTimeframe).matches()) {
            logger.warn("Invalid timeframe: {}, using default", timeframe);
            return "24h"; // Default to 24h if invalid
        }
        
        logger.debug("Timeframe validated: {} -> {}", timeframe, sanitizedTimeframe);
        return sanitizedTimeframe;
    }
    
    /**
     * Sanitize string input to prevent injection attacks
     */
    public String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        
        String sanitized = inputSanitizer.removeDangerousChars(input);
        
        // Limit length
        if (sanitized.length() > 1000) {
            sanitized = sanitized.substring(0, 1000);
            logger.warn("Input truncated to 1000 characters");
        }
        
        return sanitized.trim();
    }
    
    /**
     * Validate numeric input
     */
    public boolean isValidNumber(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        try {
            Double.parseDouble(input.trim());
            return true;
        } catch (NumberFormatException e) {
            logger.warn("Invalid number format: {}", input);
            return false;
        }
    }
} 