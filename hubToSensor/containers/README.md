# Containers
- [Docker commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Docker/commands/README.md)
- [Docker compose commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Docker/dockercompose/README.md)
- [Setting up containers](#setting-up-containers)
  - [Prerequisites](#prerequisites)
  - [Using docker compose](#using-docker-compose)
## Setting up containers
### Prerequisites 

| Steps                   | Ingestion service | Hub service |
|-------------------------|-------------------|-------------|
| application.yml | [application.yml](https://github.com/sbhrwl/microservices/blob/main/gRPC/ingestion-service/src/main/resources/application.yml)    |[application.yml](https://github.com/sbhrwl/microservices/blob/main/gRPC/hub-service/src/main/resources/application.yml)    |
| Build jar       | `mvn clean package`  |`mvn clean package`   |
| Dockerfile      | [Dockerfile](https://github.com/sbhrwl/microservices/blob/main/main/gRPC/ingestion-service/Dockerfile)  |[Dockerfile](https://github.com/sbhrwl/microservices/blob/main/main/gRPC/hub-service/Dockerfile)  |

### Using docker compose

| Steps                  | Ingestion service | Hub service |
|------------------------|-------------------|------------- |
| docker-compose.yml     | [docker-compose.yml](https://github.com/sbhrwl/microservices/blob/main/gRPC/ingestion-service/docker-compose.yml) |[docker-compose.yml](https://github.com/sbhrwl/microservices/blob/main/gRPC/hub-service/docker-compose.yml)|
| Run container          | `docker-compose up --build -d` |`docker-compose up --build -d`  |
| Stop container         | `docker-compose stop`   |`docker-compose stop`   |
| Stop & remove resources| `docker-compose down`   |`docker-compose down`   |
