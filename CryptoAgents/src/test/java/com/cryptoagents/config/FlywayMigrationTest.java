package com.cryptoagents.config;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
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
@TestPropertySource(locations = "classpath:application-test.properties")
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
        // Clean database first
        flyway.clean();
        
        // Run migrations
        var migrationResult = flyway.migrate();
        assertTrue(migrationResult.migrationsExecuted >= 1, 
                "At least one migration should be executed");
        
        // Verify migration state
        var migrationInfo = flyway.info();
        assertEquals(0, migrationInfo.pending().length, 
                "No pending migrations should remain");
    }

    @Test
    void testDatabaseSchemaAfterMigration() {
        // Ensure migrations are applied
        flyway.migrate();
        
        // Test that all expected tables exist
        assertTableExists("analysis_results");
        assertTableExists("analyst_reports");
        assertTableExists("risk_manager_reports");
        assertTableExists("trader_reports");
    }

    @Test
    void testAnalysisResultsTableStructure() {
        flyway.migrate();
        
        // Test basic table structure
        assertColumnExists("analysis_results", "id");
        assertColumnExists("analysis_results", "dtype");
        assertColumnExists("analysis_results", "ticker");
        assertColumnExists("analysis_results", "analysis_time");
        assertColumnExists("analysis_results", "agent_name");
        assertColumnExists("analysis_results", "result_summary");
        assertColumnExists("analysis_results", "confidence_score");
        assertColumnExists("analysis_results", "status");
        assertColumnExists("analysis_results", "processing_time_ms");
        assertColumnExists("analysis_results", "error_message");
        assertColumnExists("analysis_results", "created_at");
        assertColumnExists("analysis_results", "updated_at");
    }

    @Test
    void testAnalystReportsTableStructure() {
        flyway.migrate();
        
        assertColumnExists("analyst_reports", "id");
        assertColumnExists("analyst_reports", "market_trend");
        assertColumnExists("analyst_reports", "technical_indicators");
        assertColumnExists("analyst_reports", "support_level");
        assertColumnExists("analyst_reports", "resistance_level");
        assertColumnExists("analyst_reports", "current_price");
        assertColumnExists("analyst_reports", "price_target");
        assertColumnExists("analyst_reports", "signal_strength");
        assertColumnExists("analyst_reports", "volume_analysis");
        assertColumnExists("analyst_reports", "momentum_indicators");
        assertColumnExists("analyst_reports", "pattern_recognition");
        assertColumnExists("analyst_reports", "time_horizon_days");
    }

    @Test
    void testRiskManagerReportsTableStructure() {
        flyway.migrate();
        
        assertColumnExists("risk_manager_reports", "id");
        assertColumnExists("risk_manager_reports", "risk_level");
        assertColumnExists("risk_manager_reports", "risk_score");
        assertColumnExists("risk_manager_reports", "volatility_score");
        assertColumnExists("risk_manager_reports", "value_at_risk");
        assertColumnExists("risk_manager_reports", "max_drawdown");
        assertColumnExists("risk_manager_reports", "beta_coefficient");
        assertColumnExists("risk_manager_reports", "sharpe_ratio");
        assertColumnExists("risk_manager_reports", "recommended_position_size");
        assertColumnExists("risk_manager_reports", "stop_loss_level");
    }

    @Test
    void testTraderReportsTableStructure() {
        flyway.migrate();
        
        assertColumnExists("trader_reports", "id");
        assertColumnExists("trader_reports", "action_recommendation");
        assertColumnExists("trader_reports", "entry_price");
        assertColumnExists("trader_reports", "exit_price");
        assertColumnExists("trader_reports", "stop_loss");
        assertColumnExists("trader_reports", "take_profit");
        assertColumnExists("trader_reports", "position_size");
        assertColumnExists("trader_reports", "risk_reward_ratio");
        assertColumnExists("trader_reports", "portfolio_allocation");
        assertColumnExists("trader_reports", "order_type");
        assertColumnExists("trader_reports", "time_in_force");
        assertColumnExists("trader_reports", "holding_period_days");
        assertColumnExists("trader_reports", "urgency_level");
        assertColumnExists("trader_reports", "expected_return");
    }

    @Test
    void testForeignKeyConstraints() {
        flyway.migrate();
        
        // Test that foreign key constraints are properly created
        // These constraints ensure referential integrity between tables
        
        // Verify that we can query the constraint information
        String constraintQuery = """
            SELECT tc.constraint_name, tc.table_name, kcu.column_name, ccu.table_name AS foreign_table_name
            FROM information_schema.table_constraints AS tc 
            JOIN information_schema.key_column_usage AS kcu 
                ON tc.constraint_name = kcu.constraint_name
            JOIN information_schema.constraint_column_usage AS ccu 
                ON ccu.constraint_name = tc.constraint_name
            WHERE tc.constraint_type = 'FOREIGN KEY'
            AND tc.table_schema = 'public'
            AND tc.table_name IN ('analyst_reports', 'risk_manager_reports', 'trader_reports')
            """;
        
        var constraints = jdbcTemplate.queryForList(constraintQuery);
        
        // Should have 3 foreign key constraints (one for each specialized table)
        assertEquals(3, constraints.size(), 
                "Should have foreign key constraints for all specialized tables");
    }

    @Test
    void testIndexCreation() {
        flyway.migrate();
        
        // Test that indexes are created for performance optimization
        String indexQuery = """
            SELECT indexname, tablename 
            FROM pg_indexes 
            WHERE schemaname = 'public' 
            AND tablename IN ('analysis_results', 'analyst_reports', 'risk_manager_reports', 'trader_reports')
            ORDER BY tablename, indexname
            """;
        
        var indexes = jdbcTemplate.queryForList(indexQuery);
        
        // Should have multiple indexes for query optimization
        assertTrue(indexes.size() > 10, 
                "Should have multiple indexes created for performance");
        
        // Verify some key indexes exist
        boolean hasTickerIndex = indexes.stream()
                .anyMatch(idx -> idx.get("indexname").toString().contains("ticker"));
        assertTrue(hasTickerIndex, "Should have ticker index for fast lookups");
    }

    @Test
    void testMigrationIdempotency() {
        // Run migration twice to test idempotency
        flyway.clean();
        flyway.migrate();
        
        var firstResult = flyway.info();
        
        // Run migration again
        var secondResult = flyway.migrate();
        
        // No additional migrations should be executed
        assertEquals(0, secondResult.migrationsExecuted, 
                "Second migration run should execute no additional migrations");
        
        // Schema should remain the same
        assertTableExists("analysis_results");
        assertTableExists("analyst_reports");
        assertTableExists("risk_manager_reports");
        assertTableExists("trader_reports");
    }

    // Helper methods for assertions
    private void assertTableExists(String tableName) {
        String query = """
            SELECT COUNT(*) FROM information_schema.tables 
            WHERE table_schema = 'public' AND table_name = ?
            """;
        
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, tableName);
        assertEquals(1, count, "Table '" + tableName + "' should exist");
    }

    private void assertColumnExists(String tableName, String columnName) {
        String query = """
            SELECT COUNT(*) FROM information_schema.columns 
            WHERE table_schema = 'public' AND table_name = ? AND column_name = ?
            """;
        
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, tableName, columnName);
        assertEquals(1, count, 
                "Column '" + columnName + "' should exist in table '" + tableName + "'");
    }
} 