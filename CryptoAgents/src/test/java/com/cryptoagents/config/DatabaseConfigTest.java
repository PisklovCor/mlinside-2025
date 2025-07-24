package com.cryptoagents.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for database configuration.
 * Tests database connectivity and configuration validation.
 */
@SpringBootTest
@ActiveProfiles("test")
class DatabaseConfigTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testDatabaseConnectionIsValid() throws SQLException {
        // Test that we can obtain a valid connection
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertTrue(connection.isValid(5), "Connection should be valid");
            assertFalse(connection.isClosed(), "Connection should not be closed");
        }
    }

    @Test
    void testJdbcTemplateCanExecuteQueries() {
        // Test basic query execution
        String result = jdbcTemplate.queryForObject("SELECT 'Database connection test'", String.class);
        assertEquals("Database connection test", result);
    }

    @Test
    void testDatabaseVersionQuery() {
        // Test that we can query database version (works with both H2 and PostgreSQL)
        try {
            // Try PostgreSQL version query first
            String version = jdbcTemplate.queryForObject("SELECT version()", String.class);
            assertNotNull(version, "Database version should not be null");
            assertFalse(version.trim().isEmpty(), "Database version should not be empty");
        } catch (Exception e) {
            // For H2 database, use alternative query
            String version = jdbcTemplate.queryForObject("SELECT H2VERSION()", String.class);
            assertNotNull(version, "H2 version should not be null");
            assertFalse(version.trim().isEmpty(), "H2 version should not be empty");
        }
    }

    @Test
    void testDataSourceConfiguration() {
        // Test basic DataSource properties
        assertNotNull(dataSource, "DataSource should be configured");
        
        // Test that we can get connection metadata
        try (Connection connection = dataSource.getConnection()) {
            String url = connection.getMetaData().getURL();
            assertNotNull(url, "Connection URL should not be null");
            
            String driverName = connection.getMetaData().getDriverName();
            assertNotNull(driverName, "Driver name should not be null");
            
        } catch (SQLException e) {
            fail("Should be able to get connection metadata: " + e.getMessage());
        }
    }

    @Test
    void testDatabasePropertiesBean() {
        // Test that DatabaseProperties can be autowired (configuration validation)
        DatabaseConfig.DatabaseProperties properties = new DatabaseConfig.DatabaseProperties();
        
        // Test default values
        assertEquals(20, properties.getMaxConnections());
        assertEquals(5, properties.getMinConnections());
        assertEquals(30000, properties.getConnectionTimeout());
        assertFalse(properties.isEnableMetrics());
        
        // Test setters
        properties.setMaxConnections(15);
        properties.setMinConnections(3);
        properties.setConnectionTimeout(25000);
        properties.setEnableMetrics(true);
        
        assertEquals(15, properties.getMaxConnections());
        assertEquals(3, properties.getMinConnections());
        assertEquals(25000, properties.getConnectionTimeout());
        assertTrue(properties.isEnableMetrics());
    }
} 