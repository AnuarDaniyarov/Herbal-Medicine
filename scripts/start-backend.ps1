param(
    [int]$Port = 8081
)

$projectRoot = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path $projectRoot '.env'
$jarPath = Join-Path $projectRoot 'target/medicine-platform-0.0.1-SNAPSHOT.jar'

if (Test-Path -LiteralPath $envFile) {
    foreach ($line in Get-Content -LiteralPath $envFile -Encoding utf8) {
        if ($line -match '^\s*#' -or $line -notmatch '^\s*([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*)$') {
            continue
        }

        $key = $Matches[1]
        $value = $Matches[2].Trim()
        if (($value.StartsWith('"') -and $value.EndsWith('"')) -or
            ($value.StartsWith("'") -and $value.EndsWith("'"))) {
            $value = $value.Substring(1, $value.Length - 2)
        }
        [Environment]::SetEnvironmentVariable($key, $value, 'Process')
    }
}

if ([string]::IsNullOrWhiteSpace($env:DB_PASSWORD) -or
    $env:DB_PASSWORD -eq 'change-me-for-local-use') {
    throw 'Set a real DB_PASSWORD in the local .env file or process environment. Do not commit the password.'
}

if (-not $env:DB_URL) {
    $env:DB_URL = 'jdbc:postgresql://localhost:5432/medicine_platform'
}
if (-not $env:DB_USERNAME) {
    $env:DB_USERNAME = 'postgres'
}
$env:SERVER_PORT = "$Port"
if (-not $env:CORS_ALLOWED_ORIGINS) {
    $env:CORS_ALLOWED_ORIGINS = 'http://localhost:4200'
}
if (-not $env:APP_MODEL_PATH) {
    $env:APP_MODEL_PATH = Join-Path $projectRoot 'data/processed/ache-baseline-model.json'
}

if (-not (Test-Path -LiteralPath $jarPath)) {
    throw 'Backend JAR not found. Build the project first with .\mvnw.cmd package.'
}

$javaPath = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin/java.exe' } else { 'java' }
& $javaPath -jar $jarPath
exit $LASTEXITCODE
