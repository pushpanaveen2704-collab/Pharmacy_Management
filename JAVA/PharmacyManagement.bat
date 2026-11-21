@echo off
setlocal enabledelayedexpansion
title PHARMACY MANAGEMENT SYSTEM - CENTRALIZED MAIN MENU
cd /d "%~dp0"

echo ============================================================
echo   PHARMACY STOCK MANAGEMENT AND MEDICINE SALES ANALYSIS
echo   Module: Centralized Main Menu & Dashboard
echo ============================================================
echo.

if not exist bin mkdir bin

echo Checking and compiling Java source files...
javac -encoding UTF-8 -cp "lib/*;lib/sqlite-jdbc-3.45.1.0.jar;lib/slf4j-api-1.7.36.jar" -d bin src/com/pharmacy/model/*.java src/com/pharmacy/util/*.java src/com/pharmacy/dao/*.java src/com/pharmacy/service/*.java src/com/pharmacy/main/*.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java compilation failed! Please verify JDK 17+ installation.
    pause
    exit /b %ERRORLEVEL%
)

echo Starting Centralized Pharmacy Management System...
echo.
java -cp "bin;lib/*" com.pharmacy.main.PharmacyManagementSystem

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Application terminated with error code %ERRORLEVEL%
)

echo.
echo ============================================================
echo   Session complete. Transcript saved to PharmacyManagement_Output.txt
echo ============================================================
echo.
pause
