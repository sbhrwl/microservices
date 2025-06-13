# Send command to sensors
## Services
- Build services and decide among
  * `HTTP (REST)`
  * `gRPC`
  * `Kafka/Broker`
### UserCommandService
- UI/API Gateway
* Receives commands (like "LS") from user interface.
* Authenticates user.
- **External interface** →
  - Use **HTTP REST** (browser/mobile clients need it)
### AuthService
* Verifies tokens, returns user info.
- Use **gRPC** (internal, low latency, structured schema)
### CommandOrchestratorService
* **Central brain** that:
  * Validates input.
  * Resolves sensor ID, type.
  * Decides protocol.
  * Creates a full `CommandRequest`.
- Use **gRPC** for communication from UserCommand → Orchestrator
- Use **gRPC** to call:
  * **SensorRegistryService**
  * **ProtocolAdapterService**
  * **CommandDispatcherService**
#### SensorRegistryService
* Knows what sensors exist and their metadata.
- Use **gRPC**
#### ProtocolAdapterService
* Converts logical command ("LS") into protocol-specific frame (DLMS, LoRa, etc.)
- Use **gRPC**
#### CommandDispatcherService
* Takes protocol message and sends to sensor.
* Talks to external systems (modems, concentrators).
- Use **gRPC**
## CommandResultIngestorService
* Receives delivery status / result (ACK, output of "LS", timeout, etc.)
- **Sensor → system communication** is **event-based** →
- Use **Kafka topic** like `command-results`
## CommandLoggerService or DashboardUpdater
* Listens for command success/failure.
* Updates dashboard or audit trail.
- Use **Kafka** (as consumer of `command-results` topic)
## Summary

| Service                      | Protocol Used    | Why                           |
| - | - | -- |
| UserCommandService           | HTTP REST        | Exposed to external UI        |
| AuthService                  | gRPC             | Fast internal auth            |
| CommandOrchestratorService   | gRPC             | Central coordination          |
| SensorRegistryService        | gRPC             | Structured data access        |
| ProtocolAdapterService       | gRPC             | Tight conversion logic        |
| CommandDispatcherService     | Kafka (producer) | Send commands                 |
| CommandResultIngestorService | Kafka (consumer) | Sensor replies are async      |

## Flow
```
[User (UI / Browser)]
        |
        v
[UserCommandService]  <--(HTTP REST)--  🔸 Entry point from user
        |
        v
   [AuthService]       <--(gRPC)--  🔐 Validates token, returns user info
        |
        v
[CommandOrchestratorService]         🔁 Central coordinator
┌----------------------------------------------------------------------------------------┐
|       |                                                                                |
|     (gRPC)                                                                             |
|       v                                                                                |
| [SensorRegistry] -(gRPC) > [ProtocolAdapterService]-(gRPC) > [CommandDispatcherService]|
|                                                                          |             |
|                                                                          v             |
└-------------------------------------------------------------------------kafka----------┘

[CommandResultIngestorService]  <--(Kafka Consumer)-- Receives result from sensor
```
