param(
    [string]$env = "dev"
)

$chartName = "orchestration-services"
$releaseName = "orchestration-release-$env"
$valuesFile = ".\envs\$env\values.yaml"

if (-Not (Test-Path $valuesFile)) {
    Write-Error "Environment values file not found: $valuesFile"
    exit 1
}

helm upgrade --install $releaseName .\$chartName -f $valuesFile
