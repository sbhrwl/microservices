1. [**Introduction**](Introduction.md)
   - **Purpose**: What `gfc-service` does and where it fits.
   - **High-level responsibilities** (devices, events, tags, organizations, authorization).
   - **Key technologies**: gRPC, MongoDB, Dapr, Keycloak, Dagger DI, Typesafe Config.
   - **Simple context diagram** (Mermaid flowchart): Clients ↔ gRPC ↔ gfc-service ↔ MongoDB / Dapr / Keycloak.
2. [**Startup Sequence**](Runtime.md)
   - **Narrative**: Flow from `Bootstrap.main` → `Server.run` → DI wiring → gRPC server start.
   - **Mermaid sequence diagram**:
     - `Client` (operator) → JVM → `Bootstrap` → `Server` → `ApplicationComponent` / Dagger → `GrpcServerComponent` → `io.grpc.Server`.
   - **Shutdown sequence**: `GracefulShutdownHook`, readiness indicator, gRPC stop.
3. [**Architecture & Components**](Architecture.md)
   - **Mermaid component diagram** (flowchart style):
     - `grpc` layer (`*ServiceImpl`)  
     - `services` layer (`*QueryService`, `*MutationService`, `OrganizationService`, etc.)  
     - `dao` layer (`DeviceDao`, `EventDao`, `TagDao`, `OrganizationDao`, `AuthorizationDao`)  
     - `domain` model (device/event/org/query filters).
   - **Subsections**:
     - gRPC server and Dagger modules (`GrpcModule`, `GrpcServerComponent`, `AppModule`).
     - Persistence and MongoDB integration (`ApplicationSetting.Mongodb`, `MongoClient` in `AppModule`, `AggregationPipelineBuilder`, DAOs).
     - Dapr integration (health, actors if used).
     - Configuration model (`ApplicationSetting`, `application.conf`).
4. [**External Interfaces (gRPC APIs)**](ExternalInterfaces.md)
   - Overview of main APIs:
     - **Device**: `DeviceServiceImpl` (query/mutation flows).
     - **Event**: `EventServiceImpl`.
     - **Organization**: `OrganizationServiceImpl`.
     - **Tag**: `TagServiceImpl`, `TagMutationService`, `TagQueryService`.
     - **Authorization**: `AuthorizationServiceImpl`.
     - **Health / Revision**: `GrpcHealthServiceImpl`, `DaprHealthServiceImpl`, `RevisionServiceImpl`.
   - For each major service:
     - **Short description**.
     - **Key RPCs** and their purpose (table: method, request, response, notes).
     - **Sequence diagram** for 1–2 important flows (e.g. “query devices”, “tag devices”, “get app permissions”).
5. [**Data Model & Persistence**](DataModel.md)
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
7. **Configuration & Environments**
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
8. **Observability, Health & Readiness**
   - **Health mechanisms**:
     - `ReadinessHealthIndicator`, `DaprHealthStatusManager`, gRPC health service.
     - How MongoDB health is monitored (server monitor listener).
   - **Logs**:
     - `logback.xml`, `logging.properties`, SLF4J + logback, logging bridge via `SLF4JBridgeHandler`.
   - **Sequence diagram**:
     - Liveness/readiness probe → health endpoints → health indicators → dependencies (Mongo, Dapr).
9. **Error Handling & Limits**
   - Core exception types (`exceptions.*`): invalid request, access denied, too many requests, request too large, etc.
   - How interceptors (`ExceptionHandlerInterceptor`, `LatencyLimiterInterceptor`, `LoggingInterceptor`) wrap and translate errors.
   - Timeouts, max inbound message size, latency limiter behavior.
10. **Performance & Scalability Considerations**
    - Threading model and executors:
      - Netty worker/boss executors, Dapr client executor.
    - Search strategies for Mongo (from `ApplicationSetting.SearchStrategy`).
    - Any caching / performance features (e.g. `MovingAverageLatencyTracker`).
    - Recommended deployment topologies (horizontal scaling, DB considerations).
11. **Developer Guide**
    - **Running locally**:
      - `mvn clean compile`, config hints, dependencies like MongoDB/Dapr/Keycloak.
    - **Adding a new gRPC service**:
      - Protobuf, `*ServiceImpl`, Dagger wiring (`GrpcServerComponent`, modules), DAOs, domain objects.
    - **Extending queries**:
      - Adding new filters, updating `AggregationPipelineBuilder` and filter visitors.
### Mermaid usage summary
- **High-level architecture**: 1–2 flowcharts.
- **Startup/shutdown**: 1 sequence diagram.
- **Per-major-use-case** (e.g. device query, tag mutation, auth check): 1 sequence diagram each.
- **Data/query pipeline**: 1 flowchart.
- **Health/readiness**: 1 simple sequence or flowchart.
