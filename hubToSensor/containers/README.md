# Containers
- [Docker commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Docker/commands/README.md)
- [Docker compose commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Docker/dockercompose/README.md)
- [Setting up containers](#setting-up-containers)
  - [Prerequisites](#prerequisites)
  - [Using docker compose](#using-docker-compose)
## Setting up containers
### Prerequisites 

| Steps                   | Flexibility hub simulator | Flexibility bridge |
|-------------------------|-------------------|-------------|
| application.yml | [application.yml](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/hub-to-sensor/flexibility-hub-simulator/src/main/resources/application.yml)    |[application.yml](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/hub-to-sensor/flexibility-bridge-service/src/main/resources/application.yml)    |
| Build jar       | `mvn clean package`  |`mvn clean package`   |
| Dockerfile      | [Dockerfile](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/hub-to-sensor/flexibility-hub-simulator/Dockerfile)  |[Dockerfile](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/hub-to-sensor/flexibility-bridge-service/Dockerfile)  |

### Using docker compose

| Steps                  | Flexibility hub simulator | Flexibility bridge |
|------------------------|-------------------|------------- |
| docker-compose.yml     | [docker-compose.yml](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/hub-to-sensor/flexibility-hub-simulator/docker-compose.yml) |[docker-compose.yml](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/hub-to-sensor/flexibility-bridge-service/docker-compose.yml)|
| Run container          | `docker-compose up --build -d` |`docker-compose up --build -d`  |
| Stop container         | `docker-compose stop`   |`docker-compose stop`   |
| Stop & remove resources| `docker-compose down`   |`docker-compose down`   |
