# Ingress setup
- [Ingress setup](#ingress-setup)
  - [Get repo](#get-repo)
  - [Install](#install)
  - [Verify](#verify)
- [Map DNS to localhost on windows machine to test Ingress routing](#map-dns-to-localhost-on-windows-machine-to-test-ingress-routing)
  - [Test ingress routing](#test-ingress-routing)
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
## Map DNS to localhost on windows machine to test Ingress routing
- Map `fhs.local` to localhost on your Windows machine so Ingress routes correctly.
1. **Open hosts file as Administrator**
```text
C:\Windows\System32\drivers\etc\hosts
```
* I already have an entry for `kubernetes.docker.internal`
```text
# To allow the same kube context to work on the host and the container:
127.0.0.1 kubernetes.docker.internal
```
2. **Add the mapping at the end of the file**
```text
# Map fhs.local to localhost so Ingress routes can be tested on the local machine
127.0.0.1 fhs.local
```

3. **Save the file** (you need admin rights).
### Test ingress routing
* Find the NodePort of the ingress controller
```bash
kubectl get svc -n ingress-nginx
```
* If ingress pod is not running or crashed, restart the deployment
```
kubectl rollout restart deployment ingress-nginx-controller -n ingress-nginx
```
* Suppose `ingress-nginx-controller` shows `NodePort: 30080` for HTTP (or your NodePort for dev).
  * Nodeport
    * When `NodePort matters`
      * **For direct host access**
       *If you want to test your Ingress from your **Windows host** without setting up a LoadBalancer, the host connects to `127.0.0.1:<NodePort>`.
      * Example:
        ```bash
        curl http://127.0.0.1:30171/ui
        ```
      * This bypasses DNS (`fhs.local`) and verifies the ingress controller is serving traffic.
    * When `NodePort doesn’t matter`
      * Once you map `fhs.local → 127.0.0.1` in `hosts` file, you can just do:
        ```bash
        curl http://fhs.local/ui
        ```
      * The Ingress controller automatically listens on the NodePort internally, so you don’t need to type it manually.
  * NodePort is useful for debugging or local access without a DNS entry. With `hosts` mapping, you can just use your domain (`fhs.local`) directly.
* Test access in browser or curl:
```bash
curl http://fhs.local/ui      # should reach UI
curl http://fhs.local/api     # should reach Data API
curl -X POST http://fhs.local/simulator -H "Content-Type: application/json" -d '{"test":"ok"}'
```
