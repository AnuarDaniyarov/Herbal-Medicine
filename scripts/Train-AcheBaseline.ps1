param(
    [string]$ProcessedDirectory = (Join-Path (Split-Path -Parent $PSScriptRoot) 'data/processed'),
    [string]$OutputPath = (Join-Path (Split-Path -Parent $PSScriptRoot) 'data/processed/ache-baseline-model.json')
)

$ErrorActionPreference = 'Stop'
$activityCode = 'imppat_ic50_e662faf14ac2'
$thresholdNm = 10000.0
$featureCount = 256
$learningRate = 0.15
$regularization = 0.08
$iterations = 700
$invariant = [Globalization.CultureInfo]::InvariantCulture
$sha = [Security.Cryptography.SHA256]::Create()

function Get-Features([string]$smiles) {
    $normalized = '^' + $smiles.Trim() + '$'
    $features = @{}
    foreach ($width in @(2, 3)) {
        for ($i = 0; $i -le $normalized.Length - $width; $i++) {
            $gram = $normalized.Substring($i, $width)
            $digest = $script:sha.ComputeHash([Text.Encoding]::UTF8.GetBytes($gram))
            $hash = [BitConverter]::ToInt32($digest, 0)
            $index = [Math]::Abs($hash % $script:featureCount)
            $sign = if (($hash -band [int]::MinValue) -eq 0) { 1.0 } else { -1.0 }
            if (-not $features.ContainsKey($index)) { $features[$index] = 0.0 }
            $features[$index] += $sign
        }
    }
    $norm = [Math]::Sqrt((($features.Values | ForEach-Object { $_ * $_ } | Measure-Object -Sum).Sum))
    if ($norm -gt 0) { foreach ($index in @($features.Keys)) { $features[$index] /= $norm } }
    return $features
}

function Get-Score($features, [double[]]$weights, [double]$bias) {
    $z = $bias
    foreach ($index in $features.Keys) { $z += $weights[[int]$index] * [double]$features[$index] }
    $z = [Math]::Max(-30.0, [Math]::Min(30.0, $z))
    return 1.0 / (1.0 + [Math]::Exp(-$z))
}

$compoundById = @{}
foreach ($compound in (Import-Csv (Join-Path $ProcessedDirectory 'compounds.csv'))) {
    if ($compound.canonical_smiles) { $compoundById[$compound.external_id] = $compound.canonical_smiles }
}
$measurements = Import-Csv (Join-Path $ProcessedDirectory 'measurements.csv') | Where-Object {
    $_.activity_code -eq $activityCode -and $_.relation -eq '=' -and $_.unit -eq 'nM' -and
    $_.measurement_value -match '^\d+(\.\d+)?([eE][-+]?\d+)?$' -and
    [double]::Parse($_.measurement_value, $invariant) -gt 0 -and $compoundById.ContainsKey($_.compound_external_id)
}
$records = [Collections.Generic.List[object]]::new()
$excludedConflictCount = 0
foreach ($group in ($measurements | Group-Object compound_external_id)) {
    $thresholdLabels = @($group.Group | ForEach-Object {
        if ([double]::Parse($_.measurement_value, $invariant) -le $thresholdNm) { 1 } else { 0 }
    } | Sort-Object -Unique)
    if ($thresholdLabels.Count -ne 1) { $excludedConflictCount++; continue }
    $smiles = $compoundById[$group.Name]
    if ($smiles -notmatch '^[A-Za-z0-9@+\-\[\]()=:#$/\\.%*]+$' -or $smiles -match '\s') { continue }
    $records.Add([pscustomobject]@{
        compoundId = $group.Name
        smiles = $smiles
        label = [int]$thresholdLabels[0]
        features = (Get-Features $smiles)
    })
}

if ($records.Count -lt 30 -or @($records | Where-Object label -eq 1).Count -lt 10 -or @($records | Where-Object label -eq 0).Count -lt 10) {
    throw "Not enough clean records for the exploratory baseline: $($records.Count)"
}

$test = [Collections.Generic.List[object]]::new()
$train = [Collections.Generic.List[object]]::new()
$random = [Random]::new(20261111)
foreach ($label in @(0, 1)) {
    $classRecords = [Collections.Generic.List[object]]::new()
    foreach ($record in ($records | Where-Object label -eq $label)) { $classRecords.Add($record) }
    for ($i = $classRecords.Count - 1; $i -gt 0; $i--) {
        $j = $random.Next($i + 1)
        $temporary = $classRecords[$i]
        $classRecords[$i] = $classRecords[$j]
        $classRecords[$j] = $temporary
    }
    $testCount = [Math]::Max(1, [int][Math]::Ceiling($classRecords.Count * 0.2))
    for ($i = 0; $i -lt $classRecords.Count; $i++) {
        if ($i -lt $testCount) { $test.Add($classRecords[$i]) } else { $train.Add($classRecords[$i]) }
    }
}

$weights = [double[]]::new($featureCount)
$bias = 0.0
for ($iteration = 0; $iteration -lt $iterations; $iteration++) {
    $gradient = [double[]]::new($featureCount)
    $biasGradient = 0.0
    foreach ($record in $train) {
        $probability = Get-Score $record.features $weights $bias
        $errorValue = $probability - $record.label
        $biasGradient += $errorValue
        foreach ($index in $record.features.Keys) { $gradient[[int]$index] += $errorValue * [double]$record.features[$index] }
    }
    for ($i = 0; $i -lt $featureCount; $i++) { $weights[$i] -= $learningRate * ($gradient[$i] / $train.Count + $regularization * $weights[$i]) }
    $bias -= $learningRate * $biasGradient / $train.Count
}

$predictions = @($test | ForEach-Object {
    [pscustomobject]@{ label = $_.label; probability = (Get-Score $_.features $weights $bias) }
})
$tp = 0; $tn = 0; $fp = 0; $fn = 0; $brier = 0.0
foreach ($item in $predictions) {
    $predicted = if ($item.probability -ge 0.5) { 1 } else { 0 }
    if ($predicted -eq 1 -and $item.label -eq 1) { $tp++ }
    elseif ($predicted -eq 0 -and $item.label -eq 0) { $tn++ }
    elseif ($predicted -eq 1) { $fp++ }
    else { $fn++ }
    $brier += [Math]::Pow($item.probability - $item.label, 2)
}
$brier /= $predictions.Count
$aucWins = 0.0; $positiveCount = @($predictions | Where-Object label -eq 1).Count; $negativeCount = @($predictions | Where-Object label -eq 0).Count
foreach ($positive in ($predictions | Where-Object label -eq 1)) {
    foreach ($negative in ($predictions | Where-Object label -eq 0)) {
        if ($positive.probability -gt $negative.probability) { $aucWins += 1 }
        elseif ($positive.probability -eq $negative.probability) { $aucWins += 0.5 }
    }
}
$rocAuc = if ($positiveCount -and $negativeCount) { $aucWins / ($positiveCount * $negativeCount) } else { $null }
$rank = 0; $precisionSum = 0.0; $seenPositive = 0
foreach ($item in ($predictions | Sort-Object probability -Descending)) { $rank++; if ($item.label -eq 1) { $seenPositive++; $precisionSum += $seenPositive / $rank } }
$prAuc = if ($positiveCount) { $precisionSum / $positiveCount } else { $null }
$accuracy = ($tp + $tn) / $predictions.Count
$majorityBaselineAccuracy = [Math]::Max($positiveCount, $negativeCount) / $predictions.Count
$balancedAccuracy = (($tp / [Math]::Max(1, $tp + $fn)) + ($tn / [Math]::Max(1, $tn + $fp))) / 2

$artifact = [ordered]@{
    schemaVersion = 1
    modelName = 'AChE SMILES n-gram logistic baseline'
    modelVersion = '0.1.0'
    activityCode = $activityCode
    featureAlgorithm = 'SHA-256 signed character n-grams (width 2 and 3), 256 bins, L2 normalized'
    threshold = [ordered]@{ standardType = 'IC50'; unit = 'nM'; activeAtOrBelow = $thresholdNm; pic50Equivalent = 5.0 }
    training = [ordered]@{ source = 'IMPPAT 3.0 local import'; cleanCompounds = $records.Count; trainingCompounds = $train.Count; testCompounds = $test.Count; excludedConflictingCompounds = $excludedConflictCount; split = 'stratified random split by compound ID, seed 20261111'; positive = @($records | Where-Object label -eq 1).Count; negative = @($records | Where-Object label -eq 0).Count }
    metrics = [ordered]@{ testAccuracy = $accuracy; majorityBaselineAccuracy = $majorityBaselineAccuracy; majorityBaselineRocAuc = 0.5; testBalancedAccuracy = $balancedAccuracy; testRocAuc = $rocAuc; testPrAuc = $prAuc; testBrier = $brier; testTruePositive = $tp; testTrueNegative = $tn; testFalsePositive = $fp; testFalseNegative = $fn }
    caveats = @('Exploratory baseline only; data labels have UNVERIFIED provenance.', 'Random molecule split is not scaffold split and can overestimate generalization.', 'The source groups several original publications; cross-assay comparability is not established.', 'SMILES format checking is not chemical structure validation.', 'Not a medical recommendation or evidence of efficacy or safety.')
    bias = $bias
    weights = @($weights)
}
$parent = Split-Path -Parent $OutputPath
if (-not (Test-Path -LiteralPath $parent)) { New-Item -ItemType Directory -Path $parent -Force | Out-Null }
$artifact | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $OutputPath -Encoding utf8
[pscustomobject]@{ model = $artifact.modelName; version = $artifact.modelVersion; trainingCompounds = $train.Count; testCompounds = $test.Count; testAccuracy = [Math]::Round($accuracy, 3); majorityBaselineAccuracy = [Math]::Round($majorityBaselineAccuracy, 3); testBalancedAccuracy = [Math]::Round($balancedAccuracy, 3); testRocAuc = [Math]::Round($rocAuc, 3); testPrAuc = [Math]::Round($prAuc, 3); testBrier = [Math]::Round($brier, 3); output = $OutputPath } | Format-List
