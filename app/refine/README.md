- [**Introduction**](Introduction.md)
  - Motivation, Design principles (Layered architecture, Dependency injection, Multi-tenancy, Security first, Observability), Technology stack
- [**Startup Sequence**](Runtime.md)
   - Bootstrap and configuration, Dependency injection wiring, Server startup, Shutdown sequence
- [**Architecture & Components**](Architecture.md)
  - Component layers, gRPC server and Dagger modules, Persistence and MongoDB integration, Dapr integration, Configuration model, Protos
- [**gRPC APIs**](gRPC-APIs.md)
  - Query devices flow, Tag devices flow, Get app permissions flow 
- [**Data Model & Persistence**](DataModel.md)
  - Domain entities, Query model, SearchFilter interface, PageRequest, Sorting, Aggregation pipeline builder, Data flow, Document mappers

---   
   - **Domain entities**:
     - Devices (`domain.device.*`).
     - Events (`domain.event.*`).
     - Organizations and settings (`domain.organization.*`).
     - Tags and groups (`domain.common.*`, tag model).
   - **Query model**:
     - `domain.query` filters, `PageRequest`, `Sorting`.
     - `AggregationPipelineBuilder` — how query filters translate to MongoDB aggregation.
     - Mermaid flowchart: From incoming gRPC request → query objects → aggregation pipeline → MongoDB → mapped domain objects → gRPC responses.
   - High-level mapping responsibilities (`*DocumentMapper` classes).
6. [**Security & Authorization**](SecurityAuthorization.md)
   - **Authentication**:
     - Keycloak integration (`ApplicationSetting.Keycloak`, `AccessTokenVerifier`, `IssuedForCheck`, `IssuersCheck`, `KeyProvider`, `SecurityUtils`).
     - How tokens are validated and what claims are used.
   - **Authorization**:
     - `Rbac`, `AuthorizationDao`, `AuthorizationQueryService`, `AuthorizationServiceImpl`.
     - Method-level role enforcement (likely via interceptors like `AuthorizationInterceptor`).
     - Table: example RPC → required roles/permissions (if derivable from config).
   - **Sequence diagram**:
     - Client with JWT → gRPC call → interceptors → token verification → RBAC check → service logic.
7. [**Configuration & Environments**](ConfigurationEnvironments.md)
   - **Configuration sources**:
     - `application.conf` (and dist `etc/application.conf`).
     - JVM/system properties (`-Dconfig.file=...` as in `Server`).
   - **ApplicationSetting structure**:
     - `gfc.grpcServer`, `gfc.mongodb`, `gfc.keycloak`, `gfc.authorization`, feature flags, search strategy.
   - **MongoDB connection** (URI, options, heartbeat).
   - **Dapr and external endpoints** (Dapr ports, sidecar expectations).
   - **Docker & deployment**:
     - `Dockerfile`, `Dockerfile-dev`, `envfile.env`.
     - Simple deployment flowchart: CI → image → environment config → runtime.
8. [**Observability, Health & Readiness**](Observability.md)
   - **Health mechanisms**:
     - `ReadinessHealthIndicator`, `DaprHealthStatusManager`, gRPC health service.
     - How MongoDB health is monitored (server monitor listener).
   - **Logs**:
     - `logback.xml`, `logging.properties`, SLF4J + logback, logging bridge via `SLF4JBridgeHandler`.
   - **Sequence diagram**:
     - Liveness/readiness probe → health endpoints → health indicators → dependencies (Mongo, Dapr).
9. [**Error Handling & Limits**](ErrorHandling.md)
   - Core exception types (`exceptions.*`): invalid request, access denied, too many requests, request too large, etc.
   - How interceptors (`ExceptionHandlerInterceptor`, `LatencyLimiterInterceptor`, `LoggingInterceptor`) wrap and translate errors.
   - Timeouts, max inbound message size, latency limiter behavior.
10. [**Performance & Scalability Considerations**](PerformanceScalability.md)
    - Threading model and executors:
      - Netty worker/boss executors, Dapr client executor.
    - Search strategies for Mongo (from `ApplicationSetting.SearchStrategy`).
    - Any caching / performance features (e.g. `MovingAverageLatencyTracker`).
    - Recommended deployment topologies (horizontal scaling, DB considerations).
11. [**Developer Guide**](DeveloperGuide.md)
    - **Running locally**:
      - `mvn clean compile`, config hints, dependencies like MongoDB/Dapr/Keycloak.
    - **Adding a new gRPC service**:
      - Protobuf, `*ServiceImpl`, Dagger wiring (`GrpcServerComponent`, modules), DAOs, domain objects.
    - **Extending queries**:
      - Adding new filters, updating `AggregationPipelineBuilder` and filter visitors.
