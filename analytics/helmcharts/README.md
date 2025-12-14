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
- [**ingestion-deployment**](orchestrate-ingestion-services/templates/ui-deployment.yaml)
- [**ingestion-service**](orchestrate-ingestion-services/templates/ui-service.yaml)
- [**`values.yaml`**](orchestrate-ingestion-services/values.yaml)
### Chart structure
```pgsql
orchestrate-ingestion-services/
├── templates/
│   ├── ingestion-deployment.yaml
│   ├── ingestion-service.yaml
│   ├── ingestion-service-hpa.yaml
│   └── _helpers.tpl
├── Chart.yaml
├── values.yaml
├── .helmignore
```
## Install Helm release
- Cleanup existing Kubernetes deployment 
```
kubectl delete -f ingestion-services.yaml
```
- Go to Helm chart folder [**orchestrate-ingestion-services**](orchestrate-ingestion-services)
```powershell
helm install orchestrate-ingestion-services-release .
```
- **`orchestrate-ingestion-services-release`** is the name you're assigning to this Helm release (you can change it if you like).
- `.` means Helm will *install using the chart in the current directory*.
## Verify deployment
```
PS C:\Git\microservices\analytics\helmcharts\orchestrate-ingestion-services> helm install orchestrate-ingestion-services-release .
NAME: orchestrate-ingestion-services-release
LAST DEPLOYED: Tue Jun  3 13:43:28 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
PS C:\Git\microservices\analytics\helmcharts\orchestrate-ingestion-services> helm list
NAME                                    NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                           APP VERSION
orchestrate-ingestion-services-release  default         1               2025-06-03 13:43:28.7138803 +0300 EEST  deployed        ingestion-service-chart-0.1.0   1.0        
PS C:\Git\microservices\analytics\helmcharts\orchestrate-ingestion-services> helm list -A
NAME                                    NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                           APP VERSION
orchestrate-ingestion-services-release  default         1               2025-06-03 13:43:28.7138803 +0300 EEST  deployed        ingestion-service-chart-0.1.0   1.0        
PS C:\Git\microservices\analytics\helmcharts\orchestrate-ingestion-services> kubectl get pods 
NAME                                                              READY   STATUS    RESTARTS   AGE
orchestrate-ingestion-services-release-ingestion-service-7kzkns   1/1     Running   0          22s
PS C:\Git\microservices\analytics\helmcharts\orchestrate-ingestion-services> kubectl get svc
NAME                                                       TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
kubernetes                                                 ClusterIP   10.96.0.1      <none>        443/TCP          29d
orchestrate-ingestion-services-release-ingestion-service   NodePort    10.98.242.98   <none>        9081:30091/TCP   29s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `localhost:30081/api/powerquality/generate` → `ingestion-service`
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
## Uninstall Helm release
- Delete all Kubernetes resources (Deployments, Services, etc.) that were created by the Helm release named `orchestrate-ingestion-services-release`
```
helm uninstall orchestrate-ingestion-services-release
``` 
