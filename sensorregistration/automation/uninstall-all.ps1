# Uninstall Helm releases
helm uninstall orchestration-dev -n dev
helm uninstall orchestration-staging -n staging
helm uninstall orchestration-prod -n prod

# Delete namespaces
kubectl delete namespace dev
kubectl delete namespace staging
kubectl delete namespace prod