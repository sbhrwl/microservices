# [Helm charts](https://github.com/sbhrwl/system_design/blob/main/docs/devops/containerisation/Kubernetes/deploymentstrategies/README.md)
- [Dapr Commands](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/dapr/introduction/commands/README.md)
- [Setting up docker images](#setting-up-docker-images)
- [Chart setup](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/helmcharts/docs/setup/README.md)
- [Create templates YAMLs](#create-helm-templates-yamls)
- [Chart structure](#chart-structure)
- [Install Helm release](#install-helm-release)
- [Verify deployment](#verify-deployment)
- [Access services](#access-services)
- [Update Helm release](#update-helm-release)
- [Uninstall Helm release](#uninstall-helm-release)
## Setting up docker images 

| Steps | Flexibility hub simulator | Flexibility bridge | Command orchestrator | Protocol adapter | HES-AIM simulator |
|---|---|---|---|---|---|
| application.yml | [application.yml](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/flexibility-hub-simulator/src/main/resources/application.yml) | [application.yml](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/flexibility-bridge-service/src/main/resources/application.yml) | [application.yml](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/storage-service-grpc/src/main/resources/application.yml) | [application.yml](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/protocol-adapter-service/src/main/resources/application.yml) | [application.yml](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/hes-simulator/src/main/resources/application.yml) |
| Dapr configuration scope | [rabbitmq-pubsub.yaml](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/dapr/hub-to-sensor/flexibility-hub-simulator/dapr/config-files/rabbitmq-pubsub.yaml) | [rabbitmq-pubsub.yaml](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/dapr/hub-to-sensor/flexibility-bridge-service/dapr/config-files/rabbitmq-pubsub.yaml) | [postgres-statestore.yaml](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/dapr/hub-to-sensor/storage-service-grpc/dapr/config-files/postgres-statestore.yaml) | [rabbitmq-pubsub.yaml](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/dapr/hub-to-sensor/protocol-adapter-service/dapr/config-files/rabbitmq-pubsub.yaml) | [rabbitmq-pubsub.yaml](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/dapr/hub-to-sensor/hes-simulator/dapr/config-files/rabbitmq-pubsub.yaml) |
| Build jar | `mvn clean package` | `mvn clean package` | `mvn clean package` | `mvn clean package` | `mvn clean package` |
| Dockerfile | [Dockerfile](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/flexibility-hub-simulator/Dockerfile) | [Dockerfile](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/flexibility-bridge-service/Dockerfile) | [Dockerfile](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/flexibility-hub-simulator/Dockerfile) | [Dockerfile](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/flexibility-bridge-service/Dockerfile) | [Dockerfile](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/flexibility-hub-simulator/Dockerfile) |
| Build image | `docker build -t sbhrwldocker/flexibility-hub-simulator:dapr-latest .` | `docker build -t sbhrwldocker/flexibility-bridge-service:dapr-latest .` | `docker build -t sbhrwldocker/storage-service:dapr-latest .` | `docker build -t sbhrwldocker/protocol-adapter-service:dapr-latest .` | `docker build -t sbhrwldocker/hes-simulator:dapr-latest .` |
| Push image | `docker push sbhrwldocker/flexibility-hub-simulator:dapr-latest` | `docker push sbhrwldocker/flexibility-bridge-service:dapr-latest` | `docker push sbhrwldocker/storage-service:dapr-latest` | `docker push sbhrwldocker/protocol-adapter-service:dapr-latest` | `docker push sbhrwldocker/hes-simulator:dapr-latest` |

## Create templates YAMLs
- [**flexibility-hub-simulator-deployment**](orchestrate-hubtosensor-services/templates/flexibility-hub-simulator-deployment.yaml)
- [**flexibility-hub-simulator-service**](orchestrate-hubtosensor-services/templates/flexibility-hub-simulator-service.yaml)
- [**storage-service-deployment**](orchestrate-hubtosensor-services/templates/storage-service-deployment.yaml)
- [**storage-service-service**](orchestrate-hubtosensor-services/templates/storage-service-service.yaml)
- [**flexibility-bridge-deployment**](orchestrate-hubtosensor-services/templates/flexibility-bridge-deployment.yaml)
- [**protocol-adapter-deployment**](orchestrate-hubtosensor-services/templates/protocol-adapter-deployment.yaml)
- [**hes-simulator-deployment**](orchestrate-hubtosensor-services/templates/hes-simulator-deployment.yaml)
- [**data-api-deployment.yaml**](orchestrate-hubtosensor-services/templates/data-api-deployment.yaml)
- [**data-api-service.yaml**](orchestrate-hubtosensor-services/templates/data-api-service.yaml)
- [**`values.yaml`**](orchestrate-hubtosensor-services/values.yaml)
### Chart structure
```pgsql
orchestrate-hubtosensor-services/
├── templates/
│   ├── flexibility-hub-simulator-deployment.yaml
│   ├── flexibility-hub-simulator-service.yaml
│   ├── storage-service-deployment.yaml
│   ├── storage-service-service.yaml
│   ├── flexibility-bridge-deployment.yaml
│   ├── protocol-adapter-deployment.yaml
│   ├── hes-simulator-deployment.yaml
│   ├── data-api-deployment.yaml
│   ├── data-api-service.yaml
│   └── _helpers.tpl
├── Chart.yaml
├── values.yaml
├── .helmignore
```
## Install Helm release
- Pre-requisites: to clean the dapr componenet which were used when running app locally
  - `kubectl delete component rabbitmq-pubsub`
  - `kubectl delete component postgres-statestore`
- [Dependencies](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/prerequisites/README.md)
- [Initialise DAPR](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/dapr/prerequisites/README.md)
- Go to Helm chart folder [**orchestrate-hubtosensor-services**](orchestrate-hubtosensor-services)
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
C:\Git\microservices\hubToSensor\dapr\helmcharts\orchestrate-hubtosensor-services>helm install ocs-release . -n dev
NAME: ocs-h2s-release
LAST DEPLOYED: Fri Dec  5 12:59:03 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None

PS C:\Users\sabharwalr> helm list -n dev
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                           APP VERSION
ocs-h2s-release default         1               2025-12-05 12:59:03.4566343 +0200 EET   deployed        flexibility-hub-simulator-0.1.0 1.0.0
PS C:\Users\sabharwalr> kubectl get pods -n dev
NAME                                         READY   STATUS    RESTARTS   AGE
flexibility-hub-simulator-654f7d97fc-gfhpp   2/2     Running   0          9m12s
api-layer-77ddb6449c-mvmhk                   1/1     Running   0          9m12s
flexibility-bridge-7d485dfbdc-qhz5q          2/2     Running   0          9m12s
command-orchestrator-7d6855bd8f-vmg4x        2/2     Running   0          9m12s
protocol-adapter-58cfd6df87-894h6            2/2     Running   0          9m12s
hes-aim-59dbcfc476-4r29k                     2/2     Running   0          9m12s
PS C:\Users\sabharwalr> kubectl get svc -n dev
NAME                             TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)                               AGE
flexibility-hub-simulator        NodePort    10.109.4.63      <none>        8081:30081/TCP                        9m17s
api-layer                        NodePort    10.103.229.230   <none>        8085:30885/TCP                        9m17s
command-orchestrator             NodePort    10.100.86.100    <none>        8086:30086/TCP,50003:31417/TCP        9m17s
flexibility-hub-simulator-dapr   ClusterIP   None             <none>        80/TCP,50001/TCP,50002/TCP,9090/TCP   9m17s
flexibility-bridge-dapr          ClusterIP   None             <none>        80/TCP,50001/TCP,50002/TCP,9090/TCP   9m17s
command-orchestrator-dapr        ClusterIP   None             <none>        80/TCP,50001/TCP,50002/TCP,9090/TCP   9m17s
protocol-adapter-dapr            ClusterIP   None             <none>        80/TCP,50001/TCP,50002/TCP,9090/TCP   9m17s
hes-aim-dapr                     ClusterIP   None             <none>        80/TCP,50001/TCP,50002/TCP,9090/TCP   9m17s
kubernetes                       ClusterIP   10.96.0.1        <none>        443/TCP                               214d
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `flexibility-hub-simulator` → `http://localhost:30081/api/messages`
  * `api-layer` → `http://localhost:30885/api/v1/requests/<requestID>/tracker`
    * `kubectl port-forward service/api-layer 8085:8085`
  * [`ui-app`](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/hub-to-sensor/ui-app/README.md) → `http://localhost:4200/`
  * `Get sensor state` → `http://localhost:30086/sensor/<sensor-nbr>`
## Update Helm release
```
helm upgrade --install ocs-release . -n dev
```
## Uninstall Helm release
- Delete all Kubernetes resources (Deployments, Services, etc.) that were created by the Helm release named `ocs-h2s-release`
```
helm uninstall ocs-release -n dev
``` 
