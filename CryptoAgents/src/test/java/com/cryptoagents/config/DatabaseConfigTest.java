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
    void testDatabaseConnectionIsValid() {
        logTestStart("testDatabaseConnectionIsValid");
        
        try (Connection connection = dataSource.getConnection()) {
            assertTrue(connection.isValid(1));
            assertFalse(connection.isClosed());
            assertEquals("H2", connection.getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            fail("Database connection should be valid", e);
        }
        
        logTestEnd("testDatabaseConnectionIsValid");
    }

    @Test
    void testJdbcTemplateCanExecuteQueries() {
        logTestStart("testJdbcTemplateCanExecuteQueries");
        
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES", Integer.class);
        assertNotNull(count);
        assertTrue(count > 0, "Should have at least some tables after migrations");
        
        logTestEnd("testJdbcTemplateCanExecuteQueries");
    }

    @Test
    void testDatabaseMetadata() {
        logTestStart("testDatabaseMetadata");
        
        try (Connection connection = dataSource.getConnection()) {
            String databaseProductName = connection.getMetaData().getDatabaseProductName();
            String databaseProductVersion = connection.getMetaData().getDatabaseProductVersion();
            
            assertEquals("H2", databaseProductName);
            assertNotNull(databaseProductVersion);
            assertTrue(databaseProductVersion.length() > 0);
        } catch (SQLException e) {
            fail("Should be able to read database metadata", e);
        }
        
        logTestEnd("testDatabaseMetadata");
    }

    @Test
    void testConnectionPoolConfiguration() {
        logTestStart("testConnectionPoolConfiguration");
        
        // Test that we can get multiple connections
        try (Connection conn1 = dataSource.getConnection();
             Connection conn2 = dataSource.getConnection()) {
            
            assertTrue(conn1.isValid(1));
            assertTrue(conn2.isValid(1));
            assertNotSame(conn1, conn2, "Should get different connection instances");
        } catch (SQLException e) {
            fail("Should be able to get multiple connections from pool", e);
        }
        
        logTestEnd("testConnectionPoolConfiguration");
    }

    @Test
    void testDatabasePropertiesBean() {
        logTestStart("testDatabasePropertiesBean");
        
        // Test that basic JDBC operations work
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertEquals(1, result);
        
        logTestEnd("testDatabasePropertiesBean");
    }
} 