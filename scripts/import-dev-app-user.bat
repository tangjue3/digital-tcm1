@echo off
chcp 65001 >nul
setlocal EnableExtensions
title Digital TCM Import Dev App User

set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "ROOT_DIR=%%~fI"
set "SQL_FILE=%ROOT_DIR%\database\sql\dev_app_user.sql"

if not defined MYSQL_DATABASE set "MYSQL_DATABASE=tcm_vsl"
if not defined MYSQL_USER set "MYSQL_USER=root"
if not defined MYSQL_PASSWORD set "MYSQL_PASSWORD=123456"

if not exist "%SQL_FILE%" (
    echo [ERROR] SQL file not found: %SQL_FILE%
    pause
    exit /b 1
)

if not defined MYSQL_EXE (
    for %%P in (
        "mysql.exe"
        "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
        "C:\Program Files\MySQL\MySQL Server 5.7\bin\mysql.exe"
        "C:\Program Files\MariaDB 11.0\bin\mysql.exe"
        "C:\Program Files\MariaDB 10.11\bin\mysql.exe"
    ) do (
        if not defined MYSQL_EXE (
            if exist "%%~P" set "MYSQL_EXE=%%~P"
        )
    )
)

if not defined MYSQL_EXE (
    where mysql.exe >nul 2>nul
    if not errorlevel 1 set "MYSQL_EXE=mysql.exe"
)

if not defined MYSQL_EXE (
    echo [ERROR] mysql.exe was not found.
    echo.
    echo Please run this SQL manually in Navicat, DBeaver, DataGrip, or MySQL Workbench:
    echo %SQL_FILE%
    echo.
    echo Connection:
    echo Host:     localhost:3306
    echo Database: %MYSQL_DATABASE%
    echo User:     %MYSQL_USER%
    echo Password: %MYSQL_PASSWORD%
    echo.
    echo Or set MYSQL_EXE before running this script, for example:
    echo set MYSQL_EXE=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
    echo import-dev-app-user.bat
    pause
    exit /b 1
)

echo ================================
echo Import Digital TCM dev app user
echo ================================
echo MySQL:    %MYSQL_EXE%
echo Database: %MYSQL_DATABASE%
echo SQL:      %SQL_FILE%
echo Account:  13800138000 / 123456
echo.

"%MYSQL_EXE%" -u%MYSQL_USER% -p%MYSQL_PASSWORD% %MYSQL_DATABASE% < "%SQL_FILE%"
if errorlevel 1 (
    echo.
    echo [ERROR] SQL import failed.
    echo Check that MySQL is running and the database exists: %MYSQL_DATABASE%
    pause
    exit /b 1
)

echo.
echo ================================
echo SQL import completed.
echo Restart backend with:
echo stop-project.bat
echo start-project.bat
echo ================================
pause
endlocal
