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
- [ingestion-service-hpa.yaml](orchestrate-ingestion-services-hpa/templates/ingestion-service-hpa.yaml)
- [values.yaml](orchestrate-ingestion-services-hpa/values.yaml)
## Chart structure
- [orchestrate-ingestion-services-hpa](orchestrate-ingestion-services-hpa)
- Each service has its own Deployment, Service, and Horizontal Pod Autoscaler (HPA) configuration.
  ```
  orchestrate-ingestion-services-hpa/
  ├── templates/
  │   ├── ingestion-deployment.yaml
  │   ├── ingestion-service.yaml
  │   ├── ingestion-service-hpa.yaml
  │   └── _helpers.tpl
  ├── Chart.yaml
  ├── values.yaml
  ├── values-staging.yaml
  ├── values-prod.yaml
  ```
## Install Helm release
- Go to Helm chart folder (e.g., [orchestrate-ingestion-services-hpa](orchestrate-ingestion-services-hpa)), run this command:
  ```bash
  helm install orchestrate-ingestion-services-hpa-release .
  ```

## Verify release
```
PS C:\Git\microservices\analytics\hpa\orchestrate-ingestion-services-hpa> helm install orchestrate-ingestion-services-hpa-release .
NAME: orchestrate-ingestion-services-hpa-release
LAST DEPLOYED: Tue Jun  3 14:16:06 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
PS C:\Git\microservices\analytics\hpa\orchestrate-ingestion-services-hpa> helm list
NAME                                            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                           APP VERSION
orchestrate-ingestion-services-hpa-release      default         1               2025-06-03 14:16:06.4760078 +0300 EEST  deployed        ingestion-service-chart-0.1.0   1.0        
PS C:\Git\microservices\analytics\hpa\orchestrate-ingestion-services-hpa> helm list -A
NAME                                            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                           APP VERSION
orchestrate-ingestion-services-hpa-release      default         1               2025-06-03 14:16:06.4760078 +0300 EEST  deployed        ingestion-service-chart-0.1.0   1.0        
PS C:\Git\microservices\analytics\hpa\orchestrate-ingestion-services-hpa> kubectl get pods
NAME                                                              READY   STATUS    RESTARTS   AGE
orchestrate-ingestion-services-hpa-release-ingestion-servilkt8j   1/1     Running   0          17s
PS C:\Git\microservices\analytics\hpa\orchestrate-ingestion-services-hpa> kubectl get svc 
NAME                                                           TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
kubernetes                                                     ClusterIP   10.96.0.1       <none>        443/TCP          29d
orchestrate-ingestion-services-hpa-release-ingestion-service   NodePort    10.109.44.178   <none>        9081:30081/TCP   21s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Verify HPA
- **`kubectl get hpa`**
```
PS C:\Git\microservices\analytics\hpa\orchestrate-ingestion-services-hpa> kubectl get hpa
NAME                                                           REFERENCE                                                                 TARGETS              MINPODS   MAXPODS   REPLICAS   AGE
orchestrate-ingestion-services-hpa-release-ingestion-service   Deployment/orchestrate-ingestion-services-hpa-release-ingestion-service   cpu: <unknown>/70%   1         5         0          46s
PS C:\Git\microservices\analytics\hpa\orchestrate-ingestion-services-hpa> kubectl describe hpa orchestrate-ingestion-services-hpa-release-ingestion-service
Name:                                                  orchestrate-ingestion-services-hpa-release-ingestion-service
Namespace:                                             default
Labels:                                                app.kubernetes.io/managed-by=Helm
Annotations:                                           meta.helm.sh/release-name: orchestrate-ingestion-services-hpa-release
                                                       meta.helm.sh/release-namespace: default
CreationTimestamp:                                     Tue, 03 Jun 2025 14:16:06 +0300
Reference:                                             Deployment/orchestrate-ingestion-services-hpa-release-ingestion-service
Metrics:                                               ( current / target )
  resource cpu on pods  (as a percentage of request):  <unknown> / 70%
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
  Warning  FailedGetResourceMetric       56s   horizontal-pod-autoscaler  failed to get cpu utilization: unable to get metrics for resource cpu: unable to fetch metrics from resource metrics API: the server could not find the requested resource (get pods.metrics.k8s.io)
  Warning  FailedComputeMetricsReplicas  56s   horizontal-pod-autoscaler  invalid metrics (1 invalid out of 1), first error is: failed to get cpu resource metric value: failed to get cpu utilization: unable to get metrics for resource cpu: unable to fetch metrics from resource metrics API: the server could not find the requested resource (get pods.metrics.k8s.io)
```
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `localhost:30081/api/powerquality/generate` → `ingestion-service`
## Uninstall Helm release
```
helm uninstall orchestrate-ingestion-services-hpa-release
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
