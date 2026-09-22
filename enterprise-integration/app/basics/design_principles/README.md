# Gradle build
- [Build summary](#build-summary)
- [Observations](#observations)
- [Design principal](#design-principal)
- [Dependencies](#dependencies)
- [Dependency graph with intent](#dependency-graph-with-intent)
- [Shared libraries](#shared-libraries)
## Build summary
| Module | Type | Depends On | Primary Purpose | Code Generation | Deployable |
| --- | --- | --- | --- | --- | --- |
| `data-hub-api` | Shared library | None | SOAP contract library (WSDL/JAXB classes) | CXF wsdl2java from WSDL | ❌ |
| `data-hub-simulator` | Application | `data-hub-api` | Simulates the external DataHub (SOAP server/client for testing) | None | ✅ |
| `flex-hub-connector` (FHC) | Application | `gfc-apis`, `data-hub-api` | Bridges DataHub SOAP ↔ Internal gRPC. Flex Hub Connector acts as a gateway | Dagger annotation processing | ✅ |
| `gfc-apis` | Shared library | None | Shared Protobuf/gRPC contracts | Protobuf + gRPC plugin | ❌ |
| `gfc-core` | Application | `gfc-apis` | Core business service, persistence, orchestration | Dagger annotation processing | ✅ |
| `iec61968-connector` (IEC) | Application | `gfc-apis` | Bridges Core gRPC ↔ IEC/JMS | JAXB XJC + Dagger | ✅ |
## Observations
| Concern | Used By |
| --- | --- |
| SOAP / CXF | `data-hub-api`, `data-hub-simulator`, `flex-hub-connector` |
| gRPC | `gfc-apis`, `gfc-core`, `flex-hub-connector`, `iec61968-connector` |
| Apache Camel | All applications except `gfc-apis` and `data-hub-api` |
| Dagger DI | `gfc-core`, `flex-hub-connector`, `iec61968-connector` |
| JAXB/XSD | `data-hub-api`, `iec61968-connector` |
| Version Catalog (`libs.versions.toml`) | Shared by all modules |
## Design principal
- No application depends directly on another application's code.
- They depend only on shared contracts.
- `flex-hub-connector` does not depend on `gfc-core`.
  - Instead it depends on `gfc-apis`, which defines the gRPC interface.
- `data-hub-simulator` does not depend on `flex-hub-connector`.
  - Both simply share the SOAP contract from `data-hub-api`.
- This keeps services independently deployable and avoids tight coupling.
## Dependencies
| Dependency | Why it exists | Architecture role |
| --- | --- | --- |
| `flex-hub-connector` → `data-hub-api` | Needs generated SOAP client/server classes and JAXB models. | Contract reuse |
| `data-hub-simulator` → `data-hub-api` | Simulates the real DataHub using the exact same SOAP contract. | Testability |
| `gfc-core` → `gfc-apis` | Exposes and consumes internal gRPC services. | Internal service contract |
| `flex-hub-connector` → `gfc-apis` | Sends requests to Core via gRPC. | Client contract |
| `iec61968-connector` → `gfc-apis` | Exchanges gRPC messages with Core. | Client contract |
## Dependency graph with intent
```text
                 SOAP Contract
                 data-hub-api
                  ▲          ▲
                  │          │
                  │          │
      DataHub Simulator    Flex Hub Connector
                               │
                               │ gRPC Contract
                               ▼
                          gfc-apis
                          ▲      ▲
                          │      │
                          │      │
                     GFC Core   IEC Connector
```
## Shared libraries
- The project is organized around contracts, not implementations.
- Applications communicate through:
  - SOAP contracts externally (`data-hub-api`)
  - gRPC contracts internally (`gfc-apis`)
| Library | Shares |
| --- | --- |
| `data-hub-api` | External contract (SOAP/WSDL) |
| `gfc-apis` | Internal contract (gRPC/Protobuf) |
