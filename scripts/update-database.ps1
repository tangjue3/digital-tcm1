param(
    [string]$SqlFile = "",
    [string]$Database = "",
    [string]$User = "",
    [string]$Password = "",
    [string]$VerifyTable = "tcm_pulse_record",
    [string[]]$VerifyColumns = @("id", "user_id", "pulse_rate", "sampled_at", "received_at")
)

$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$rootDir = Split-Path -Parent $scriptDir

if ([string]::IsNullOrWhiteSpace($SqlFile)) {
    $SqlFile = Join-Path $rootDir "database\sql\tcm_pulse_record.sql"
}
if ([string]::IsNullOrWhiteSpace($Database)) {
    $Database = if ($env:MYSQL_DATABASE) { $env:MYSQL_DATABASE } else { "tcm_vsl" }
}
if ([string]::IsNullOrWhiteSpace($User)) {
    $User = if ($env:MYSQL_USER) { $env:MYSQL_USER } else { "root" }
}
if ([string]::IsNullOrWhiteSpace($Password)) {
    $Password = if ($env:MYSQL_PASSWORD) { $env:MYSQL_PASSWORD } else { "123456" }
}

if (-not (Test-Path -LiteralPath $SqlFile)) {
    throw "SQL file not found: $SqlFile"
}

function Find-MySqlExe {
    if ($env:MYSQL_EXE -and (Test-Path -LiteralPath $env:MYSQL_EXE)) {
        return $env:MYSQL_EXE
    }

    $command = Get-Command mysql -ErrorAction SilentlyContinue
    if ($command) {
        return $command.Source
    }

    $candidates = @(
        "D:\MySQL\MySQL Server 8.0\bin\mysql.exe",
        "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
        "C:\Program Files\MySQL\MySQL Server 5.7\bin\mysql.exe",
        "C:\Program Files\MariaDB 11.0\bin\mysql.exe",
        "C:\Program Files\MariaDB 10.11\bin\mysql.exe"
    )

    foreach ($candidate in $candidates) {
        if (Test-Path -LiteralPath $candidate) {
            return $candidate
        }
    }

    throw "mysql.exe was not found. Set MYSQL_EXE to the full mysql.exe path."
}

function Invoke-MySqlText {
    param(
        [string]$MySqlExe,
        [string[]]$Arguments,
        [string]$InputText = $null
    )

    $oldPassword = $env:MYSQL_PWD
    $env:MYSQL_PWD = $Password
    try {
        if ($null -eq $InputText) {
            $output = & $MySqlExe @Arguments 2>&1
        } else {
            $output = $InputText | & $MySqlExe @Arguments 2>&1
        }
        if ($LASTEXITCODE -ne 0) {
            throw ($output -join [Environment]::NewLine)
        }
        return $output
    } finally {
        $env:MYSQL_PWD = $oldPassword
    }
}

$mysqlExe = Find-MySqlExe
$sqlText = Get-Content -LiteralPath $SqlFile -Raw -Encoding UTF8

Write-Host "================================"
Write-Host "Digital TCM database update"
Write-Host "================================"
Write-Host "MySQL:    $mysqlExe"
Write-Host "Database: $Database"
Write-Host "SQL:      $SqlFile"
Write-Host "Verify:   $VerifyTable"
Write-Host ""

Invoke-MySqlText `
    -MySqlExe $mysqlExe `
    -Arguments @("--default-character-set=utf8mb4", "-u$User", $Database) `
    -InputText $sqlText | Out-Null

$tableQuery = "select count(*) from information_schema.tables where table_schema = '$Database' and table_name = '$VerifyTable';"
$tableCount = Invoke-MySqlText `
    -MySqlExe $mysqlExe `
    -Arguments @("-N", "-B", "--default-character-set=utf8mb4", "-u$User", "-e", $tableQuery)

if ([int]($tableCount | Select-Object -First 1) -ne 1) {
    throw "Verification failed: table '$VerifyTable' was not found in database '$Database'."
}

foreach ($column in $VerifyColumns) {
    $columnQuery = "select count(*) from information_schema.columns where table_schema = '$Database' and table_name = '$VerifyTable' and column_name = '$column';"
    $columnCount = Invoke-MySqlText `
        -MySqlExe $mysqlExe `
        -Arguments @("-N", "-B", "--default-character-set=utf8mb4", "-u$User", "-e", $columnQuery)

    if ([int]($columnCount | Select-Object -First 1) -ne 1) {
        throw "Verification failed: column '$VerifyTable.$column' was not found."
    }
}

Write-Host "Database update completed and verified."
