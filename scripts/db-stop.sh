#!/bin/bash

echo "Stopping CryptoAgents PostgreSQL database..."

# Stop PostgreSQL container
docker-compose down

echo "PostgreSQL container stopped."
echo ""
echo "To start the database again: ./scripts/db-start.sh"
echo "" 