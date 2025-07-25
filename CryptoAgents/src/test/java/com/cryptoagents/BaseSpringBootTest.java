package com.cryptoagents;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Базовый абстрактный класс для всех Spring Boot тестов.
 * Содержит общую конфигурацию для тестирования.
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseSpringBootTest {
    
    /**
     * Базовая инициализация для всех тестов
     */
    protected void logTestStart(String testName) {
        log.info("🧪 Starting test: {}", testName);
    }
    
    /**
     * Базовое завершение для всех тестов
     */
    protected void logTestEnd(String testName) {
        log.info("✅ Test completed: {}", testName);
    }
} 