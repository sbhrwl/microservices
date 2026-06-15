# Introduction
- [Purpose](#purpose)
- [GraphQL and gRPC dual API strategy](#graphql-and-grpc-dual-api-strategy)
- [Domain organization](#domain-organization)
## Purpose
- The **GFC APIs** project is the central API layer for a Grid Field Control (GFC) system in the energy and utilities domain.
- It manages API schemas and bridges backend microservices with client applications, enabling consistent and efficient communication across a distributed system.
- Provide a unified `API gateway` that `abstracts underlying microservices`
- Support `multiple protocols` to address diverse client and integration needs
- Maintain and version API schemas as stable contracts
- Ensure standards compliance, especially IEC 61968
- Organize APIs around clear business domains
## GraphQL and gRPC dual API strategy
- **GraphQL**
  - Client-driven queries
  - Reduced over-fetching and under-fetching
  - Single-request aggregation
  - Schema introspection and discoverability
- **gRPC**
  - High-performance binary protocol
  - Strong typing with Protocol Buffers
  - Streaming support
  - Optimized for service-to-service communication

| Use case                            | Protocol |
| ----------------------------------- | -------------------- |
| Web and mobile user interfaces      | GraphQL              |
| Dashboards and analytics            | GraphQL              |
| Device registration and ingestion   | gRPC                 |
| Bulk and streaming data operations  | gRPC                 |
| Third-party integrations            | GraphQL or REST      |
| Internal microservice communication | gRPC                 |

## Domain organization
- **`core/`**
  - **`api/`**
    - device
    - event
    - organization
    - authorization
    - tag
  - **`type/`**
    - shared domain types
    - geospatial and metering data
    - pagination and search utilities
- **`graphql/`**
  - Client-facing queries and mutations
  - Frontend-optimized type definitions
- **`iec61968_connector/`**
  - IEC 61968 compliant integration components
