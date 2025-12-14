# [Helm charts](https://github.com/sbhrwl/system_design/blob/main/docs/devops/containerisation/Kubernetes/deploymentstrategies/README.md)
- [Settign up docker images](docs/containers/README.md)
- [Kubernetes](docs/kubernetes/README.md)
- [Chart setup](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/helmcharts/docs/setup/README.md)
- [Create templates YAMLs](#create-helm-templates-yamls)
- [Chart structure](#chart-structure)
- [Install Helm release](#install-helm-release)
- [Verify deployment](#verify-deployment)
- [Access services](#access-services)
- [Verify HPA](#verify-hpa)
- [HPA simulations](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/helmcharts/docs/hpa/README.md)
- [Uninstall Helm release](#uninstall-helm-release)
- [Deployment across environments](docs/deploymentacrossenv/README.md)
## Create templates YAMLs
- [**ingestion-deployment**](orchestrate-ingestion-grpc-services/templates/ingestion-grpc-deployment.yaml)
- [**ingestion-service**](orchestrate-ingestion-grpc-services/templates/ingestion-grpc-service.yaml)
- [**hub-deployment**](orchestrate-ingestion-grpc-services/templates/hub-deployment.yaml)
- [**hub-service**](orchestrate-ingestion-grpc-services/templates/hub-service.yaml)
- [**`values.yaml`**](orchestrate-ingestion-grpc-services/values.yaml)
### Chart structure
```pgsql
orchestrate-ingestion-grpc-services/
├── templates/
│   ├── ingestion-grpc-deployment.yaml
│   ├── ingestion-grpc-service.yaml
│   ├── hub-grpc-deployment.yaml
│   ├── hub-grpc-service.yaml
│   └── _helpers.tpl
├── Chart.yaml
├── values.yaml
├── .helmignore
```
## Install Helm release
- Cleanup existing Kubernetes deployment 
```
kubectl delete -f ingestion-grpc-service.yaml
kubectl delete -f hub-service.yaml
```
- Go to Helm chart folder [**orchestrate-ingestion-services**](orchestrate-ingestion-grpc-services)
```powershell
helm install ocs-grpc-release .
```
- **`ocs-grpc-release`** is the name you're assigning to this Helm release (you can change it if you like).
- `.` means Helm will *install using the chart in the current directory*.
## Verify deployment
```
PS C:\Git\microservices\gRPC\helmcharts\orchestrate-ingestion-grpc-services> helm install ocs-grpc-release .
NAME: ocs-grpc-release
LAST DEPLOYED: Fri Jun 13 12:26:40 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
PS C:\Git\microservices\gRPC\helmcharts\orchestrate-ingestion-grpc-services> helm list
NAME                    NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                           APP VERSION
ocs-grpc-release        default         1               2025-06-13 12:26:40.5940937 +0300 EEST  deployed        ingestion-grpc-service-0.1.0
PS C:\Git\microservices\gRPC\helmcharts\orchestrate-ingestion-grpc-services> kubectl get pods
NAME                                                       READY   STATUS    RESTARTS   AGE
ocs-grpc-release-hub-service-766d89bbfc-g9xbh              1/1     Running   0          7s
ocs-grpc-release-ingestion-grpc-service-86879f9db6-h5bc6   1/1     Running   0          7s
PS C:\Git\microservices\gRPC\helmcharts\orchestrate-ingestion-grpc-services> kubectl get svc
NAME                                      TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)              AGE
kubernetes                                ClusterIP   10.96.0.1        <none>        443/TCP              39d
ocs-grpc-release-hub-service              ClusterIP   10.102.37.156    <none>        9082/TCP,50051/TCP   13s
ocs-grpc-release-ingestion-grpc-service   NodePort    10.105.126.205   <none>        9081:30081/TCP       13s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `localhost:30081/generate/registration` → `ingestion-service`
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
## Uninstall Helm release
- Delete all Kubernetes resources (Deployments, Services, etc.) that were created by the Helm release named `ocs-grpc-release`
```
helm uninstall ocs-grpc-release
``` 