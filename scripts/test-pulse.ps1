param(
    [string]$BaseUrl = $env:TCM_BACKEND_URL,
    [string]$Username = "",
    [string]$Password = "",
    [int]$PulseRate = 0
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($BaseUrl)) {
    $BaseUrl = "http://127.0.0.1:8080"
}
$BaseUrl = $BaseUrl.TrimEnd("/")

if ([string]::IsNullOrWhiteSpace($Username)) {
    $Username = if ($env:TCM_TEST_USERNAME) { $env:TCM_TEST_USERNAME } else { "13800138000" }
}
if ([string]::IsNullOrWhiteSpace($Password)) {
    $Password = if ($env:TCM_TEST_PASSWORD) { $env:TCM_TEST_PASSWORD } else { "123456" }
}
if ($PulseRate -le 0) {
    $PulseRate = Get-Random -Minimum 62 -Maximum 96
}

$stamp = Get-Date -Format "yyyyMMddHHmmss"
$sampledAt = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
$deviceId = "smoke-test-$stamp"
$deviceDirectId = "bracelet-test-$stamp"
$devicePulseRate = if ($PulseRate -lt 220) { $PulseRate + 1 } else { $PulseRate - 1 }

function Invoke-Json {
    param(
        [Parameter(Mandatory = $true)][string]$Method,
        [Parameter(Mandatory = $true)][string]$Url,
        [object]$Body = $null,
        [hashtable]$Headers = @{}
    )

    if ($null -eq $Body) {
        return Invoke-RestMethod -Method $Method -Uri $Url -Headers $Headers -TimeoutSec 15
    }

    $json = $Body | ConvertTo-Json -Depth 10
    return Invoke-RestMethod -Method $Method -Uri $Url -Headers $Headers -ContentType "application/json;charset=UTF-8" -Body $json -TimeoutSec 15
}

Write-Host "================================"
Write-Host "Digital TCM pulse smoke test"
Write-Host "Backend: $BaseUrl"
Write-Host "Account: $Username"
Write-Host "Pulse:   $PulseRate bpm"
Write-Host "================================"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$databaseScript = Join-Path $scriptDir "update-database.ps1"
if (Test-Path -LiteralPath $databaseScript) {
    Write-Host ""
    Write-Host "[0/7] Ensure pulse table exists"
    & powershell -NoProfile -ExecutionPolicy Bypass -File $databaseScript
    if ($LASTEXITCODE -ne 0) {
        throw "Database update script failed."
    }
}

try {
    Invoke-WebRequest -Uri "$BaseUrl/captchaImage" -UseBasicParsing -TimeoutSec 3 | Out-Null
} catch {
    throw "Backend is not running at $BaseUrl. Start it with start-project.bat, then rerun scripts/test-pulse.bat."
}

Write-Host ""
Write-Host "[1/7] POST /login"
$loginResult = Invoke-Json -Method POST -Url "$BaseUrl/login" -Body @{
    username = $Username
    password = $Password
    code = ""
    uuid = ""
}

$token = $loginResult.token
if ([string]::IsNullOrWhiteSpace($token)) {
    throw "Login did not return token. If this is a fresh database, run scripts/import-dev-app-user.bat first."
}
$headers = @{
    Authorization = "Bearer $token"
}
Write-Host "login code:" $loginResult.code

Write-Host ""
Write-Host "[2/7] GET /getInfo"
$info = Invoke-Json -Method GET -Url "$BaseUrl/getInfo" -Headers $headers
$userId = $info.user.userId
if ($null -eq $userId) {
    throw "getInfo did not return user.userId."
}
Write-Host "userId:" $userId ", nickName:" $info.user.nickName

Write-Host ""
Write-Host "[3/7] POST /app/pulse/upload"
$uploadResult = Invoke-Json -Method POST -Url "$BaseUrl/app/pulse/upload" -Headers $headers -Body @{
    pulseRate = $PulseRate
    deviceId = $deviceId
    signalQuality = 100
    sampledAt = $sampledAt
    source = "script"
    remark = "scripts/test-pulse.ps1"
}
Write-Host "upload code:" $uploadResult.code ", msg:" $uploadResult.msg
if ($uploadResult.code -ne 200) {
    throw "Pulse upload failed: $($uploadResult.msg)"
}

Write-Host ""
Write-Host "[4/7] GET /app/pulse/latest after app upload"
$latestApp = Invoke-Json -Method GET -Url "$BaseUrl/app/pulse/latest" -Headers $headers
Write-Host "latest app pulse:" $latestApp.data.pulseRate ", device:" $latestApp.data.deviceId
if ($latestApp.data.pulseRate -ne $PulseRate -or $latestApp.data.deviceId -ne $deviceId) {
    throw "App latest pulse verification failed."
}

Write-Host ""
Write-Host "[5/7] POST /device/pulse/upload without app token"
$deviceUploadResult = Invoke-Json -Method POST -Url "$BaseUrl/device/pulse/upload" -Body @{
    userId = $userId
    pulseRate = $devicePulseRate
    deviceId = $deviceDirectId
    signalQuality = 98
    sampledAt = (Get-Date).AddSeconds(1).ToString("yyyy-MM-dd HH:mm:ss")
    source = "device"
    remark = "scripts/test-pulse.ps1 direct device upload"
}
Write-Host "device upload code:" $deviceUploadResult.code ", msg:" $deviceUploadResult.msg
if ($deviceUploadResult.code -ne 200) {
    throw "Device pulse upload failed: $($deviceUploadResult.msg)"
}

Write-Host ""
Write-Host "[6/7] GET /app/pulse/latest after device upload"
$latestDeviceApp = Invoke-Json -Method GET -Url "$BaseUrl/app/pulse/latest" -Headers $headers
Write-Host "latest app pulse:" $latestDeviceApp.data.pulseRate ", device:" $latestDeviceApp.data.deviceId
if ($latestDeviceApp.data.pulseRate -ne $devicePulseRate -or $latestDeviceApp.data.deviceId -ne $deviceDirectId) {
    throw "App latest pulse did not sync device upload."
}

Write-Host ""
Write-Host "[7/7] GET /system/pulse/latest/$userId"
$latestSystem = Invoke-Json -Method GET -Url "$BaseUrl/system/pulse/latest/$userId" -Headers $headers
Write-Host "latest system pulse:" $latestSystem.data.pulseRate ", sampledAt:" $latestSystem.data.sampledAt
if ($latestSystem.data.pulseRate -ne $devicePulseRate -or $latestSystem.data.deviceId -ne $deviceDirectId) {
    throw "System latest pulse verification failed."
}

Write-Host ""
Write-Host "================================"
Write-Host "Pulse smoke test passed."
Write-Host "UserId:          $userId"
Write-Host "App pulse:       $PulseRate bpm"
Write-Host "App deviceId:    $deviceId"
Write-Host "Device pulse:    $devicePulseRate bpm"
Write-Host "Device deviceId: $deviceDirectId"
Write-Host "================================"
