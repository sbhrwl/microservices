# Containers
- [Docker commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Docker/commands/README.md)

|Steps|ui service|sensor service|registration service|notification service|
|-----|----------|--------------|--------------------|--------------------|
|Build jar|`mvn clean package`|`mvn clean package`|`mvn clean package`|`mvn clean package`|
|Dockerfile|[Dockerfile](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/sensor-registration/ui-service/Dockerfile)|[Dockerfile](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/sensor-registration/sensor-service/Dockerfile)|[Dockerfile](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/sensor-registration/registration-service/Dockerfile)|[Dockerfile](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/sensor-registration/notification-service/Dockerfile)|
|Build image|`docker build -t sbhrwldocker/ui-service:latest .`|`docker build -t sbhrwldocker/sensor-service:latest .`|`docker build -t sbhrwldocker/registration-service:latest .`|`docker build -t sbhrwldocker/notification-service:latest .`|
|docker-compose.yml|[docker-compose.yml](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/sensor-registration/ui-service/docker-compose.yml)|[docker-compose.yml](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/sensor-registration/sensor-service/docker-compose.yml)|[docker-compose.yml](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/sensor-registration/registration-service/docker-compose.yml)|[docker-compose.yml](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/sensor-registration/notification-service/docker-compose.yml)|
|Run container|`docker-compose up --build`|`docker-compose up --build`|`docker-compose up --build`|`docker-compose up --build`|

## Docker run
- ui service
```
docker run --name sensor-service -p 9082:9082 -e SERVER_PORT=9082 -e KEYCLOAK_ISSUER_URI=http://host.docker.internal:8080/realms/master -e KEYCLOAK_CLIENT_ID=sensor-service -e KAFKA_HOST=host.docker.internal -e KAFKA_PORT=29092 -e KAFKA_SENSOR_REG_TOPIC=sensor-registrations -e CORS_ALLOWED_ORIGINS=http://host.docker.internal:9081 sensor-service:latest
```
- sensor service
```
docker run --name sensor-service -p 9082:9082 -e SERVER_PORT=9082 -e KEYCLOAK_ISSUER_URI=http://host.docker.internal:8080/realms/master -e KEYCLOAK_CLIENT_ID=sensor-service -e KAFKA_HOST=host.docker.internal -e KAFKA_PORT=29092 -e KAFKA_SENSOR_REG_TOPIC=sensor-registrations -e CORS_ALLOWED_ORIGINS=http://host.docker.internal:9081 sensor-service:latest
```
- registration service
```
docker run --name registration-service -p 9083:9083 -e SERVER_PORT=9083 -e KAFKA_HOST=host.docker.internal -e KAFKA_PORT=29092 -e NOTIFICATION_SERVICE_URL=http://host.docker.internal:9084 registration-service:latest
```
- notification service
```
docker run --name sensor-service -p 9082:9082 -e SERVER_PORT=9082 -e KEYCLOAK_ISSUER_URI=http://host.docker.internal:8080/realms/master -e KEYCLOAK_CLIENT_ID=sensor-service -e KAFKA_HOST=host.docker.internal -e KAFKA_PORT=29092 -e KAFKA_SENSOR_REG_TOPIC=sensor-registrations -e CORS_ALLOWED_ORIGINS=http://host.docker.internal:9081 sensor-service:latest
```

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
