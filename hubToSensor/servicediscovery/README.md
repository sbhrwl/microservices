# Service discovery
- [Internal services](#internal-services)
- [Service discovery](#service-discovery) 
## Internal services
* **Storage Service** → gRPC server on `9090`
* **Flexibility Bridge** → gRPC client → communicates with Storage Service
* **Protocol Adapter** → gRPC client → communicates with Storage Service
## Service discovery
- Service discovery is only needed for **Storage Service** so that gRPC clients (Flexibility Bridge + Protocol Adapter) can **`locate it dynamically`**
* Service discovery helps by avoiding **hardcoding** `IPs` or `ports`
* For `gRPC resolution`, storage service can use
  * **Kubernetes DNS / ClusterIP** or a
  * **Lightweight service registry**
* `Health-aware`: `only healthy storage service instances` are returned to clients
### Kubernetes
- Kubernetes provides built-in service discovery out of the box.
- You just need to:
  - Define a Service resource for your gRPC server (like `storage-service`).
  - Reference it by name + port (`storage-service:9090`) from any client in the **`same cluster`**.
  - No extra setup is needed.