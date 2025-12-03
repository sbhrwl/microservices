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
- Go to Helm chart folder (e.g., [orchestrate-hubtosensor-services](orchestrate-hubtosensor-services), run this command:
  ```bash
  helm install ocs-h2shpa-release .
  ```

## Verify release
```
C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>helm install ocs-h2shpa-release .
NAME: ocs-h2shpa-release
LAST DEPLOYED: Mon Oct 13 10:51:34 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None

C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>helm list
NAME                    NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                                   APP VERSION
ocs-h2shpa-release      default         1               2025-10-13 10:51:34.5736087 +0300 EEST  deployed        orchestrate-hubtosensor-services-0.1.0  1.16.0

C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>helm list -A
NAME                    NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                                   APP VERSION
ocs-h2shpa-release      default         1               2025-10-13 10:51:34.5736087 +0300 EEST  deployed        orchestrate-hubtosensor-services-0.1.0  1.16.0

C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>kubectl get pods
NAME                                                    READY   STATUS    RESTARTS   AGE
data-api-66c7b99b9-4qndq                                1/1     Running   0          38s
flexibility-bridge-deployment-5d787fbd76-klxkg          1/1     Running   0          38s
flexibility-hub-simulator-deployment-6567556765-kjr67   1/1     Running   0          38s
hes-simulator-deployment-58fdc95776-mpf4m               1/1     Running   0          38s
protocol-adapter-deployment-7668d7b9b5-ml5n9            1/1     Running   0          38s
storage-service-deployment-6f6bd769c6-88xdr             1/1     Running   0          38s
ui-app-676567d78f-wn785                                 1/1     Running   0          38s

C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>kubectl get svc
NAME                                TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
data-api-service                    NodePort    10.110.32.148    <none>        8085:30885/TCP   44s
flexibility-hub-simulator-service   NodePort    10.108.117.126   <none>        8081:30881/TCP   44s
kubernetes                          ClusterIP   10.96.0.1        <none>        443/TCP          161d
storage-service-service             ClusterIP   10.103.232.43    <none>        9090/TCP         44s
ui-app-service                      NodePort    10.100.68.173    <none>        8080:30880/TCP   44s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Verify HPA
- **`kubectl get hpa`**
```
C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>kubectl get hpa
NAME                            REFERENCE                                         TARGETS                                     MINPODS   MAXPODS   REPLICAS   AGE
data-api-hpa                    Deployment/data-api-deployment                    cpu: <unknown>/50%, memory: <unknown>/60%   1         2         0          77s
flexibility-hub-simulator-hpa   Deployment/flexibility-hub-simulator-deployment   cpu: <unknown>/50%, memory: <unknown>/60%   1         2         1          77s
```
- Describe a specific HPA: **`kubectl describe hpa registration-hpa`**
```
C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>kubectl describe hpa flexibility-hub-simulator-hpa
Name:                                                     flexibility-hub-simulator-hpa
Namespace:                                                default
Labels:                                                   app.kubernetes.io/managed-by=Helm
Annotations:                                              meta.helm.sh/release-name: ocs-h2shpa-release
                                                          meta.helm.sh/release-namespace: default
CreationTimestamp:                                        Mon, 13 Oct 2025 10:51:35 +0300
Reference:                                                Deployment/flexibility-hub-simulator-deployment
Metrics:                                                  ( current / target )
  resource cpu on pods  (as a percentage of request):     <unknown> / 50%
  resource memory on pods  (as a percentage of request):  <unknown> / 60%
Min replicas:                                             1
Max replicas:                                             2
Deployment pods:                                          1 current / 0 desired
Conditions:
  Type           Status  Reason                   Message
  ----           ------  ------                   -------
  AbleToScale    True    SucceededGetScale        the HPA controller was able to get the target's current scale
  ScalingActive  False   FailedGetResourceMetric  the HPA was unable to compute the replica count: failed to get cpu utilization: unable to get metrics for resource cpu: unable to fetch metrics from resource metrics API: the server could not find the requested resource (get pods.metrics.k8s.io)
Events:
  Type     Reason                        Age               From                       Message
  ----     ------                        ----              ----                       -------
  Warning  FailedGetResourceMetric       4s (x2 over 64s)  horizontal-pod-autoscaler  failed to get cpu utilization: unable to get metrics for resource cpu: unable to fetch metrics from resource metrics API: the server could not find the requested resource (get pods.metrics.k8s.io)
  Warning  FailedGetResourceMetric       4s (x2 over 64s)  horizontal-pod-autoscaler  failed to get memory utilization: unable to get metrics for resource memory: unable to fetch metrics from resource metrics API: the server could not find the requested resource (get pods.metrics.k8s.io)
  Warning  FailedComputeMetricsReplicas  4s (x2 over 64s)  horizontal-pod-autoscaler  invalid metrics (2 invalid out of 2), first error is: failed to get cpu resource metric value: failed to get cpu utilization: unable to get metrics for resource cpu: unable to fetch metrics from resource metrics API: the server could not find the requested resource (get pods.metrics.k8s.io)
```
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `flexibility-hub-simulator` → `http://localhost:30881/api/messages`
  * `data-api-service` → `http://localhost:30885/api/v1/requests/<requestID>/tracker`
  * [`ui-app`](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/hub-to-sensor/ui-app/README.md) → `http://localhost:4200/`
* Database verification
```sql
PS C:\Users\sabharwalr> psql -h localhost -U myuser -d mydatabase
Password for user myuser:

psql (17.6, server 16.9 (Debian 16.9-1.pgdg120+1))
WARNING: Console code page (850) differs from Windows code page (1252)
         8-bit characters might not work correctly. See psql reference
         page "Notes for Windows users" for details.
Type "help" for help.

mydatabase=# select * from control_requests;
mydatabase=# select * from request_change_log;
```
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
