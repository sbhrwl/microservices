# Command Orchestrator Service
- [Properties](#properties)
- [How to run](#how-to-run)
- [Test](#test)
- [Containerise application](#containerise-application)
  - [Build your app](#build-your-app)
  - [Create Dockerfile in your project root](#create-dockerfile-in-your-project-root)
  - [Build Docker image](#build-docker-image)
  - [Run Docker image as a Docker container](#run-docker-image-as-a-docker-container)
  - [Test container](#test-container)
## Properties
- [application.properties](src/main/resources/application.properties)
## How to run
- Build 
```bash
mvn clean install

mvn clean install -DskipTests
```
- Run
```bash
mvn spring-boot:run
```

## Test
- request "POST" `localhost:9081/api/commands`
- with request body as JSON payload
  ```json
  {
    "command_type": "SET_TOU",
    "command_params": {
      "tou_profile_id": "TOU-2025-TEST"
    },
    "device_ids": [
      "meter-001",
      "meter-002",
      "meter-003"
    ],
    "requested_by": "userA"
  } 
  ```

## Containerise application
- [Docker commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Docker/commands/README.md)
### Build your app
- For Spring Boot use below command to **create a `.jar`** file.
  - `mvn clean package` 
### Create Dockerfile in your project root
- [Dockerfile](Dockerfile)
### Build Docker image
```bash
docker build -t command-orchestration .

# For pushing image to Docker registry, build the image with the full tag directly
docker build -t sbhrwldocker/command-orchestration:v1.0.0 .

docker build -t sbhrwldocker/command-orchestration:latest .
```
## Run Docker image as a Docker container
### Option 1
- `docker run` command
  - `MONGO_HOST=host.docker.internal`: 
    - This sets the environment variable `MONGO_HOST` to `host.docker.internal`, which **from inside the container points to your host machine**, where MongoDB is running
  - `KAFKA_HOST=host.docker.internal`:
    - This sets the environment variable `KAFKA_HOST` to `host.docker.internal`, which **from inside the container points to your host machine**, where KAFKA is running
  ```bash
    docker run -p 9081:9081 -e SERVER_PORT=9081 -e MONGO_HOST=host.docker.internal -e MONGO_PORT=27017 -e MONGO_USERNAME=root -e MONGO_PASSWORD=root123 -e KAFKA_HOST=host.docker.internal -e KAFKA_PORT=29092 --name command-orchestration command-orchestration
  ```
### Option 2
- `docker-compose.yml` followed by `docker-compose up`
  ```
  version: "3.9"
  
  services:
    java-app:
      image: commandorchestratorservice        # use the already built image
      ports:
        - "8081:9091"             # map container port to host
      restart: unless-stopped
  ```
## Test container
- Send `POST` request from **Postman** `localhost:8081/api/commands`
  ```json
  {
    "requested_by": "userD",
    "device_ids": [
      "meter-001",
      "meter-002",
      "meter-003"
    ],
    "command_type": "SET_TOU",
    "command_params": {
      "tou_profile_id": "TOU-2025-TEST"
    }
  }
  ```