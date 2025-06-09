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
  - Dev environment: [`values.yaml`](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/hpa/orchestrate-sensor-services-with-hpa/values.yaml)
  - Staging environment: [`values-staging.yaml`](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/hpa/orchestrate-sensor-services-with-hpa/values-staging.yaml)
  - Production environment: [`values-prod.yaml`](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/hpa/orchestrate-sensor-services-with-hpa/values-prod.yaml)
```
orchestrate-sensor-services-with-hpa/
├── templates/
│   ├── notification-deployment.yaml
│   ├── notification-service.yaml
│   ├── notification-service-hpa.yaml
│   ├── registration-deployment.yaml
│   ├── registration-service.yaml
│   ├── registration-service-hpa.yaml
│   ├── sensor-deployment.yaml
│   ├── sensor-service.yaml
│   ├── sensor-service-hpa.yaml
│   ├── ui-deployment.yaml
│   ├── ui-service.yaml
│   ├── ui-service-hpa.yaml
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
- Go to Helm chart folder [orchestrate-sensor-services-with-hpa](https://github.com/sbhrwl/microservices/tree/main/sensorregistration/hpa/orchestrate-sensor-services-with-hpa)
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
PS C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa> helm list -n dev
NAME                            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                   APP VERSION
orchestrate-sensor-services-dev dev             1               2025-05-30 11:04:03.8511928 +0300 EEST  deployed        sensor-app-chart-0.1.0  1.0
PS C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa> kubectl get pods -n dev
NAME                                    READY   STATUS    RESTARTS   AGE
notification-service-7f5845c77c-ndt8t   1/1     Running   0          2m13s
registration-service-7c4555d588-vfz2j   1/1     Running   0          2m13s
sensor-service-59b4d96b5-q4kbl          1/1     Running   0          2m13s
ui-service-55f94d6747-gqw57             1/1     Running   0          2m13s
PS C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa> kubectl get svc -n dev
NAME                   TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
notification-service   ClusterIP   10.102.19.193    <none>        9084/TCP         2m24s
registration-service   ClusterIP   10.102.135.152   <none>        9083/TCP         2m24s
sensor-service         NodePort    10.101.218.249   <none>        9082:30082/TCP   2m24s
ui-service             NodePort    10.101.36.130    <none>        9081:30081/TCP   2m24s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `http://localhost:30081/` → `ui-service`
  * `http://localhost:30082/api/register/sensor` → `sensor-service`
  * ClusterIP service → `registration-service`
  * ClusterIP service → `notification-service`
### Uninstall Helm release
```
helm uninstall orchestrate-sensor-services-dev -n dev
helm uninstall orchestrate-sensor-services-staging -n staging
helm uninstall orchestrate-sensor-services-prod -n prod
```
## Upgrades per environment
- Go to Helm chart folder [orchestrate-sensor-services-with-hpa](https://github.com/sbhrwl/microservices/tree/main/sensorregistration/hpa/orchestrate-sensor-services-with-hpa)
```bash
helm upgrade orchestrate-sensor-services-staging . -f values-staging.yaml -n staging
helm upgrade orchestrate-sensor-services-prod . -f values-prod.yaml -n prod
```
## Environment specific secrets and configs
- Avoid putting secrets in values files.
- Instead:
  * Use Kubernetes `Secrets` or `External Secrets` per environment.
  * Use environment-specific ConfigMaps if needed.
