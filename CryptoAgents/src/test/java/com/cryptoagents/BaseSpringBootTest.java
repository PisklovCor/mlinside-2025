package com.cryptoagents;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Базовый абстрактный класс для всех Spring Boot тестов.
 * Содержит общую конфигурацию для тестирования.
 */

@ActiveProfiles("test")
@SpringBootTest
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//@TestPropertySource(properties = {"spring.config.location=classpath:application-test.properties"})
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