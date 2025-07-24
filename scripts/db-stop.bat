@echo off
echo Stopping CryptoAgents PostgreSQL database...

REM Stop PostgreSQL container
docker-compose down

echo PostgreSQL container stopped.
echo.
echo To start the database again: scripts\db-start.bat
echo.
pause 