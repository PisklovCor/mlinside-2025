package com.cryptoagents.config;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for verifying Flyway database migrations.
 * 
 * This test ensures that database schema migrations are working correctly
 * and that the database structure matches the expected schema.
 */
@SpringBootTest
@ActiveProfiles("test")
class FlywayMigrationTest {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private Flyway flyway;

    @BeforeEach
    void setUp() {
        flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)  // Allow Flyway to baseline existing schema
                .baselineVersion("0")     // Set baseline version
                .load();
    }

    @Test
    void testFlywayConfiguration() {
        assertNotNull(flyway, "Flyway should be properly configured");
        assertNotNull(dataSource, "DataSource should be available");
    }

    @Test
    void testMigrationInfo() {
        var migrationInfo = flyway.info();
        assertNotNull(migrationInfo, "Migration info should be available");
        
        var migrations = migrationInfo.all();
        assertNotNull(migrations, "Migrations array should not be null");
        assertTrue(migrations.length > 0, "At least one migration should exist");
        
        // Check that we have at least one migration
        var current = migrationInfo.current();
        if (current != null) {
            assertNotNull(current.getVersion(), "Migration should have a version");
        }
    }

    @Test
    void testMigrationExecution() {
        // Run migrations (clean not needed as test database is fresh)
        var migrationResult = flyway.migrate();
        assertTrue(migrationResult.migrationsExecuted >= 0, 
                "Migrations should be executed or already applied");
        
        // Verify migration state
        var migrationInfo = flyway.info();
        assertEquals(0, migrationInfo.pending().length, 
                "No pending migrations should remain");
    }

    @Test
    void testDatabaseSchemaAfterMigration() {
        // Verify main tables exist
        assertTableExists("analysis_results");
        assertTableExists("analyst_reports");
        assertTableExists("risk_manager_reports");
        assertTableExists("trader_reports");
        assertTableExists("analysis_reports");
    }

    @Test
    void testAnalysisResultsTableStructure() {
        // Test primary key and discriminator column
        assertColumnExists("analysis_results", "id");
        assertColumnExists("analysis_results", "agent_type");
        assertColumnExists("analysis_results", "ticker");
        assertColumnExists("analysis_results", "analysis_time");
        assertColumnExists("analysis_results", "status");
    }

    @Test
    void testAnalystReportsTableStructure() {
        // Test specific columns for analyst reports
        assertColumnExists("analyst_reports", "id");
        assertColumnExists("analyst_reports", "market_trend");
        assertColumnExists("analyst_reports", "current_price");
        assertColumnExists("analyst_reports", "price_target");
        assertColumnExists("analyst_reports", "signal_strength");
    }

    @Test
    void testRiskManagerReportsTableStructure() {
        // Test specific columns for risk manager reports
        assertColumnExists("risk_manager_reports", "id");
        assertColumnExists("risk_manager_reports", "risk_score");
        assertColumnExists("risk_manager_reports", "risk_level");
        assertColumnExists("risk_manager_reports", "value_at_risk");
        assertColumnExists("risk_manager_reports", "recommended_position_size");
    }

    @Test
    void testTraderReportsTableStructure() {
        // Test specific columns for trader reports
        assertColumnExists("trader_reports", "id");
        assertColumnExists("trader_reports", "action_recommendation");
        assertColumnExists("trader_reports", "entry_price");
        assertColumnExists("trader_reports", "exit_price");
        assertColumnExists("trader_reports", "stop_loss");
        assertColumnExists("trader_reports", "take_profit");
    }

    @Test
    void testForeignKeyConstraints() {
        // Test that foreign key constraints exist for inheritance
        String query = """
            SELECT COUNT(*) FROM information_schema.table_constraints 
            WHERE constraint_type = 'FOREIGN KEY' 
            AND UPPER(table_name) IN ('ANALYST_REPORTS', 'RISK_MANAGER_REPORTS', 'TRADER_REPORTS')
            """;
        
        Integer count = jdbcTemplate.queryForObject(query, Integer.class);
        assertEquals(3, count, "Should have foreign key constraints for all specialized tables");
    }

    @Test
    void testIndexCreation() {
        // Test that indexes exist (H2 compatible query)
        String query = """
            SELECT COUNT(*) FROM information_schema.indexes
            WHERE table_name IN ('ANALYSIS_RESULTS', 'ANALYST_REPORTS', 'RISK_MANAGER_REPORTS', 'TRADER_REPORTS')
            """;
        
        Integer count = jdbcTemplate.queryForObject(query, Integer.class);
        assertTrue(count >= 3, "Should have at least primary key indexes");
    }

    @Test
    void testMigrationIdempotency() {
        // First run migration to ensure schema is up to date
        var firstMigrationResult = flyway.migrate();
        
        // Run migration again - should be idempotent (no new migrations executed)
        var secondMigrationResult = flyway.migrate();
        assertEquals(0, secondMigrationResult.migrationsExecuted, 
                "No new migrations should be executed on second run");
                
        // Verify migration state is consistent
        var migrationInfo = flyway.info();
        assertEquals(0, migrationInfo.pending().length, 
                "No pending migrations should remain after idempotent run");
    }

    private void assertTableExists(String tableName) {
        String query = """
            SELECT COUNT(*) FROM information_schema.tables 
            WHERE table_schema = 'PUBLIC' AND UPPER(table_name) = UPPER(?)
            """;
        
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, tableName);
        assertEquals(1, count, "Table '" + tableName + "' should exist");
    }

    private void assertColumnExists(String tableName, String columnName) {
        String query = """
            SELECT COUNT(*) FROM information_schema.columns 
            WHERE table_schema = 'PUBLIC' AND UPPER(table_name) = UPPER(?) AND UPPER(column_name) = UPPER(?)
            """;
        
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, tableName, columnName);
        assertEquals(1, count, 
                "Column '" + columnName + "' should exist in table '" + tableName + "'");
    }
} 