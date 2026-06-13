@echo off
chcp 65001 >nul
setlocal EnableExtensions
title Digital TCM Stop

set "DRY_RUN="
if /I "%~1"=="check" set "DRY_RUN=1"

set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "ROOT_DIR=%%~fI"
set "RUNTIME_DIR=%SCRIPT_DIR%.runtime"
set "PID_FILE=%RUNTIME_DIR%\pids.txt"
set "SEEN_PIDS= "
if not defined REDIS_DIR set "REDIS_DIR=D:\Redis"
set "REDIS_EXE=%REDIS_DIR%\redis-server.exe"

echo Closing services started by Digital TCM scripts...
if defined DRY_RUN echo Check mode: no processes will be stopped.

if exist "%PID_FILE%" (
    echo.
    echo PIDs recorded in: %PID_FILE%
    for /f "usebackq delims=" %%P in ("%PID_FILE%") do (
        echo %%P| findstr /R "^[0-9][0-9]*$" >nul && call :stop_pid %%P "recorded PID"
    )
) else (
    echo.
    echo No PID file found. Falling back to local dev ports and known Redis path.
)

echo.
echo Checking local dev ports...
call :stop_port 8080
call :stop_port 8089
call :stop_port 5174

echo.
echo Checking Redis started from: %REDIS_EXE%
if exist "%REDIS_EXE%" (
    for /f %%P in ('powershell -NoProfile -ExecutionPolicy Bypass -Command "Get-Process redis-server -ErrorAction SilentlyContinue | Where-Object { $_.Path -eq $env:REDIS_EXE } | ForEach-Object { $_.Id }"') do call :stop_pid %%P "Redis"
) else (
    echo Redis executable not found at the configured path, Redis fallback skipped.
)

echo.
for %%T in ("Digital TCM Redis*" "Digital TCM ruoyi-admin*" "Digital TCM Admin Web*" "Digital TCM App Frontend*" "Digital TCM Mobile H5*") do (
    if defined DRY_RUN (
        echo Would close window title: %%~T
    ) else (
        taskkill /FI "WINDOWTITLE eq %%~T" /T /F >nul 2>nul
    )
)

if not defined DRY_RUN if exist "%PID_FILE%" del /f /q "%PID_FILE%" >nul 2>nul

echo.
echo Done.
pause
endlocal
exit /b 0

:stop_port
for /f "tokens=5" %%P in ('netstat -ano ^| findstr /R /C:"LISTENING" ^| findstr /R /C:":%~1 "') do call :stop_pid %%P "port %~1"
exit /b 0

:stop_pid
if "%~1"=="0" exit /b 0
echo %SEEN_PIDS%| findstr /C:" %~1 " >nul && exit /b 0
set "SEEN_PIDS=%SEEN_PIDS%%~1 "
if defined DRY_RUN (
    echo Would stop %~2: PID %~1
) else (
    taskkill /PID %~1 /T /F >nul 2>nul
    if errorlevel 1 (
        echo Already stopped or inaccessible: PID %~1
    ) else (
        echo Stopped %~2: PID %~1
    )
)
exit /b 0
