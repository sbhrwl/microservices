param(
    [string]$env = "dev"
)

$releaseName = "orchestration-release-$env"
$valuesFile = "values-$env.yaml"

Write-Host "Rendering Helm templates for environment: $env..."
helm template $releaseName . -f $valuesFile