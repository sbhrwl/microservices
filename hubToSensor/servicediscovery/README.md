# Service discovery
- [Internal services](#internal-services)
  - [Service discovery](#service-discovery) 
## Internal services
* **Storage Service** → gRPC server on `9090`
* **Flexibility Bridge** → gRPC client → communicates with Storage Service
* **Protocol Adapter** → gRPC client → communicates with Storage Service
### Service discovery
* Only needed for **Storage Service** so that gRPC clients (Flexibility Bridge + Protocol Adapter) can **`locate it dynamically`**
* Avoids **hardcoding** `IPs` or `ports`
* For `gRPC resolution`, storage service can use
  * **Kubernetes DNS / ClusterIP** or a
  * **Lightweight service registry**
* `Health-aware`: `only healthy storage service instances` are returned to clients