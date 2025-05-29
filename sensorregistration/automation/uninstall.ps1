param(
    [string]$env = "dev"
)

$releaseName = "orchestration-release-$env"

Write-Host "Uninstalling Helm release: $releaseName..."
helm uninstall $releaseName
