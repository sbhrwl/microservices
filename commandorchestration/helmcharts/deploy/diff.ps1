param(
    [string]$env = "dev"
)

$releaseName = "orchestration-release-$env"
$valuesFile = "values-$env.yaml"

Write-Host "Showing diff for Helm release: $releaseName..."
helm diff upgrade $releaseName . -f $valuesFile