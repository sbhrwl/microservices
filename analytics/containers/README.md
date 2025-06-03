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
| Dockerfile      | [Dockerfile](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/sensor-registration/ui-service/Dockerfile)                 |

### Using docker compose

| Steps                  | Details                                                                                                                        |
|------------------------|--------------------------------------------------------------------------------------------------------------------------------|
| docker-compose.yml     | [docker-compose.yml](https://github.com/sbhrwl/microservices/blob/main/analytics/ingestion-service/docker-compose.yml) |
| Run container          | `docker-compose up --build -d`                                                                                                 |
| Stop container         | `docker-compose stop`                                                                                                          |
| Stop & remove resources| `docker-compose down`                                                                                                          |

### Using docker commands 

| Steps                  | Details                                                                                                                        |
|------------------------|--------------------------------------------------------------------------------------------------------------------------------|
| Build image            | `docker build -t sbhrwldocker/ingestion-service:latest .`                                                                             |                    
| Run container          | `docker run -p 9081:9081 -e SERVER_PORT=9081 -e ACTIVEMQ_BROKER_URL=tcp://192.168.0.102:61616 -e ACTIVEMQ_USER=admin -e ACTIVEMQ_PASSWORD=admin -e ACTIVEMQ_DEFAULT_DESTINATION=power-quality -e INFLUXDB_HOST=http://192.168.0.102:8086 -e INFLUXDB_DATABASE=power_quality -e INFLUXDB_USERNAME=admin -e INFLUXDB_PASSWORD=admin123 -e INFLUXDB_SINGLE_MEASUREMENT=true sbhrwldocker/ingestion-service:latest` |
| Stop container         | `docker stop ui-service`      |
| Remove container       | `docker rm -f ui-service`     |
      
## Test
