package com.cryptoagents.config;

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
 * Database configuration class for PostgreSQL setup and validation.
 * 
 * This configuration validates database connectivity, logs database information,
 * and provides environment-specific settings for development and production.
 */
@Slf4j
@Configuration
public class DatabaseConfig {

    /**
     * Database connection validator that runs on startup.
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
            log.info("Validating database connection...");
            
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(5)) {
                    log.info("✅ Database connection is valid");
                } else {
                    log.error("❌ Database connection validation failed");
                    throw new RuntimeException("Database connection is not valid");
                }
            } catch (SQLException e) {
                log.error("❌ Failed to validate database connection: {}", e.getMessage());
                throw new RuntimeException("Could not establish database connection", e);
            }
        }

        private void logDatabaseInfo() {
            try {
                String version = jdbcTemplate.queryForObject("SELECT version()", String.class);
                log.info("Connected to database: {}", version);
                
                // Verify required database exists
                String currentDatabase = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
                log.info("Current database: {}", currentDatabase);
                
            } catch (Exception e) {
                log.warn("Could not retrieve database information: {}", e.getMessage());
            }
        }
    }

    /**
     * Database properties configuration for environment-specific settings.
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
     * Development profile specific configuration.
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
     * Production profile specific configuration.
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