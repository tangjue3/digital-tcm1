@echo off
setlocal

cd /d "%~dp0\.."

echo [1/8] Checking Git...
where git >nul 2>nul
if errorlevel 1 (
  echo Git is not installed or not in PATH.
  pause
  exit /b 1
)

echo [2/8] Cleaning failed local Git metadata...
if exist ".git" rmdir /s /q ".git"
if exist ".gitrepo" rmdir /s /q ".gitrepo"

echo [3/8] Initializing repository...
git init
if errorlevel 1 goto failed

echo [4/8] Configuring local author...
git config user.name "sanctuary906"
git config user.email "279188827+sanctuary906@users.noreply.github.com"
git branch -M main

echo [5/8] Staging files...
git add .
if errorlevel 1 goto failed

echo [6/8] Creating initial commit...
git commit -m "Initial commit"
if errorlevel 1 goto failed

echo [7/8] Setting GitHub remote...
git remote remove origin >nul 2>nul
git remote add origin https://github.com/sanctuary906/digital-tcm.git
if errorlevel 1 goto failed

echo [8/8] Pushing to GitHub...
git push -u origin main
if errorlevel 1 goto failed

echo.
echo Done. Project pushed to:
echo https://github.com/sanctuary906/digital-tcm
pause
exit /b 0

:failed
echo.
echo Publish failed. Read the message above.
echo If GitHub asks you to sign in, finish the browser login and run this script again.
pause
exit /b 1
