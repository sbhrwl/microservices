# Command Orchestrator Service
- [Responsibilities](#responsibilities)
- [API contract](#api-contract)
## Responsibilities
* Accepting **command requests** from upstream applications
* Sending requests to Kafka (`task.requested`)
* Accept **command responses** from downstream services (via Kafka topic `orchestrator.responses`)

## API contract
- [POST](post/README.md)
- [Kafka topic](kafkatopic/README.md)
- [DB schema](dbschema/README.md)
- [GET](get/README.md)
