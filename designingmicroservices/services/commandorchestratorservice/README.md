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
- Create a [`docker-compose.yml`](kafkasetup/docker-compose.yml)
- Run docker compose: `docker-compose up -d`
- Varify: `docker-compose logs kafka`
- Log in to container via `Docker Desktop` or `console`
  - `docker exec -it <kafka_container_id_or_name> /bin/bash`
- Verify topics: `kafka-topics --bootstrap-server localhost:9092 --list`
- View messages from a topic
  - If you don’t want to read from the beginning but only new messages, omit the `--from-beginning` flag
  ```
  kafka-console-consumer --bootstrap-server localhost:9092 --topic task.requested --from-beginning --max-messages 10
  ```
## MongoDB setup
- Create a [`docker-compose.yml`](mongodbsetup/docker-compose.yml)
- Run docker compose: `docker-compose up -d`
- Verify MySQL Docker container is running: `docker ps`
- [Download MongoDB shell](https://www.mongodb.com/try/download/shell)
- Open MongoDB shell
  ```bash
  Please enter a MongoDB connection string (Default: mongodb://localhost/): mongodb://root:root123@localhost:27017/admin
  ```