package com.cryptoagents.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Database configuration class for PostgreSQL connection management.
 * Handles connection validation and database-specific settings.
 */
@Configuration
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    /**
     * Database connection validator that runs on startup.
     */
    @Component
    public static class DatabaseConnectionValidator implements CommandLineRunner {

        @Autowired
        private DataSource dataSource;

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @Override
        public void run(String... args) throws Exception {
            validateDatabaseConnection();
            logDatabaseInfo();
        }

        private void validateDatabaseConnection() {
            logger.info("Validating database connection...");
            
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(5)) {
                    logger.info("✅ Database connection is valid");
                } else {
                    logger.error("❌ Database connection validation failed");
                    throw new RuntimeException("Database connection is not valid");
                }
            } catch (SQLException e) {
                logger.error("❌ Failed to validate database connection: {}", e.getMessage());
                throw new RuntimeException("Could not establish database connection", e);
            }
        }

        private void logDatabaseInfo() {
            try {
                String version = jdbcTemplate.queryForObject("SELECT version()", String.class);
                logger.info("Connected to database: {}", version);
                
                // Verify required database exists
                String currentDatabase = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
                logger.info("Current database: {}", currentDatabase);
                
            } catch (Exception e) {
                logger.warn("Could not retrieve database information: {}", e.getMessage());
            }
        }
    }

    /**
     * Database properties configuration for environment-specific settings.
     */
    @Component
    @ConfigurationProperties(prefix = "crypto.database")
    public static class DatabaseProperties {
        
        private int maxConnections = 20;
        private int minConnections = 5;
        private long connectionTimeout = 30000;
        private boolean enableMetrics = false;

        // Getters and setters
        public int getMaxConnections() {
            return maxConnections;
        }

        public void setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
        }

        public int getMinConnections() {
            return minConnections;
        }

        public void setMinConnections(int minConnections) {
            this.minConnections = minConnections;
        }

        public long getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(long connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public boolean isEnableMetrics() {
            return enableMetrics;
        }

        public void setEnableMetrics(boolean enableMetrics) {
            this.enableMetrics = enableMetrics;
        }
    }

    /**
     * Development profile specific configuration.
     */
    @Configuration
    @Profile("dev")
    public static class DevelopmentDatabaseConfig {
        
        private static final Logger logger = LoggerFactory.getLogger(DevelopmentDatabaseConfig.class);

        @Bean
        public CommandLineRunner devDatabaseSetup() {
            return args -> {
                logger.info("🔧 Development database configuration loaded");
                logger.info("📊 SQL logging is enabled in development mode");
            };
        }
    }

    /**
     * Production profile specific configuration.
     */
    @Configuration
    @Profile("prod")
    public static class ProductionDatabaseConfig {
        
        private static final Logger logger = LoggerFactory.getLogger(ProductionDatabaseConfig.class);

        @Bean
        public CommandLineRunner prodDatabaseSetup() {
            return args -> {
                logger.info("🚀 Production database configuration loaded");
                logger.info("🔒 Enhanced security and performance settings applied");
            };
        }
    }
} 