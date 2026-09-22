# FHC gRPC routes
- [Request flow](#request-flow)
- [Use cases](#use-cases)
## Request flow
- `FlexibilityHubGrpcAdapter` exposes the inbound gRPC API, while `ControlCommandGrpcClient` performs the outbound gRPC call. `ControlCommandGrpcClientAdapter` connects the application port to that outbound client.
```text
External Client
      │
      │ gRPC
      ▼
FlexibilityHubGrpcAdapter
      │
      ▼
Use Case / Domain
      │
      ▼
ControlCommandGrpcClientAdapter
      │
      │ Proto mapping
      ▼
ControlCommandGrpcClient
      │
      │ gRPC: sendCommand
      │ + Tenant-Id
      │ + Origin
      ▼
gfc-core
```
## Use cases
- `PeekMessageUseCase`
- `SendConfirmationMessageUseCase`
- `UpdateAccountingPointControllabilityUseCase`
