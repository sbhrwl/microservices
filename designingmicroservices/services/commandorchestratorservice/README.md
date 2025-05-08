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
## MongoDB setup
- Create a [`docker-compose.yml`](mongodbsetup/docker-compose.yml)
- Run docker compose: `docker-compose up -d`
- Verify MySQL Docker container is running: `docker ps`
- [Download MongoDB shell](https://www.mongodb.com/try/download/shell)
- Open MongoDB shell
  ```bash
  Please enter a MongoDB connection string (Default: mongodb://localhost/): mongodb://root:root123@localhost:27017/admin
  ```
- **Configure** [`application.properties`](src\main\resources\application.properties):
  ```properties
  spring.data.mongodb.host=localhost
  spring.data.mongodb.port=27017
  spring.data.mongodb.database=mydatabase
  spring.data.mongodb.username=root
  spring.data.mongodb.password=root123
  spring.data.mongodb.authentication-database=admin
  ```