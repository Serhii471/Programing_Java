$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$driver = Join-Path $projectRoot "lib\sqlite-jdbc-3.53.4.0.jar"
$source = Join-Path $projectRoot "src\Main.java"
$output = Join-Path $projectRoot "out"

if (-not (Test-Path -LiteralPath $driver)) {
    throw "Не знайдено JDBC-драйвер: $driver"
}

New-Item -ItemType Directory -Force -Path $output | Out-Null

javac -encoding UTF-8 -d $output $source
if ($LASTEXITCODE -ne 0) {
    throw "Компіляція завершилась з помилкою."
}

$oldLocation = Get-Location
try {
    Set-Location -LiteralPath $projectRoot
    [Console]::InputEncoding = [System.Text.UTF8Encoding]::new($false)
    [Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
    java "-Dsun.stdout.encoding=UTF-8" "-Dsun.stderr.encoding=UTF-8" -cp "$output;$driver" Main
} finally {
    Set-Location -LiteralPath $oldLocation
}
