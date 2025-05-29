# Set your Helm chart directory path
$chartPath = "./orchestration-services"

# Create namespaces
kubectl create namespace dev -o yaml --dry-run=client | kubectl apply -f -
kubectl create namespace staging -o yaml --dry-run=client | kubectl apply -f -
kubectl create namespace prod -o yaml --dry-run=client | kubectl apply -f -

# Deploy to dev (using default values.yaml)
helm install orchestration-dev $chartPath -n dev

# Deploy to staging
helm install orchestration-staging $chartPath -f "$chartPath/values-staging.yaml" -n staging

# Deploy to prod
helm install orchestration-prod $chartPath -f "$chartPath/values-prod.yaml" -n prod