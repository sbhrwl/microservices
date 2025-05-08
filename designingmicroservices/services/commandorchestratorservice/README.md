# Command Orchestrator Service
- [Responsibilities](#responsibilities)
- [API contract](#api-contract)
## Responsibilities
* Accepting **command requests** from upstream applications
* Sending requests to Kafka (`task.requested`)
* Accept **command responses** from downstream services (via Kafka topic `orchestrator.responses`)

## API contract
- [POST](docs/post/README.md)
- [Kafka topic](docs/kafkatopic/README.md)
- [DB schema](docs/dbschema/README.md)
- [GET](docs/get/README.md)

## Kafka setup
- [`docker-compose.yml`](docker-compose.yml)
- Run: `docker-compose up -d`
- Varify: `docker-compose logs kafka`