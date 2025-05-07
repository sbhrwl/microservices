# Command Orchestrator Service
- [Responsibilities](#responsibilities)
- [API contract](#api-contract)
## Responsibilities
* Accepting **command requests** from upstream applications
* Sending requests to Kafka (`task.requested`)
* Subscribing to **responses** via Kafka (`orchestrator.responses`) and surfacing them back
## API contract
- [POST](post/README.md)
- [Kafka topic](kafkatopic/README.md)
- [GET](get/README.md)
