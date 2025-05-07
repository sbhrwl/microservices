# Task Orchestration Service
## Responsibilities
* Accepting **command requests** from upstream applications (e.g., `CommandOrchestratorService`)
* Creating a **task entry** with metadata and list of devices
* Publishing task metadata to Kafka (`task.created`) for downstream command generation
* Optionally exposing task query API for monitoring/debugging
## API contract
- [POST](post/README.md)
- [Kafka topic](kafkatopic/README.md)
- [GET](get/README.md)
