package com.multiagent.util;

import lombok.extern.slf4j.Slf4j;

/**
 * Утилитарный класс для извлечения рекомендаций и уверенности из анализа агентов
 */
@Slf4j
public class AnalysisUtils {

    /**
     * Извлекает рекомендацию из текста анализа
     * @param analysis текст анализа
     * @return рекомендация (ПОКУПАТЬ/ПРОДАВАТЬ/ДЕРЖАТЬ)
     */
    public static String extractRecommendation(String analysis) {
        log.debug("Извлечение рекомендации из анализа длиной {} символов", analysis.length());
        
        String lowerAnalysis = analysis.toLowerCase();
        
        if (containsBuySignals(lowerAnalysis)) {
            log.debug("Обнаружены сигналы покупки, рекомендация: ПОКУПАТЬ");
            return "ПОКУПАТЬ";
        } else if (containsSellSignals(lowerAnalysis)) {
            log.debug("Обнаружены сигналы продажи, рекомендация: ПРОДАВАТЬ");
            return "ПРОДАВАТЬ";
        } else {
            log.debug("Сигналы неоднозначны, рекомендация: ДЕРЖАТЬ");
            return "ДЕРЖАТЬ";
        }
    }

    /**
     * Извлекает уровень уверенности из текста анализа
     * @param analysis текст анализа
     * @return уровень уверенности (0.0 - 1.0)
     */
    public static double extractConfidence(String analysis) {
        log.debug("Извлечение уровня уверенности из анализа");
        
        String lowerAnalysis = analysis.toLowerCase();
        
        if (lowerAnalysis.contains("высокая уверенность") ||
                lowerAnalysis.contains("настоятельно рекомендую") ||
                lowerAnalysis.contains("очень уверен")) {
            log.debug("Обнаружена высокая уверенность: 0.9");
            return 0.9;
        } else if (lowerAnalysis.contains("умеренная уверенность") ||
                lowerAnalysis.contains("рекомендую") ||
                lowerAnalysis.contains("довольно уверен")) {
            log.debug("Обнаружена умеренная уверенность: 0.7");
            return 0.7;
        } else if (lowerAnalysis.contains("низкая уверенность") ||
                lowerAnalysis.contains("осторожно") ||
                lowerAnalysis.contains("не уверен")) {
            log.debug("Обнаружена низкая уверенность: 0.5");
            return 0.5;
        } else if (lowerAnalysis.contains("неопределенность") ||
                lowerAnalysis.contains("сложно сказать")) {
            log.debug("Обнаружена неопределенность: 0.3");
            return 0.3;
        }
        
        log.debug("Используется значение по умолчанию: 0.6");
        return 0.6; // значение по умолчанию
    }

    private static boolean containsBuySignals(String analysis) {
        boolean hasBuySignals = analysis.contains("покупать") || 
               analysis.contains("buy") ||
               analysis.contains("перспективн") || 
               analysis.contains("сильн") ||
               analysis.contains("инновацион") ||
               analysis.contains("позитивн") || 
               analysis.contains("оптимизм") ||
               analysis.contains("бычий") || 
               analysis.contains("хайп") ||
               analysis.contains("восходящий");
        
        if (hasBuySignals) {
            log.trace("Обнаружены сигналы покупки в анализе");
        }
        
        return hasBuySignals;
    }

    private static boolean containsSellSignals(String analysis) {
        boolean hasSellSignals = analysis.contains("продавать") || 
               analysis.contains("sell") ||
               analysis.contains("рискованн") || 
               analysis.contains("слаб") ||
               analysis.contains("проблем") ||
               analysis.contains("негативн") || 
               analysis.contains("пессимизм") ||
               analysis.contains("медвежий") || 
               analysis.contains("fud") ||
               analysis.contains("нисходящий");
        
        if (hasSellSignals) {
            log.trace("Обнаружены сигналы продажи в анализе");
        }
        
        return hasSellSignals;
    }
} 