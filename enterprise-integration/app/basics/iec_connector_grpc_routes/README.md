# IEC connector gRPC routes
- [Request Flow](#request-flow)
- [Components](#components)
```text
                   gfc-core
                      │
                      │ gRPC: sendCommand()
                      ▼
DeviceInteractionService
(Inbound gRPC Adapter)
                      │
                      │ Inbound ProtoMapper
                      │ (Protobuf → ControlCommandRequest)
                      │ + Tenant-Id
                      ▼
CommandProcessor
(Application Service)
                      │
                      │ Process command
                      │
                      │ Outbound ProtoMapper
                      │ (ControlCommandResponse
                      │  → NotifyCommandExecutionResult)
                      ▼
ControlCommandGrpcClient
(Outbound gRPC Client)
                      │
                      │ gRPC: notifyCommandExecution()
                      │ + Tenant-Id
                      │ + Origin
                      ▼
                   gfc-core
```
## Request Flow
- `gfc-core` calls:
  - `sendCommand()`
  - `batchSendCommand()`
- `DeviceInteractionService`
  - Receives the gRPC request.
  - Extracts `Tenant-Id` from the interceptor.
  - Maps the protobuf request to `ControlCommandRequest`.
  - Invokes `CommandProcessor.sendCommand()`.
- `CommandProcessor`
  - Executes the command processing logic.
  - Once processing is complete, triggers the notification of the execution result.
- `ControlCommandGrpcClient`
  - Calls `gfc-core.notifyCommandExecution()`.
  - Sends the execution result with the required metadata: 
    - `Tenant-Id`
	- `Origin`
## Components
| Layer | Component | Responsibility |
| --- | --- | --- |
| Inbound Adapter | `DeviceInteractionService` | Receives command requests from gfc-core |
| Application | `CommandProcessor` | Processes control commands |
| Outbound Adapter | `ControlCommandGrpcClient` | Reports command execution results back to gfc-core |
| External System | `gfc-core` | Sends commands and receives execution notifications |
