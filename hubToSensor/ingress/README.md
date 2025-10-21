# Ingress
- [External services](#external-services)
- [Ingress controller for external routing](#ingress-controller-for-external-routing)
- [Ingress setup](#ingress-setup)
  - [Get repo](#get-repo)
  - [Install](#install)
  - [Verify](#verify)
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
