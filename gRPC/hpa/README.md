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
- [ingestion-grpc-service-hpa.yaml](orchestrate-ingestion-grpc-services-hpa/templates/ingestion-grpc-service-hpa.yaml)
- [hub-service-hpa.yaml](orchestrate-ingestion-grpc-services-hpa/templates/hub-service-hpa.yaml)
- [values.yaml](orchestrate-ingestion-grpc-services-hpa/values.yaml)
## Chart structure
- [orchestrate-ingestion-grpc-services-hpa](orchestrate-ingestion-grpc-services-hpa)
- Each service has its own Deployment, Service, and Horizontal Pod Autoscaler (HPA) configuration.
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
## Install Helm release
- Go to Helm chart folder (e.g., [orchestrate-ingestion-grpc-services-hpa](orchestrate-ingestion-grpc-services-hpa)), run this command:
  ```bash
  helm install orchestrate-ingestion-grpc-services-hpa-release .
  ```

## Verify release
```
PS C:\Git\microservices\gRPC\hpa\orchestrate-ingestion-grpc-services-hpa> helm install orchestrate-ingestion-grpc-services-hpa-release .
NAME: orchestrate-ingestion-grpc-services-hpa-release
LAST DEPLOYED: Fri Jun 13 13:55:31 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
PS C:\Git\microservices\gRPC\hpa\orchestrate-ingestion-grpc-services-hpa> helm list
NAME                                            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                           APP VERSION
orchestrate-ingestion-grpc-services-hpa-release default         1               2025-06-13 13:55:31.4430381 +0300 EEST  deployed        ingestion-grpc-service-0.1.0
PS C:\Git\microservices\gRPC\hpa\orchestrate-ingestion-grpc-services-hpa> kubectl get pods
NAME                                                              READY   STATUS    RESTARTS   AGE
orchestrate-ingestion-grpc-services-hpa-release-hub-servicfzh4c   1/1     Running   0          12s
orchestrate-ingestion-grpc-services-hpa-release-ingestion-wc66t   1/1     Running   0          12s
PS C:\Git\microservices\gRPC\hpa\orchestrate-ingestion-grpc-services-hpa> kubectl get svc
NAME                                                             TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)              AGE
kubernetes                                                       ClusterIP   10.96.0.1       <none>        443/TCP              39d
orchestrate-ingestion-grpc-services-hpa-release-hub-service      ClusterIP   10.99.120.170   <none>        9082/TCP,50051/TCP   17s
orchestrate-ingestion-grpc-services-hpa-release-ingestion-grpc   NodePort    10.103.55.247   <none>        9081:30081/TCP       17s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Verify HPA
- **`kubectl get hpa`**
```
PS C:\Git\microservices\gRPC\hpa\orchestrate-ingestion-grpc-services-hpa> kubectl get hpa
NAME                                                                 REFERENCE                                                                   TARGETS              MINPODS   MAXPODS   REPLICAS   AGE
orchestrate-ingestion-grpc-services-hpa-release-hub-service-hpa      Deployment/orchestrate-ingestion-grpc-services-hpa-release-hub-service      cpu: <unknown>/75%   1         5         0          35s
orchestrate-ingestion-grpc-services-hpa-release-ingestion-grpc-hpa   Deployment/orchestrate-ingestion-grpc-services-hpa-release-ingestion-grpc   cpu: <unknown>/75%   1         5         0          35s
PS C:\Git\microservices\gRPC\hpa\orchestrate-ingestion-grpc-services-hpa> kubectl describe hpa orchestrate-ingestion-grpc-services-hpa-release-hub-service-hpa
Name:                                                  orchestrate-ingestion-grpc-services-hpa-release-hub-service-hpa
Namespace:                                             default
Labels:                                                app.kubernetes.io/instance=orchestrate-ingestion-grpc-services-hpa-release
                                                       app.kubernetes.io/managed-by=Helm
                                                       app.kubernetes.io/name=hub-service
Annotations:                                           meta.helm.sh/release-name: orchestrate-ingestion-grpc-services-hpa-release
                                                       meta.helm.sh/release-namespace: default
CreationTimestamp:                                     Fri, 13 Jun 2025 13:55:31 +0300
Reference:                                             Deployment/orchestrate-ingestion-grpc-services-hpa-release-hub-service
Metrics:                                               ( current / target )
  resource cpu on pods  (as a percentage of request):  <unknown> / 75%
Min replicas:                                          1
Max replicas:                                          5
Deployment pods:                                       1 current / 0 desired
Conditions:
  Type           Status  Reason                   Message
  ----           ------  ------                   -------
  AbleToScale    True    SucceededGetScale        the HPA controller was able to get the target's current scale
  ScalingActive  False   FailedGetResourceMetric  the HPA was unable to compute the replica count: failed to get cpu utilization: unable to get metrics for resource cpu: unable to fetch metrics from resource metrics API: the server could not find the requested resource (get pods.metrics.k8s.io)
Events:
  Type     Reason                        Age   From                       Message
  ----     ------                        ----  ----                       -------
  Warning  FailedGetResourceMetric       24s   horizontal-pod-autoscaler  failed to get cpu utilization: unable to get metrics for resource cpu: unable to fetch metrics from resource metrics API: the server could not find the requested resource (get pods.metrics.k8s.io)
  Warning  FailedComputeMetricsReplicas  24s   horizontal-pod-autoscaler  invalid metrics (1 invalid out of 1), first error is: failed to get cpu resource metric value: failed to get cpu utilization: unable to get metrics for resource cpu: unable to fetch metrics from resource metrics API: the server could not find the requested resource (get pods.metrics.k8s.io)
```
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `localhost:30081/generate/registration` → `ingestion-grpc-service`
## Uninstall Helm release
```
helm uninstall orchestrate-ingestion-grpc-services-hpa-release
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
