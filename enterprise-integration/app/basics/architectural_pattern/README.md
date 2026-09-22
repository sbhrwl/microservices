# Architectural pattern
- [Pattern](#pattern)
- [Pipeline](#pipeline)
- [Framework responsibilities](#framework-responsibilities)
- [Dependency injection](#dependency-injection)
- [Architectural decisions and trade-offs](#architectural-decisions-and-trade-offs)
## Pattern
| Pattern | Where | Why |
| --- | --- | --- |
| Ports & Adapters (Hexagonal) | All deployable services | Isolates business logic from transport protocols. |
| Adapter | FHC, IEC | Converts one protocol into another (SOAP ↔ gRPC, gRPC ↔ JMS). |
| Contract-First | `data-hub-api`, `gfc-apis` | APIs are defined before implementation. |
| Dependency Injection | Core, FHC, IEC | Dagger wires components together. |
| Repository | Core | Database access abstraction (JDBI). |
| Gateway / Facade | FHC | Single entry point to the DataHub. |
| Message Translator | FHC, IEC | Maps between external and internal models. |
| Simulation/Test Double | DataHub Simulator | Mimics the real DataHub. |
## Pipeline
- CXF handles communication.
- Camel handles orchestration.
- Dagger handles construction.
- gRPC/SOAP/JMS handle transport.
- Core handles business.
- JDBI handles storage.
```text
External Contract
      │
      ▼
Code Generation
      │
      ▼
Dependency Injection
      │
      ▼
Camel Route
      │
      ▼
Transport (SOAP/gRPC/JMS)
      │
      ▼
Business Logic
      │
      ▼
Database
```
## Framework responsibilities
| Framework | Responsibility |
| --- | --- |
| Typesafe Config | Load configuration |
| Dagger | Create dependencies |
| Camel | Register routes |
| CXF | Publish SOAP endpoint |
| gRPC | Connect to Core |
## Dependency injection
- Core, FHC, and IEC all use Dagger.
- This keeps object creation separate from business logic.
```text
Bootstrap
Dagger Component
Application
Services
Repositories / Clients
```
## Architectural decisions and trade-offs
| Decision | Why they likely chose it | Benefit | Trade-off |
| --- | --- | --- | --- |
| Separate contract modules | Share APIs without sharing implementations | Loose coupling | More modules to maintain |
| SOAP externally, gRPC internally | Integrate with enterprise systems while keeping internal communication efficient | Compatibility + performance | Two technologies to maintain |
| Camel for integration | Centralize routing and orchestration | Flexible integration | Learning curve |
| Dagger instead of Spring | Lightweight DI with compile-time validation | Fast startup, smaller footprint | More manual wiring |
| Generated code | Contracts are the source of truth | Fewer manual DTOs | Build becomes more complex |
| Connectors around Core | Keep business logic centralized | Clean separation | Extra network hops |
