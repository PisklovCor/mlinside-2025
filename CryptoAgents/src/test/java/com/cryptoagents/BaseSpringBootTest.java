package com.cryptoagents;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * Базовый абстрактный класс для всех Spring Boot тестов.
 * Содержит общую конфигурацию для тестирования.
 */
@Slf4j
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class BaseSpringBootTest {

    /**
     * Базовая инициализация для всех тестов
     */
    protected void logTestStart(String testName) {
        System.out.println("🧪 Начало теста: " + testName);
    }
    
    /**
     * Базовое завершение для всех тестов
     */
    protected void logTestEnd(String testName) {
        System.out.println("✅ Тест завершен: " + testName);
    }
} 