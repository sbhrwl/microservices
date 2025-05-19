# Task Orchestration Service
- [Responsibilities](#responsibilities)
- [Implementation](taskorchestrationservice/README.md)
## Responsibilities
* Subscribing to **command requests** from `task.requested` [Kafka topic](docs/kafkatopic/README.md) (produced by `CommandOrchestratorService`)
* Creating a **task** in the [database](docs/dbschema/README.md), including device list and metadata
* Publishing **task metadata** to [Kafka topic](docs/kafkatopic/README.md) (`task.created`) for downstream processing (command generation)
