param(
    [Parameter(Position = 0)]
    [string] $DatasetDirectory = '',

    [Parameter(Position = 1)]
    [string] $OutputDirectory = ''
)

$ErrorActionPreference = 'Stop'

$projectDirectory = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
if (-not $DatasetDirectory) {
    $DatasetDirectory = Join-Path $projectDirectory 'data\raw'
}

function Read-Tsv([string] $Name) {
    $path = Join-Path $DatasetDirectory $Name
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
        throw "Не найден файл датасета: $path"
    }
    Import-Csv -LiteralPath $path -Delimiter "`t"
}

function Write-Csv([string] $Name, [object[]] $Rows) {
    $path = Join-Path $OutputDirectory $Name
    if ($Rows.Count -eq 0) {
        throw "После подготовки файл $Name пуст. Проверь входные файлы."
    }
    $lines = @($Rows | ConvertTo-Csv -NoTypeInformation)
    [System.IO.File]::WriteAllLines($path, $lines, [System.Text.UTF8Encoding]::new($false))
    Write-Host ("{0}: {1} строк" -f $Name, $Rows.Count)
}

if (-not (Test-Path -LiteralPath $DatasetDirectory -PathType Container)) {
    throw "Не найдена папка датасета: $DatasetDirectory"
}
if (-not $OutputDirectory) {
    $OutputDirectory = Join-Path $projectDirectory 'data\processed'
}
New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null

$plantsRaw = @(Read-Tsv 'Plant_Information_IMPPAT.tsv')
$compoundsRaw = @(Read-Tsv 'Chemical_Information_IMPPAT_Phytochemicals.tsv')
$relationshipsRaw = @(Read-Tsv 'IMPPAT_Phytochemical_Plant_Association.tsv')
$bioactivityRaw = @(Read-Tsv 'Bioactivity_IMPPAT_Phytochemicals.tsv')

$plants = @($plantsRaw | Where-Object {
    $_.Plant_identifier -and $_.Indian_Medicinal_plant
} | ForEach-Object {
    [pscustomobject]@{
        external_id = $_.Plant_identifier.Trim()
        scientific_name = $_.Indian_Medicinal_plant.Trim()
        display_name = if ($_.Common_name) { $_.Common_name.Trim() } else { $_.Indian_Medicinal_plant.Trim() }
        family = if ($_.Family) { $_.Family.Trim() } else { $null }
    }
})

$compoundIdToName = @{}
$compounds = @($compoundsRaw | Where-Object {
    $_.IMPPAT_Phytochemical_identifier -and $_.'Phytochemical name_standardised'
} | ForEach-Object {
    $id = $_.IMPPAT_Phytochemical_identifier.Trim()
    $name = $_.'Phytochemical name_standardised'.Trim()
    $compoundIdToName[$id] = $name
    [pscustomobject]@{
        external_id = $id
        name = $name
        canonical_smiles = if ($_.'Canonical_SMILES') { $_.'Canonical_SMILES'.Trim() } else { $null }
        inchi_key = if ($_.InChIKey) { $_.InChIKey.Trim() } else { $null }
    }
})

# The application schema requires a structure and unique InChIKey. Keep compounds
# without SMILES out of the import, and leave duplicate InChIKeys blank for later
# curation rather than silently merging distinct IMPPAT identifiers.
$compounds = @($compounds | Where-Object { $_.canonical_smiles })
$inchiOwners = @{}
foreach ($compound in $compounds) {
    if (-not $compound.inchi_key) { continue }
    if ($inchiOwners.ContainsKey($compound.inchi_key)) {
        $compound.inchi_key = $null
    } else {
        $inchiOwners[$compound.inchi_key] = $compound.external_id
    }
}
$validCompoundIds = @{}
foreach ($compound in $compounds) { $validCompoundIds[$compound.external_id] = $true }

$relationships = @($relationshipsRaw | Where-Object {
    $_.Plant_identifier -and $_.IMPPAT_Phytochemical_identifier -and
    $validCompoundIds.ContainsKey($_.IMPPAT_Phytochemical_identifier.Trim())
} | ForEach-Object {
    [pscustomobject]@{
        plant_external_id = $_.Plant_identifier.Trim()
        compound_external_id = $_.IMPPAT_Phytochemical_identifier.Trim()
        plant_part = if ($_.Plant_part) { $_.Plant_part.Trim() } else { $null }
    }
})

$source = @([pscustomobject]@{
    source_code = 'IMPPAT3'
    title = 'IMPPAT 3.0: Indian Medicinal Plants, Phytochemistry And Therapeutics'
    url = 'https://cb.imsc.res.in/imppat/download'
    source_type = 'DATABASE'
    license_note = 'Review current IMPPAT terms and citation requirements before reuse or redistribution.'
})

$assayMap = @{}
$targetMap = @{}
$measurements = [System.Collections.Generic.List[object]]::new()
$types = @('EC50', 'IC50', 'Kd', 'Ki')
foreach ($row in $bioactivityRaw) {
    $compoundId = if ($row.IMPPAT_Phytochemical_identifier) { $row.IMPPAT_Phytochemical_identifier.Trim() } else { '' }
    if (-not $validCompoundIds.ContainsKey($compoundId)) { continue }
    foreach ($type in $types) {
        $rawValue = [string]$row.$type
        if (-not $rawValue) { continue }
        foreach ($item in ($rawValue -split '\|')) {
            if ($item -notmatch '^\s*(<=|>=|=|<|>)\s*([0-9]+(?:\.[0-9]+)?)\s*([A-Za-zµμ]+)\s*$') { continue }
            $relation = $Matches[1]
            $value = $Matches[2]
            $unit = $Matches[3]
            $target = if ($row.Target_name) { $row.Target_name.Trim() } else { 'Unspecified target' }
            $organism = if ($row.Target_organism) { $row.Target_organism.Trim() } else { 'Unspecified organism' }
            $targetKey = "$target|$organism"
            if (-not $targetMap.ContainsKey($targetKey)) {
                $targetHash = [System.Security.Cryptography.SHA256]::Create()
                try {
                    $targetBytes = $targetHash.ComputeHash([Text.Encoding]::UTF8.GetBytes($targetKey))
                } finally {
                    $targetHash.Dispose()
                }
                $targetIdentifier = 'imppat_target_' + [BitConverter]::ToString($targetBytes).Replace('-', '').ToLowerInvariant().Substring(0, 16)
                $targetMap[$targetKey] = [pscustomobject]@{
                    normalized_identifier = $targetIdentifier
                    name = $target
                    organism = $organism
                }
            }
            $key = "$type|$target|$organism|$unit"
            if (-not $assayMap.ContainsKey($key)) {
                $sha = [System.Security.Cryptography.SHA256]::Create()
                try {
                    $hash = $sha.ComputeHash([Text.Encoding]::UTF8.GetBytes($key))
                } finally {
                    $sha.Dispose()
                }
                $hashText = [BitConverter]::ToString($hash).Replace('-', '').ToLowerInvariant()
                $code = 'imppat_' + $type.ToLowerInvariant() + '_' + $hashText.Substring(0, 12)
                $assayMap[$key] = [pscustomobject]@{
                    activity_code = $code
                    name = "$type against $target"
                    target_name = $target
                    assay_type = $type
                    description = "IMPPAT reported activity; organism: $organism"
                    standard_unit = $unit
                    target_identifier = $targetMap[$targetKey].normalized_identifier
                }
            }
            $measurements.Add([pscustomobject]@{
                compound_external_id = $compoundId
                activity_code = $assayMap[$key].activity_code
                source_code = 'IMPPAT3'
                measurement_value = $value
                unit = $unit
                relation = $relation
                experimental_conditions = "organism=$organism; original_source=$($row.Source); uniprot=$($row.UniProt_identifier)"
                quality_status = 'UNVERIFIED'
            })
        }
    }
}

Write-Csv 'plants.csv' $plants
Write-Csv 'compounds.csv' $compounds
Write-Csv 'plant_compounds.csv' $relationships
Write-Csv 'sources.csv' $source
Write-Csv 'targets.csv' @($targetMap.Values | Sort-Object normalized_identifier)
Write-Csv 'assays.csv' @($assayMap.Values | Sort-Object activity_code)
if ($measurements.Count -gt 0) { Write-Csv 'measurements.csv' @($measurements) }

$report = @{
    dataset = 'IMPPAT 3.0'
    generated_at_utc = [DateTime]::UtcNow.ToString('o')
    input_counts = @{
        plants = $plantsRaw.Count
        compounds = $compoundsRaw.Count
        plant_compound_relationships = $relationshipsRaw.Count
        bioactivity_rows = $bioactivityRaw.Count
    }
    prepared_counts = @{
        plants = $plants.Count
        compounds_with_smiles = $compounds.Count
        plant_compound_relationships = $relationships.Count
        assays = $assayMap.Count
        measurements = $measurements.Count
    }
    excluded_compounds_without_smiles = @($compoundsRaw | Where-Object { -not $_.Canonical_SMILES }).Count
    notes = @(
        'Структуры не стандартизованы RDKit; статусы должны оставаться UNVERIFIED.',
        'Метки активности импортируются с исходными relation, значением и единицей; численные условия assay требуют научной проверки.',
        'Исходные файлы не копируются в проект.'
    )
}
$reportPath = Join-Path $OutputDirectory 'preparation-report.json'
[System.IO.File]::WriteAllText($reportPath, ($report | ConvertTo-Json -Depth 6), [System.Text.UTF8Encoding]::new($false))
Write-Host "Отчёт: $reportPath"
