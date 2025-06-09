# Horizontal Pod Autoscalar
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
- [notification-service-hpa.yaml](orchestrate-sensor-services-with-hpa/templates/notification-service-hpa.yaml)
- [registration-service-hpa.yaml](orchestrate-sensor-services-with-hpa/templates/registration-service-hpa.yaml)
- [sensor-service-hpa.yaml](orchestrate-sensor-services-with-hpa/templates/sensor-service-hpa.yaml)
- [ui-service-hpa.yaml](orchestrate-sensor-services-with-hpa/templates/ui-service-hpa.yaml)
- [values.yaml](orchestrate-sensor-services-with-hpa/values.yaml)
## Chart structure
- [orchestrate-sensor-services-with-hpa](orchestrate-sensor-services-with-hpa)
- Each service has its own Deployment, Service, and Horizontal Pod Autoscaler (HPA) configuration.
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
## Install Helm release
- Go to Helm chart folder (e.g., [orchestrate-sensor-services-with-hpa](orchestrate-sensor-services-with-hpa)), run this command:
  ```bash
  helm install orchestrate-sensor-services-with-hpa-release .
  ```

## Verify release
```
PS C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa> helm install orchestrate-sensor-services-with-hpa-release .
NAME: orchestrate-sensor-services-with-hpa-release
LAST DEPLOYED: Fri May 30 11:01:11 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
PS C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa> helm list
NAME                                            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                   APP VERSION
orchestrate-sensor-services-with-hpa-release    default         1               2025-05-30 11:01:11.2868806 +0300 EEST  deployed        sensor-app-chart-0.1.0  1.0
PS C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa> helm list -A
NAME                                            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                   APP VERSION
orchestrate-sensor-services-with-hpa-release    default         1               2025-05-30 11:01:11.2868806 +0300 EEST  deployed        sensor-app-chart-0.1.0  1.0
PS C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa> kubectl get pods
NAME                                    READY   STATUS    RESTARTS   AGE
notification-service-7f5845c77c-nm2qt   1/1     Running   0          23s
registration-service-7c4555d588-b4mpk   1/1     Running   0          23s
sensor-service-59b4d96b5-l2zwl          1/1     Running   0          23s
ui-service-55f94d6747-qbssp             1/1     Running   0          23s
PS C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa> kubectl get svc
NAME                   TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
kubernetes             ClusterIP   10.96.0.1        <none>        443/TCP          25d
notification-service   ClusterIP   10.107.55.248    <none>        9084/TCP         27s
registration-service   ClusterIP   10.109.192.122   <none>        9083/TCP         27s
sensor-service         NodePort    10.97.158.255    <none>        9082:30082/TCP   27s
ui-service             NodePort    10.102.72.111    <none>        9081:30081/TCP   27s
PS C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa>
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Verify HPA
- **`kubectl get hpa`**
```
PS C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa> kubectl get hpa
NAME               REFERENCE                 TARGETS              MINPODS   MAXPODS   REPLICAS   AGE
notification-hpa   Deployment/notification   cpu: <unknown>/80%   1         5         0          59s
registration-hpa   Deployment/registration   cpu: <unknown>/80%   1         5         0          59s
sensor-hpa         Deployment/sensor         cpu: <unknown>/80%   1         5         0          59s
ui-hpa             Deployment/ui             cpu: <unknown>/80%   1         5         0          59s
```
- Describe a specific HPA: **`kubectl describe hpa registration-hpa`**
```
PS C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa> kubectl get hpa
NAME               REFERENCE                 TARGETS              MINPODS   MAXPODS   REPLICAS   AGE
notification-hpa   Deployment/notification   cpu: <unknown>/80%   1         5         0          59s
registration-hpa   Deployment/registration   cpu: <unknown>/80%   1         5         0          59s
sensor-hpa         Deployment/sensor         cpu: <unknown>/80%   1         5         0          59s
ui-hpa             Deployment/ui             cpu: <unknown>/80%   1         5         0          59s
PS C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa> kubectl describe hpa registration-hpa
Name:                                                  registration-hpa
Namespace:                                             default
Labels:                                                app=registration
                                                       app.kubernetes.io/managed-by=Helm
Annotations:                                           meta.helm.sh/release-name: orchestrate-sensor-services-with-hpa-release
                                                       meta.helm.sh/release-namespace: default
CreationTimestamp:                                     Fri, 30 May 2025 11:01:11 +0300
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
  Type     Reason          Age   From                       Message
  ----     ------          ----  ----                       -------
  Warning  FailedGetScale  11s   horizontal-pod-autoscaler  deployments/scale.apps "registration" not found
```
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `http://localhost:30081/` → `task-orchestrator`
  * ClusterIP service → `command-orchestrator`
  * Kafka listener → `protocol-gateway`
  * ClusterIP service → `sensor-simulator`
## Uninstall Helm release
```
helm uninstall ocs-hpa-release
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
