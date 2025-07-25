package com.cryptoagents.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Утилитарный класс для валидации и санитизации входных данных
 */
@Component
public class InputValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(InputValidator.class);
    
    // Паттерн для валидных тикеров (буквенно-цифровые, 1-10 символов)
    private static final Pattern TICKER_PATTERN = Pattern.compile("^[A-Za-z0-9]{1,10}$");
    
    // Паттерн для валидных временных интервалов
    private static final Pattern TIMEFRAME_PATTERN = Pattern.compile("^(1h|4h|24h|7d|30d)$");
    
    private final InputSanitizer inputSanitizer;
    
    public InputValidator(InputSanitizer inputSanitizer) {
        this.inputSanitizer = inputSanitizer;
    }
    
    /**
     * Валидация и санитизация тикера
     */
    public String validateTicker(String ticker) {
        try {
            return inputSanitizer.sanitizeTicker(ticker);
        } catch (IllegalArgumentException e) {
            logger.warn("Неверный формат тикера: {}", ticker);
            throw e;
        }
    }
    
    /**
     * Валидация параметра временного интервала
     */
    public String validateTimeframe(String timeframe) {
        if (timeframe == null || timeframe.trim().isEmpty()) {
            return "24h"; // Временной интервал по умолчанию
        }
        
        String sanitizedTimeframe = timeframe.trim().toLowerCase();
        
        if (!TIMEFRAME_PATTERN.matcher(sanitizedTimeframe).matches()) {
            logger.warn("Неверный временной интервал: {}, используется по умолчанию", timeframe);
            return "24h"; // По умолчанию 24h если неверный
        }
        
        logger.debug("Временной интервал валидирован: {} -> {}", timeframe, sanitizedTimeframe);
        return sanitizedTimeframe;
    }
    
    /**
     * Санитизация строкового ввода для предотвращения атак внедрения
     */
    public String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        
        String sanitized = inputSanitizer.removeDangerousChars(input);
        
        // Ограничение длины
        if (sanitized.length() > 1000) {
            sanitized = sanitized.substring(0, 1000);
            logger.warn("Ввод обрезан до 1000 символов");
        }
        
        return sanitized.trim();
    }
    
    /**
     * Валидация числового ввода
     */
    public boolean isValidNumber(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        try {
            Double.parseDouble(input.trim());
            return true;
        } catch (NumberFormatException e) {
            logger.warn("Неверный формат числа: {}", input);
            return false;
        }
    }
} 