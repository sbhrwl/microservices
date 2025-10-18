# Service discovery
- [Internal services](#internal-services)
  - [Service discovery](#service-discovery) 
- [External services](#external-services)
  - [Ingress controller for external routing](#ingress-controller-for-external-routing)
## Internal services
* **Storage Service** → gRPC server on `9090`
* **Flexibility Bridge** → gRPC client → communicates with Storage Service
* **Protocol Adapter** → gRPC client → communicates with Storage Service
* **HES Simulator** → communicates only with RabbitMQ (external)
* **Other message-driven services** → decoupled via RabbitMQ, no direct discovery needed
### Service discovery
* Only needed for **Storage Service** so that gRPC clients (Flexibility Bridge + Protocol Adapter) can locate it dynamically
* Avoids hardcoding IPs or ports
* Can use **Kubernetes DNS / ClusterIP** or a lightweight service registry for gRPC resolution
* Health-aware: only healthy Storage Service instances are returned to clients
## External services
* **UI App** → web frontend
* **Data API** → REST API for external clients
* **Flexibility Hub Simulator** → REST entry point
### Ingress controller for external routing
* **Single public endpoint** (e.g., `flex-hub-connector.example.com`)
* **Routing paths:**
  * `/api` → `data-api`
  * `/ui` → `ui-app`
  * `/simulator` → `flexibility-hub-simulator`
* Purpose: clean external access without exposing multiple NodePorts
