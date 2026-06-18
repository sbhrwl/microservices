# Ad-hoc command
- [Overview](#overview)
- [Request flow](#request-flow)
- [Message structure](#message-structure)
  - [Identity fields](#identity-fields)
  - [Routing fields](#routing-fields)
  - [Control intent](#control-intent)
  - [Identifier separation](#identifier-separation)
- [Data hub simulator](#data-hub-simulator)
  - [Transport security](#transport-security)
  - [Message handling](#message-handling)
  - [Mailbox model](#mailbox-model)
- [Flex hub connector](#flex-hub-connector)
  - [Polling process](#polling-process)
  - [Identity mapping](#identity-mapping)
  - [SOAP security](#soap-security)
  - [Message transformation](#message-transformation)
  - [Handoff to core](#handoff-to-core)
- [GFC core](#gfc-core)
  - [Security validation](#security-validation)
  - [Command processing](#command-processing)
  - [Reliability controls](#reliability-controls)
  - [Downstream communication](#downstream-communication)
  - [Feedback handling](#feedback-handling)
- [IEC61968 connector](#iec61968-connector)
  - [gRPC input](#grpc-input)
  - [IEC message creation](#iec-message-creation)
  - [JMS dispatch](#jms-dispatch)
  - [Response handling](#response-handling)
- [System design pillars](#system-design-pillars)
  - [Security](#security)
  - [Observability](#observability)
  - [Reliability](#reliability)
  - [Performance](#performance)
  - [Maintainability](#maintainability)
  - [Traceability](#traceability)
- [Takeaways](#takeaways)
## Overview
- A single SOAP load-control request travels through multiple microservices before reaching the operational device layer.
- The flow crosses:
  - **Transport security**
    - SOAP over HTTPS with mutual TLS
    - Certificate validation
  - **Identity translation**
    - Tenant mapping
    - Organization routing
    - Correlation tracking
  - **Protocol boundaries**
    - SOAP → internal command model
    - gRPC → IEC message
    - JMS → device communication
  - **Persistence and reconciliation**
    - Store command state
    - Track execution result
- The core lesson:
  - A small input message creates multiple distributed system responsibilities.
  - Each service boundary introduces:
    - security requirements
    - failure scenarios
    - observability needs
    - traceability requirements
## Request flow
- The sample `F35` load-control request moves through four services:
- **Data Hub Simulator**
  - Receives SOAP message
  - Validates request
  - Stores message for retrieval
- **Flex Hub Connector**
  - Polls simulator
  - Converts SOAP payload into internal command
- **GFC Core**
  - Validates command
  - Persists state
  - Orchestrates downstream execution
- **IEC61968 Connector**
  - Converts command into IEC message
  - Sends through JMS
  - Returns execution status

| Service | Responsibility | Protocols | Security boundary | Output |
| --- | --- | --- | --- | --- |
| Data Hub Simulator | Message intake and storage | SOAP, REST | mTLS, TLS 1.3 | Document reference |
| Flex Hub Connector | Message translation | SOAP, gRPC | Client certificate, tenant metadata | Internal command |
| GFC Core | Business orchestration | gRPC, PostgreSQL | JWT, trusted tenant metadata | Stored command |
| IEC61968 Connector | Device communication | gRPC, JMS | Tenant metadata, broker credentials | Execution callback |

## Message structure
- The SOAP envelope contains multiple identifiers and business fields.
### Identity fields
- `Identification`
  - Unique business message identifier
- `DocumentType`
  - Defines message type
  - Example: `F35`
- `Creation`
  - Message creation timestamp
- `OriginalBusinessDocumentReference`
  - External trace reference
- These fields answer:
  - Which business event is this?
  - Where did it originate?
### Routing fields
- The request contains sender and receiver identities:
  - `PhysicalSenderEnergyParty`
  - `JuridicalSenderEnergyParty`
  - `JuridicalRecipientEnergyParty`
  - `PhysicalRecipientEnergyParty`
- Example:
  - `PhysicalRecipientEnergyParty`
    - Maps to tenant configuration
    - Example: `gfc1-dev`
### Control intent
- The actual device operation comes from:
  - `EnergyBusinessProcess`
    - Example: `DH-1223-2`
  - `MeteringPointUsedDomainLocation`
    - Flexibility or metering point
  - `EndDeviceControl.Identification`
    - Device command identifier
  - `RelayIdentification`
    - Target relay
  - `Request`
    - Example: `BP02`
    - Converted into internal relay state
### Identifier separation
- Different identifiers have different responsibilities:
  - **Business message id**
    - External document identity
  - **Document reference number**
    - Simulator inbox receipt
  - **Tenant id**
    - Internal service identity
  - **Organization user**
    - Simulator lookup key
  - **Correlation id**
    - Connects queue messages and callbacks
- Mixing these identifiers creates poor traceability.
## Data hub simulator
- The simulator acts as a secure mailbox.
- It does not execute device commands.
- Its responsibility:
  - receive
  - validate
  - store
  - expose messages for downstream services
### Transport security
[`ServerTlsConfig`](ServerTlsConfig.md) provides:
  - server keystore
  - trusted client certificates
  - client authentication
  - `TLSv1.3`
  - restricted cipher configuration
- This creates an mTLS boundary.
### Message handling
- `MarketMessagingSoapService.sendMessage()` performs:
  - Validate message container
  - Validate payload
  - Detect payload type
  - Read recipient organization
  - Store message in inbox
  - Generate document reference number
  - Return acknowledgment
- The response means:
  - message accepted
- It does not mean:
  - device command completed
### Mailbox model
- The simulator behaves like a queue:
  - `sendMessage`
    - Stores message  
  - `peekMessage`
    - Reads pending message 
  - `dequeueMessage`
    - Removes processed message
## Flex hub connector
- [Boot flow](flexhubconnector/README.md)
  - [Dagger](flexhubconnector/dagger/README.md)
- The connector converts external market messages into internal commands.
- Main responsibilities:
  - Poll simulator
  - Map identities
  - Transform payload
  - Forward command
### [Polling process](app/switching/flexhubconnector/pollmessages/README.md)
- `ScheduledCamelRoutes`:
  - Starts periodic polling
  - Default behavior:
    - initial delay: `5 seconds`
    - interval: `20 seconds`
- `PeekMessagesUseCase`:
  - Iterates tenant registry
  - Requests messages for each tenant
### Identity mapping
- The connector translates:
  - tenant id → organization user
  - organization id → recipient organization
  - SOAP payload → internal command
- Example:
  - Tenant:
    - `gfc1-dev`  
  - Organization:
    - `6411802010007`
### SOAP security
- `SSLContextParameterFactory` creates client TLS configuration:
  - PKCS12 client keystore
  - JKS truststore
  - certificate alias selection
- The connector becomes an authenticated SOAP client.
### Message transformation
- `MessageMapper` converts: `LoadControlMessageMessageType` into `SendRelayControlCommand`
- Mapping:
  - message identification → command id
  - recipient → network id
  - metering point → flexibility id
  - end device control → relay target
  - `BP02` → relay state
- This isolates protocol-specific XML from internal business logic.
### Handoff to core
- The connector calls GFC Core using:
  - gRPC
    - `Tenant-Id` metadata
  - The metadata provides tenant context for internal processing.
## GFC core
- GFC Core is the orchestration layer.
- Responsibilities:
  - security validation
  - business processing
  - persistence
  - downstream communication
### Security validation
- `AuthorizationInterceptor` supports:
  - External requests:
    - Bearer JWT
  - Internal service calls:
    - Trusted `Tenant-Id` metadata
  - JWT validation checks:
    - issuer
    - audience
    - subject
    - expiration
    - token id
    - authorized party
- Trusted tenant metadata depends on:
  - secure deployment boundary
  - controlled service communication
### Command processing
- `ControlCommandMutationService.sendCommand()`:
  - Resolve flexibility targets
  - Calculate aggregate power
  - Mark command initiated
  - Persist command
  - Call IEC61968 Connector
- Important design choice:
  - Persist before downstream execution
  - Benefits:
    - traceability
    - recovery
    - failure analysis
### Reliability controls
- Core provides:
  - latency limiting
  - request timing logs
  - exception mapping
  - message size limits
- These convert an integration service into an operable production system.
### Downstream communication
- Core sends:
  - gRPC `SendCommandRequest`
- The code uses plaintext channels.
- Transport security depends on deployment:
  - application layer
  - service mesh
  - network layer
- The important point:
  - Know where security actually exists.
### Feedback handling
- Execution callback flow:
  - IEC connector reports result
  - Core receives `notifyCommandExecution`
  - Database state is updated
- Lifecycle:
  - accepted
  - dispatched
  - executed or failed
  - reconciled
## IEC61968 connector
- The IEC connector bridges business commands and device protocols.
- Responsibilities:
  - Receive command
  - Build IEC message
  - Dispatch through JMS
  - Return execution status
### gRPC input
- `TenantIdInterceptor` requires:
  - `Tenant-Id` metadata
- Processing:
  - Convert protobuf request
  - Attach tenant context
  - Pass to command processor
### IEC message creation
- `CommandProcessor` and `EndDeviceControlBuilder`:
  - Resolve network id
  - Resolve relay control type
  - Group compatible commands
  - Build IEC XML
  - Add correlation id
  - Add message id
  - Configure queue metadata
- Grouping improves:
  - message efficiency
  - downstream throughput
### JMS dispatch
- `RequestDispatcher`:
  - Sends request to tenant-specific JMS route
  - Marshals XML
  - Places message on broker queue
- The broker becomes the operational boundary.
### Response handling
- The connector:
  - Receives JMS response
  - Unmarshals XML
  - Maps response
  - Calls Core callback API
- The request completes only after state reconciliation.
## System design pillars
### Security
- mTLS for SOAP communication
- TLS 1.3 configuration
- JWT validation
- Tenant metadata propagation
- JMS broker authentication
### Observability
- Structured JSON logs
- SOAP operation logging
- gRPC timing
- Certificate subject logging
- Correlation identifiers
### Reliability
- Inbox and dequeue model
- Persistence before dispatch
- Callback-based reconciliation
- Failure status mapping
- Health management
### Performance
- Scheduled polling
- Message size limits
- Command grouping
- Latency protection
- Controlled thread pools
### Maintainability
- Adapter-based architecture
- Use-case separation
- Domain models
- Dedicated protocol mappers
- Configuration-driven tenants
### Traceability
- Important identifiers:
  - business message id
  - document reference
  - command id
  - correlation id
  - tenant id
  - organization user
- Traceability allows the system to explain:
  - what happened
  - who requested it
  - where it failed
- Example runtime trace:
```text
[Simulator] SOAP operation received: sendMessage
[Simulator] Stored message for organization 6411802010007
[Flex] Retrieved message <document-ref>
[Core] Received sendCommand request
[IEC] Sent message correlation-id=<command-id>
[Core] Updated execution state
```
## Takeaways
- A simple request becomes a distributed workflow.
- Every protocol transition creates new failure boundaries.
- Security must exist at every service boundary.
- Identifiers are the backbone of debugging.
- Observability is a system capability, not an optional feature.
