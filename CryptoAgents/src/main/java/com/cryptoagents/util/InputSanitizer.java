package com.cryptoagents.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Утилитарный класс для очистки и санитизации входных данных.
 * 
 * Этот класс предоставляет методы для удаления потенциально опасных символов
 * и нормализации входных данных для предотвращения атак.
 */
@Component
public class InputSanitizer {
    
    // Паттерн для удаления потенциально опасных символов
    private static final Pattern DANGEROUS_CHARS_PATTERN = Pattern.compile("[<>\"'&;(){}[\\]]");
    
    // Паттерн для валидных тикеров (только буквы и цифры)
    private static final Pattern TICKER_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");
    
    /**
     * Удаляет потенциально опасные символы из строки.
     * 
     * @param input входная строка
     * @return очищенная строка
     */
    public String removeDangerousChars(String input) {
        if (input == null) {
            return null;
        }
        
        return DANGEROUS_CHARS_PATTERN.matcher(input).replaceAll("");
    }
    
    /**
     * Очищает и валидирует тикер криптовалюты.
     * 
     * @param ticker тикер для очистки
     * @return очищенный тикер
     * @throws IllegalArgumentException если тикер невалиден
     */
    public String sanitizeTicker(String ticker) {
        if (ticker == null || ticker.trim().isEmpty()) {
            throw new IllegalArgumentException("Тикер не может быть пустым");
        }
        
        String sanitized = ticker.trim().toUpperCase();
        
        if (!TICKER_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Тикер содержит недопустимые символы: " + ticker);
        }
        
        if (sanitized.length() > 10) {
            throw new IllegalArgumentException("Тикер слишком длинный: " + ticker);
        }
        
        return sanitized;
    }
    
    /**
     * Очищает строку от лишних пробелов и ограничивает длину.
     * 
     * @param input входная строка
     * @param maxLength максимальная длина
     * @return очищенная строка
     */
    public String sanitizeString(String input, int maxLength) {
        if (input == null) {
            return null;
        }
        
        String sanitized = removeDangerousChars(input).trim();
        
        if (sanitized.length() > maxLength) {
            sanitized = sanitized.substring(0, maxLength);
        }
        
        return sanitized;
    }
    
    /**
     * Очищает числовую строку.
     * 
     * @param input числовая строка
     * @return очищенная числовая строка
     */
    public String sanitizeNumber(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        
        // Удаляем все символы кроме цифр, точки и минуса
        return input.replaceAll("[^0-9.-]", "");
    }
    
    /**
     * Очищает email адрес.
     * 
     * @param email email для очистки
     * @return очищенный email
     */
    public String sanitizeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        
        String sanitized = email.trim().toLowerCase();
        
        // Простая проверка формата email
        if (!sanitized.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Неверный формат email: " + email);
        }
        
        return sanitized;
    }
} 