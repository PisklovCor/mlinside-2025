package com.multiagent.util;

import com.multiagent.util.AnalysisUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для AnalysisUtils")
class AnalysisUtilsTest {

    @Test
    @DisplayName("Должен извлечь рекомендацию ПОКУПАТЬ из позитивного анализа")
    void shouldExtractBuyRecommendation() {
        String analysis = "Анализ показывает перспективность проекта. Рекомендую покупать.";
        String recommendation = AnalysisUtils.extractRecommendation(analysis);
        assertEquals("ПОКУПАТЬ", recommendation);
    }

    @Test
    @DisplayName("Должен извлечь рекомендацию ПРОДАВАТЬ из негативного анализа")
    void shouldExtractSellRecommendation() {
        String analysis = "Проект имеет серьезные проблемы. Рекомендую продавать.";
        String recommendation = AnalysisUtils.extractRecommendation(analysis);
        assertEquals("ПРОДАВАТЬ", recommendation);
    }

    @Test
    @DisplayName("Должен извлечь рекомендацию ДЕРЖАТЬ из нейтрального анализа")
    void shouldExtractHoldRecommendation() {
        String analysis = "Ситуация неопределенная. Лучше подождать.";
        String recommendation = AnalysisUtils.extractRecommendation(analysis);
        assertEquals("ДЕРЖАТЬ", recommendation);
    }

    @Test
    @DisplayName("Должен извлечь высокую уверенность")
    void shouldExtractHighConfidence() {
        String analysis = "Настоятельно рекомендую покупать с высокой уверенностью.";
        double confidence = AnalysisUtils.extractConfidence(analysis);
        assertEquals(0.9, confidence, 0.01);
    }

    @Test
    @DisplayName("Должен извлечь умеренную уверенность")
    void shouldExtractMediumConfidence() {
        String analysis = "Рекомендую с умеренной уверенностью.";
        double confidence = AnalysisUtils.extractConfidence(analysis);
        assertEquals(0.7, confidence, 0.01);
    }

    @Test
    @DisplayName("Должен извлечь низкую уверенность")
    void shouldExtractLowConfidence() {
        String analysis = "Осторожно, низкая уверенность в прогнозе.";
        double confidence = AnalysisUtils.extractConfidence(analysis);
        assertEquals(0.5, confidence, 0.01);
    }

    @Test
    @DisplayName("Должен вернуть значение по умолчанию для неопределенной уверенности")
    void shouldReturnDefaultConfidence() {
        String analysis = "Обычный анализ без указания уверенности.";
        double confidence = AnalysisUtils.extractConfidence(analysis);
        assertEquals(0.6, confidence, 0.01);
    }
} 