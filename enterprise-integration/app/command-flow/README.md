# Command flow
- [Component wise responsibilities](#component-wise-responsibilities)
- [Sequence of flow](#sequence-of-flow)
- [Layered architecture pattern](#layered-architecture-pattern)
- [Request flow](#request-flow)
- [Response flow](#response-flow)
- [Package structure](#package-structure)
  - [FHC](#fhc)
  - [Core](#core)
  - [IEC](#iec)
## Component wise responsibilities

| Layer             | Responsibility                                                                                                       |
| ----------------- | -------------------------------------------------------------------------------------------------------------------- |
|**Request**||
| FHC   | Receives SOAP messages from the market/hub, converts them to internal models, and initiates gRPC requests.               |
| Core  | Business logic layer. Validates, persists, and orchestrates control command processing.                              |
| IEC   | Converts business requests into IEC/device-specific commands and communicates with the device infrastructure.        |
|**Response**||
| IEC  | Receives device acknowledgements or execution results and publishes them back through gRPC.                          |
| Core | Retrieves responses from the outbox, prepares confirmation messages, and forwards them to the Hub.                   |
| FHC  | Maps the internal response back into the SOAP market message and sends the confirmation to the external Hub/DataHub. |

## Sequence of flow

```text
Hub
 │ SOAP request 
 ▼
FHC
 │ gRPC
 ▼
Core
 │ gRPC
 ▼
IEC
 │ JMS: IEC request
 ▼
HES
 │ JMS: IEC response
 ▼
IEC
 │ gRPC
 ▼
Core
 │ gRPC
 ▼
FHC
 │ SOAP response 
 ▼
Hub
```

## Layered architecture pattern
* **Camel Routes** handle message ingestion and scheduling.
* **Use Cases** orchestrate business workflows.
* **Adapters** translate between protocols (SOAP ↔ gRPC ↔ IEC).
* **Services** encapsulate business logic.
* **gRPC clients/adapters** provide inter-service communication.
* **Outbox processing** enables reliable, asynchronous response delivery back to the Hub.

## Request flow

```text
                        CONTROL COMMAND REQUEST FLOW
┌─────────────────────────────────────────────────────────────────────────────┐
│ Hub                                                                         │
└─────────────────────────────────────────────────────────────────────────────┘
        │ SOAP
        ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ FHC                                                                         │
└─────────────────────────────────────────────────────────────────────────────┘
        │ 
        ▼
ScheduledCamelRoutes
        │
        ▼
PeekMessagesUseCase
        │
        ▼
SoapClientAdapter
        │
        ▼
MessageMapper
        │
        ▼
ControlCommandGrpcClientAdapter
        │ gRPC
        ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ Core                                                                        │
└─────────────────────────────────────────────────────────────────────────────┘
ControlCommandServiceImpl
        │
        ▼
ControlCommandMutationService
        │
        ▼
DeviceInteractionGrpcClient
        │ gRPC
        ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ IEC                                                                         │
└─────────────────────────────────────────────────────────────────────────────┘
DeviceInteractionService
        │
        ▼
CommandProcessor
        │
        ▼
RequestDispatcher
        │JMS
        ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ HES                                                                         │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Response flow

```text
                     CONTROL COMMAND RESPONSE FLOW
┌─────────────────────────────────────────────────────────────────────────────┐
│ HES                                                                         │
└─────────────────────────────────────────────────────────────────────────────┘
        │JMS
        ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ IEC                                                                    │
└─────────────────────────────────────────────────────────────────────────────┘
InboundCamelRouteBuilder
        │
        ▼
CommandResponseProcessor
        │
        ▼
ControlCommandGrpcClient
        │ gRPC
        ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ Core                                                                        │
└─────────────────────────────────────────────────────────────────────────────┘
ControlCommandServiceImpl
        │
        ▼
ControlCommandMutationService
        │
        ▼
ScheduledCamelRoutes
        │
        ▼
ProcessOutboxUseCase
        │
        ▼
FlexibilityConnectorGrpcAdapter
        │ gRPC
        ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ FHC                                                                         │
└─────────────────────────────────────────────────────────────────────────────┘
FlexibilityHubGrpcAdapter
        │
        ▼
SendConfirmationMessageUseCase
        │
        ▼
SoapClientAdapter
        │
        ▼
OutboundCamelRoutes
        │SOAP
        ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ Hub (MarketMessagingSoapService)                                            │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Package structure

### FHC
```txt
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
### Core
```txt
com.landisgyr.gfc.core
├── adapters
│   ├── inbound
│   │   ├── grpc
│   │   │   ├── support
│   │   │   ├── ControlCommandServiceImpl
│   │   │   ├── EventServiceImpl
│   │   │   ├── FlexibilityServiceImpl
│   │   │   ├── LatencyLimiterInterceptor
│   │   │   ├── LoggingInterceptor
│   │   │   ├── MeteringPointServiceImpl
│   │   │   ├── MovingAverageLatencyTracker
│   │   │   ├── ProtoMapper
│   │   │   └── RevisionServiceImpl
│   │   ├── scheduler
│   │   │   └── ScheduledCamelRoutes
│   │   └── security
│   │       ├── AuthClaims
│   │       ├── AuthorizationInterceptor
│   │       ├── JwtVerifier
│   │       └── SecurityContext
│   └── outbound
│       ├── grpc
│       │   ├── DeviceInteractionGrpcClient
│       │   ├── FlexibilityConnectorGrpcAdapter
│       │   └── ProtoMapper
│       └── persistence
│           ├── mapper
│           ├── AccountingPointDao
│           ├── CommandDao
│           ├── DbTransactionContext
│           ├── FlexibilityDao
│           ├── ImportFileDao
│           ├── JobRepositoryAdapter
│           ├── MarketEventDao
│           ├── OutboxRepositoryAdapter
│           ├── PostgresCopyAdapter
│           └── UnitOfWork
├── app
│   ├── exceptions
│   ├── port
│   │   ├── DataHubNotificationPort
│   │   ├── JobRepository
│   │   └── OutboxRepositoryPort
│   ├── service
│   │   ├── helper
│   │   ├── mapper
│   │   │   └── FlexibilityDocumentMapper
│   │   ├── AccountingPointQueryService
│   │   ├── ControlCommandMutationService
│   │   ├── ControlCommandQueryService
│   │   ├── CsvImportService
│   │   ├── EventQueryService
│   │   ├── FlexibilityMutationService
│   │   ├── FlexibilityQueryService
│   │   └── FlexibilityUploadService
│   ├── usecase
│   │   ├── ProcessBatchJobUseCase
│   │   └── ProcessOutboxUseCase
│   ├── GracefulShutdownHook
│   └── Main
├── domain
│   ├── accounting_point
│   ├── command
│   ├── common
│   ├── event
│   ├── flexibility
│   ├── job
│   ├── outbox
│   └── query
└── infrastructure
    └── Bootstrap
```

### IEC
```txt
java
└── com.landisgyr.gfc.iec61968_connector
    ├── adapters
    │   ├── inbound
    │   │   ├── grpc
    │   │   │   ├── DeviceInteractionService
    │   │   │   ├── HealthGrpcService
    │   │   │   ├── ProtoMapper
    │   │   │   └── TenantIdInterceptor
    │   │   └── jms
    │   │       ├── helper
    │   │       │   └── ShortIdGenerator
    │   │       ├── ControlCommandResponseMapper
    │   │       ├── CreateTouCalendarResponse
    │   │       └── InboundCamelRouteBuilder
    │   └── outbound
    │       ├── grpc
    │       │   ├── ControlCommandGrpcClient
    │       │   └── ProtoMapper
    │       └── jms
    │           ├── EndDeviceControlBuilder
    │           ├── OutboundCamelRouteBuilder
    │           ├── RequestDispatcher
    │           ├── RolloutCalendarMapper
    │           ├── TouCalendarMapper
    │           └── TouCalendarModel
    ├── app
    │   ├── service
    │   │   ├── BrokerIdLookupService
    │   │   ├── CommandProcessor
    │   │   ├── CommandResponseProcessor
    │   │   ├── NetworkIdLookupService
    │   │   ├── RelayControlTypeResolver
    │   │   └── TenantIdLookupService
    │   └── Main
    ├── domain
    │   ├── model
    │   └── types
    └── infrastructure
```