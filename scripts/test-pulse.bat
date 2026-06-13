@echo off
chcp 65001 >nul
setlocal
title Digital TCM Pulse Test

set "SCRIPT_DIR=%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT_DIR%test-pulse.ps1" %*

echo.
pause
endlocal
