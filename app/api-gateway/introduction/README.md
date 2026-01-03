# Introduction
- This is a **GraphQL API Gateway** (Backend for Frontend) built with `Apollo Server` and `Fastify`
- Serves as an entry point for microservices communication using **Dapr** (Distributed Application Runtime)
- Supports both **gRPC and HTTP protocols** for service-to-service communication via Dapr sidecars
- Integrates **OAuth2/JWT authentication** via Keycloak for secure access
- Implements **distributed tracing** with Zipkin for observability
- Includes **GraphQL security hardening** using GraphQL Armor plugins (cost limits, depth limits, token limits, etc.)
- Uses **Protocol Buffers** for gRPC service definitions with automated code generation
- Provides **GraphQL schema code generation** for TypeScript type safety
- Designed for **cloud-native deployment** with Kubernetes health probes and Dapr sidecar integration
- Supports **hybrid architecture** with both GraphQL queries and REST endpoints (e.g., CSV import)

```mermaid
graph TD
    A["**Client / Frontend**"] --> B["**GraphQL API Gateway** Apollo Server + Fastify"]

    subgraph L1 ["**Authentication Layer** OAuth2/JWT via Keycloak"]
        B
    end

    subgraph L2 ["**Security Layer** GraphQL Armor Plugins"]
        L1
    end

    subgraph L3 ["**Distributed Tracing** Zipkin"]
        L2
    end

    L3 --> F["**Microservices via Dapr**"]
    L3 --> I["**Hybrid Endpoints** REST e.g., CSV import"]

    F --> G["**gRPC Services** Protocol Buffers"]
    F --> H["**HTTP Services**"]
```