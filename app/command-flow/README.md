# Command flow

```text
                        CONTROL COMMAND REQUEST FLOW

┌─────────────────────────────────────────────────────────────────────────────┐
│ Hub                                                                         │
└─────────────────────────────────────────────────────────────────────────────┘
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
        │
        │ gRPC
        ▼

┌─────────────────────────────────────────────────────────────────────────────┐
│ FHC-Core                                                                    │
└─────────────────────────────────────────────────────────────────────────────┘
ControlCommandServiceImpl
        │
        ▼
ControlCommandMutationService
        │
        ▼
DeviceInteractionGrpcClient
        │
        │ gRPC
        ▼

┌─────────────────────────────────────────────────────────────────────────────┐
│ Core-IEC                                                                    │
└─────────────────────────────────────────────────────────────────────────────┘
DeviceInteractionService
        │
        ▼
CommandProcessor
        │
        ▼
RequestDispatcher
        │
        |JMS
        ▼
Head End System
```

---

# Control Command Response Flow

```text
                     CONTROL COMMAND RESPONSE FLOW

┌─────────────────────────────────────────────────────────────────────────────┐
│ IEC-Core                                                                    │
└─────────────────────────────────────────────────────────────────────────────┘
InboundCamelRouteBuilder
        │
        ▼
CommandResponseProcessor
        │
        ▼
ControlCommandGrpcClient
        │
        │ gRPC
        ▼

┌─────────────────────────────────────────────────────────────────────────────┐
│ Core-FHC                                                                    │
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
        │
        │ gRPC
        ▼

┌─────────────────────────────────────────────────────────────────────────────┐
│ FHC-Hub                                                                     │
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
        │
        ▼
MarketMessagingSoapService
        │
        ▼
Hub / DataHub
```

## Component Responsibilities

| Layer                 | Responsibility                                                                                                       |
| --------------------- | -------------------------------------------------------------------------------------------------------------------- |
| **Hub**               | Receives SOAP messages from the market, converts them to internal models, and initiates gRPC requests.               |
| **FHC-Core**          | Business logic layer. Validates, persists, and orchestrates control command processing.                              |
| **Core-IEC**          | Converts business requests into IEC/device-specific commands and communicates with the device infrastructure.        |
| **IEC-Core Response** | Receives device acknowledgements or execution results and publishes them back through gRPC.                          |
| **Core-FHC Response** | Retrieves responses from the outbox, prepares confirmation messages, and forwards them to the Hub.                   |
| **FHC-Hub Response**  | Maps the internal response back into the SOAP market message and sends the confirmation to the external Hub/DataHub. |

## Sequence Summary

```text
Hub
 │
 │ SOAP Control Command
 ▼
FHC-Hub
 │
 │ gRPC
 ▼
FHC-Core
 │
 │ gRPC
 ▼
Core-IEC
 │
 │ IEC Command
 ▼
Device
 │
 │ Execution Result
 ▼
IEC-Core
 │
 │ gRPC
 ▼
FHC-Core
 │
 │ gRPC
 ▼
FHC-Hub
 │
 │ SOAP Confirmation
 ▼
Hub
```

This architecture follows a layered pattern:

* **Camel Routes** handle message ingestion and scheduling.
* **Use Cases** orchestrate business workflows.
* **Adapters** translate between protocols (SOAP ↔ gRPC ↔ IEC).
* **Services** encapsulate business logic.
* **gRPC clients/adapters** provide inter-service communication.
* **Outbox processing** enables reliable, asynchronous response delivery back to the Hub.
