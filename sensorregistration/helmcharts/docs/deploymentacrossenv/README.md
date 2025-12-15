# Deployment across environments

| Category           | dev                  | staging              | prod                       |
| ------------------ | -------------------- | -------------------- | -------------------------- |
| Create namespace   | `kubectl create namespace dev`               | `kubectl create namespace staging`                                          | `kubectl create namespace prod`                                    |
| Verify namespace   | `kubectl get namespace dev`                  | `kubectl get namespace staging`                                             | `kubectl get namespace prod`                                       |
| Values file        | `values.yaml` (default)                      | `values-staging.yaml`                                                       | `values-prod.yaml`                                                 |
| Install command    | `helm install helm-app-release-dev . -n dev` | `helm install helm-app-release-staging . -f values-staging.yaml -n staging` | `helm install helm-app-release-prod . -f values-prod.yaml -n prod` |
| List helm releases | `helm list -n dev`                           | `helm list -n staging`                                                      | `helm list -n prod`                                                |
| Verify resources   | `kubectl get all -n dev`                     | `kubectl get all -n staging`                                                | `kubectl get all -n prod`                                          |
| Upgrade command    | `helm upgrade helm-app-release-dev . -n dev` | `helm upgrade helm-app-release-staging . -f values-staging.yaml -n staging` | `helm upgrade helm-app-release-prod . -f values-prod.yaml -n prod` |
| uninstall command  | `helm uninstall helm-app-release-dev -n dev` | `helm uninstall helm-app-release-staging -n staging`                        | `helm uninstall helm-app-release-prod -n prod`                     |