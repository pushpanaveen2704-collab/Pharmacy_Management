@echo off
setlocal enabledelayedexpansion
title PHARMACY MANAGEMENT SYSTEM - PURCHASE MANAGEMENT
cd /d "%~dp0"

echo ============================================================
echo   PHARMACY STOCK MANAGEMENT AND MEDICINE SALES ANALYSIS
echo   Module: Purchase Management
echo ============================================================
echo.

if not exist bin mkdir bin

javac -encoding UTF-8 -cp "lib/*;lib/sqlite-jdbc-3.45.1.0.jar;lib/slf4j-api-1.7.36.jar" -d bin src/com/pharmacy/model/*.java src/com/pharmacy/util/*.java src/com/pharmacy/dao/*.java src/com/pharmacy/service/*.java src/com/pharmacy/main/*.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo Starting Purchase Management Module...
echo.
java -cp "bin;lib/*" com.pharmacy.main.PurchaseManagementApp

echo.
echo ============================================================
echo   Output logged to PurchaseManagement_Output.txt
echo ============================================================
echo.
pause
