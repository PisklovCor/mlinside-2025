@echo off
echo Showing CryptoAgents PostgreSQL database logs...
echo Press Ctrl+C to exit log view
echo.

docker-compose logs -f postgres 