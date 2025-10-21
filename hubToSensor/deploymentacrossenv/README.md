# Creating environments
- [Introduction](#introduction)
- [Create environment wise values file](#create-environment-wise-values-file)
- [Use Kubernetes namespaces](#use-kubernetes-namespaces)
- [Helm releases per environment](#install-helm-releases-per-environment)
  - [Push images for staging](#push-images-for-staging)
  - [Make a release](#make-a-release)
  - [Verify release](#verify-release)
  - [Access services](#access-services)
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
### Push images for staging
- Tag the image with `staging` tag
- Push the image
```
docker tag sbhrwldocker/flexibility-hub-simulator:latest sbhrwldocker/flexibility-hub-simulator:staging
docker push sbhrwldocker/flexibility-hub-simulator:staging

docker tag sbhrwldocker/flexibility-bridge:latest sbhrwldocker/flexibility-bridge:staging
docker push sbhrwldocker/flexibility-bridge:staging

docker tag sbhrwldocker/storage-service:latest sbhrwldocker/storage-service:staging
docker push sbhrwldocker/storage-service:staging

docker tag sbhrwldocker/protocol-adapter:latest sbhrwldocker/protocol-adapter:staging
docker push sbhrwldocker/protocol-adapter:staging

docker tag sbhrwldocker/hes-simulator:latest sbhrwldocker/hes-simulator:staging
docker push sbhrwldocker/hes-simulator:staging

docker tag sbhrwldocker/ui-app:latest sbhrwldocker/ui-app:staging
docker push sbhrwldocker/ui-app:staging

docker tag sbhrwldocker/data-api:latest sbhrwldocker/data-api:staging
docker push sbhrwldocker/data-api:staging
```
### Make a release
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
- `kubectl get pods -n staging`
- `kubectl get svc -n staging`
```
C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>kubectl create namespace staging
namespace/staging created

C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>helm install ocs-staging . -f values-staging.yaml -n staging
NAME: ocs-staging
LAST DEPLOYED: Mon Oct 13 11:06:27 2025
NAMESPACE: staging
STATUS: deployed
REVISION: 1
TEST SUITE: None

C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>helm list -A
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                                   APP VERSION
ocs-staging     staging         1               2025-10-13 11:06:27.5528108 +0300 EEST  deployed        orchestrate-hubtosensor-services-0.1.0  1.16.0

C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>helm list -n staging
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                                   APP VERSION
ocs-staging     staging         1               2025-10-13 11:06:27.5528108 +0300 EEST  deployed        orchestrate-hubtosensor-services-0.1.0  1.16.0

C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>kubectl get all -n staging
NAME                                                        READY   STATUS             RESTARTS   AGE
pod/data-api-546dbcd7c5-dwjxv                               0/1     ErrImagePull       0          21s
pod/flexibility-bridge-deployment-6d5d5c5cc8-j976f          0/1     ErrImagePull       0          21s
pod/flexibility-hub-simulator-deployment-77fdf75db9-g6gzz   0/1     ErrImagePull       0          21s
pod/hes-simulator-deployment-5fc6b865d6-rpg47               0/1     ErrImagePull       0          21s
pod/protocol-adapter-deployment-67bdb4db9c-l79ds            0/1     ImagePullBackOff   0          21s
pod/storage-service-deployment-795bf87594-nxq6f             0/1     ImagePullBackOff   0          21s
pod/ui-app-8bf7fb47c-p8mxr                                  0/1     ErrImagePull       0          21s

NAME                                        TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
service/data-api-service                    NodePort    10.106.179.103   <none>        8085:30885/TCP   21s
service/flexibility-hub-simulator-service   NodePort    10.96.63.5       <none>        8081:30881/TCP   21s
service/storage-service-service             ClusterIP   10.99.13.202     <none>        9090/TCP         21s
service/ui-app-service                      NodePort    10.96.200.210    <none>        8080:30880/TCP   21s

NAME                                                   READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/data-api                               0/1     1            0           21s
deployment.apps/flexibility-bridge-deployment          0/1     1            0           21s
deployment.apps/flexibility-hub-simulator-deployment   0/1     1            0           21s
deployment.apps/hes-simulator-deployment               0/1     1            0           21s
deployment.apps/protocol-adapter-deployment            0/1     1            0           21s
deployment.apps/storage-service-deployment             0/1     1            0           21s
deployment.apps/ui-app                                 0/1     1            0           21s

NAME                                                              DESIRED   CURRENT   READY   AGE
replicaset.apps/data-api-546dbcd7c5                               1         1         0       21s
replicaset.apps/flexibility-bridge-deployment-6d5d5c5cc8          1         1         0       21s
replicaset.apps/flexibility-hub-simulator-deployment-77fdf75db9   1         1         0       21s
replicaset.apps/hes-simulator-deployment-5fc6b865d6               1         1         0       21s
replicaset.apps/protocol-adapter-deployment-67bdb4db9c            1         1         0       21s
replicaset.apps/storage-service-deployment-795bf87594             1         1         0       21s
replicaset.apps/ui-app-8bf7fb47c                                  1         1         0       21s

NAME                                                                REFERENCE                                         TARGETS                                     MINPODS   MAXPODS   REPLICAS   AGE
horizontalpodautoscaler.autoscaling/data-api-hpa                    Deployment/data-api-deployment                    cpu: <unknown>/50%, memory: <unknown>/60%   1         2         0          21s
horizontalpodautoscaler.autoscaling/flexibility-hub-simulator-hpa   Deployment/flexibility-hub-simulator-deployment   cpu: <unknown>/50%, memory: <unknown>/60%   1         2         0          21s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
### Access services
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
