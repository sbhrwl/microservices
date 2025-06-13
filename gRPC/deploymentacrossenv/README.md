# Creating environments
- [Introduction](#introduction)
- [Create environment wise values file](#create-environment-wise-values-file)
- [Use Kubernetes namespaces](#use-kubernetes-namespaces)
- [Helm releases per environment](#install-helm-releases-per-environment)
  - [Verify release](#verify-release)
  - [Uninstall Helm release](#uninstall-helm-release)
- [Upgrades per environment](#upgrades-per-environment)
- [Environment specific secrets and configs](#environment-specific-secrets-and-configs)
## Introduction
- To create **dev/test, staging, and prod environments** using Helm, we should
  - follow a **clean and structured** approach
  - that uses **separate `values.yaml` files** for each environment,
  - optionally with **separate namespaces** and **Git branching** or directory strategies.

## Create environment wise values file
- `values-<env>.yaml` files
  - Dev environment: [`values.yaml`](https://github.com/sbhrwl/microservices/blob/main/analytics/hpa/orchestrate-ingestion-services-hpa/values.yaml)
  - Staging environment: [`values-staging.yaml`](https://github.com/sbhrwl/microservices/blob/main/analytics/hpa/orchestrate-ingestion-services-hpa/values-staging.yaml)
  - Production environment: [`values-prod.yaml`](https://github.com/sbhrwl/microservices/blob/main/analytics/hpa/orchestrate-ingestion-services-hpa/values-prod.yaml)
```
orchestrate-sensor-services-hpa/
├── templates/
│   ├── ingestion-deployment.yaml
│   ├── ingestion-service.yaml
│   ├── ingestion-service-hpa.yaml
│   └── _helpers.tpl
├── Chart.yaml
├── values.yaml
├── values-staging.yaml
├── values-prod.yaml
```
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
## Helm release per environment
- Go to Helm chart folder [orchestrate-sensor-services-hpa](https://github.com/sbhrwl/microservices/tree/main/analytics/hpa/orchestrate-ingestion-services-hpa)
```bash
# For dev (default values.yaml)
helm install orchestrate-sensor-services-dev . -n dev

# For staging
helm install orchestrate-sensor-services-staging . -f values-staging.yaml -n staging

# For prod
helm install orchestrate-sensor-services-prod . -f values-prod.yaml -n prod
```
### Verify release
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
- `kubectl get pods -n dev`
- `kubectl get svc -n dev`
```
PS C:\Git\microservices\analytics\hpa\orchestrate-ingestion-services-hpa> helm install orchestrate-sensor-services-dev . -n dev
NAME: orchestrate-sensor-services-dev
LAST DEPLOYED: Tue Jun  3 14:39:07 2025
NAMESPACE: dev
STATUS: deployed
REVISION: 1
TEST SUITE: None
PS C:\Git\microservices\analytics\hpa\orchestrate-ingestion-services-hpa> helm list -n dev
NAME                            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                           APP VERSION
orchestrate-sensor-services-dev dev             1               2025-06-03 14:39:07.7390047 +0300 EEST  deployed        ingestion-service-chart-0.1.0   1.0        
PS C:\Git\microservices\analytics\hpa\orchestrate-ingestion-services-hpa> kubectl get pods -n dev
NAME                                                              READY   STATUS    RESTARTS   AGE
orchestrate-sensor-services-dev-ingestion-service-78846b7chkh5x   1/1     Running   0          23s
PS C:\Git\microservices\analytics\hpa\orchestrate-ingestion-services-hpa> kubectl get svc -n dev
NAME                                                TYPE       CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
orchestrate-sensor-services-dev-ingestion-service   NodePort   10.103.144.169   <none>        9081:30081/TCP   31s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `localhost:30081/api/powerquality/generate` → `ingestion-service`
### Uninstall Helm release
```
helm uninstall orchestrate-sensor-services-dev -n dev
helm uninstall orchestrate-sensor-services-staging -n staging
helm uninstall orchestrate-sensor-services-prod -n prod
```
## Upgrades per environment
- Go to Helm chart folder [orchestrate-sensor-services-hpa](https://github.com/sbhrwl/microservices/tree/main/analytics/hpa/orchestrate-ingestion-services-hpa)
```bash
helm upgrade orchestrate-sensor-services-staging . -f values-staging.yaml -n staging
helm upgrade orchestrate-sensor-services-prod . -f values-prod.yaml -n prod
```
## Environment specific secrets and configs
- Avoid putting secrets in values files.
- Instead:
  * Use Kubernetes `Secrets` or `External Secrets` per environment.
  * Use environment-specific ConfigMaps if needed.
