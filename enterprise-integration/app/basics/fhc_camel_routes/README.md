# FHC camel routes
- [Inbound routes](#inbound-routes)
  - [Responsibilities](#responsibilities)
  - [Execution flow](#execution-flow)
  - [Configuration](#configuration)
  - [High availability](#high-availability)
  - [Runtime behavior](#runtime-behavior)
  - [Layer interaction](#layer-interaction)
- [Outbound routes](#outbound-routes)
  - [Responsibilities](#responsibilities-1)
  - [Supported routes](#supported-routes)
  - [SOAP endpoint configuration](#soap-endpoint-configuration)
  - [Request processing](#request-processing)
  - [Execution flow](#execution-flow)
  - [Logging](#logging)
  - [Layer interaction](#layer-interaction-1)
  - [Design observations](#design-observations)
- [Difference between ScheduledCamelRoutes and direct:peek](#difference-between-scheduledcamelroutes-and-directpeek)
```text
                         INBOUND
                            │
                            ▼
                   ScheduledCamelRoutes
                            │
                            ▼
                       Camel Timer
                            │
                            ▼
                   PeekMessagesUseCase
                            │
                            │
              ┌─────────────┼─────────────┐
              │             │             │
              ▼             ▼             ▼
        direct:peek    direct:send   direct:dequeueMessage
              │             │             │
              └─────────────┼─────────────┘
                            │
                            ▼
                       direct:soap
                            │
                            ▼
                   Prepare SOAP Request
                            │
                            ├── SOAP Headers
                            ├── Request Context
                            └── organisationUser
                            │
                            ▼
                     Apache CXF Endpoint
                            │
                            │ SOAP / TLS
                            ▼
                         Flex-Hub
```
### Inbound routes
| Camel route | Purpose |
| --- | --- |
| `ScheduledCamelRoutes` | Camel route responsible for scheduled polling |
- `C:\Git\gfc-app\flex-hub-connector\src\main\java\com\landisgyr\gfc\flexhub_connector\adapters\inbound\scheduler\ScheduledCamelRoutes.java`
- Acts as the entry point of the outbound processing workflow.
- Triggers `PeekMessagesUseCase` at a configurable interval.
- Does not contain business logic.
- Delegates all processing to the application layer.
### Responsibilities
- Reads timer configuration from `ApplicationSetting`.
  - `periodMillis`
  - `initialDelayMillis`
- Creates a Camel `timer` endpoint.
- Invokes `PeekMessagesUseCase.peekMessages()`.
- Optionally supports Camel `master` endpoint for high availability.
### Execution flow
```
@startuml
top to bottom direction

rectangle "ApplicationSetting" as A
rectangle "ScheduledCamelRoutes" as B
rectangle "Camel Timer" as C
rectangle "PeekMessagesUseCase" as D

A --> B : Read configuration
B --> C : Create timer endpoint
C --> D : Trigger poll()

@enduml
```
### Configuration
| Property | Description |
| --- | --- |
| `periodMillis` | Polling interval between consecutive executions |
| `initialDelayMillis` | Delay before the first polling cycle starts |
### High availability
- Supports Camel `master` endpoint.
- When `haEnabled` is:
  - `false`: Every service instance executes the scheduler.
  - `true`: Only the elected master instance executes the scheduler. Prevents multiple instances from polling Flex-Hub simultaneously.
### Runtime behavior
```text
Service starts
      │
      ▼
Read timer configuration
      │
      ▼
Wait initial delay
      │
      ▼
Trigger Camel timer
      │
      ▼
Invoke PeekMessagesUseCase
      │
      ▼
Repeat every periodMillis
```
### Layer interaction
| Layer | Component | Responsibility |
| --- | --- | --- |
| Adapter | `ScheduledCamelRoutes` | Trigger scheduled polling |
| Application | `PeekMessagesUseCase` | Execute polling workflow |
| Domain | Not involved at this stage | |
## Outbound routes
- `C:\Git\gfc-app\flex-hub-connector\src\main\java\com\landisgyr\gfc\flexhub_connector\adapters\outbound\soap\OutboundCamelRoutes.java`
- Defines all outbound Camel routes for communicating with *Flex-Hub* using *SOAP*.
- Creates and configures the shared *Apache CXF* SOAP endpoint.
- Exposes reusable Camel `direct:` endpoints for SOAP operations.
- Performs protocol-specific request preparation before invoking Flex-Hub.
- Centralizes common SOAP communication logic for all outbound operations.
### Responsibilities
- Configure the shared `CXF` SOAP client.
- Load remote service configuration from `ApplicationSetting`.
- Configure:
  - Endpoint address
  - SSL context
  - Service interface
  - Data format (`POJO`)
- Populate the SOAP request context with `organisationUser`.
- Invoke Flex-Hub SOAP operations.
- Log SOAP requests and dequeue operations.
- Handle SOAP faults.
### Supported routes
| Camel route | SOAP operation | Purpose |
| --- | --- | --- |
| `direct:peek` | `peekMessage` | Retrieve the next available message from Flex-Hub |
| `direct:send` | `sendMessage` | Send a message to Flex-Hub |
| `direct:dequeueMessage` | `dequeueMessage` | Remove a successfully processed message |
| `direct:soap` | Generic SOAP endpoint | Shared route used by all SOAP operations |

### Route architecture
```
@startuml
top to bottom direction

rectangle "direct:peek" as Peek
rectangle "direct:send" as Send
rectangle "direct:dequeueMessage" as Dequeue

rectangle "direct:soap" as Soap
rectangle "Prepare SOAP Request" as Prepare
rectangle "CXF Endpoint" as CXF
rectangle "Flex-Hub" as FH

Peek --> Soap
Send --> Soap
Dequeue --> Soap

Soap --> Prepare
Prepare --> CXF
CXF --> FH
@enduml
```
### SOAP endpoint configuration
| Configuration | Purpose |
| --- | --- |
| `address` | Flex-Hub SOAP endpoint URL |
| `serviceClass` | `MarketMessagingB2BInboundServiceV01PortType` |
| `DataFormat.POJO` | Exchange Java objects instead of raw XML |
| `SSLContextParameters` | Configure secure TLS communication |
| `CamelContext` | Register the endpoint with Apache Camel |
### Request processing
- Every SOAP operation follows the same execution pipeline:
  - Receive the request through a Camel `direct:` endpoint.
  - Set the SOAP operation headers.
  - Populate the request context.
  - Add the `organisationUser` query parameter.
  - Forward the request to the shared `direct:soap` route.
  - Invoke the Apache CXF endpoint.
  - Return the SOAP response to the caller.
## Execution flow
```
@startuml
top to bottom direction

rectangle "direct:* Route" as Route
rectangle "Set SOAP Headers" as Headers
rectangle "Build Request Context" as Context
rectangle "Add organisationUser" as User
rectangle "CXF Endpoint" as CXF
rectangle "Flex-Hub" as FH

Route --> Headers
Headers --> Context
Context --> User
User --> CXF
CXF --> FH

@enduml
```

### Logging
- Logs every fifth `PeekMessageRequestType` request to reduce log volume.
- Logs every `dequeueMessage` request together with the document reference number.
- Logs SOAP faults for all outbound operations.
### Layer interaction
| Layer | Component | Responsibility |
| --- | --- | --- |
| Adapter | `OutboundCamelRoutes` | Execute outbound SOAP communication |
| Application | Use cases | Decide which SOAP operation to invoke |
| Domain | Domain models | Carry business data between layers |
### Design observations
- Implements a reusable *shared SOAP route* through `direct:soap`.
- Individual routes (`direct:peek`, `direct:send`, `direct:dequeueMessage`) are responsible only for selecting the SOAP operation.
- Common responsibilities are centralized in the shared route:
  - Request context creation
  - `organisationUser` injection
  - SSL configuration
  - Apache CXF invocation
  - Request logging
  - Exception handling
- This design minimizes code duplication and simplifies the addition of new SOAP operations.
## Difference between ScheduledCamelRoutes and direct:peek
- `ScheduledCamelRoutes` acts as the polling trigger.
- It creates the `timer:peek-messages` route and invokes `PeekMessagesUseCase.peekMessages()` at scheduled intervals, based on the configuration defined in `application.conf` (`ScheduledCamelRoutes.java`, `application.conf`).
- `ScheduledCamelRoutes` does not do the peek itself. It just fires on the timer and calls `PeekMessagesUseCase.peekMessages()`.
- Then `PeekMessagesUseCase` calls `SoapClientAdapter.peekMessage()`, and that adapter uses `direct:peek`.
- `direct:peek` handles the outbound SOAP request.
- When `SoapClientAdapter.peekMessage()` is called, it sends a `PeekMessageRequestType` to the `direct:peek` route.
- `OutboundCamelRoutes` then enriches the exchange with the required SOAP headers and forwards the request to `direct:soap`, where the actual SOAP operation is executed (`peekMessage`, `SoapClientAdapter.java`, `OutboundCamelRoutes.java`).
- `direct:peek` is actively used in the `"peek messages from Data Hub"` flow.
- This flow can be triggered either by the scheduled polling mechanism or through the gRPC `peekMessages` endpoint (`FlexibilityHubGrpcAdapter.java`, `PeekMessagesUseCase.java`).
- In summary: `ScheduledCamelRoutes` initiates the polling cycle, while `direct:peek` performs the SOAP `peekMessage` request as part of that polling workflow.
