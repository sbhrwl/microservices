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
- [**task-orchestrator-deployment**](orchestrate-command-services/templates/task-orchestrator-deployment.yaml)
- [**task-orchestrator-service**](orchestrate-command-services/templates/task-orchestrator-service.yaml)
- [**command-orchestrator-deployment**](orchestrate-command-services/templates/command-orchestrator-deployment.yaml)
- [**command-orchestrator-service**](orchestrate-command-services/templates/command-orchestrator-service.yaml)
- [**protocol-gateway-deployment**](orchestrate-command-services/templates/protocol-gateway-deployment.yaml)
- [**sensor-simulator-deployment**](orchestrate-command-services/templates/sensor-simulator-deployment.yaml)
- [**sensor-simulator-service**](orchestrate-command-services/templates/sensor-simulator-service.yaml)
- [**`values.yaml`**](orchestrate-command-services/values.yaml)
- [**`values-staging.yaml`**](orchestrate-command-services/values-staging.yaml)
- [**`values-prod.yaml`**](orchestrate-command-services/values-prod.yaml)
### Chart structure
```pgsql
orchestrate-command-services/
├── templates/
│   ├── task-orchestrator-deployment.yaml
│   ├── task-orchestrator-service.yaml
│   ├── command-orchestrator-deployment.yaml
│   ├── command-orchestrator-service.yaml
│   ├── protocol-gateway-deployment.yaml
│   ├── sensor-simulator-deployment.yaml
│   ├── sensor-simulator-service.yaml
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
- [Dependencies](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/prerequisites/README.md)
- Go to Helm chart folder [**orchestrate-command-services**](orchestrate-command-services)
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
PS C:\Git\microservices\commandorchestration\helmcharts\orchestrate-command-services> helm install ocs-release . -f values.yaml -n dev
NAME: ocs-release
LAST DEPLOYED: Mon Dec 15 10:21:46 2025
NAMESPACE: dev
STATUS: deployed
REVISION: 1
TEST SUITE: None
PS C:\Git\microservices\commandorchestration\helmcharts\orchestrate-command-services> helm list -n dev
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                   APP VERSION
ocs-release     dev             1               2025-12-15 10:21:46.8873332 +0200 EET   deployed        microservices-0.1.0     1.0
PS C:\Git\microservices\commandorchestration\helmcharts\orchestrate-command-services> kubectl get pods -n dev
NAME                                                             READY   STATUS    RESTARTS   AGE
ocs-release-microservices-command-orchestrator-75b55bb46-ggwrz   1/1     Running   0          22s
ocs-release-microservices-protocol-gateway-78d49fd4bc-nqltj      1/1     Running   0          22s
ocs-release-microservices-sensor-simulator-5b7686d8c-x9726       1/1     Running   0          22s
ocs-release-microservices-task-orchestrator-7b9788dd5d-h9w87     1/1     Running   0          22s
PS C:\Git\microservices\commandorchestration\helmcharts\orchestrate-command-services> kubectl get svc -n dev
NAME                                             TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
ocs-release-microservices-command-orchestrator   ClusterIP   10.97.15.189    <none>        9082/TCP         29s
ocs-release-microservices-sensor-simulator       ClusterIP   10.102.149.46   <none>        9084/TCP         29s
ocs-release-microservices-task-orchestrator      NodePort    10.97.22.149    <none>        9081:30081/TCP   29s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `http://localhost:30081/` → `task-orchestrator`
  * ClusterIP service → `command-orchestrator`
  * Kafka listener → `protocol-gateway`
  * ClusterIP service → `sensor-simulator`
## Verify HPA
- **`kubectl get hpa -n dev`**
```
PS C:\Git\microservices\commandorchestration\helmcharts\orchestrate-command-services> kubectl get hpa -n dev
NAME                                                 REFERENCE                                                   TARGETS              MINPODS   MAXPODS   REPLICAS   AGE
ocs-release-microservices-command-orchestrator-hpa   Deployment/ocs-release-microservices-command-orchestrator   cpu: <unknown>/80%   1         5         0          56s
ocs-release-microservices-protocol-gateway-hpa       Deployment/ocs-release-microservices-protocol-gateway       cpu: <unknown>/80%   1         5         0          56s
ocs-release-microservices-sensor-simulator-hpa       Deployment/ocs-release-microservices-sensor-simulator       cpu: <unknown>/80%   1         5         0          56s
ocs-release-microservices-task-orchestrator-hpa      Deployment/ocs-release-microservices-task-orchestrator      cpu: <unknown>/80%   1         5         0          56s
```
- Describe a specific HPA: **`kubectl describe hpa ocs-release-microservices-sensor-simulator-hpa -n dev`**
```
PS C:\Git\microservices\commandorchestration\hpa\orchestrate-command-services-with-hpa> kubectl describe hpa ocs-release-microservices-sensor-simulator-hpa -n dev

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
