# Horizontal Pod Autoscalar
## Introduction 
- The Horizontal Pod Autoscaler (HPA) is a Kubernetes resource that **automatically scales** the number of pods in a deployment, replica set, or stateful set based on observed metrics like `CPU utilization`, `memory usage`, or `custom metrics`.
## Structure
- [orchestrate-sensor-services-with-hpa](orchestrate-sensor-services-with-hpa)
- This Helm chart deploys two services:
  - **Command Orchestration**
  - **Task Orchestration**
- Each service has its own Deployment, Service, and `optional` Horizontal Pod Autoscaler (HPA) configuration.
  ```
  orchestrate-sensor-services-with-hpa-release/
  ├── charts/
  ├── templates/
  │   ├── ui-deployment.yaml
  │   ├── ui-service.yaml
  │   ├── ui-service-hpa.yaml
  │   ├── sensor-deployment.yaml
  │   ├── sensor-service.yaml
  │   ├── sensor-service-hpa.yaml
  │   ├── registration-deployment.yaml
  │   ├── registration-service.yaml
  │   ├── registration-service-hpa.yaml
  │   ├── notification-deployment.yaml
  │   ├── notification-service.yaml
  │   ├── notification-service-hpa.yaml
  │   └── _helpers.tpl
  ├── Chart.yaml
  ├── values.yaml
  ├── values-staging.yaml
  ├── values-prod.yaml
  ```
## HPA configuration
- [notification-service-hpa.yaml](orchestrate-sensor-services-with-hpa/templates/notification-service-hpa.yaml)
- [registration-service-hpa.yaml](orchestrate-sensor-services-with-hpa/templates/registration-service-hpa.yaml)
- [sensor-service-hpa.yaml](orchestrate-sensor-services-with-hpa/templates/sensor-service-hpa.yaml)
- [ui-service-hpa.yaml](orchestrate-sensor-services-with-hpa/templates/ui-service-hpa.yaml)
- [values.yaml](orchestrate-sensor-services-with-hpa/values.yaml)
## Installation
- Go to Helm chart folder (e.g., [orchestrate-sensor-services-with-hpa](orchestrate-sensor-services-with-hpa)), run this command:
  ```bash
  helm install orchestrate-sensor-services-with-hpa-release .
  ```
- To install with a specific environment configuration:
  ```bash
  helm install orchestrate-sensor-services-with-hpa-services . -f values-prod.yaml
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
## Install Helm releases per environment
- Go to Helm chart folder (e.g., `orchestrate-sensor-services-with-hpa-release`)
```bash
# For dev (default values.yaml)
helm install orchestrate-sensor-services-with-hpa-release-dev . -n dev

# For staging
helm install orchestrate-sensor-services-with-hpa-release-staging . -f values-staging.yaml -n staging

# For prod
helm install orchestrate-sensor-services-with-hpa-release-prod . -f values-prod.yaml -n prod
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
- `kubectl get pods -n dev`
- `kubectl get svc -n dev`
```
C:\Git\microservices\sensorregistration\helmcharts\deploy\orchestrate-sensor-services>helm list -n dev
NAME                            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                   APP VERSION
orchestrate-sensor-services-dev dev             1               2025-05-29 10:28:52.6299129 +0300 EEST  deployed        sensor-app-chart-0.1.0  1.0

C:\Git\microservices\sensorregistration\helmcharts\deploy\orchestrate-sensor-services>kubectl get pods -n dev
NAME                                    READY   STATUS    RESTARTS   AGE
notification-service-7f5845c77c-vskkc   1/1     Running   0          44s
registration-service-7c4555d588-tc8vs   1/1     Running   0          44s
sensor-service-59b4d96b5-pxz7k          1/1     Running   0          44s
ui-service-55f94d6747-r8jkh             1/1     Running   0          44s

C:\Git\microservices\sensorregistration\helmcharts\deploy\orchestrate-sensor-services>kubectl get svc -n dev
NAME                   TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
notification-service   ClusterIP   10.98.195.16     <none>        9084/TCP         102s
registration-service   ClusterIP   10.110.139.200   <none>        9083/TCP         102s
sensor-service         NodePort    10.109.147.130   <none>        9082:30082/TCP   102s
ui-service             NodePort    10.106.105.86    <none>        9081:30081/TCP   102s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `http://localhost:30081/` → `ui-service`
  * `http://localhost:30082/api/register/sensor` → `sensor-service`
  * ClusterIP service → `registration-service`
  * ClusterIP service → `notification-service`
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
