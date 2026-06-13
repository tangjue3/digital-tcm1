@echo off
chcp 65001 >nul
setlocal EnableExtensions EnableDelayedExpansion
title Digital TCM Start

set "DRY_RUN="
if /I "%~1"=="check" set "DRY_RUN=1"

set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "ROOT_DIR=%%~fI"
set "BACKEND_ROOT=%ROOT_DIR%\server\ruoyi-backend"
set "BACKEND_SECRET_CONFIG=%BACKEND_ROOT%\config\application-secret.yml"
set "ADMIN_WEB=%ROOT_DIR%\apps\admin-web"
set "MOBILE_APP=%ROOT_DIR%\apps\mobile-app"
set "ENV_BATCH=%ROOT_DIR%\env\local-dev.bat"
set "RUNTIME_DIR=%SCRIPT_DIR%.runtime"
set "PID_FILE=%RUNTIME_DIR%\pids.txt"
if not defined REDIS_DIR set "REDIS_DIR=D:\Redis"

if exist "%ENV_BATCH%" (
    call "%ENV_BATCH%"
)

echo ================================
echo Start Digital TCM local project
echo ================================
if defined DRY_RUN echo Check mode: no services will be started.
echo Root:        %ROOT_DIR%
echo Backend:     %BACKEND_ROOT%
echo Backend cfg: %BACKEND_SECRET_CONFIG%
echo Admin web:   %ADMIN_WEB%
echo Mobile app:  %MOBILE_APP%
echo Redis dir:   %REDIS_DIR%
if defined MYSQL_USERNAME (
    echo MySQL user:   %MYSQL_USERNAME%
) else (
    echo MySQL user:   [not set, backend default will be used]
)
if defined MYSQL_PASSWORD (
    echo MySQL pass:   [configured]
) else (
    echo MySQL pass:   [empty]
)
echo.

if not defined DRY_RUN if not exist "%RUNTIME_DIR%" mkdir "%RUNTIME_DIR%" >nul 2>nul

if not exist "%BACKEND_ROOT%\pom.xml" echo [WARN] Backend pom.xml not found.
if not exist "%BACKEND_ROOT%\ruoyi-admin\pom.xml" echo [WARN] ruoyi-admin pom.xml not found.
if not exist "%BACKEND_SECRET_CONFIG%" echo [WARN] Backend private config not found: %BACKEND_SECRET_CONFIG%
if not exist "%ADMIN_WEB%\package.json" echo [WARN] Admin web package.json not found.
if not exist "%MOBILE_APP%\package.json" echo [WARN] Mobile app package.json not found.

REM ====== 1. Redis ======
echo [1/4] Start Redis...
if exist "%REDIS_DIR%\redis-server.exe" (
    if defined DRY_RUN (
        echo Would start Redis from: %REDIS_DIR%
    ) else (
        call :start_tracked "Digital TCM Redis" "%REDIS_DIR%" "redis-server.exe"
    )
) else (
    echo Redis executable not found: %REDIS_DIR%\redis-server.exe
    echo If Redis is already running, you can ignore this warning.
)

if not defined DRY_RUN timeout /t 3 /nobreak >nul

REM ====== 2. Backend build ======
echo.
echo [2/4] Build and install RuoYi backend modules...
if exist "%BACKEND_ROOT%\pom.xml" (
    if defined DRY_RUN (
        echo Would build and install backend in: %BACKEND_ROOT%
    ) else (
        pushd "%BACKEND_ROOT%"
        call mvn -pl ruoyi-admin -am install -DskipTests
        if errorlevel 1 (
            echo [ERROR] Backend build failed. ruoyi-admin will not be started.
            popd
            pause
            endlocal
            exit /b 1
        )
        popd
    )
) else (
    echo Backend build skipped.
)

if not defined DRY_RUN timeout /t 2 /nobreak >nul

REM ====== 3. Backend ======
echo.
echo [3/4] Start ruoyi-admin...
if not defined MYSQL_PASSWORD (
    echo [WARN] MYSQL_PASSWORD is empty.
    echo        If your local MySQL root account has a password, backend startup will fail.
    echo        Create env\local-dev.bat and set MYSQL_USERNAME / MYSQL_PASSWORD first.
)
if exist "%BACKEND_ROOT%\ruoyi-admin\pom.xml" (
    if defined DRY_RUN (
        echo Would start ruoyi-admin in: %BACKEND_ROOT%\ruoyi-admin
    ) else (
        call :start_tracked "Digital TCM ruoyi-admin" "%BACKEND_ROOT%\ruoyi-admin" "mvn spring-boot:run -Dspring-boot.run.arguments=--spring.config.additional-location=optional:file:../config/application-secret.yml"
    )
) else (
    echo ruoyi-admin start skipped.
)

if not defined DRY_RUN timeout /t 12 /nobreak >nul

if not defined DRY_RUN (
    echo Waiting for ruoyi-admin to become ready...
    powershell -NoProfile -ExecutionPolicy Bypass -Command "$deadline=(Get-Date).AddSeconds(60); while((Get-Date) -lt $deadline){ try { $r=Invoke-WebRequest -Uri 'http://127.0.0.1:8080/captchaImage' -UseBasicParsing -TimeoutSec 2; if($r.StatusCode -ge 200){ exit 0 } } catch { }; Start-Sleep -Seconds 1 }; exit 1"
    if errorlevel 1 (
        echo [WARN] ruoyi-admin did not respond on http://127.0.0.1:8080 within 60 seconds.
        echo       Admin web will still start, but API requests may fail until the backend finishes starting.
    ) else (
        echo ruoyi-admin is ready.
    )
)

REM ====== 4. Admin frontend ======
echo.
echo [4/4] Start admin web...
if exist "%ADMIN_WEB%\package.json" (
    if not exist "%ADMIN_WEB%\node_modules" (
        echo admin-web node_modules not found.
        if defined DRY_RUN (
            echo Would run npm install in: %ADMIN_WEB%
        ) else (
            pushd "%ADMIN_WEB%"
            call npm install
            popd
        )
    )
    if defined DRY_RUN (
        echo Would start admin web in: %ADMIN_WEB%
        echo Would open: http://127.0.0.1:8089/
    ) else (
        call :start_tracked "Digital TCM Admin Web" "%ADMIN_WEB%" "npm run dev -- --host 127.0.0.1"
        timeout /t 3 /nobreak >nul
        start "" "http://127.0.0.1:8089/"
    )
) else (
    echo Admin web start skipped.
)

echo.
echo ================================
echo Startup commands have been launched.
echo Backend:   http://127.0.0.1:8080
echo Admin web: http://127.0.0.1:8089
echo API proxy: /api -^> http://127.0.0.1:8080
echo ================================
echo Notes:
echo - MySQL database tcm_vsl must be ready before ruoyi-admin can start.
echo - Optional local env loader: %ENV_BATCH%
echo - Optional backend private config: %BACKEND_SECRET_CONFIG%
echo - Redis default path is D:\Redis. Set REDIS_DIR before running this script to override it.
echo - Mobile app H5 is started separately by scripts\start-mobile-h5.bat.
echo - Run start-project.bat check to verify paths without starting services.
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
