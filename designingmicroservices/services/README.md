# Services
| **Service Name**               | **Role / Responsibility**                                                               |
| ------------------------------ | --------------------------------------------------------------------------------------- |
| **CommandOrchestratorService** | Accepts commands from upstream apps; sends and receives status/responses                |
| **TaskOrchestrationService**   | Parses incoming requests; creates and manages tasks with list of devices                |
| **CommandLifecycleService**    | Generates device-specific commands; handles device responses and updates task status    |
| **ProtocolAdapterService**     | Converts commands from XML to DLMS PDU and vice versa; interfaces directly with devices |
| **UpstreamIntegrationService** | Pushes parsed and formatted responses back to orchestrator or external systems          |
