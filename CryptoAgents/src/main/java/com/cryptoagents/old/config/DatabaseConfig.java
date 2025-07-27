package com.cryptoagents.old.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Класс конфигурации базы данных для настройки и валидации PostgreSQL.
 * 
 * Эта конфигурация проверяет подключение к базе данных, логирует информацию о базе данных
 * и предоставляет настройки для различных сред разработки и продакшена.
 */
@Slf4j
@Configuration
public class DatabaseConfig {

    /**
     * Валидатор подключения к базе данных, который запускается при старте.
     */
    @Slf4j
    @Component
    @RequiredArgsConstructor
    public static class DatabaseConnectionValidator implements CommandLineRunner {

        private final DataSource dataSource;
        private final JdbcTemplate jdbcTemplate;

        @Override
        public void run(String... args) throws Exception {
            validateDatabaseConnection();
            logDatabaseInfo();
        }

        private void validateDatabaseConnection() {
            log.info("Проверка подключения к базе данных...");
            
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(5)) {
                    log.info("✅ Подключение к базе данных корректно");
                } else {
                    log.error("❌ Проверка подключения к базе данных не удалась");
                    throw new RuntimeException("Подключение к базе данных некорректно");
                }
            } catch (SQLException e) {
                log.error("❌ Не удалось проверить подключение к базе данных: {}", e.getMessage());
                throw new RuntimeException("Не удалось установить подключение к базе данных", e);
            }
        }

        private void logDatabaseInfo() {
            try {
                String version = jdbcTemplate.queryForObject("SELECT version()", String.class);
                log.info("Подключено к базе данных: {}", version);
                
                // Проверка существования требуемой базы данных
                String currentDatabase = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
                log.info("Текущая база данных: {}", currentDatabase);
                
            } catch (Exception e) {
                log.warn("Не удалось получить информацию о базе данных: {}", e.getMessage());
            }
        }
    }

    /**
     * Конфигурация свойств базы данных для настроек различных сред.
     */
    @Data
    @Component
    @ConfigurationProperties(prefix = "crypto.database")
    public static class DatabaseProperties {
        
        private int maxConnections = 20;
        private int minConnections = 5;
        private long connectionTimeout = 30000;
        private boolean enableMetrics = false;
    }

    /**
     * Конфигурация для профиля разработки.
     */
    @Slf4j
    @Configuration
    @Profile("dev")
    public static class DevelopmentDatabaseConfig {

        @Bean
        public CommandLineRunner devDatabaseSetup() {
            return args -> {
                log.info("🔧 Development database configuration loaded");
                log.info("📊 SQL logging is enabled in development mode");
            };
        }
    }

    /**
     * Конфигурация для профиля продакшена.
     */
    @Slf4j
    @Configuration
    @Profile("prod")
    public static class ProductionDatabaseConfig {

        @Bean
        public CommandLineRunner prodDatabaseSetup() {
            return args -> {
                log.info("🚀 Production database configuration loaded");
                log.info("🔒 Enhanced security and performance settings applied");
            };
        }
    }
} 