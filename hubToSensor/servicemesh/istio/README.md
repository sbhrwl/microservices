# Istio
- [Introduction](#introduction)
- [Concepts](#concepts)
## Introduction 
- A service mesh is a dedicated infrastructure layer for handling service-to-service communication within a microservices architecture. 
- Istio provides the necessary features to secure, connect, and observe these services. 
## Concepts
* **Sidecar Proxy (Envoy)**
  * Deployed alongside each pod in the mesh.
  * Handles **internal service-to-service traffic**.
  * Provides **mTLS, retries, circuit breaking, observability**.
* **Istio Ingress Gateway**
  * Handles **external traffic entering the mesh**.
  * Performs **TLS termination, routing, and traffic policies**.
  * Replaces or complements existing Kubernetes Ingress.
* **VirtualService**
  * Defines **routing rules** within the mesh.
  * Controls **traffic splits, routing based on headers/paths, weighted deployments**.
* **DestinationRule**
  * Defines **policies for connections to services**, like retries, load balancing, and circuit breaking.
* **mTLS (Mutual TLS)**
  * Encrypts and authenticates **traffic between services**.
  * Supports **zero-trust security**.
* **Pilot**
  * Distributes **configuration to sidecars**.
  * Handles **service discovery** inside the mesh.
* **What Istio does NOT do**
  * It does **not manage databases** (sharding, replication).
  * It does **not replace application logic**; it manages communication.
* **Industrial best practice**
  * Deploy **sidecars automatically or manually** for internal services.
  * Route all external traffic via **Istio Ingress Gateway**.
  * Use **VirtualService and DestinationRule** for traffic policies.
  * Observability via **Prometheus, Grafana, tracing**.
