package com.multiagent.agent;

import com.multiagent.BaseTestConfiguration;
import com.multiagent.model.AgentAnalysis;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Базовый класс для тестов агентов с общей логикой
 */
public abstract class BaseAgentTest extends BaseTestConfiguration {

    /**
     * Создает мок ответа от AI модели
     */
    protected ChatResponse createMockChatResponse(String content) {
        AssistantMessage message = new AssistantMessage(content);
        Generation generation = new Generation(message);
        return new ChatResponse(java.util.List.of(generation));
    }

    /**
     * Проверяет базовые поля AgentAnalysis
     */
    protected void assertBasicAgentAnalysis(AgentAnalysis result, String expectedAgentName) {
        assertNotNull(result);
        assertEquals(expectedAgentName, result.getAgentName());
        assertNotNull(result.getAnalysis());
        assertNotNull(result.getRecommendation());
        assertTrue(result.getConfidence() >= 0.0 && result.getConfidence() <= 1.0);
    }

    /**
     * Проверяет рекомендацию ПОКУПАТЬ
     */
    protected void assertBuyRecommendation(AgentAnalysis result) {
        assertEquals("ПОКУПАТЬ", result.getRecommendation());
    }

    /**
     * Проверяет рекомендацию ПРОДАВАТЬ
     */
    protected void assertSellRecommendation(AgentAnalysis result) {
        assertEquals("ПРОДАВАТЬ", result.getRecommendation());
    }

    /**
     * Проверяет рекомендацию ДЕРЖАТЬ
     */
    protected void assertHoldRecommendation(AgentAnalysis result) {
        assertEquals("ДЕРЖАТЬ", result.getRecommendation());
    }

    /**
     * Проверяет высокую уверенность (> 0.8)
     */
    protected void assertHighConfidence(AgentAnalysis result) {
        assertTrue(result.getConfidence() > 0.8);
    }

    /**
     * Проверяет умеренную уверенность (0.6-0.8)
     */
    protected void assertModerateConfidence(AgentAnalysis result) {
        assertTrue(result.getConfidence() >= 0.6 && result.getConfidence() <= 0.8);
    }

    /**
     * Проверяет низкую уверенность (0.4-0.6)
     */
    protected void assertLowConfidence(AgentAnalysis result) {
        assertTrue(result.getConfidence() >= 0.4 && result.getConfidence() <= 0.6);
    }

    /**
     * Проверяет наличие ошибки в анализе
     */
    protected void assertErrorInAnalysis(AgentAnalysis result) {
        assertTrue(result.getAnalysis().contains("Ошибка при получении анализа"));
    }
} 