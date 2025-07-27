package com.cryptoagents;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения CryptoAgents - Система мультиагентного анализа криптовалют.
 * 
 * Это приложение предоставляет комплексный анализ токенов криптовалют
 * с использованием трех специализированных агентов: Аналитик, Риск-менеджер и Трейдер.
 * 
 * @author CryptoAgents Team
 * @version 1.0.0
 */
@Slf4j
@SpringBootApplication
public class CryptoAgentsApplication {

    public static void main(String[] args) {
        log.info("🚀 Запуск приложения CryptoAgents...");
        try {
            SpringApplication.run(CryptoAgentsApplication.class, args);
            log.info("✅ Приложение CryptoAgents успешно запущено");
        } catch (Exception e) {
            log.error("❌ Не удалось запустить приложение CryptoAgents: {}", e.getMessage(), e);
            throw e;
        }
    }
} 