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
  - Dev environment: [`values.yaml`](https://github.com/sbhrwl/microservices/blob/main/gRPC/hpa/orchestrate-ingestion-grpc-services-hpa/values.yaml)
  - Staging environment: [`values-staging.yaml`](https://github.com/sbhrwl/microservices/blob/main/gRPC/hpa/orchestrate-ingestion-grpc-services-hpa/values-staging.yaml)
  - Production environment: [`values-prod.yaml`](https://github.com/sbhrwl/microservices/blob/main/gRPC/hpa/orchestrate-ingestion-grpc-services-hpa/values-prod.yaml)
```
orchestrate-ingestion-grpc-services-hpa/
├── templates/
│   ├── ingestion-deployment.yaml
│   ├── ingestion-grpc-service.yaml
│   ├── ingestion-grpc-service-hpa.yaml
│   ├── hub-deployment.yaml
│   ├── hub-service.yaml
│   ├── hub-service-hpa.yaml  
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
- Go to Helm chart folder [orchestrate-ingestion-grpc-services-hpa](https://github.com/sbhrwl/microservices/tree/main/gRPC/hpa/orchestrate-ingestion-grpc-services-hpa)
```bash
# For dev (default values.yaml)
helm install orchestrate-ingestion-grpc-services-dev . -n dev

# For staging
helm install orchestrate-ingestion-grpc-services-staging . -f values-staging.yaml -n staging

# For prod
helm install orchestrate-ingestion-grpc-services-prod . -f values-prod.yaml -n prod
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
PS C:\Git\microservices\gRPC\hpa\orchestrate-ingestion-grpc-services-hpa> helm install orchestrate-ingestion-grpc-services-dev . -n dev
NAME: orchestrate-ingestion-grpc-services-dev
LAST DEPLOYED: Fri Jun 13 14:01:44 2025
NAMESPACE: dev
STATUS: deployed
REVISION: 1
TEST SUITE: None
PS C:\Git\microservices\gRPC\hpa\orchestrate-ingestion-grpc-services-hpa> helm list -n dev
NAME                                            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                           APP VERSION
orchestrate-ingestion-grpc-services-dev        dev             1               2025-06-13 14:01:44.3319483 +0300 EEST  deployed        ingestion-grpc-service-0.1.0
PS C:\Git\microservices\gRPC\hpa\orchestrate-ingestion-grpc-services-hpa> kubectl get pods -n dev
NAME                                                              READY   STATUS    RESTARTS   AGE
orchestrate-ingestion-grpc-services-dev-hub-service-b7c46xl4fz   1/1     Running   0          14s
orchestrate-ingestion-grpc-services-dev-ingestion-grpc-sexh78m   1/1     Running   0          14s
PS C:\Git\microservices\gRPC\hpa\orchestrate-ingestion-grpc-services-hpa> kubectl get svc -n dev
NAME                                                              TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)              AGE
orchestrate-ingestion-grpc-services-dev-hub-service              ClusterIP   10.100.232.237   <none>        9082/TCP,50051/TCP   19s
orchestrate-ingestion-grpc-services-dev-ingestion-grpc-service   NodePort    10.99.126.76     <none>        9081:30081/TCP       19s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `localhost:30081/generate/registration` → `ingestion-grpc-service`
### Uninstall Helm release
```
helm uninstall orchestrate-ingestion-grpc-services-dev -n dev
helm uninstall orchestrate-ingestion-grpc-services-staging -n staging
helm uninstall orchestrate-ingestion-grpc-services-prod -n prod
```
## Upgrades per environment
- Go to Helm chart folder [orchestrate-ingestion-grpc-services-hpa](https://github.com/sbhrwl/microservices/tree/main/gRPC/hpa/orchestrate-ingestion-grpc-services-hpa)
```bash
helm upgrade orchestrate-ingestion-grpc-services-staging . -f values-staging.yaml -n staging
helm upgrade orchestrate-ingestion-grpc-services-prod . -f values-prod.yaml -n prod
```
## Environment specific secrets and configs
- Avoid putting secrets in values files.
- Instead:
  * Use Kubernetes `Secrets` or `External Secrets` per environment.
  * Use environment-specific ConfigMaps if needed.
