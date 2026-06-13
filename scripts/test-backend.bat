@echo off
chcp 65001 >nul
setlocal
title Digital TCM Backend Test

set "SCRIPT_DIR=%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT_DIR%test-backend.ps1" %*

echo.
pause
endlocal
