package com.cryptoagents.util;

import org.springframework.util.StringUtils;

/**
 * Утилитарный класс для валидации входных данных.
 * 
 * Этот класс предоставляет методы для проверки корректности входных параметров,
 * таких как тикеры криптовалют, временные периоды и другие данные.
 */
public class InputValidator {
    
    /**
     * Проверяет, является ли тикер криптовалюты валидным.
     * 
     * @param ticker тикер для проверки
     * @return true, если тикер валиден, false в противном случае
     */
    public static boolean isValidTicker(String ticker) {
        if (!StringUtils.hasText(ticker)) {
            return false;
        }
        
        // Проверяем, что тикер содержит только буквы и цифры, длиной от 1 до 10 символов
        return ticker.matches("^[A-Za-z0-9]{1,10}$");
    }
    
    /**
     * Проверяет, является ли временной период валидным.
     * 
     * @param period период для проверки
     * @return true, если период валиден, false в противном случае
     */
    public static boolean isValidTimePeriod(String period) {
        if (!StringUtils.hasText(period)) {
            return false;
        }
        
        // Проверяем, что период соответствует ожидаемому формату
        return period.matches("^(1d|7d|30d|1y|max)$");
    }
    
    /**
     * Проверяет, является ли цена валидной.
     * 
     * @param price цена для проверки
     * @return true, если цена валидна, false в противном случае
     */
    public static boolean isValidPrice(String price) {
        if (!StringUtils.hasText(price)) {
            return false;
        }
        
        try {
            double priceValue = Double.parseDouble(price);
            return priceValue > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Проверяет, является ли объем валидным.
     * 
     * @param volume объем для проверки
     * @return true, если объем валиден, false в противном случае
     */
    public static boolean isValidVolume(String volume) {
        if (!StringUtils.hasText(volume)) {
            return false;
        }
        
        try {
            double volumeValue = Double.parseDouble(volume);
            return volumeValue >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
} 