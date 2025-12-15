# [Helm charts](https://github.com/sbhrwl/system_design/blob/main/docs/devops/containerisation/Kubernetes/deploymentstrategies/README.md)
- [Setting up docker images](docs/containers/README.md)
- [Kubernetes](docs/kubernetes/README.md)
- [Chart setup](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/helmcharts/docs/setup/README.md)
- [Create templates YAMLs](#create-helm-templates-yamls)
- [Chart structure](#chart-structure)
- [Install Helm release](#install-helm-release)
- [Verify deployment](#verify-deployment)
- [Access services](#access-services)
- [Verify HPA](#verify-hpa)
- [HPA simulations](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/helmcharts/docs/hpa/README.md)
- [Update Helm release](#update-helm-release)
- [Uninstall Helm release](#uninstall-helm-release)
- [Deployment across environments](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/helmcharts/docs/deploymentacrossenv/README.md)
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
- [Dependencies](../prerequisites/README.md)
- Go to Helm chart folder [**orchestrate-ingestion-services**](orchestrate-ingestion-services)
- Verify existing namepsaces: `kubectl get ns`
- Create namepsace: `kubectl create namespace dev`
  - Verify: `kubectl get ns`
- Install Helm release
```powershell
helm install ocs-release . -f values.yaml -n dev
```
- **`orchestrate-ingestion-services-release`** is the name you're assigning to this Helm release (you can change it if you like).
- `.` means Helm will *install using the chart in the current directory*.
## Verify deployment
```
PS C:\Git\microservices\analytics\helmcharts\orchestrate-ingestion-services> helm install ocs-release . -f values.yaml -n dev
NAME: ocs-release
LAST DEPLOYED: Mon Dec 15 11:57:15 2025
NAMESPACE: dev
STATUS: deployed
REVISION: 1
TEST SUITE: None
PS C:\Git\microservices\analytics\helmcharts\orchestrate-ingestion-services> helm list -n dev
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                           APP VERSION
ocs-release     dev             1               2025-12-15 11:57:15.609587 +0200 EET    deployed        ingestion-service-chart-0.1.0   1.0
PS C:\Git\microservices\analytics\helmcharts\orchestrate-ingestion-services> kubectl get pods -n dev
NAME                                            READY   STATUS    RESTARTS   AGE
ocs-release-ingestion-service-78846b7cb-zwdpv   1/1     Running   0          18s
PS C:\Git\microservices\analytics\helmcharts\orchestrate-ingestion-services> kubectl get svc -n dev
NAME                            TYPE       CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
ocs-release-ingestion-service   NodePort   10.111.140.97   <none>        9081:30081/TCP   24s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `localhost:30081/api/powerquality/generate` → `ingestion-service`
## Verify HPA
- **`kubectl get hpa`**
```
PS C:\Git\microservices\analytics\helmcharts\orchestrate-ingestion-services> kubectl get hpa -n dev
NAME                            REFERENCE                                  TARGETS              MINPODS   MAXPODS   REPLICAS   AGE
ocs-release-ingestion-service   Deployment/ocs-release-ingestion-service   cpu: <unknown>/70%   1         5         1          86s
```
- Describe a specific HPA: **`kubectl describe hpa <hpa-name>`**
```
PS C:\Git\microservices\analytics\helmcharts\orchestrate-ingestion-services> kubectl describe hpa ocs-release-ingestion-service -n dev
Name:                                                  ocs-release-ingestion-service
Namespace:                                             dev
Labels:                                                app.kubernetes.io/managed-by=Helm
Annotations:                                           meta.helm.sh/release-name: ocs-release
                                                       meta.helm.sh/release-namespace: dev
CreationTimestamp:                                     Mon, 15 Dec 2025 13:35:21 +0200
Reference:                                             Deployment/ocs-release-ingestion-service
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
  Warning  FailedGetResourceMetric       49s   horizontal-pod-autoscaler  failed to get cpu utilization: unable to get metrics for resource cpu: unable to fetch metrics from resource metrics API: the server could not find the requested resource (get pods.metrics.k8s.io)
  Warning  FailedComputeMetricsReplicas  49s   horizontal-pod-autoscaler  invalid metrics (1 invalid out of 1), first error is: failed to get cpu resource metric value: failed to get cpu utilization: unable to get metrics for resource cpu: unable to fetch metrics from resource metrics API: the server could not find the requested resource (get pods.metrics.k8s.io)
```
## Update Helm release
```
helm upgrade --install ocs-release . -n dev
```
## Uninstall Helm release
- Delete all Kubernetes resources (Deployments, Services, etc.) that were created by the Helm release named `ocs-release`
```
helm uninstall ocs-release -n dev
``` 