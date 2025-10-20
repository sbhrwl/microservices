# Ingress
- [External services](#external-services)
  - [Ingress controller for external routing](#ingress-controller-for-external-routing)
- [Ingress setup](#ingress-setup)
## External services
* **UI App** → Web frontend
* **Data API** → REST API for external clients
* **Flexibility Hub Simulator** → REST entry point
### Ingress controller for external routing
* **Single public endpoint** (e.g., `flex-hub-connector.example.com`)
* **Routing paths:**
  * `/api` → `data-api`
  * `/ui` → `ui-app`
  * `/simulator` → `flexibility-hub-simulator`
* Purpose: clean external access without exposing multiple NodePorts
## Ingress setup
- Verify: `kubectl get pods -n ingress-nginx`
- Install
```
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm install ingress-nginx ingress-nginx/ingress-nginx \
  --namespace ingress-nginx --create-namespace
```
