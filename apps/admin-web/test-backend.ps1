param([Parameter(ValueFromRemainingArguments = $true)][string[]]$Args)
$root = Resolve-Path (Join-Path $PSScriptRoot "..\..")
& (Join-Path $root "scripts\test-backend.ps1") @Args
