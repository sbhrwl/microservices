# Ingress
- [External services](#external-services)
- [Ingress controller for external routing](#ingress-controller-for-external-routing)
- [Ingress setup](#ingress-setup)
  - [Get repo](#get-repo)
  - [Install](#install)
  - [Verify](#verify)
- [Changes to existing helm charts](#changes-to-existing-helm-charts)
- [Install Helm release](#install-helm-release)
- [Verify release](#verify-release)
- [Access services](#access-services)
  - [Map DNS to localhost on windows machine to test Ingress routing](#map-dns-to-localhost-on-windows-machine-to-test-ingress-routing)
- [Uninstall Helm release](#uninstall-helm-release)
- [Upgrade Helm release](#upgrades-helm-release)
## External services
* **UI App** → Web frontend
* **Data API** → REST API for external clients
* **Flexibility Hub Simulator** → REST entry point
## Ingress controller for external routing
* **Single public endpoint** (e.g., `flex-hub-connector.example.com`)
* **Routing paths:**
  * `/api` → `data-api`
  * `/ui` → `ui-app`
  * `/simulator` → `flexibility-hub-simulator`
* Purpose: clean external access without exposing multiple NodePorts
## Ingress setup
- Verify: `kubectl get pods -n ingress-nginx`
### Get repo
```
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
```
### Install
- This command installs a cluster-wide NGINX Ingress Controller in an `isolated namespace`, exposes it via `NodePort` for local access, and enables routing traffic to multiple applications across namespaces through their respective Ingress definitions.
```bash
helm install ingress-nginx ingress-nginx/ingress-nginx --namespace ingress-nginx --create-namespace --set controller.service.type=NodePort --set controller.progressDeadlineSeconds=600
```

* **`helm install`** – installs a Helm chart (a packaged Kubernetes application).
* **`ingress-nginx` (first)** – release name; identifies this specific installation instance.
* **`ingress-nginx/ingress-nginx` (second)** – chart reference from the Helm repository (`ingress-nginx` repo).
* **`--namespace ingress-nginx`** – deploys all ingress controller resources into a dedicated namespace called `ingress-nginx`.
* **`--create-namespace`** – creates the namespace automatically if it doesn’t exist.
* **`--set controller.service.type=NodePort`** – exposes the ingress controller via NodePort, which is suitable for **local setups** (like Docker Desktop or Minikube) where **`LoadBalancer`** is not available.
* **`--set controller.progressDeadlineSeconds=600`** → explicitly sets a valid timeout (10 minutes).
  * Prevents the Deployment validation error (must be greater than minReadySeconds).
* Why use a separate namespace?
  * Keeps ingress controller isolated from application workloads.
  * Simplifies upgrades, troubleshooting, and access control.
  * Allows you to manage system components (like ingress, monitoring, or logging) independently of app namespaces.
* Usage for other apps
  * The NGINX Ingress Controller is **cluster-wide** — it watches all namespaces for `Ingress` resources with
 ```yaml
 ingressClassName: nginx
 ```
* **Multiple applications across different namespaces** can `share` this **single ingress controller** while defining their own routing rules and services independently.
### Verify
- `helm list -A | grep ingress-nginx`
```
C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>helm list -A | grep ingress-nginx
ingress-nginx   ingress-nginx   1               2025-10-21 19:57:47.7135786 +0300 EEST  deployed        ingress-nginx-4.13.3    1.13.3
```
- Ingress `pod`
```
C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>kubectl get pods -n ingress-nginx
NAME                                        READY   STATUS              RESTARTS   AGE
ingress-nginx-controller-7d8cffd99c-rqz6d   0/1     ContainerCreating   0          2m2s
```
## Changes to existing helm charts
- Copy existing helm charts to [ingress folder](orchestrate-hubtosensor-services)
  - Lets not update the existing one
- Modify [values.yaml](orchestrate-hubtosensor-services/values.yaml) and add section for `ingress`
```
ingress:
  enabled: true
  host: fhs.local
  paths:
    uiApp: /ui
    dataApi: /api
    flexibilityHubSimulator: /simulator
  tls: false   # set true later when you configure TLS/Keycloak
```
- Create [ingress.yaml](orchestrate-hubtosensor-services/template/ingress.yaml)
- Update [values.yaml](orchestrate-hubtosensor-services/values.yaml) ports to ClusterIP and remove nodePort for these services so **`Ingress` can route them `internally`**.
  - Remove rows with `  nodePort: ` for `dataApi`, `uiApp` and `flexibilityHubSimulator`
## Install Helm release
- Go to Helm chart folder (e.g., [orchestrate-hubtosensor-services](orchestrate-hubtosensor-services), run this command:
```bash
helm install ocs-staging . -f values-staging.yaml -n staging --set ingress.enabled=true --set ingress.host=fhs.local
```

## Verify release
```
C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>helm install ocs-staging . -f values-staging.yaml -n staging --set ingress.enabled=true --set ingress.host=fhs.local
NAME: ocs-staging
LAST DEPLOYED: Tue Oct 21 20:30:56 2025
NAMESPACE: staging
STATUS: deployed
REVISION: 1
TEST SUITE: None

C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>helm list -A
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                                   APP VERSION
ingress-nginx   ingress-nginx   1               2025-10-21 19:57:47.7135786 +0300 EEST  deployed        ingress-nginx-4.13.3                    1.13.3
ocs-staging     staging         1               2025-10-21 20:30:56.6490242 +0300 EEST  deployed        orchestrate-hubtosensor-services-0.1.0  1.16.0

C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>helm list -n staging
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                                   APP VERSION
ocs-staging     staging         1               2025-10-21 20:30:56.6490242 +0300 EEST  deployed        orchestrate-hubtosensor-services-0.1.0  1.16.0

C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>kubectl get all -n staging
NAME                                                       READY   STATUS    RESTARTS     AGE
pod/data-api-5786ddb557-xndhn                              1/1     Running   0            52s
pod/flexibility-bridge-deployment-bdcf89c84-ddbsh          1/1     Running   1 (4s ago)   52s
pod/flexibility-hub-simulator-deployment-b56c88d6c-5p9kf   1/1     Running   0            52s
pod/hes-simulator-deployment-6d5c5849d4-jrjtq              1/1     Running   0            52s
pod/protocol-adapter-deployment-764d97944c-xrgwd           1/1     Running   1 (6s ago)   52s
pod/storage-service-deployment-556c68b9b4-dm2j2            1/1     Running   1 (4s ago)   52s
pod/ui-app-8bf7fb47c-c7qqw                                 1/1     Running   0            52s

NAME                                        TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
service/data-api-service                    NodePort    10.101.251.234   <none>        8085:30885/TCP   54s
service/flexibility-hub-simulator-service   NodePort    10.109.43.97     <none>        8081:30881/TCP   54s
service/storage-service-service             ClusterIP   10.106.208.122   <none>        9090/TCP         54s
service/ui-app-service                      NodePort    10.100.249.111   <none>        8080:30880/TCP   54s

NAME                                                   READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/data-api                               1/1     1            1           53s
deployment.apps/flexibility-bridge-deployment          1/1     1            1           53s
deployment.apps/flexibility-hub-simulator-deployment   1/1     1            1           53s
deployment.apps/hes-simulator-deployment               1/1     1            1           53s
deployment.apps/protocol-adapter-deployment            1/1     1            1           53s
deployment.apps/storage-service-deployment             1/1     1            1           53s
deployment.apps/ui-app                                 1/1     1            1           53s

NAME                                                             DESIRED   CURRENT   READY   AGE
replicaset.apps/data-api-5786ddb557                              1         1         1       53s
replicaset.apps/flexibility-bridge-deployment-bdcf89c84          1         1         1       53s
replicaset.apps/flexibility-hub-simulator-deployment-b56c88d6c   1         1         1       53s
replicaset.apps/hes-simulator-deployment-6d5c5849d4              1         1         1       53s
replicaset.apps/protocol-adapter-deployment-764d97944c           1         1         1       53s
replicaset.apps/storage-service-deployment-556c68b9b4            1         1         1       53s
replicaset.apps/ui-app-8bf7fb47c                                 1         1         1       53s

NAME                                                                REFERENCE                                         TARGETS                                     MINPODS   MAXPODS   REPLICAS   AGE
horizontalpodautoscaler.autoscaling/data-api-hpa                    Deployment/data-api-deployment                    cpu: <unknown>/50%, memory: <unknown>/60%   1         2         0          53s
horizontalpodautoscaler.autoscaling/flexibility-hub-simulator-hpa   Deployment/flexibility-hub-simulator-deployment   cpu: <unknown>/50%, memory: <unknown>/60%   1         2         0          53s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
### Map DNS to localhost on windows machine to test Ingress routing
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `flexibility-hub-simulator` → `http://localhost:30881/api/messages`
  * `data-api-service` → `http://localhost:30885/api/v1/requests/<requestID>/tracker`
  * `ui-app` → `http://localhost:30880/`
## Uninstall Helm release
```
helm uninstall ocs-dev -n dev
helm uninstall ocs-staging -n staging
helm uninstall ocs-prod -n prod
```
## Upgrade Helm release
