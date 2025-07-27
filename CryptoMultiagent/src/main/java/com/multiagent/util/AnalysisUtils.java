package com.multiagent.util;

/**
 * Утилитарный класс для извлечения рекомендаций и уверенности из анализа агентов
 */
public class AnalysisUtils {

    /**
     * Извлекает рекомендацию из текста анализа
     * @param analysis текст анализа
     * @return рекомендация (ПОКУПАТЬ/ПРОДАВАТЬ/ДЕРЖАТЬ)
     */
    public static String extractRecommendation(String analysis) {
        String lowerAnalysis = analysis.toLowerCase();
        
        if (containsBuySignals(lowerAnalysis)) {
            return "ПОКУПАТЬ";
        } else if (containsSellSignals(lowerAnalysis)) {
            return "ПРОДАВАТЬ";
        } else {
            return "ДЕРЖАТЬ";
        }
    }

    /**
     * Извлекает уровень уверенности из текста анализа
     * @param analysis текст анализа
     * @return уровень уверенности (0.0 - 1.0)
     */
    public static double extractConfidence(String analysis) {
        String lowerAnalysis = analysis.toLowerCase();
        
        if (lowerAnalysis.contains("высокая уверенность") ||
                lowerAnalysis.contains("настоятельно рекомендую") ||
                lowerAnalysis.contains("очень уверен")) {
            return 0.9;
        } else if (lowerAnalysis.contains("умеренная уверенность") ||
                lowerAnalysis.contains("рекомендую") ||
                lowerAnalysis.contains("довольно уверен")) {
            return 0.7;
        } else if (lowerAnalysis.contains("низкая уверенность") ||
                lowerAnalysis.contains("осторожно") ||
                lowerAnalysis.contains("не уверен")) {
            return 0.5;
        } else if (lowerAnalysis.contains("неопределенность") ||
                lowerAnalysis.contains("сложно сказать")) {
            return 0.3;
        }
        return 0.6; // значение по умолчанию
    }

    private static boolean containsBuySignals(String analysis) {
        return analysis.contains("покупать") || 
               analysis.contains("buy") ||
               analysis.contains("перспективн") || 
               analysis.contains("сильн") ||
               analysis.contains("инновацион") ||
               analysis.contains("позитивн") || 
               analysis.contains("оптимизм") ||
               analysis.contains("бычий") || 
               analysis.contains("хайп") ||
               analysis.contains("восходящий");
    }

    private static boolean containsSellSignals(String analysis) {
        return analysis.contains("продавать") || 
               analysis.contains("sell") ||
               analysis.contains("рискованн") || 
               analysis.contains("слаб") ||
               analysis.contains("проблем") ||
               analysis.contains("негативн") || 
               analysis.contains("пессимизм") ||
               analysis.contains("медвежий") || 
               analysis.contains("fud") ||
               analysis.contains("нисходящий");
    }
} 