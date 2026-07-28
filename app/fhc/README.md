# FHC
- [Service overview](#service-overview)
- [Architecture](#architecture)
- [Package structure](#package-structure)
- [Message flow](#message-flow)
- [Processing lifecycle](#processing-lifecycle)
- [Layer responsibilities](#layer-responsibilities)
- [Architecture assessment](#architecture-assessment)
- [Recommendations](#recommendations)
## Service overview
* **Purpose**
  * Acts as an **integration connector** between **Flex-Hub** and **GFC-Core**.
  * Bridges different communication protocols while isolating business processing.
* **Responsibilities**
  * Periodically **peek** messages from **Flex-Hub**.
  * Convert external SOAP payloads into internal domain models.
  * Forward requests to **GFC-Core** using `gRPC`.
  * Receive processing results from **GFC-Core**.
  * Convert responses into Flex-Hub message format.
  * Push responses back to **Flex-Hub**.
* **Design principles**
  * Business logic resides in **GFC-Core**.
  * Connector focuses on **integration**, **orchestration**, **mapping**, and **protocol translation**.
  * Follows **Hexagonal Architecture (Ports & Adapters)**.

<details>
  <summary>prompt</summary>

```mermaid
flowchart TD
    FH[Flex-Hub]
    FC[FlexHub Connector]
    GC[GFC-Core]

    FH -->|Peek messages| FC
    FC -->|gRPC request| GC
    GC -->|Processing result| FC
    FC -->|SOAP response| FH
```
</details>

## Architecture
* **Architectural style**
  * Implements **Hexagonal Architecture**.
  * Separates **Application**, **Domain**, and **Infrastructure** concerns.
* **Inbound adapters**
  * Receive requests or trigger application workflows.
* **Application layer**
  * Coordinates business use cases.
  * Depends only on **Ports**.
* **Outbound adapters**
  * Communicate with external systems.
  * Implement application ports.
* **Domain**
  * Contains business models.
  * Independent of frameworks and transport protocols.

<details>
  <summary>prompt</summary>

```mermaid
flowchart TD
    A[Inbound adapters]
    B[Application]
    C[Ports]
    D[Outbound adapters]
    E[External systems]

    A --> B
    B --> C
    C --> D
    D --> E
```
</details>

- `Adapters` are the **gates** where messages enter and leave.
- `Application` is the **controller** deciding where every message goes next.
- `Domain` is the **cargo** being transported. It doesn't know who carries it or where it came from.
## Package structure
```
java
└── com.landisgyr.gfc.flexhub_connector
    ├── adapters
    │   ├── inbound
    │   │   ├── grpc
    │   │   │   ├── FlexibilityHubGrpcAdapter
    │   │   │   ├── HealthGrpcService
    │   │   │   ├── ProtoMapper
    │   │   │   └── TenantIdInterceptor
    │   │   └── scheduler
    │   │       └── ScheduledCamelRoutes
    │   └── outbound
    │       ├── grpc
    │       │   ├── ControlCommandGrpcClient
    │       │   ├── ControlCommandGrpcClientAdapter
    │       │   └── ProtoMapper
    │       └── soap
    │           ├── MasterDataMPEventMapper
    │           ├── MessageMapper
    │           ├── OutboundCamelRoutes
    │           └── SoapClientAdapter
    ├── app
    │   ├── port
    │   │   ├── PeekMessagesPort
    │   │   └── RelayControlCommandPort
    │   ├── service
    │   │   ├── OrganizationIdLookupService
    │   │   ├── OrganizationUserLookupService
    │   │   └── TenantIdLookupService
    │   ├── usecase
    │   │   ├── PeekMessagesUseCase
    │   │   ├── SendConfirmationMessageUseCase
    │   │   └── UpdateAccountingPointControllabilityUseCase
    │   └── Main
    ├── domain
    │   ├── model
    │   └── type
    └── infrastructure
```

| **Layer**               | **Package / Component**                       | **Purpose / Responsibility**                                                                                                                   |
| ----------------------- | --------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- |
| **Adapters**            |                                               | Contains infrastructure-specific implementations and isolates protocol/framework dependencies.                                                 |
|                         | **adapters.inbound.grpc**                     | Handles inbound gRPC communication.                                                                                                            |
|                         | `FlexibilityHubGrpcAdapter`                   | Receives incoming gRPC requests.                                                                                                               |
|                         | `HealthGrpcService`                           | Provides health check endpoints.                                                                                                               |
|                         | `TenantIdInterceptor`                         | Handles tenant context propagation.                                                                                                            |
|                         | `ProtoMapper`                                 | Maps between Protobuf messages and domain models.                                                                                              |
|                         | **adapters.inbound.scheduler**                | Handles scheduled processing.                                                                                                                  |
|                         | `ScheduledCamelRoutes`                        | Periodically polls Flex-Hub and triggers message processing workflows.                                                                         |
|                         | **adapters.outbound.grpc**                    | Handles outbound gRPC communication with GFC-Core.                                                                                             |
|                         | `ControlCommandGrpcClient`                    | Low-level gRPC client.                                                                                                                         |
|                         | `ControlCommandGrpcClientAdapter`             | Implements the application port and invokes **GFC-Core**.                                                                                      |
|                         | `ProtoMapper`                                 | Converts domain models into Protobuf messages.                                                                                                 |
|                         | **adapters.outbound.soap**                    | Handles outbound SOAP communication with Flex-Hub.                                                                                             |
|                         | `SoapClientAdapter`                           | Sends responses to Flex-Hub.                                                                                                                   |
|                         | `OutboundCamelRoutes`                         | Defines Apache Camel integration routes.                                                                                                       |
|                         | `MessageMapper`                               | Converts domain objects into SOAP messages.                                                                                                    |
|                         | `MasterDataMPEventMapper`                     | Maps master data events into SOAP payloads.                                                                                                    |
| **Application (`app`)** |                                               | Contains application orchestration logic.                                                                                                      |
|                         | **app.port**                                  | Defines application ports (interfaces) for external interactions.                                                                              |
|                         | `PeekMessagesPort`                            | Abstraction for retrieving messages from Flex-Hub.                                                                                             |
|                         | `RelayControlCommandPort`                     | Abstraction for forwarding control commands to GFC-Core.                                                                                       |
|                         | **app.service**                               | Provides reusable application services shared across use cases.                                                                                |
|                         | `OrganizationIdLookupService`                 | Resolves organization identifiers.                                                                                                             |
|                         | `OrganizationUserLookupService`               | Resolves organization users.                                                                                                                   |
|                         | `TenantIdLookupService`                       | Resolves tenant identifiers.                                                                                                                   |
|                         | **app.usecase**                               | Implements business workflows and application use cases.                                                                                       |
|                         | `PeekMessagesUseCase`                         | Retrieves and processes messages from Flex-Hub.                                                                                                |
|                         | `SendConfirmationMessageUseCase`              | Sends confirmation messages back to Flex-Hub.                                                                                                  |
|                         | `UpdateAccountingPointControllabilityUseCase` | Updates the controllability status of accounting points.                                                                                       |
| **Domain**              |                                               | Contains the core business models and business rules. Independent of infrastructure technologies such as SOAP, gRPC, Apache Camel, and Spring. |

<details>
  <summary>prompt</summary>

```mermaid
flowchart TD
    A[Adapters]
    B[Application]
    C[Domain]

    A --> B
    B --> C
```
</details>

## Message flow
- The connector continuously polls **Flex-Hub** for pending messages.
- Retrieved SOAP messages are mapped into internal domain models.
- The application layer orchestrates message processing.
- Requests are forwarded to **GFC-Core** using `gRPC`.
- After successful delivery, the original message is removed from the Flex-Hub queue.
- Once business processing is completed, **GFC-Core** sends an asynchronous confirmation to the connector.
- The connector processes the confirmation and prepares the corresponding response for **Flex-Hub**.

<details>
  <summary>prompt</summary>

```mermaid
flowchart TD
    A[Flex-Hub]
    B[SOAP Adapter]
    C[Application Use Case]
    D[gRPC Adapter]
    E[GFC-Core]

    A -->|Peek message| B
    B --> C
    C --> D
    D --> E
    E -->|Async confirmation| D
    D --> B
    B -->|Response| A
```
</details>

## Processing lifecycle
- **Polling**
  - `ScheduledCamelRoutes` periodically triggers message polling.
  - `OutboundCamelRoutes` sends a `PeekMessageRequest` to Flex-Hub.
- **Message retrieval**
  - Flex-Hub returns the next available SOAP message.
  - `MessageMapper` converts the SOAP payload into a domain model.
- **Application processing**
  - `PeekMessagesUseCase` receives the mapped message.
  - The appropriate application workflow is initiated.
- **Notification relay**
  - `ControlCommandGrpcClient` maps the domain model into a `gRPC` request.
  - The request is delivered to **GFC-Core**.
- **Queue management**
  - After successful delivery, `OutboundCamelRoutes` sends a `DequeueMessage` request.
  - The processed message is removed from the Flex-Hub queue.
- **Asynchronous callback**
  - After completing business processing, **GFC-Core** invokes `sendLoadControlConfirmation`.
  - `FlexibilityHubGrpcAdapter` receives the callback.
  - The connector acknowledges the request and prepares the response for Flex-Hub.

<img src="images/processing-lifecycle.png">

<details>
  <summary>prompt</summary>

```mermaid
sequenceDiagram
    participant Scheduler
    participant Camel as OutboundCamelRoutes
    participant FlexHub
    participant Mapper as MessageMapper
    participant UseCase as PeekMessagesUseCase
    participant GFC as GFC-Core
    participant Adapter as FlexibilityHubGrpcAdapter

    Scheduler->>Camel: Trigger polling
    Camel->>FlexHub: PeekMessageRequest
    FlexHub-->>Camel: SOAP message
    Camel->>Mapper: Map SOAP payload
    Mapper->>UseCase: Domain message
    UseCase->>GFC: Relay notification (gRPC)
    GFC-->>UseCase: Notification delivered
    UseCase->>Camel: Dequeue message
    Camel->>FlexHub: DequeueMessage
    GFC->>Adapter: sendLoadControlConfirmation()
    Adapter-->>GFC: Acknowledgment
```
</details>

### Runtime log correlation

| Timestamp | Component | Layer | Event |
|-----------|-----------|-------|-------|
| `13:04:31.700` | `OutboundCamelRoutes` | **Adapter** (Outbound SOAP) | Send `PeekMessageRequest` to Flex-Hub |
| `13:04:31.701` | `MessageMapper` | **Adapter** (Outbound SOAP) | Map incoming `LoadControl` SOAP message to a domain model |
| `13:04:31.730` | `PeekMessagesUseCase` | **Application** | Start application workflow |
| `13:04:32.372` | `ControlCommandGrpcClient` | **Adapter** (Outbound gRPC) | Deliver notification to GFC-Core |
| `13:04:32.409` | `OutboundCamelRoutes` | **Adapter** (Outbound SOAP) | Dequeue processed message from Flex-Hub |
| `13:04:39.564` | `FlexibilityHubGrpcAdapter` | **Adapter** (Inbound gRPC) | Receive `sendLoadControlConfirmation` callback |
| `13:04:39.578` | `FlexibilityHubGrpcAdapter` | **Adapter** (Inbound gRPC) | Send acknowledgment |

## Layer responsibilities
* **Inbound adapters**
  * Accept external requests.
  * Trigger application workflows.
  * Perform protocol-specific mapping.
* **Application**
  * Coordinates use cases.
  * Depends only on ports.
  * Contains no transport-specific logic.
* **Ports**
  * Define contracts for external dependencies.
  * Enable dependency inversion.
* **Outbound adapters**
  * Implement ports
  * Integrate with SOAP and `gRPC`
* **Domain**
  * Encapsulates business models.
  * Remains framework independent.
## Architecture assessment
* **Strengths**
  * Clear separation of concerns.
  * Strong adherence to **Hexagonal Architecture**.
  * External protocols isolated within adapters.
  * Business workflows encapsulated in use cases.
  * Dependency inversion achieved through ports.
  * Domain remains independent of infrastructure.
  * Easily testable through port mocking.
  * Supports replacing external systems with minimal impact.

## Recommendations
* Add resilience patterns for external integrations.
  * `Retry`
  * `Circuit Breaker`
  * `Dead Letter Queue (DLQ)`
  * `Idempotency`
* Add observability.
  * Distributed tracing.
  * Structured logging.
  * Metrics.
  * Correlation IDs.
* Maintain framework independence by keeping `SOAP`, `gRPC`, and `Apache Camel` confined to adapter implementations.
