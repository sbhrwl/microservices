# Task Orchestration Service
## Responsibilities
* * Subscribing to **command requests** from `task.requested` Kafka topic (produced by `CommandOrchestratorService`)
* Creating a **task** in the database, including device list and metadata
* Publishing **task metadata** to Kafka (`task.created`) for downstream processing (command generation)

## API contract
- [POST](post/README.md)
- [Kafka topic](kafkatopic/README.md)
- [GET](get/README.md)
