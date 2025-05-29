# Horizontal Pod Autoscalar
- [Introduction](#introduction)
- [Chart structure](#chart-structure)
- [HPA configuration](#hpa-configuration)
- [Installation](#installation)
  - [Use Kubernetes namespaces](#use-kubernetes-namespaces)
  - [Releases per environment](#releases-per-environment)
- [Verify release](#verify-release)
- [Verify HPA](#verify-hpa)
- [Access services](#access-services)
- [Uninstall](#uninstall)
- [Upgrades per environment](#upgrades-per-environment)
- [HPA simulations](#hpa-simulations)
  - [Check if metrics server is running](#check-if-metrics-server-is-running)
  - [Force a CPU load to test autoscaling](#force-a-cpu-load-to-test-autoscaling)
  - [Watch pod scaling in real time](#watch-pod-scaling-in-real-time)
## Introduction 
- The Horizontal Pod Autoscaler (HPA) is a Kubernetes resource that **automatically scales** the number of pods in a deployment, replica set, or stateful set based on observed metrics like `CPU utilization`, `memory usage`, or `custom metrics`.
## Chart structure
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
### Use Kubernetes namespaces
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
### Releases per environment
- Go to Helm chart folder [`orchestrate-sensor-services-with-hpa`](orchestrate-sensor-services-with-hpa)
```bash
# For dev (default values.yaml)
helm install orchestrate-sensor-services-with-hpa-release-dev . -n dev

# For staging
helm install orchestrate-sensor-services-with-hpa-release-staging . -f values-staging.yaml -n staging

# For prod
helm install orchestrate-sensor-services-with-hpa-release-prod . -f values-prod.yaml -n prod
```
## Verify release
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
C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa>helm install orchestrate-sensor-services-with-hpa-release-dev . -n dev
NAME: orchestrate-sensor-services-with-hpa-release-dev
LAST DEPLOYED: Thu May 29 18:10:01 2025
NAMESPACE: dev
STATUS: deployed
REVISION: 1
TEST SUITE: None

C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa>helm list -n dev
NAME                                                    NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                   APP VERSION
orchestrate-sensor-services-with-hpa-release-dev        dev             1               2025-05-29 18:10:01.6976586 +0300 EEST  deployed        sensor-app-chart-0.1.0  1.0

C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa>kubectl get pods -n dev
NAME                                    READY   STATUS    RESTARTS   AGE
notification-service-7f5845c77c-k98lr   1/1     Running   0          26s
registration-service-7c4555d588-5qm9v   1/1     Running   0          26s
sensor-service-59b4d96b5-m4wdb          1/1     Running   0          26s
ui-service-55f94d6747-clsck             1/1     Running   0          26s

C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa>kubectl get svc -n dev
NAME                   TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
notification-service   ClusterIP   10.107.107.90   <none>        9084/TCP         32s
registration-service   ClusterIP   10.100.167.26   <none>        9083/TCP         32s
sensor-service         NodePort    10.97.154.243   <none>        9082:30082/TCP   32s
ui-service             NodePort    10.105.71.251   <none>        9081:30081/TCP   32s

C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa>
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Verify HPA
- **`kubectl get hpa -n dev`**
```
C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa>kubectl get hpa -n dev
NAME               REFERENCE                 TARGETS              MINPODS   MAXPODS   REPLICAS   AGE
notification-hpa   Deployment/notification   cpu: <unknown>/80%   1         5         0          4m10s
registration-hpa   Deployment/registration   cpu: <unknown>/80%   1         5         0          4m10s
sensor-hpa         Deployment/sensor         cpu: <unknown>/80%   1         5         0          4m10s
ui-hpa             Deployment/ui             cpu: <unknown>/80%   1         5         0          4m10s
```
- Describe a specific HPA: **`kubectl describe hpa registration-hpa -n dev`*"
```
C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa>kubectl describe hpa registration-hpa -n dev
Name:                                                  registration-hpa
Namespace:                                             dev
Labels:                                                app=registration
                                                       app.kubernetes.io/managed-by=Helm
Annotations:                                           meta.helm.sh/release-name: orchestrate-sensor-services-with-hpa-release-dev
                                                       meta.helm.sh/release-namespace: dev
CreationTimestamp:                                     Thu, 29 May 2025 18:10:02 +0300
Reference:                                             Deployment/registration
Metrics:                                               ( current / target )
  resource cpu on pods  (as a percentage of request):  <unknown> / 80%
Min replicas:                                          1
Max replicas:                                          5
Deployment pods:                                       0 current / 0 desired
Conditions:
  Type         Status  Reason          Message
  ----         ------  ------          -------
  AbleToScale  False   FailedGetScale  the HPA controller was unable to get the target's current scale: deployments/scale.apps "registration" not found
Events:
  Type     Reason          Age                  From                       Message
  ----     ------          ----                 ----                       -------
  Warning  FailedGetScale  27s (x5 over 4m27s)  horizontal-pod-autoscaler  deployments/scale.apps "registration" not found
```
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `http://localhost:30081/` → `ui-service`
  * `http://localhost:30082/api/register/sensor` → `sensor-service`
  * ClusterIP service → `registration-service`
  * ClusterIP service → `notification-service`
## Uninstall
```
helm uninstall orchestrate-sensor-services-with-hpa-release-dev -n dev
helm uninstall orchestrate-sensor-services-with-hpa-release-staging -n staging
helm uninstall orchestrate-sensor-services-with-hpa-release-prod -n prod
```
## Upgrades per environment
- Go to Helm chart folder (e.g., `orchestrate-sensor-services`)
```bash
helm upgrade orchestrate-sensor-services-with-hpa-release-staging . -f values-staging.yaml -n staging
helm upgrade orchestrate-sensor-services-with-hpa-release-prod . -f values-prod.yaml -n prod
```
## HPA simulations
### Check if metrics server is running
- HPAs require the Kubernetes Metrics Server.
- Ensure it's running:
```sh
kubectl get deployment metrics-server -n kube-system
```
- If not installed, [follow these instructions to install it](https://github.com/kubernetes-sigs/metrics-server#installation).
### Force a CPU load to test autoscaling
- You can create a load generator pod to increase CPU usage and trigger scaling:
```sh
kubectl run -i --tty load-generator --rm \
  --image=busybox /bin/sh

# Inside the shell, run:
while true; do :; done
```
### Watch pod scaling in real time
- This lets you observe autoscaling behavior:
```sh
watch kubectl get hpa
```
- Or for the pods directly:
```sh
watch kubectl get pods -l app=registration
```
- replace `registration` with other service names as needed
