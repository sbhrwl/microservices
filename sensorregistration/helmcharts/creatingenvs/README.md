# Creating environments
- [Introduction](#introduction)
- [Organize your Helm chart directory](#organize-your-helm-chart-directory)
- [Create environment wise values file](#create-environment-wise-values-file)
- [Use Kubernetes namespaces](#use-kubernetes-namespaces)
- [Install Helm releases per environment](#install-helm-releases-per-environment)
  - [Verify what this installation created](#verify-what-this-installation-created)
  - [Uninstall](#uninstall)
- [Upgrades per environment](#upgrades-per-environment)
- [Environment specific secrets and configs](#environment-specific-secrets-and-configs)
- [Using GitOps tools](#using-gitops-tools)
- [Script to create namespaces and install all 3 environments in one go](#script-to-create-namespaces-and-install-all-3-environments-in-one-go)
- [Uninstallation](#uninstallation)
- [Scripts that takes environment name as input parameter](#scripts-that-takes-environment-name-as-input-parameter)
- [Checking differences before applying changes](#checking-differences-before-applying-changes)
## Introduction
- To create **dev/test, staging, and prod environments** using Helm, we should
  - follow a **clean and structured** approach
  - that uses **separate `values.yaml` files** for each environment,
  - optionally with **separate namespaces** and **Git branching** or directory strategies.

## Organize your Helm chart directory
- Assuming your chart is named [`orchestrate-sensor-services`](https://github.com/sbhrwl/microservices/tree/main/sensorregistration/helmcharts/deploy/orchestrate-sensor-services):
```
orchestrate-sensor-services/
├── charts/
├── templates/
│   ├── ui-deployment.yaml
│   ├── ui-service.yaml
│   ├── sensor-deployment.yaml
│   ├── sensor-service.yaml
│   ├── registration-deployment.yaml
│   ├── registration-service.yaml
│   ├── notification-deployment.yaml
│   ├── notification-service.yaml
│   └── _helpers.tpl
├── Chart.yaml
├── values.yaml         # default (used for dev/test)
├── values-staging.yaml
├── values-prod.yaml
```
## Create environment wise values file
- `values-<env>.yaml` files
  - Dev environment: [`values.yaml`](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/helmcharts/deploy/orchestrate-sensor-services/values.yaml)
  - Staging environment: [`values-staging.yaml`](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/helmcharts/deploy/orchestrate-sensor-services/values-staging.yaml)
  - Production environment: [`values-prod.yaml`](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/helmcharts/deploy/orchestrate-sensor-services/values-prod.yaml)
## Use Kubernetes namespaces
- Namespaces keep your environments isolated on the same cluster.
  ```bash
  kubectl create namespace dev
  kubectl create namespace staging
  kubectl create namespace prod
  ```
- Verify namespaces
  ```
  kubectl get namespaces
  kubectl get ns
  ```
- Delete namespace
  ```
  kubectl delete namespace dev
  ```
  - To see what you're **about to delete**
    ```
    kubectl get all -n dev
    ``` 
## Install Helm releases per environment
- Go to Helm chart folder (e.g., `orchestrate-sensor-services`)
```bash
# For dev (default values.yaml)
helm install orchestrate-sensor-services-dev . -n dev

# For staging
helm install orchestrate-sensor-services-staging . -f values-staging.yaml -n staging

# For prod
helm install orchestrate-sensor-services-prod . -f values-prod.yaml -n prod
```
### Verify what this installation created
- Helm list
  ```
  helm list -A

  # List releases in a specific namespace
  helm list -n dev
  helm list -n staging
  helm list -n prod
  ```
- kubectl
  ```
  kubectl get all -n dev
  kubectl get all -n staging
  kubectl get all -n prod
  ```
### Uninstall
```
helm uninstall orchestrate-sensor-services-dev -n dev
helm uninstall orchestrate-sensor-services-staging -n staging
helm uninstall orchestrate-sensor-services-prod -n prod
```
## Upgrades per environment
- Go to Helm chart folder (e.g., `orchestrate-sensor-services`)
```bash
helm upgrade orchestrate-sensor-services-staging . -f values-staging.yaml -n staging
helm upgrade orchestrate-sensor-services-prod . -f values-prod.yaml -n prod
```
## Environment specific secrets and configs
- Avoid putting secrets in values files.
- Instead:
  * Use Kubernetes `Secrets` or `External Secrets` per environment.
  * Use environment-specific ConfigMaps if needed.
## Using GitOps tools
- This is Optional but consider tools like ArgoCD or Flux to deploy based on Git branches/folders for `dev`, `staging`, `prod`.

## Script to create namespaces and install all 3 environments in one go
- This PowerShell script ([`deploy-all.ps1`](https://github.com/sbhrwl/microservices/tree/main/sensorregistration/helmcharts/deploy/deploy-all.ps1)) creates the `dev`, `staging`, and `prod` namespaces and deploy Helm releases using the appropriate values files:
- Save this as `deploy-all.ps1` in your project root.
  ```
  project-root/
  ├── orchestrate-sensor-services/
  ├── envs/
  ├── deploy-all.ps1   <-- here
  ├── uninstall-all.ps1   <-- here
  ```
- Open PowerShell and run:
  ```powershell
  Set-ExecutionPolicy RemoteSigned -Scope Process
  ./deploy-all.ps1
  ```
## Uninstallation
- The PowerShell script ([`uninstall-all.ps1`](https://github.com/sbhrwl/microservices/tree/main/sensorregistration/helmcharts/deploy/uninstall-all.ps1)) **uninstalls** the Helm releases and delete the associated namespaces:
- Save this as `uninstall-all.ps1` in your project folder.
- Run it in PowerShell:
  ```powershell
  ./uninstall-all.ps1
  ```
## Scripts that takes environment name as input parameter
- Installation
  - From project root, Run: [`.\deploy.ps1 dev`](https://github.com/sbhrwl/microservices/tree/main/sensorregistration/helmcharts/deploy/deploy.ps1)
  - This will:
    - Use the `orchestrate-sensor-services` Helm chart.
    - Install or upgrade the release with name `orchestrate-sensor-services-release-dev`.
    - Apply the config from `envs/dev/values.yaml`
- Uninstallation
  - From project root, Run: [`.\uninstall.ps1 dev`](https://github.com/sbhrwl/microservices/tree/main/sensorregistration/helmcharts/deploy/uninstall.ps1)
  - This will:
    - Uninstall the Helm release `orchestrate-sensor-services-release-dev`.
## Checking differences before applying changes
- **Preview rendered Kubernetes YAML with Helm**
  - From project root, Run: [.\preview.ps1 dev](https://github.com/sbhrwl/microservices/tree/main/sensorregistration/helmcharts/deploy/preview.ps1)
  - This will show **what Helm will deploy**, without actually deploying it.
- **Diff changes before upgrading a release (requires `helm-diff` plugin)**
  - From project root, Run: [.\diff.ps1 dev](https://github.com/sbhrwl/microservices/tree/main/sensorregistration/helmcharts/deploy/diff.ps1)
  - To use the `helm diff` plugin, install it once:
    ```bash
    helm plugin install https://github.com/databus23/helm-diff
    ```
