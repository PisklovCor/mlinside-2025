package com.cryptoagents.config;

import com.cryptoagents.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for database configuration.
 * Tests database connectivity and configuration validation.
 */
class DatabaseConfigTest extends BaseSpringBootTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testDatabaseConnectionIsValid() throws SQLException {
        logTestStart("testDatabaseConnectionIsValid");
        // Test that we can obtain a valid connection
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertTrue(connection.isValid(5), "Connection should be valid");
            assertFalse(connection.isClosed(), "Connection should not be closed");
        }
        logTestEnd("testDatabaseConnectionIsValid");
    }

    @Test
    void testJdbcTemplateCanExecuteQueries() {
        logTestStart("testJdbcTemplateCanExecuteQueries");
        // Test basic query execution
        String result = jdbcTemplate.queryForObject("SELECT 'Database connection test'", String.class);
        assertEquals("Database connection test", result);
        logTestEnd("testJdbcTemplateCanExecuteQueries");
    }

    @Test
    void testDatabaseMetadata() throws SQLException {
        logTestStart("testDatabaseMetadata");
        try (Connection connection = dataSource.getConnection()) {
            var metadata = connection.getMetaData();
            
            assertNotNull(metadata, "Database metadata should be available");
            assertNotNull(metadata.getDatabaseProductName(), "Database product name should be available");
            assertNotNull(metadata.getDatabaseProductVersion(), "Database version should be available");
            
            // For H2 test database
            assertTrue(metadata.getDatabaseProductName().contains("H2"), 
                "Should be using H2 database for tests");
        }
        logTestEnd("testDatabaseMetadata");
    }

    @Test
    void testConnectionPoolConfiguration() throws SQLException {
        logTestStart("testConnectionPoolConfiguration");
        // Test that we can create multiple connections (pool functionality)
        try (Connection conn1 = dataSource.getConnection();
             Connection conn2 = dataSource.getConnection()) {
            
            assertNotNull(conn1, "First connection should be valid");
            assertNotNull(conn2, "Second connection should be valid");
            assertTrue(conn1.isValid(5), "First connection should be valid");
            assertTrue(conn2.isValid(5), "Second connection should be valid");
            
            // Connections should be different instances
            assertNotSame(conn1, conn2, "Connections should be different instances");
        }
        logTestEnd("testConnectionPoolConfiguration");
    }

    @Test
    void testDatabasePropertiesBean() {
        logTestStart("testDatabasePropertiesBean");
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
        logTestEnd("testDatabasePropertiesBean");
    }

} 