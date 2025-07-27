//package com.cryptoagents.config;
//
//import com.cryptoagents.BaseSpringBootTest;
//import org.flywaydb.core.Flyway;
//import org.flywaydb.core.api.MigrationInfo;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import javax.sql.DataSource;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Test class for verifying Flyway database migrations.
// *
// * This test ensures that database schema migrations are working correctly
// * and that the database structure matches the expected schema.
// */
//class FlywayMigrationTest extends BaseSpringBootTest {
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    private Flyway flyway;
//
//    @BeforeEach
//    void setUp() {
//        logTestStart("setUp");
//        // Verify that we have a clean state for each test
//        assertNotNull(dataSource, "DataSource should be autowired");
//        assertNotNull(jdbcTemplate, "JdbcTemplate should be autowired");
//        assertNotNull(flyway, "Flyway should be autowired");
//        logTestEnd("setUp");
//    }
//
//    @Test
//    void testMigrationExecution() {
//        logTestStart("testMigrationExecution");
//
//        // Test that Flyway has executed migrations
//        var info = flyway.info();
//        assertNotNull(info, "Migration info should be available");
//
//        var migrations = info.all();
//        assertTrue(migrations.length > 0, "Should have at least one migration");
//
//        // Check that the current migration was applied successfully
//        var current = info.current();
//        assertNotNull(current, "Should have a current migration");
//        assertEquals("1", current.getVersion().getVersion(), "Should be at version 1");
//
//        logTestEnd("testMigrationExecution");
//    }
//
//    @Test
//    void testMigrationInfo() {
//        logTestStart("testMigrationInfo");
//
//        var info = flyway.info();
//        MigrationInfo[] allMigrations = info.all();
//
//        assertTrue(allMigrations.length >= 1, "Should have at least one migration");
//        assertEquals("1", allMigrations[0].getVersion().getVersion(), "First migration should be version 1");
//        assertEquals("init schema", allMigrations[0].getDescription(), "Should have correct description");
//
//        logTestEnd("testMigrationInfo");
//    }
//
//    @Test
//    void testMigrationIdempotency() {
//        logTestStart("testMigrationIdempotency");
//
//        // Run migration again - should be idempotent
//        var result = flyway.migrate();
//        assertEquals(0, result.migrationsExecuted, "No new migrations should be executed on repeat");
//
//        logTestEnd("testMigrationIdempotency");
//    }
//
//    @Test
//    void testDatabaseSchemaAfterMigration() {
//        logTestStart("testDatabaseSchemaAfterMigration");
//
//        // Test that expected tables exist (H2 uses lowercase table names in PostgreSQL mode)
//        String[] expectedTables = {
//            "analysis_reports",
//            "analysis_results",
//            "analyst_reports",
//            "risk_manager_reports",
//            "trader_reports"
//        };
//
//        for (String table : expectedTables) {
//            Integer count = jdbcTemplate.queryForObject(
//                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE LOWER(TABLE_NAME) = ?",
//                Integer.class, table);
//            assertEquals(1, count, "Table " + table + " should exist");
//        }
//
//        logTestEnd("testDatabaseSchemaAfterMigration");
//    }
//
//    @Test
//    void testAnalysisResultsTableStructure() {
//        logTestStart("testAnalysisResultsTableStructure");
//
//        // Test that we can describe the table structure
//        var columns = jdbcTemplate.queryForList(
//            "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE LOWER(TABLE_NAME) = 'analysis_results'");
//
//        assertFalse(columns.isEmpty(), "ANALYSIS_RESULTS table should have columns");
//
//        // Check for key columns
//        List<String> columnNames = columns.stream()
//            .map(row -> ((String) row.get("COLUMN_NAME")).toLowerCase())
//            .collect(Collectors.toList());
//
//        assertTrue(columnNames.contains("id"), "Should have ID column");
//        assertTrue(columnNames.contains("agent_type"), "Should have AGENT_TYPE column");
//        assertTrue(columnNames.contains("ticker"), "Should have TICKER column");
//
//        logTestEnd("testAnalysisResultsTableStructure");
//    }
//
//    @Test
//    void testAnalystReportsTableStructure() {
//        logTestStart("testAnalystReportsTableStructure");
//
//        var columns = jdbcTemplate.queryForList(
//            "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE LOWER(TABLE_NAME) = 'analyst_reports'");
//
//        assertFalse(columns.isEmpty(), "ANALYST_REPORTS table should have columns");
//
//        List<String> columnNames = columns.stream()
//            .map(row -> ((String) row.get("COLUMN_NAME")).toLowerCase())
//            .collect(Collectors.toList());
//
//        assertTrue(columnNames.contains("id"), "Should have ID column");
//        assertTrue(columnNames.contains("market_trend"), "Should have MARKET_TREND column");
//
//        logTestEnd("testAnalystReportsTableStructure");
//    }
//
//    @Test
//    void testRiskManagerReportsTableStructure() {
//        logTestStart("testRiskManagerReportsTableStructure");
//
//        var columns = jdbcTemplate.queryForList(
//            "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE LOWER(TABLE_NAME) = 'risk_manager_reports'");
//
//        assertFalse(columns.isEmpty(), "RISK_MANAGER_REPORTS table should have columns");
//
//        List<String> columnNames = columns.stream()
//            .map(row -> ((String) row.get("COLUMN_NAME")).toLowerCase())
//            .collect(Collectors.toList());
//
//        assertTrue(columnNames.contains("id"), "Should have ID column");
//        assertTrue(columnNames.contains("risk_level"), "Should have RISK_LEVEL column");
//
//        logTestEnd("testRiskManagerReportsTableStructure");
//    }
//
//    @Test
//    void testTraderReportsTableStructure() {
//        logTestStart("testTraderReportsTableStructure");
//
//        var columns = jdbcTemplate.queryForList(
//            "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE LOWER(TABLE_NAME) = 'trader_reports'");
//
//        assertFalse(columns.isEmpty(), "TRADER_REPORTS table should have columns");
//
//        List<String> columnNames = columns.stream()
//            .map(row -> ((String) row.get("COLUMN_NAME")).toLowerCase())
//            .collect(Collectors.toList());
//
//        assertTrue(columnNames.contains("id"), "Should have ID column");
//        assertTrue(columnNames.contains("action_recommendation"), "Should have ACTION_RECOMMENDATION column");
//
//        logTestEnd("testTraderReportsTableStructure");
//    }
//
//    @Test
//    void testIndexCreation() {
//        logTestStart("testIndexCreation");
//
//        // Test that indexes were created successfully
//        var indexes = jdbcTemplate.queryForList(
//            "SELECT INDEX_NAME FROM INFORMATION_SCHEMA.INDEXES WHERE LOWER(TABLE_NAME) LIKE '%reports%'");
//
//        assertFalse(indexes.isEmpty(), "Should have indexes on report tables");
//
//        logTestEnd("testIndexCreation");
//    }
//
//    @Test
//    void testForeignKeyConstraints() {
//        logTestStart("testForeignKeyConstraints");
//
//        // Test that foreign key constraints exist by attempting invalid operations
//        // This is more reliable than querying H2's INFORMATION_SCHEMA
//
//        // Try to insert into child table without parent - should fail
//        assertThrows(Exception.class, () -> {
//            jdbcTemplate.execute(
//                "INSERT INTO analyst_reports (id) VALUES (999)"
//            );
//        }, "Should not be able to insert into analyst_reports without parent analysis_results");
//
//        // Try to insert into child table without parent - should fail for other tables too
//        assertThrows(Exception.class, () -> {
//            jdbcTemplate.execute(
//                "INSERT INTO risk_manager_reports (id, risk_level) VALUES (998, 'HIGH')"
//            );
//        }, "Should not be able to insert into risk_manager_reports without parent analysis_results");
//
//        assertThrows(Exception.class, () -> {
//            jdbcTemplate.execute(
//                "INSERT INTO trader_reports (id, action_recommendation) VALUES (997, 'BUY')"
//            );
//        }, "Should not be able to insert into trader_reports without parent analysis_results");
//
//        logTestEnd("testForeignKeyConstraints");
//    }
//
//    @Test
//    void testFlywayConfiguration() {
//        logTestStart("testFlywayConfiguration");
//
//        // Test Flyway configuration
//        assertEquals("db/migration", flyway.getConfiguration().getLocations()[0].getPath(),
//            "Should use correct migration location");
//
//        logTestEnd("testFlywayConfiguration");
//    }
//}