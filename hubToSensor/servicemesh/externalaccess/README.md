# Istio gateway configuration
* [Problem](#problem)
* [Motivation](#motivation)
* [Evolution](#evolution)
* [Solution](#solution)
* [Implementation](#implementation)
* [Takeaway](#takeaway)
## Problem
* Multiple Kubernetes services were exposed externally via distinct NodePorts.
* Each service had its own external port, creating fragmented ingress management.
* Example:
  * `ui-app-service` → NodePort 30880
  * `data-api-service` → NodePort 30885
  * `flexibility-hub-simulator-service` → NodePort 30881
## Motivation
* Simplify ingress configuration by consolidating access to all externally exposed services.
* Replace multiple NodePort entries with a unified Istio ingress point.
* Enable path-based routing through the Istio Gateway for better scalability and control.
## Evolution
* Transition from direct NodePort access to a service mesh-managed ingress.
* Introduced Istio Gateway and VirtualService resources in the `staging` namespace.
* Enabled centralized control of HTTP traffic with a single external IP and port.
## Solution
* **Istio Gateway (`staging-ingress-gateway`)**
  * Configures Istio ingress controller to listen on port 80.
  * Accepts HTTP traffic from any hostname (`*`).
  * Acts as the unified entry point for external requests.
### Istio ingress gateway setup
* **Terminal/WSL Command (Install the Gateway Helm Chart):** 
  * We will install the official Istio Gateway chart into the `istio-system` namespace.
  ```bash
  helm install istio-ingressgateway istio/gateway -n istio-system --wait
  ```
* *Note: We are naming the release `istio-ingressgateway` to match the selector `istio: ingressgateway` used in your `Gateway` YAML.*
* Verify the Gateway Service
  * **Terminal/WSL Command (Check for the new Service):** 
  * Run this command to confirm the `istio-ingressgateway` service is now running and check its exposure type and ports.
    ```bash
    kubectl get svc -n istio-system
    ```
### Istio VirtualService setup
* **Istio VirtualService (`staging-external-routes`)**
  * Binds routing rules to the above Gateway.
  * Implements URI path–based routing:
    * `/ui-app/` → `ui-app-service:8080`
    * `/data-api/` → `data-api-service:8085`
    * `/flex-sim/` → `flexibility-hub-simulator-service:8081`
## Implementation
* **YAML configuration:** [`istio-config/app-ingress.yaml`](istio-config/app-ingress.yaml)
* Go to `\servicemesh\externalaccess`
* **Apply command:**
  ```bash
  kubectl apply -f istio-config/app-ingress.yaml
  ```
* **Validate ingress port:**
  ```bash
  kubectl get svc istio-ingressgateway -n istio-system -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}'
  ```
* **Access after Istio configuration:**

  | Service                   | Before Istio                                          | After Istio                                                             |
  | ------------------------- | ----------------------------------------------------- | ----------------------------------------------------------------------- |
  | flexibility-hub-simulator | `http://localhost:30881/api/messages`                 | `http://localhost:<INGRESS_PORT>/flex-sim/api/messages`                 |
  | data-api-service          | `http://localhost:30885/api/v1/requests/<ID>/tracker` | `http://localhost:<INGRESS_PORT>/data-api/api/v1/requests/<ID>/tracker` |
  | ui-app                    | `http://localhost:30880/`                             | `http://localhost:<INGRESS_PORT>/ui-app/`                               |

* **Key change:** single ingress IP/port replaces all NodePorts.
* Requests are distinguished using the URI prefix.

## Takeaway
* Consolidates external traffic routing under one Gateway.
* Reduces port management complexity and improves observability.
* Enables future scalability with Istio traffic policies and security features.
* All external access now flows through Istio’s managed ingress path structure.
