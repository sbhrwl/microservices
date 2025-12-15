# [Helm charts](https://github.com/sbhrwl/system_design/blob/main/docs/devops/containerisation/Kubernetes/deploymentstrategies/README.md)
- [Setting up docker images](docs/containers/README.md)
- [Kubernetes](docs/kubernetes/README.md)
- [Chart setup](docs/setup/README.md)
- [Create templates YAMLs](#create-helm-templates-yamls)
- [Chart structure](#chart-structure)
- [Install Helm release](#install-helm-release)
- [Verify deployment](#verify-deployment)
- [Access services](#access-services)
- [Verify HPA](#verify-hpa)
- [HPA simulations](docs/hpa/README.md)
- [Update Helm release](#update-helm-release)
- [Uninstall Helm release](#uninstall-helm-release)
- [Deployment across environments](docs/deploymentacrossenv/README.md)
## Create templates YAMLs
- [ui-deployment](orchestrate-sensor-services/templates/ui-deployment.yaml)
- [ui-service](orchestrate-sensor-services/templates/ui-service.yaml)
- [ui-service-hpa.yaml](orchestrate-sensor-services/templates/ui-service-hpa.yaml)
- [sensor-deployment](orchestrate-sensor-services/templates/sensor-deployment.yaml)
- [sensor-service](orchestrate-sensor-services/templates/sensor-service.yaml)
- [sensor-service-hpa.yaml](orchestrate-sensor-services/templates/sensor-service-hpa.yaml)
- [registration-deployment](orchestrate-sensor-services/templates/registration-deployment.yaml)
- [registration-service](orchestrate-sensor-services/templates/registration-service.yaml)
- [registration-service-hpa.yaml](orchestrate-sensor-services/templates/registration-service-hpa.yaml)
- [notification-deployment](orchestrate-sensor-services/templates/notification-deployment.yaml)
- [notification-service](orchestrate-sensor-services/templates/notification-service.yaml)
- [notification-configmap](orchestrate-sensor-services/templates/notification-configmap.yaml)
- [notification-service-hpa.yaml](orchestrate-sensor-services/templates/notification-service-hpa.yaml)
- [`values.yaml`](orchestrate-sensor-services/values.yaml)
- [`values-staging.yaml`](orchestrate-sensor-services/values-staging.yaml)
- [`values-prod.yaml`](orchestrate-sensor-services/values-prod.yaml)
### Chart structure
```pgsql
orchestrate-sensor-services/
├── templates/
│   ├── ui-deployment.yaml
│   ├── ui-service.yaml
│   ├── ui-service-hpa.yaml
│   ├── registration-deployment.yaml
│   ├── registration-service.yaml
│   ├── registration-service-hpa.yaml
│   ├── sensor-deployment.yaml
│   ├── sensor-service.yaml
│   ├── sensor-service-hpa.yaml
│   ├── notification-deployment.yaml
│   ├── notification-service.yaml
│   ├── notification-service-hpa.yaml
│   └── _helpers.tpl
├── Chart.yaml
├── values.yaml
├── values-staging.yaml
├── values-prod.yaml
├── .helmignore
```
## Install Helm release
- Cleanup existing Kubernetes deployment 
```
kubectl delete -f orchestrate-sensor-services.yaml
```
- [Dependencies](../prerequisites/README.md)
- Go to Helm chart folder [**orchestrate-sensor-services**](orchestrate-sensor-services)
- Verify existing namepsaces: `kubectl get ns`
- Create namepsace: `kubectl create namespace dev`
  - Verify: `kubectl get ns`
- Install Helm release
```powershell
helm install ocs-release . -f values.yaml -n dev
```
- **`ocs-release`** is the name you're assigning to this Helm release (you can change it if you like).
- `.` means Helm will *install using the chart in the current directory*.
## Verify deployment
```
PS C:\Git\microservices\sensorregistration\helmcharts\orchestrate-sensor-services> helm install ocs-release . -f values.yaml -n dev
W1215 09:57:57.615364   19436 warnings.go:70] spec.template.spec.containers[0].env[8]: hides previous definition of "NOTIFICATION_SERVICE_URL", which may be dropped when using apply
NAME: ocs-release
LAST DEPLOYED: Mon Dec 15 09:57:57 2025
NAMESPACE: dev
STATUS: deployed
REVISION: 1
TEST SUITE: None
PS C:\Git\microservices\sensorregistration\helmcharts\orchestrate-sensor-services> helm list -n dev
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                   APP VERSION
ocs-release     dev             1               2025-12-15 09:57:57.2213469 +0200 EET   deployed        sensor-app-chart-0.1.0  1.0
PS C:\Git\microservices\sensorregistration\helmcharts\orchestrate-sensor-services> kubectl get pods -n dev
NAME                                                              READY   STATUS    RESTARTS   AGE
ocs-release-sensor-app-chart-notification-service-7cf97b46lrwdp   1/1     Running   0          23s
ocs-release-sensor-app-chart-registration-service-6dd7d847x6r4z   1/1     Running   0          23s
ocs-release-sensor-app-chart-sensor-service-69c58c96cc-2d9rc      1/1     Running   0          23s
ocs-release-sensor-app-chart-ui-service-7c8dfbb9c7-tw5kt          1/1     Running   0          23s
PS C:\Git\microservices\sensorregistration\helmcharts\orchestrate-sensor-services> kubectl get svc -n dev
NAME                                                TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
ocs-release-sensor-app-chart-notification-service   ClusterIP   10.107.95.126    <none>        9084/TCP         30s
ocs-release-sensor-app-chart-sensor-service         NodePort    10.107.103.156   <none>        9082:30082/TCP   30s
ocs-release-sensor-app-chart-ui-service             NodePort    10.104.4.18      <none>        9081:30081/TCP   30s
registration-service                                ClusterIP   10.104.23.73     <none>        9083/TCP         30s
PS C:\Git\microservices\sensorregistration\helmcharts\orchestrate-sensor-services> kubectl get all -n dev
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `http://localhost:30081/` → `ui-service`
    * username: endpointaccessuser
    * password: password123
  * `http://localhost:30082/api/register/sensor` → `sensor-service`
  * ClusterIP service → `registration-service`
  * ClusterIP service → `notification-service`
## Verify HPA
- **`kubectl get hpa`**
```
PS C:\Git\microservices\sensorregistration\helmcharts\orchestrate-sensor-services> kubectl get hpa -n dev
NAME                                            REFERENCE                                                      TARGETS              MINPODS   MAXPODS   REPLICAS   AGE
ocs-release-sensor-app-chart-notification-hpa   Deployment/ocs-release-sensor-app-chart-notification-service   cpu: <unknown>/80%   1         5         1          92s
ocs-release-sensor-app-chart-sensor-hpa         Deployment/ocs-release-sensor-app-chart-sensor-service         cpu: <unknown>/80%   1         5         1          92s
ocs-release-sensor-app-chart-ui-hpa             Deployment/ocs-release-sensor-app-chart-ui-service             cpu: <unknown>/80%   1         5         1          92s
registration-hpa                                Deployment/registration                                        cpu: <unknown>/80%   1         5         0          92s
```
- Describe a specific HPA: **`kubectl describe hpa registration-hpa`**
```
PS C:\Git\microservices\sensorregistration\helmcharts\orchestrate-sensor-services> kubectl describe hpa registration-hpa -n dev
Name:                                                  registration-hpa
Namespace:                                             dev
Labels:                                                app=registration
                                                       app.kubernetes.io/managed-by=Helm
Annotations:                                           meta.helm.sh/release-name: ocs-release
                                                       meta.helm.sh/release-namespace: dev
CreationTimestamp:                                     Mon, 15 Dec 2025 09:57:57 +0200
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
  Warning  FailedGetScale  43s   horizontal-pod-autoscaler  deployments/scale.apps "registration" not found
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