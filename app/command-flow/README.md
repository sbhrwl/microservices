# Command flow

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
        │ SOAP
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
        │
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

---

# Control Command Response Flow

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
        │
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
        │
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

## Component Responsibilities

| Layer                 | Responsibility                                                                                                       |
| --------------------- | -------------------------------------------------------------------------------------------------------------------- |
| **Hub Request**       | Receives SOAP messages from the market, converts them to internal models, and initiates gRPC requests.               |
| **FHC-Core Request**  | Business logic layer. Validates, persists, and orchestrates control command processing.                              |
| **Core-IEC Request**  | Converts business requests into IEC/device-specific commands and communicates with the device infrastructure.        |
| **IEC-Core Response** | Receives device acknowledgements or execution results and publishes them back through gRPC.                          |
| **Core-FHC Response** | Retrieves responses from the outbox, prepares confirmation messages, and forwards them to the Hub.                   |
| **FHC-Hub Response**  | Maps the internal response back into the SOAP market message and sends the confirmation to the external Hub/DataHub. |

## Sequence Summary

```text
Hub
 │
 │ SOAP Control Command
 ▼
FHC
 │
 │ gRPC
 ▼
Core
 │
 │ gRPC
 ▼
IEC
 │
 │ IEC Command
 ▼
HES
 │
 │ Execution Result
 ▼
IEC
 │
 │ gRPC
 ▼
Core
 │
 │ gRPC
 ▼
FHC
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
