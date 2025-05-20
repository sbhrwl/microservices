# Containers
- [Docker commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Docker/commands/README.md)

|Steps|ui service|sensor service|registration service|notification service|
|-----|----------|--------------|--------------------|--------------------|
|Build jar|`mvn clean package`|`mvn clean package`|`mvn clean package`|`mvn clean package`|
|Dockerfile|[Dockerfile](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/sensor-registration/ui-service/Dockerfile)|[Dockerfile](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/sensor-registration/sensor-service/Dockerfile)|[Dockerfile](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/sensor-registration/registration-service/Dockerfile)|[Dockerfile](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/sensor-registration/notification-service/Dockerfile)|
|Build image|`docker build -t sbhrwldocker/ui-service:latest .`|`docker build -t sbhrwldocker/sensor-service:latest .`|`docker build -t sbhrwldocker/registration-service:latest .`|`docker build -t sbhrwldocker/notification-service:latest .`|
|Run container|`docker run -p 8081:9081 -e MONGO_HOST=host.docker.internal -e KAFKA_HOST=host.docker.internal -e KAFKA_PORT=29092 ui-service`|`docker run -p 8082:9082 -e MONGO_HOST=host.docker.internal -e KAFKA_HOST=host.docker.internal -e KAFKA_PORT=29092 sensor-service`|`docker run -p 8083:9083 -e MONGO_HOST=host.docker.internal -e KAFKA_HOST=host.docker.internal -e KAFKA_PORT=29092 registration-service`|`docker run -p 8084:9084 -e MONGO_HOST=host.docker.internal -e KAFKA_HOST=host.docker.internal -e KAFKA_PORT=29092 notification-service`|
|Test|Log in to UI||||

## Test ui service

## Test sensor service
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

## Test registration service

## Test notification service
