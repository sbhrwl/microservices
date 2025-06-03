# Containers
- [Docker commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Docker/commands/README.md)
- [Docker compose commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Docker/dockercompose/README.md)
- [Change to Kafka setup for containers](#change-to-kafka-setup-for-containers)
- [Setting up containers](#setting-up-containers)
  - [Prerequisites](#prerequisites)
  - [Using docker compose](#using-docker-compose)
  - [Using docker commands](#using-docker-commands)
- [Test](#test)
## Setting up containers
### Prerequisites 

| Steps                   | Details                                                                                                                        |
|-------------------------|--------------------------------------------------------------------------------------------------------------------------------|
| application.yml | [application.yml](https://github.com/sbhrwl/microservices/blob/main/analytics/ingestion-service/src/main/resources/application.yml)    |
| Build jar       | `mvn clean package`                                                                                                                    |
| Dockerfile      | [Link](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/sensor-registration/ui-service/Dockerfile)                 |

### Using docker compose

| Steps                  | Details                                                                                                                        |
|------------------------|--------------------------------------------------------------------------------------------------------------------------------|
| docker-compose.yml     | [Link](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/sensor-registration/ui-service/docker-compose.yml) |
| Run container          | `docker-compose up --build -d`                                                                                                 |
| Stop container         | `docker-compose stop`                                                                                                          |
| Stop & remove resources| `docker-compose down`                                                                                                          |

### Using docker commands 

| Steps                  | Details                                                                                                                        |
|------------------------|--------------------------------------------------------------------------------------------------------------------------------|
| Build image            | `docker build -t sbhrwldocker/ui-service:latest .`                                                                             |                    
| Run container          | `docker run -p 9081:9081 -e SERVER_PORT=9081 -e KEYCLOAK_URL=http://host.docker.internal:8080/ -e KEYCLOAK_REALM=master -e KEYCLOAK_CLIENTID=sensor-service -e SENSOR_SERVICE_URL=http://host.docker.internal:9082 --name ui-service sbhrwldocker/ui-service:latest` |
| Stop container         | `docker stop ui-service`      |
| Remove container       | `docker rm -f ui-service`     |
      
## Test
