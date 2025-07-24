#!/bin/bash

echo "Starting CryptoAgents PostgreSQL database..."

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker first."
    exit 1
fi

# Start PostgreSQL container
docker-compose up -d postgres

# Wait for database to be ready
echo "Waiting for PostgreSQL to be ready..."
while ! docker-compose exec postgres pg_isready -U postgres -d cryptoagents >/dev/null 2>&1; do
    echo "Still waiting for database..."
    sleep 2
done

echo "PostgreSQL is ready!"
echo ""
echo "Database connection details:"
echo "  Host: localhost"
echo "  Port: 5432"
echo "  Database: cryptoagents"
echo "  Username: postgres"
echo "  Password: password"
echo ""
echo "To stop the database: ./scripts/db-stop.sh"
echo "To view logs: ./scripts/db-logs.sh"
echo "" 