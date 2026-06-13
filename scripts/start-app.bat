@echo off
chcp 65001 >nul
setlocal EnableExtensions
title Digital TCM App Frontend

set "DRY_RUN="
if /I "%~1"=="check" set "DRY_RUN=1"

set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "ROOT_DIR=%%~fI"
set "APP_DIR=%ROOT_DIR%\apps\mobile-app"
set "APP_URL=http://localhost:5174/"
set "RUNTIME_DIR=%SCRIPT_DIR%.runtime"
set "PID_FILE=%RUNTIME_DIR%\pids.txt"

echo ================================
echo Start Digital TCM app frontend
echo ================================
echo Root:    %ROOT_DIR%
echo App dir: %APP_DIR%
echo URL:     %APP_URL%
if defined DRY_RUN echo Check mode: no service will be started.
echo.

if not defined DRY_RUN if not exist "%RUNTIME_DIR%" mkdir "%RUNTIME_DIR%" >nul 2>nul

if not exist "%APP_DIR%\package.json" (
    echo [ERROR] package.json not found: %APP_DIR%\package.json
    pause
    exit /b 1
)

where npm >nul 2>nul
if errorlevel 1 (
    echo [ERROR] npm was not found. Please install Node.js first.
    pause
    exit /b 1
)

if not exist "%APP_DIR%\node_modules" (
    echo node_modules not found.
    if defined DRY_RUN (
        echo Would run npm install in: %APP_DIR%
    ) else (
        pushd "%APP_DIR%"
        call npm install
        if errorlevel 1 (
            popd
            echo [ERROR] npm install failed.
            pause
            exit /b 1
        )
        popd
    )
)

if defined DRY_RUN (
    echo Would run: npm run dev:h5
    echo Would start in: %APP_DIR%
    echo Would open: %APP_URL%
) else (
    call :start_tracked "Digital TCM App Frontend" "%APP_DIR%" "npm run dev:h5"
    timeout /t 5 /nobreak >nul
    start "" "%APP_URL%"
)

echo.
echo ================================
echo App frontend startup command has been launched.
echo App H5: %APP_URL%
echo API proxy: /api -^> http://127.0.0.1:8080
echo ================================
echo Notes:
echo - If port 5174 is already in use, close the old dev server first.
echo - Run start-app.bat check to verify paths without starting the app.
echo ================================
if not defined DRY_RUN pause
endlocal
exit /b 0

:start_tracked
set "TRACK_NAME=%~1"
set "TRACK_DIR=%~2"
set "TRACK_CMD=title %~1 && %~3"
for /f %%P in ('powershell -NoProfile -ExecutionPolicy Bypass -Command "$p=Start-Process -FilePath 'cmd.exe' -ArgumentList '/k',$env:TRACK_CMD -WorkingDirectory $env:TRACK_DIR -PassThru; $p.Id"') do (
    >>"%PID_FILE%" echo %%P
    echo %~1 PID: %%P
)
exit /b 0
