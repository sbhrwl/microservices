# Task Orchestration Service
## Responsibilities
* Subscribing to **command requests** from `task.requested` Kafka topic (produced by `CommandOrchestratorService`)
* Creating a **task** in the database, including device list and metadata
* Publishing **task metadata** to Kafka (`task.created`) for downstream processing (command generation)

## API contract
- [POST](docs/post/README.md)
- [Kafka topic](docs/kafkatopic/README.md)
- [DB schema](docs/dbschema/README.md)
- [GET](docs/get/README.md)
