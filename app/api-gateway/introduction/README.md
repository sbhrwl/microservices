# Introduction
- [Overview](#overview)
- [Layers](#layers)
## Overview
- **GraphQL API Gateway** (Backend for Frontend) built with `Apollo Server` and `Fastify`
- It serves as an entry point for microservices communication using **Dapr** (Distributed Application Runtime)
- It Supports both **gRPC and HTTP protocols** for service-to-service communication via `Dapr sidecars`
## Layers
- Authentication layer: **OAuth2/JWT authentication** via Keycloak for secure access
- Security layer: **GraphQL Armor plugins** (cost limits, depth limits, token limits, etc.)
- Distributed tracing layer: with **Zipkin** for observability

```mermaid
graph TD
    A["**Client / Frontend**"] --> B["**GraphQL API Gateway** Apollo Server + Fastify"]
    subgraph L1 ["**Authentication layer**"]
        B
    end
    subgraph L2 ["**Security layer**"]
        L1
    end
    subgraph L3 ["**Distributed tracing layer**"]
        L2
    end
    L3 --> F["**Microservices via Dapr**"]
    L3 --> I["**Hybrid Endpoints** REST e.g., CSV import"]
    F --> G["**gRPC Services** Protocol Buffers"]
    F --> H["**HTTP Services**"]
```
## 
- Uses **Protocol Buffers** for gRPC service definitions with automated code generation
- Provides **GraphQL schema code generation** for TypeScript type safety
- Designed for **cloud-native deployment** with Kubernetes health probes and Dapr sidecar integration
- Supports **hybrid architecture** with both GraphQL queries and REST endpoints (e.g., CSV import)

## Uses Protocol Buffers for gRPC service definitions with automated code generation
* **From the gateway view:**
  * The API Gateway **talks to backend microservices via gRPC**.
  * Protocol Buffers define the **exact structure of requests and responses** the gateway can send/receive.
  * Automated code generation ensures the gateway has **type-safe client stubs** to call services without manual boilerplate.
* **Why it matters:**
  * Gateway can reliably **aggregate data from multiple services**.
  * Reduces errors when the gateway orchestrates multiple microservice calls.
## Provides GraphQL schema code generation for TypeScript type safety
* **From the gateway view:**
  * The gateway exposes a **GraphQL API to clients**.
  * Code generation converts the GraphQL schema into **TypeScript types** used in the gateway code.
  * Ensures that resolvers return the **correct data types**, matching the schema.
* **Why it matters:**
  * **Prevents runtime errors** in responses to clients.
  * Makes maintaining and updating GraphQL schemas safer.
## Designed for cloud-native deployment with Kubernetes health probes and Dapr sidecar integration
* **From the gateway view:**
  * **Health probes:** Kubernetes can check if the gateway is alive and ready.
  * **Dapr sidecar:** Gateway can call microservices via Dapr (gRPC or HTTP) without worrying about service discovery, retries, or protocol details.
* **Why it matters:**
  * Gateway stays **resilient and observable** in a cloud-native environment.
  * Simplifies deployment and scaling of the gateway itself.
## Supports hybrid architecture with both GraphQL queries and REST endpoints (e.g., CSV import)
* **From the gateway view:**
  * Gateway can **serve GraphQL queries to clients** for standard data access.
  * It can also **expose REST endpoints** for specific tasks (like bulk CSV import).
  * Internally, it can route these requests to services using the same Dapr layer.
* **Why it matters:**
  * Increases **flexibility** for different client needs.
  * Makes the gateway a **single entry point** for multiple interaction patterns.
