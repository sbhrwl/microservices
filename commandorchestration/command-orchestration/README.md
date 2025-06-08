# Services
| **Service Name**               | **Role / Responsibility**                                                               |
| ------------------------------ | --------------------------------------------------------------------------------------- |
| [**CommandOrchestratorService**](commandorchestratorservice/README.md) | Accepts commands from upstream apps; sends and receives status/responses                |
| [**TaskOrchestrationService**](taskorchestrationservice/README.md)   | Parses incoming requests; creates and manages tasks with list of devices                |
| [**CommandLifecycleService**](commandlifecycleservice/README.md)    | Generates device-specific commands; handles device responses and updates task status    |
| [**ProtocolAdapterService**](protocoladapterservice/README.md)     | Converts commands from XML to DLMS PDU and vice versa; interfaces directly with devices |
