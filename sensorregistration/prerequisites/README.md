# Command Orchestrator Service
- [Kafka setup](#kafka-setup)
- [MongoDB setup](#mongodb-setup)
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
- Publish message to a topic
  ```
  kafka-console-producer.sh --bootstrap-server localhost:9092 --topic my-topic
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
- To confirm the data, query the collection:
  ```
  db.command_requests.find().pretty()
  ```
# Keycloak setup
- [`docker-compose.yaml`](docker-compose.yaml)
- Run docker compose: `docker-compose up -d`
- Access Keycloak
  * URL: [http://localhost:8080](http://localhost:8080)
  * Username: `admin`
  * Password: `admin`
- Stop Services: `docker-compose down`
