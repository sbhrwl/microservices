# Containers
- [Docker commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Docker/commands/README.md)
- [Docker compose commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Docker/dockercompose/README.md)
- [Change to Kafka setup for containers](#change-to-kafka-setup-for-containers)
- [Setting up containers](#setting-up-containers)
  - [Prerequisites](#prerequisites)
  - [Using docker compose](#using-docker-compose)
  - [Using docker commands](#using-docker-commands)
- [Test](#test)
## Change to Kafka setup for containers
- Replace: `KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092`
- With `KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://host.docker.internal:29092`
- Followed by
  - `docker-compose down`
  - `docker-compose up -d`
## Setting up containers
### Prerequisites 

Steps|[Task orchestrator](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/task-orchestrator/README.md)|[Command orchestrator](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/command-orchestrator/README.md)|[Protocol gateway](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/protocol-gateway/README.md)|[Sensor simulator](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/sensor-simulator/README.md)|
|-----|----------|--------------|--------------------|--------------------|
|application.properties|[application.properties](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/task-orchestrator/src/main/resources/application.properties)|[application.properties](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/command-orchestrator/src/main/resources/application.properties)|[application.properties](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/protocol-gateway/src/main/resources/application.properties)|[application.properties](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/sensor-simulator/src/main/resources/application.properties)|
|Build jar|`mvn clean package`|`mvn clean package`|`mvn clean package`|`mvn clean package`|
|Dockerfile|[Dockerfile](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/task-orchestrator/Dockerfile)|[Dockerfile](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/command-orchestrator/Dockerfile)|[Dockerfile](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/protocol-gateway/Dockerfile)|[Dockerfile](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/sensor-simulator/Dockerfile)|

### Using docker compose

|Steps|[Task orchestrator](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/task-orchestrator/README.md)|[Command orchestrator](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/command-orchestrator/README.md)|[Protocol gateway](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/protocol-gateway/README.md)|[Sensor simulator](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/sensor-simulator/README.md)|
|-----|----------|--------------|--------------------|--------------------|
|docker-compose.yml|[docker-compose.yml](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/task-orchestrator/docker-compose.yml)|[docker-compose.yml](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/command-orchestrator/docker-compose.yml)|[docker-compose.yml](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/protocol-gateway/docker-compose.yml)|[docker-compose.yml](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/sensor-simulator/docker-compose.yml)|
|Run container|`docker-compose up --build -d`|`docker-compose up --build -d`|`docker-compose up --build -d`|`docker-compose up --build -d`|
|Stop container|`docker-compose stop`|`docker-compose stop`|`docker-compose stop`|`docker-compose stop`|
|Stop and remove container resources|`docker-compose down`|`docker-compose down`|`docker-compose down`|`docker-compose down`|

### Using docker commands 

|Steps|[Task orchestrator](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/task-orchestrator/README.md)|[Command orchestrator](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/command-orchestrator/README.md)|[Protocol gateway](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/protocol-gateway/README.md)|[Sensor simulator](https://github.com/sbhrwl/microservices/blob/main/commandorchestration/command-orchestration/sensor-simulator/README.md)|
|-----|----------|--------------|--------------------|--------------------|
|Build image|`docker build -t sbhrwldocker/task-orchestrator:latest .`|`docker build -t sbhrwldocker/command-orchestrator:latest .`|`docker build -t sbhrwldocker/protocol-gateway:latest .`|`docker build -t sbhrwldocker/sensor-simulator:latest .`|
|Run container|`docker run -p 9081:9081 -e SERVER_PORT=9081 -e KEYCLOAK_URL=http://host.docker.internal:8080/ -e KEYCLOAK_REALM=master -e KEYCLOAK_CLIENTID=command-orchestrator -e SENSOR_SERVICE_URL=http://host.docker.internal:9082 --name task-orchestrator sbhrwldocker/task-orchestrator:latest`|`docker run -p 9082:9082 -e SERVER_PORT=9082 -e KEYCLOAK_ISSUER_URI=http://localhost:8080/realms/master -e KEYCLOAK_JWK_SET_URI=http://host.docker.internal:8080/realms/master/protocol/openid-connect/certs -e KEYCLOAK_CLIENT_ID=command-orchestrator -e KEYCLOAK_PROVIDER=keycloak -e KAFKA_HOST=host.docker.internal -e KAFKA_PORT=29092 -e KAFKA_SENSOR_REG_TOPIC=command-orchestrations -e CORS_ALLOWED_ORIGINS=http://host.docker.internal:9081 --name command-orchestrator sbhrwldocker/command-orchestrator:latest`|`docker run -p 9083:9083 -e SERVER_PORT=9083 -e MONGO_HOST=host.docker.internal -e MONGO_PORT=27017 -e MONGO_USERNAME=root -e MONGO_PASSWORD=root123 -e KAFKA_HOST=host.docker.internal -e KAFKA_PORT=29092 -e SPRING_KAFKA_CONSUMER_BOOTSTRAP-SERVERS=host.docker.internal:29092 --name protocol-gateway sbhrwldocker/protocol-gateway:latest`|`docker run -p 9084:9084 -e SERVER_PORT=9084 --name sensor-simulator sbhrwldocker/sensor-simulator:latest`|
|Stop container|`docker stop task-orchestrator`|`docker stop command-orchestrator`|`docker stop protocol-gateway`|`docker stop sensor-simulator`|
|Remove container|`docker rm -f task-orchestrator`|`docker rm -f command-orchestrator`|`docker rm -f protocol-gateway`|`docker rm -f sensor-simulator`|
||||||
      
## Test
- Send request from `Task orchestrator`
  - Login `localhost:9081`
  - Fill sensor values and press `send` button.
- Send request from `Command orchestrator`
  - Get Access token
  - request **`POST`** `http://localhost:9083/api/register/sensor`
  - with request body as JSON payload
    ```json
      {
          "sensorId": "sensor123",
          "sensorModel": "ModelX",
          "email": "user@example.com"
      }
    ```
  - Modify `Body -> x-wwww-form-url-encoded`
    ```
    grant_type : password
    client_id  : command-orchestrator (created in Keycloak)
    username  : endpointaccessuser
    password  : password123
    ```
  - Verify at Kafka console
    ```
    kafka-console-consumer --bootstrap-server localhost:9092 --topic command-orchestrations --from-beginning --max-messages 10  
    ```
- `Protocol gateway` creates a document in MongoDB
  - Verify at **MongoDB**
  - Open MongoDB shell
    - Connect: `Please enter a MongoDB connection string (Default: mongodb://localhost/): mongodb://root:root123@localhost:27017/admin`
    - Check available Databses: `show dbs`
    - Swicth to DB: `use commandorchestration`
    - Query documents in the collections: `db.commandorchestrations.find().pretty()`
      - Find a sensor: `db.commandorchestrations.findOne({ sensorId: "sensor789" })`    
    - Delete documents from the collections: `db.commandorchestrations.deleteMany({})`
      - Delete a sensor: `db.commandorchestrations.deleteOne({ sensorId: "sensor789" })`
- `Sensor simulator` sends an email, verify at console of `Sensor simulator`
