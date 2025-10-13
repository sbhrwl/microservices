ocs-release# Horizontal Pod Autoscalar
- [Introduction](#introduction)
- [Chart structure](#chart-structure)
- [HPA configuration](#hpa-configuration)
- [Install Helm release](#install-helm-release)
- [Verify release](#verify-release)
- [Verify HPA](#verify-hpa)
- [Access services](#access-services)
- [Uninstall Helm release](#uninstall-helm-release)
- [HPA simulations](#hpa-simulations)
  - [Check if metrics server is running](#check-if-metrics-server-is-running)
  - [Force a CPU load to test autoscaling](#force-a-cpu-load-to-test-autoscaling)
  - [Watch pod scaling in real time](#watch-pod-scaling-in-real-time)
## Introduction 
- The Horizontal Pod Autoscaler (HPA) is a Kubernetes resource that **automatically scales** the number of pods in a deployment, replica set, or stateful set based on observed metrics like `CPU utilization`, `memory usage`, or `custom metrics`.
## HPA configuration
- [flexibility-hub-simulator-hpa.yaml](orchestrate-hubtosensor-services/templates/flexibility-hub-simulator-hpa.yaml)
- [data-api-hpa.yaml](orchestrate-hubtosensor-services/templates/data-api-hpa.yaml)
- [values.yaml](orchestrate-hubtosensor-services/values.yaml)
## Chart structure
- [orchestrate-hubtosensor-services](orchestrate-hubtosensor-services)
- Each service has its own Deployment, Service, and Horizontal Pod Autoscaler (HPA) configuration.
  ```
  orchestrate-hubtosensor-services/
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
  ├── .helmignore
  ```
## Install Helm release
- Go to Helm chart folder (e.g., [orchestrate-hubtosensor-services](orchestrate-hubtosensor-services)), run this command:
  ```bash
  helm install ocs-h2shpa-release .
  ```

## Verify release
```
PS C:\Git\microservices\commandorchestration\hpa\orchestrate-hubtosensor-services> helm install ocs-h2shpa-release .
NAME: ocs-h2shpa-release
LAST DEPLOYED: Mon Jun  9 20:33:04 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
PS C:\Git\microservices\commandorchestration\hpa\orchestrate-hubtosensor-services> helm list
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                   APP VERSION
ocs-h2shpa-release default         1               2025-06-09 20:33:04.8829931 +0300 EEST  deployed        microservices-0.1.0     1.0
PS C:\Git\microservices\commandorchestration\hpa\orchestrate-hubtosensor-services> helm list -A
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                   APP VERSION
ocs-h2shpa-release default         1               2025-06-09 20:33:04.8829931 +0300 EEST  deployed        microservices-0.1.0     1.0
PS C:\Git\microservices\commandorchestration\hpa\orchestrate-hubtosensor-services> kubectl get pods
NAME                                                              READY   STATUS    RESTARTS   AGE
ocs-h2shpa-release-microservices-command-orchestrator-789c6d7sbdgq   1/1     Running   0          26s
ocs-h2shpa-release-microservices-protocol-gateway-7f86cfb765-mtzl4   1/1     Running   0          26s
ocs-h2shpa-release-microservices-sensor-simulator-5b7686d8c-n86fm    1/1     Running   0          26s
ocs-h2shpa-release-microservices-task-orchestrator-69555b676cbzc25   1/1     Running   0          26s
PS C:\Git\microservices\commandorchestration\hpa\orchestrate-hubtosensor-services> kubectl get svc
NAME                                                 TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
kubernetes                                           ClusterIP   10.96.0.1       <none>        443/TCP          35d
ocs-h2shpa-release-microservices-command-orchestrator   ClusterIP   10.108.97.239   <none>        9082/TCP         32s
ocs-h2shpa-release-microservices-sensor-simulator       ClusterIP   10.97.46.170    <none>        9084/TCP         32s
ocs-h2shpa-release-microservices-task-orchestrator      NodePort    10.96.175.72    <none>        9081:32147/TCP   32s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Verify HPA
- **`kubectl get hpa`**
```
PS C:\Git\microservices\commandorchestration\hpa\orchestrate-hubtosensor-services> kubectl get hpa
NAME                       REFERENCE                         TARGETS              MINPODS   MAXPODS   REPLICAS   AGE
command-orchestrator-hpa   Deployment/command-orchestrator   cpu: <unknown>/80%   1         5         0          49s
protocol-gateway-hpa       Deployment/protocol-gateway       cpu: <unknown>/80%   1         5         0          49s
sensor-simulator-hpa       Deployment/sensor-simulator       cpu: <unknown>/80%   1         5         0          49s
task-orchestrator-hpa      Deployment/task-orchestrator      cpu: <unknown>/80%   1         5         0          49s
```
- Describe a specific HPA: **`kubectl describe hpa registration-hpa`**
```
PS C:\Git\microservices\commandorchestration\hpa\orchestrate-hubtosensor-services> kubectl describe hpa task-orchestrator-hpa
Name:                                                  task-orchestrator-hpa
Namespace:                                             default
Labels:                                                app=task-orchestrator
                                                       app.kubernetes.io/managed-by=Helm
Annotations:                                           meta.helm.sh/release-name: ocs-h2shpa-release
                                                       meta.helm.sh/release-namespace: default
CreationTimestamp:                                     Mon, 09 Jun 2025 20:33:05 +0300
Reference:                                             Deployment/task-orchestrator
Metrics:                                               ( current / target )
  resource cpu on pods  (as a percentage of request):  <unknown> / 80%
Min replicas:                                          1
Max replicas:                                          5
Deployment pods:                                       0 current / 0 desired
Conditions:
  Type         Status  Reason          Message
  ----         ------  ------          -------
  AbleToScale  False   FailedGetScale  the HPA controller was unable to get the target's current scale: deployments/scale.apps "task-orchestrator" not found
Events:
  Type     Reason          Age   From                       Message
  ----     ------          ----  ----                       -------
  Warning  FailedGetScale  13s   horizontal-pod-autoscaler  deployments/scale.apps "task-orchestrator" not found
```
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `flexibility-hub-simulator` → `http://localhost:30881/api/messages`
  * `data-api-service` → `http://localhost:30885/api/v1/requests/<requestID>/tracker`
  * `ui-app` → `http://localhost:30880/`
## Uninstall Helm release
```
helm uninstall ocs-h2shpa-release
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
