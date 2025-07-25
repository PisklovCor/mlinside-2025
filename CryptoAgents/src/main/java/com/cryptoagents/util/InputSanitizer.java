package com.cryptoagents.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Utility for input sanitization and validation
 */
@Component
public class InputSanitizer {
    
    // Pattern for valid cryptocurrency ticker symbols
    private static final Pattern TICKER_PATTERN = Pattern.compile("^[A-Z0-9]{1,10}$");
    
    // Pattern for valid alphanumeric strings
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s-_]+$");
    
    // Pattern for valid email addresses
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    /**
     * Sanitize and validate cryptocurrency ticker
     */
    public String sanitizeTicker(String ticker) {
        if (ticker == null || ticker.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticker cannot be null or empty");
        }
        
        String sanitized = ticker.trim().toUpperCase();
        
        if (!TICKER_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Invalid ticker format. Must be 1-10 uppercase letters/numbers");
        }
        
        return sanitized;
    }
    
    /**
     * Sanitize alphanumeric input
     */
    public String sanitizeAlphanumeric(String input, int maxLength) {
        if (input == null) {
            return null;
        }
        
        String sanitized = input.trim();
        
        if (sanitized.length() > maxLength) {
            sanitized = sanitized.substring(0, maxLength);
        }
        
        if (!ALPHANUMERIC_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Input contains invalid characters");
        }
        
        return sanitized;
    }
    
    /**
     * Sanitize email address
     */
    public String sanitizeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        String sanitized = email.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        return sanitized;
    }
    
    /**
     * Remove potentially dangerous characters
     */
    public String removeDangerousChars(String input) {
        if (input == null) {
            return null;
        }
        
        return input.replaceAll("[<>\"'&]", "");
    }
    
    /**
     * Validate and sanitize numeric input
     */
    public Long sanitizeLong(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        
        try {
            return Long.parseLong(input.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric value");
        }
    }
    
    /**
     * Validate and sanitize double input
     */
    public Double sanitizeDouble(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        
        try {
            return Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric value");
        }
    }
} 