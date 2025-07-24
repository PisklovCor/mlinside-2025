-- CryptoAgents PostgreSQL Initialization Script
-- This script runs automatically when the PostgreSQL container starts for the first time

-- Create extensions useful for crypto analysis
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_stat_statements";

-- Create a function to automatically update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create additional indexes for performance optimization
-- (These will be created after Flyway migrations run)

-- Grant necessary permissions
GRANT ALL PRIVILEGES ON DATABASE cryptoagents TO postgres;

-- Log initialization completion
DO $$
BEGIN
    RAISE NOTICE 'CryptoAgents database initialization completed successfully';
END
$$; 