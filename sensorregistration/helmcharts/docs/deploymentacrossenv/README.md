# Deployment across environments

| Category           | dev                  | staging          | prod                       |
| ------------------ | -------------------- | ---------------- | -------------------------- |
| Create namespace   | `kubectl create namespace dev`          | `kubectl create namespace staging`                                     | `kubectl create namespace prod`                               |
| Verify namespace   | `kubectl get namespace dev`             | `kubectl get namespace staging`                                        | `kubectl get namespace prod`                                  |
| Values file        | `values.yaml` (default)                 | `values-staging.yaml`                                                  | `values-prod.yaml`                                            |
| Install command    | `helm install ocs-release-dev . -n dev` | `helm install ocs-release-staging . -f values-staging.yaml -n staging` | `helm install ocs-release-prod . -f values-prod.yaml -n prod` |
| List helm releases | `helm list -n dev`                      | `helm list -n staging`                                                 | `helm list -n prod`                                           |
| Verify resources   | `kubectl get all -n dev`                | `kubectl get all -n staging`                                           | `kubectl get all -n prod`                                     |
| Upgrade command    | `helm upgrade ocs-release-dev . -n dev` | `helm upgrade ocs-release-staging . -f values-staging.yaml -n staging` | `helm upgrade ocs-release-prod . -f values-prod.yaml -n prod` |
| Uninstall command  | `helm uninstall ocs-release-dev -n dev` | `helm uninstall ocs-release-staging -n staging`                        | `helm uninstall ocs-release-prod -n prod`                     |
| Delete namespace   | `kubectl delete namespace dev`          | `kubectl delete namespace staging`                                     | `kubectl delete namespace prod`                               |
| Check namespaces   | `kubectl get ns`                        | `kubectl get ns`                                                       | `kubectl get ns`                                              |
