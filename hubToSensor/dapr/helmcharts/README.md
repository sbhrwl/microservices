# [Helm charts](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Kubernetes/deploymentstrategies/README.md)
- [Setting up containers](#setting-up-containers)
- [Create Helm chart structure](#create-helm-chart-structure)
- [Clean up the default templates](#clean-up-the-default-templates)
- [Create Helm templates from application YAMLs](#create-helm-templates-from-application-yamls)
- [Chart structure](#chart-structure)
- [Cleanup existing Kubernetes deployment](#cleanup-existing-kubernetes-deployment)
- [Install Helm release](#install-helm-release)
- [Verify deployment](#verify-deployment)
- [Access services](#access-services)
- [Update Helm release](#update-helm-release)
- [Uninstall Helm release](#uninstall-helm-release)
- [Troubleshooting](#troubleshooting)
  - [Dapr Commands](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/dapr/introduction/commands/README.md)
## Setting up containers

| Steps | Flexibility hub simulator | Flexibility bridge | Command orchestrator | Protocol adapter | HES-AIM simulator |
|---|---|---|---|---|---|
| application.yml | [application.yml](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/flexibility-hub-simulator/src/main/resources/application.yml) | [application.yml](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/flexibility-bridge-service/src/main/resources/application.yml) | [application.yml](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/storage-service-grpc/src/main/resources/application.yml) | [application.yml](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/protocol-adapter-service/src/main/resources/application.yml) | [application.yml](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/hes-simulator/src/main/resources/application.yml) |
| Dapr configuration scope | [rabbitmq-pubsub.yaml](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/dapr/hub-to-sensor/flexibility-hub-simulator/dapr/config-files/rabbitmq-pubsub.yaml) | [rabbitmq-pubsub.yaml](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/dapr/hub-to-sensor/flexibility-bridge-service/dapr/config-files/rabbitmq-pubsub.yaml) | [postgres-statestore.yaml](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/dapr/hub-to-sensor/storage-service-grpc/dapr/config-files/postgres-statestore.yaml) | [rabbitmq-pubsub.yaml](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/dapr/hub-to-sensor/protocol-adapter-service/dapr/config-files/rabbitmq-pubsub.yaml) | [rabbitmq-pubsub.yaml](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/dapr/hub-to-sensor/hes-simulator/dapr/config-files/rabbitmq-pubsub.yaml) |
| Build jar | `mvn clean package` | `mvn clean package` | `mvn clean package` | `mvn clean package` | `mvn clean package` |
| Dockerfile | [Dockerfile](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/flexibility-hub-simulator/Dockerfile) | [Dockerfile](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/flexibility-bridge-service/Dockerfile) | [Dockerfile](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/flexibility-hub-simulator/Dockerfile) | [Dockerfile](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/flexibility-bridge-service/Dockerfile) | [Dockerfile](https://github.com/sbhrwl/microservices/tree/main/hubToSensor/dapr/hub-to-sensor/flexibility-hub-simulator/Dockerfile) |
| Build image | `docker build -t sbhrwldocker/flexibility-hub-simulator:dapr-latest .` | `docker build -t sbhrwldocker/flexibility-bridge-service:dapr-latest .` | `docker build -t sbhrwldocker/storage-service:dapr-latest .` | `docker build -t sbhrwldocker/protocol-adapter-service:dapr-latest .` | `docker build -t sbhrwldocker/hes-simulator:dapr-latest .` |
| Push image | `docker push sbhrwldocker/flexibility-hub-simulator:dapr-latest` | `docker push sbhrwldocker/flexibility-bridge-service:dapr-latest` | `docker push sbhrwldocker/storage-service:dapr-latest` | `docker push sbhrwldocker/protocol-adapter-service:dapr-latest` | `docker push sbhrwldocker/hes-simulator:dapr-latest` |

## Create Helm chart structure
- [Helm setup](setup/README.md)
- Generate the basic `Helm chart directory`. 
- Run this in your terminal:
  ```
  helm create orchestrate-hubtosensor-services
  ```
- This creates a directory called [**orchestrate-hubtosensor-services**](orchestrate-hubtosensor-services) with default templates and values.
<img src="images/directorystructure.jpg">

## Clean up the default templates
- Helm’s `create` command generates a bunch of example templates we don’t need. Let’s simplify.
- Go to the `templates` folder:
  ```
  cd orchestrate-hubtosensor-services/templates
  ```
- Delete all the default templates *except* `_helpers.tpl`.
  - *(Use **`del`** if you’re in Command Prompt on Windows instead of Git Bash or PowerShell)*
  ```bash
  del deployment.yaml service.yaml hpa.yaml ingress.yaml serviceaccount.yaml tests\test-connection.yaml
  del tests\test-connection.yaml & rmdir tests & del NOTES.txt

  rm deployment.yaml service.yaml hpa.yaml ingress.yaml serviceaccount.yaml tests/test-connection.yaml
  ```
- You should only have this file left:
  ```
  _helpers.tpl
  ```
## Create Helm templates from application YAMLs
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
## Cleanup existing Kubernetes deployment 
```
kubectl delete -f orchestrate-hubtosensor-services.yaml
```
## Install Helm release
- Pre-requisites: to clean the dapr componenet which were used when running app locally
  - `kubectl delete component rabbitmq-pubsub`
  - `kubectl delete component postgres-statestore`
- Go to Helm chart folder [**orchestrate-hubtosensor-services**](orchestrate-hubtosensor-services)
```powershell
helm install ocs-h2s-release .
```
- **`ocs-h2s-release`** is the name you're assigning to this Helm release (you can change it if you like).
- `.` means Helm will *install using the chart in the current directory*.
## Verify deployment
```
C:\Git\microservices\hubToSensor\dapr\helmcharts\orchestrate-hubtosensor-services>helm install ocs-h2s-release .
NAME: ocs-h2s-release
LAST DEPLOYED: Fri Dec  5 12:59:03 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None

PS C:\Users\sabharwalr> helm list
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                           APP VERSION
ocs-h2s-release default         1               2025-12-05 12:59:03.4566343 +0200 EET   deployed        flexibility-hub-simulator-0.1.0 1.0.0
PS C:\Users\sabharwalr> kubectl get pods
NAME                                         READY   STATUS    RESTARTS   AGE
flexibility-hub-simulator-654f7d97fc-gfhpp   2/2     Running   0          9m12s
api-layer-77ddb6449c-mvmhk                   1/1     Running   0          9m12s
flexibility-bridge-7d485dfbdc-qhz5q          2/2     Running   0          9m12s
command-orchestrator-7d6855bd8f-vmg4x        2/2     Running   0          9m12s
protocol-adapter-58cfd6df87-894h6            2/2     Running   0          9m12s
hes-aim-59dbcfc476-4r29k                     2/2     Running   0          9m12s
PS C:\Users\sabharwalr> kubectl get svc
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
helm upgrade --install ocs-h2s-release .
```
## Uninstall Helm release
- Delete all Kubernetes resources (Deployments, Services, etc.) that were created by the Helm release named `ocs-h2s-release`
```
helm uninstall ocs-h2s-release
``` 
## Troubleshooting
### Problem
- After deploying via Helm, the `flexibility-hub-simulator-service` (NodePort 30881) was unreachable from the host, even though it worked before manual deployment.
- **Approach to diagnose:**
1. **Check pods and services** — confirm all running in the `default` namespace:
   ```bash
   kubectl get pods
   kubectl get svc
   ```
2. **Get ClusterIP of the service** (used for internal pod access):
   ```bash
   kubectl get svc flexibility-hub-simulator-service -o jsonpath='{.spec.clusterIP}'
   ```

   * This gives the internal service IP (`10.x.x.x`).
3. **Get Node IP of the cluster node** (used for NodePort access inside the cluster):
   ```bash
   kubectl get nodes -o wide
   ```

   * This shows the internal node IP (`192.168.65.3` for Docker Desktop).
4. **Check NodePort mapping in service YAML** (ensure `targetPort` matches container port):
   ```bash
   kubectl get svc flexibility-hub-simulator-service -o yaml
   ```

   * Confirms `port: 8081` → `nodePort: 30881`.

5. **Check internal pod connectivity** using ClusterIP:
   ```bash
   kubectl exec -it <pod-name> -- curl http://<cluster-ip>:8081
   ```

   → Responded `404` → service working internally.

6. **Test NodePort inside the cluster** using node IP:
   ```bash
   kubectl exec -it <pod-name> -- curl http://<node-ip>:30881
   ```

   → Responded `404` → NodePort routing fine.

7. **Test NodePort externally from Windows host**:
   ```bash
   curl http://localhost:30881
   ```

   → Responded `404` → NodePort exposed correctly to host.

### Conclusion
- Kubernetes and Helm setup were correct; the service was reachable.
- The `404` response simply shows that `/` is not a valid endpoint — network connectivity is working as expected.
