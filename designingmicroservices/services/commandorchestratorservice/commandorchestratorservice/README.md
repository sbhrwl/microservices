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
- [application.properties](src\main\resources\application.properties)
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
- request "POST" `localhost:9999/api/commands`
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
docker build -t commandorchestratorservice .
```
## Run Docker image as a Docker container
### Option 1
- `docker run` command
  ```bash
  docker run -p 8081:9091 commandorchestratorservice
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