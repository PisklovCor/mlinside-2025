@echo off
echo Starting CryptoAgents PostgreSQL database...

REM Check if Docker is running
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Docker is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

REM Start PostgreSQL container
docker-compose up -d postgres

REM Wait for database to be ready
echo Waiting for PostgreSQL to be ready...
:wait_loop
docker-compose exec postgres pg_isready -U postgres -d cryptoagents >nul 2>&1
if %errorlevel% neq 0 (
    echo Still waiting for database...
    timeout /t 2 /nobreak >nul
    goto wait_loop
)

echo PostgreSQL is ready!
echo.
echo Database connection details:
echo   Host: localhost
echo   Port: 5432
echo   Database: cryptoagents
echo   Username: postgres
echo   Password: password
echo.
echo To stop the database: scripts\db-stop.bat
echo To view logs: scripts\db-logs.bat
echo.
pause 