@echo off
chcp 65001 >nul
setlocal EnableExtensions

set "SCRIPT_DIR=%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT_DIR%update-database.ps1" %*
if errorlevel 1 (
    echo.
    echo [ERROR] Database update failed.
    pause
    exit /b 1
)

echo.
echo [OK] Database update completed.
pause
endlocal
