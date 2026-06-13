param(
    [string]$BaseUrl = $env:TCM_BACKEND_URL
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($BaseUrl)) {
    $BaseUrl = "http://127.0.0.1:8080"
}
$BaseUrl = $BaseUrl.TrimEnd("/")

$stamp = Get-Date -Format "yyyyMMddHHmmss"
$caseNo = "islab_demo_$stamp"
$userId = "demo-user"

function Invoke-Json {
    param(
        [Parameter(Mandatory = $true)][string]$Method,
        [Parameter(Mandatory = $true)][string]$Url,
        [object]$Body = $null
    )

    if ($null -eq $Body) {
        return Invoke-RestMethod -Method $Method -Uri $Url -TimeoutSec 15
    }

    $json = $Body | ConvertTo-Json -Depth 10
    return Invoke-RestMethod -Method $Method -Uri $Url -ContentType "application/json;charset=UTF-8" -Body $json -TimeoutSec 15
}

Write-Host "================================"
Write-Host "Digital TCM backend smoke test"
Write-Host "Backend: $BaseUrl"
Write-Host "================================"

Write-Host ""
Write-Host "[1/6] GET /system/case/list"
$caseList = Invoke-Json -Method GET -Url "$BaseUrl/system/case/list?pageNum=1&pageSize=1"
Write-Host "case list code:" $caseList.code ", total:" $caseList.total

Write-Host ""
Write-Host "[2/6] POST /system/case create a demo case"
$caseBody = @{
    caseName = "ISLab demo case $stamp"
    caseNo = $caseNo
    caseRemark = "Demo case created by scripts/test-backend.ps1."
    picture = "/logo2.png"
    results = "Demo diagnosis result"
    treatmentMethods = "Demo treatment method"
    extend1 = "demo"
    extend2 = "smoke-test"
}
$caseResult = Invoke-Json -Method POST -Url "$BaseUrl/system/case" -Body $caseBody
Write-Host "create case code:" $caseResult.code ", msg:" $caseResult.msg

Write-Host ""
Write-Host "[3/6] POST /system/records/addPlus create experiment record"
$recordResult = Invoke-Json -Method POST -Url "$BaseUrl/system/records/addPlus" -Body @{
    userId = $userId
    caseNo = $caseNo
}
$questionNo = $recordResult.msg
Write-Host "addPlus code:" $recordResult.code
Write-Host "questionNo from msg:" $questionNo

if ([string]::IsNullOrWhiteSpace($questionNo)) {
    throw "addPlus did not return questionNo in msg."
}

Write-Host ""
Write-Host "[4/6] POST /system/detail/addList submit fake answers"
$details = @(
    @{
        caseNo = $caseNo
        module = "01"
        questionsName = "Demo inspection question"
        optionA = "A"
        optionB = "B"
        optionC = "C"
        optionD = "D"
        questionsAnswer = "A"
        answer = "A"
        imageUrl = "/profile/upload/demo/question-01.png"
        urlType = "01"
        questionNo = $questionNo
    },
    @{
        caseNo = $caseNo
        module = "02"
        questionsName = "Demo auscultation question"
        optionA = "A"
        optionB = "B"
        optionC = "C"
        optionD = "D"
        questionsAnswer = "B"
        answer = "B"
        imageUrl = "/profile/upload/demo/question-02.png"
        urlType = "01"
        questionNo = $questionNo
    },
    @{
        caseNo = $caseNo
        module = "04"
        questionsName = "Demo pulse question"
        optionA = "A"
        optionB = "B"
        optionC = "C"
        optionD = "D"
        questionsAnswer = "C"
        answer = "D"
        imageUrl = "/profile/upload/demo/question-04.png"
        urlType = "01"
        questionNo = $questionNo
    }
)
$detailResult = Invoke-Json -Method POST -Url "$BaseUrl/system/detail/addList" -Body $details
Write-Host "addList code:" $detailResult.code ", msg:" $detailResult.msg

Write-Host ""
Write-Host "[5/6] POST /system/records/tcmCaseRecordsResult calculate score"
$scoreResult = Invoke-Json -Method POST -Url "$BaseUrl/system/records/tcmCaseRecordsResult" -Body @{
    questionNo = $questionNo
    conclusion = "Demo conclusion: backend connection passed"
    treatmentMethods = "Demo treatment: interface smoke test only"
}
Write-Host "calculate code:" $scoreResult.code ", msg:" $scoreResult.msg

Write-Host ""
Write-Host "[6/6] GET /system/records/recordReult"
$encodedQuestionNo = [uri]::EscapeDataString($questionNo)
$record = Invoke-Json -Method GET -Url "$BaseUrl/system/records/recordReult?questionNo=$encodedQuestionNo"
Write-Host "record score:" $record.score
Write-Host "record extend2:" $record.extend2
Write-Host "record conclusion:" $record.conclusion

Write-Host ""
Write-Host "================================"
Write-Host "Smoke test finished."
Write-Host "Open admin web: http://127.0.0.1:8089/"
Write-Host "Demo caseNo: $caseNo"
Write-Host "Demo questionNo: $questionNo"
Write-Host "================================"
