package com.cryptoagents.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Утилита для санитизации и валидации входных данных
 */
@Component
public class InputSanitizer {
    
    // Паттерн для валидных тикеров криптовалют
    private static final Pattern TICKER_PATTERN = Pattern.compile("^[A-Z0-9]{1,10}$");
    
    // Паттерн для валидных буквенно-цифровых строк
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s-_]+$");
    
    // Паттерн для валидных email адресов
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    /**
     * Санитизация и валидация тикера криптовалюты
     */
    public String sanitizeTicker(String ticker) {
        if (ticker == null || ticker.trim().isEmpty()) {
            throw new IllegalArgumentException("Тикер не может быть null или пустым");
        }
        
        String sanitized = ticker.trim().toUpperCase();
        
        if (!TICKER_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Неверный формат тикера. Должен содержать 1-10 заглавных букв/цифр");
        }
        
        return sanitized;
    }
    
    /**
     * Санитизация буквенно-цифрового ввода
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
            throw new IllegalArgumentException("Ввод содержит неверные символы");
        }
        
        return sanitized;
    }
    
    /**
     * Санитизация email адреса
     */
    public String sanitizeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email не может быть null или пустым");
        }
        
        String sanitized = email.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Неверный формат email");
        }
        
        return sanitized;
    }
    
    /**
     * Удаление потенциально опасных символов
     */
    public String removeDangerousChars(String input) {
        if (input == null) {
            return null;
        }
        
        return input.replaceAll("[<>\"'&]", "");
    }
    
    /**
     * Валидация и санитизация числового ввода
     */
    public Long sanitizeLong(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        
        try {
            return Long.parseLong(input.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверное числовое значение");
        }
    }
    
    /**
     * Валидация и санитизация double ввода
     */
    public Double sanitizeDouble(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        
        try {
            return Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверное числовое значение");
        }
    }
} 