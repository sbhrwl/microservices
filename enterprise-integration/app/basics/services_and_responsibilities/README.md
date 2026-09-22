# Services and responsibilities
- [data-hub-api](#data-hub-api)
- [data-hub-simulator](#data-hub-simulator)
- [gfc-apis](#gfc-apis)
- [flex-hub-connector](#flex-hub-connector)
- [gfc-core](#gfc-core)
- [iec61968-connector](#iec61968-connector)
## data-hub-api
```text
WSDL
   │
Bindings (.xjb)
   │
CXF wsdl2java
   │
Generated SOAP classes
   │
Published as JAR
```
- Responsibility
  - Owns the external DataHub contract
  - Generates JAXB models
  - Generates SOAP interfaces
  - No business logic
  - No runtime
## data-hub-simulator
```text
SOAP
   │
CXF
   │
Simulation Logic
```
- Responsibility
  - Local development
  - Integration testing
  - Contract validation
## gfc-apis
```text
.proto
   │
Protobuf Plugin
   │
Generated Java Models
   │
Generated gRPC Stubs
   │
Published as JAR
```
- Responsibility
  - Internal API definition
  - Shared by all gRPC services
  - No business logic
  - No runtime
## flex-hub-connector
```text
Inbound SOAP
   │
Camel
   │
Use Cases
   │
SOAP Mapper
   │
gRPC Client
   │
Core
```
| Layer | Purpose |
| --- | --- |
| Adapters | SOAP & gRPC |
| Use Cases | Orchestrate workflows |
| Services | Lookup/business helpers |
| Domain | Internal models |
| Infrastructure | Bootstrap/configuration |
- Its job is protocol translation, not business decisions.
## gfc-core
```text
gRPC
   │
Application Services
   │
Domain
   │
Persistence
   │
Database
```
| Layer | Purpose |
| --- | --- |
| Inbound adapters | gRPC APIs |
| Application services | Business rules |
| Domain | Core business model |
| Outbound adapters | DB + gRPC clients |
| Infrastructure | Bootstrap |
- This is the heart of the platform. Almost every business decision belongs here.
## iec61968-connector
```text
gRPC
   │
Command Processor
   │
Camel
   │
JMS
   │
IEC/HES
```
| Layer | Purpose |
| --- | --- |
| Inbound | gRPC + JMS |
| Application services | Command processing |
| Outbound | JMS + gRPC |
| Domain | IEC models |
| Infrastructure | Bootstrap |
- This is an integration service, not a business service.
