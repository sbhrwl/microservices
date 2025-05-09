# Task Orchestration Service
- [Responsibilities](#responsibilities)
- [Implementation](taskorchestrationservice/README.md)
## Responsibilities
* Subscribing to **command requests** from `task.requested` [Kafka topic](docs/kafkatopic/README.md) (produced by `CommandOrchestratorService`)
* Creating a **task** in the [database](docs/dbschema/README.md), including device list and metadata
* Publishing **task metadata** to [Kafka topic](docs/kafkatopic/README.md) (`task.created`) for downstream processing (command generation)
## Containerise application
- [Docker commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Docker/commands/README.md)
### Build your app
- For Spring Boot use below command to **create a `.jar`** file.
  - `mvn clean package` 
### Create Dockerfile in your project root
- [Dockerfile](Dockerfile)
### Build Docker image
```bash
docker build -t taskorchestratorservice .
```
## Run Docker image as a Docker container
### Option 1
- `docker run` command
  ```bash
  docker run -p 8082:9092 taskorchestratorservice
  ```
### Option 2
- `docker-compose.yml` followed by `docker-compose up`
  ```
  version: "3.9"
  
  services:
    java-app:
      image: taskorchestratorservice        # use the already built image
      ports:
        - "8082:9092"             # map container port to host
      restart: unless-stopped
  ```
## Test
- Send `GET` request from **Postman** `http://localhost:8082/api/tasks`