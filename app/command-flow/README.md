# Command flow
- [Component wise responsibilities](#component-wise-responsibilities)
- [Sequence of flow](#sequence-of-flow)
- [Layered architecture pattern](#layered-architecture-pattern)
- [Request flow](#request-flow)
- [Response flow](#response-flow)
## Component wise responsibilities

| Layer             | Responsibility                                                                                                       |
| ----------------- | -------------------------------------------------------------------------------------------------------------------- |
|Request||
| **FHC Request**   | Receives SOAP messages from the market/hub, converts them to internal models, and initiates gRPC requests.               |
| **Core Request**  | Business logic layer. Validates, persists, and orchestrates control command processing.                              |
| **IEC Request**   | Converts business requests into IEC/device-specific commands and communicates with the device infrastructure.        |
| **IEC Response**  | Receives device acknowledgements or execution results and publishes them back through gRPC.                          |
| **Core Response** | Retrieves responses from the outbox, prepares confirmation messages, and forwards them to the Hub.                   |
| **FHC Response**  | Maps the internal response back into the SOAP market message and sends the confirmation to the external Hub/DataHub. |

## Sequence of flow

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
 │ JMS: IEC request
 ▼
HES
 │
 │ JMS: IEC response
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