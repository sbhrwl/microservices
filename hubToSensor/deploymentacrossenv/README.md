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
  - Dev environment: [`values.yaml`](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/hpa/orchestrate-hubtosensor-services/values.yaml)
  - Staging environment: [`values-staging.yaml`](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/hpa/orchestrate-hubtosensor-services/values-staging.yaml)
  - Production environment: [`values-prod.yaml`](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/hpa/orchestrate-hubtosensor-services/values-prod.yaml)
```
orchestrate-command-services-with-hpa/
├── templates/
│   ├── flexibility-hub-simulator-deployment.yaml
│   ├── flexibility-hub-simulator-service.yaml
│   ├── flexibility-hub-simulator-hpa.yaml
│   ├── storage-service-deployment.yaml
│   ├── storage-service-service.yaml
│   ├── flexibility-bridge-deployment.yaml
│   ├── protocol-adapter-deployment.yaml
│   ├── hes-simulator-deployment.yaml
│   ├── data-api-deployment.yaml
│   ├── data-api-service.yaml
│   ├── data-api-hpa.yaml
│   ├── ui-app-deployment.yaml
│   ├── ui-app-service.yaml
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
- Go to Helm chart folder [orchestrate-hubtosensor-services](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/hpa/orchestrate-hubtosensor-services)
```bash
# For dev (default values.yaml)
helm install ocs-dev . -n dev

# For staging
helm install ocs-staging . -f values-staging.yaml -n staging

# For prod
helm install ocs-prod . -f values-prod.yaml -n prod
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
PS C:\Git\microservices\commandorchestration\hpa\orchestrate-command-services-with-hpa> helm install ocs-dev . -n dev
NAME: ocs-dev
LAST DEPLOYED: Mon Jun  9 20:43:34 2025
NAMESPACE: dev
STATUS: deployed
REVISION: 1
TEST SUITE: None
PS C:\Git\microservices\commandorchestration\hpa\orchestrate-command-services-with-hpa> helm list -n dev
NAME    NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                   APP VERSION
ocs-dev dev             1               2025-06-09 20:43:34.7918091 +0300 EEST  deployed        microservices-0.1.0     1.0
PS C:\Git\microservices\commandorchestration\hpa\orchestrate-command-services-with-hpa> kubectl get pods -n dev
NAME                                                          READY   STATUS    RESTARTS   AGE
ocs-dev-microservices-command-orchestrator-789c6d7877-4h5hs   1/1     Running   0          26s
ocs-dev-microservices-protocol-gateway-7f86cfb765-b59lb       1/1     Running   0          26s
ocs-dev-microservices-sensor-simulator-5b7686d8c-8lthj        1/1     Running   0          26s
ocs-dev-microservices-task-orchestrator-69555b676c-ms6ft      1/1     Running   0          26s
PS C:\Git\microservices\commandorchestration\hpa\orchestrate-command-services-with-hpa> kubectl get svc -n dev
NAME                                         TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
ocs-dev-microservices-command-orchestrator   ClusterIP   10.110.31.157   <none>        9082/TCP         32s
ocs-dev-microservices-sensor-simulator       ClusterIP   10.107.86.93    <none>        9084/TCP         32s
ocs-dev-microservices-task-orchestrator      NodePort    10.108.173.61   <none>        9081:30557/TCP   32s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `flexibility-hub-simulator` → `http://localhost:30881/api/messages`
  * `data-api-service` → `http://localhost:30885/api/v1/requests/<requestID>/tracker`
  * `ui-app` → `http://localhost:30880/`
### Uninstall Helm release
```
helm uninstall ocs-dev -n dev
helm uninstall ocs-staging -n staging
helm uninstall ocs-prod -n prod
```
## Upgrades per environment
- Go to Helm chart folder [orchestrate-command-services-with-hpa](https://github.com/sbhrwl/microservices/tree/main/commandorchestration/hpa/orchestrate-command-services-with-hpa)
```bash
helm upgrade ocs-staging . -f values-staging.yaml -n staging
helm upgrade ocs-prod . -f values-prod.yaml -n prod
```
## Environment specific secrets and configs
- Avoid putting secrets in values files.
- Instead:
  * Use Kubernetes `Secrets` or `External Secrets` per environment.
  * Use environment-specific ConfigMaps if needed.
