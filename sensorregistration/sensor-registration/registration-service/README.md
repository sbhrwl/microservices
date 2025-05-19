# Registration service
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
- Create spring boot app with [spring initialiser](https://start.spring.io/)
- Build 
```bash
mvn clean install

mvn clean install -DskipTests
```
- Run
```bash
mvn spring-boot:run
```

## Test registration service
- Send request from sensor service
  - Get Access token
  - request **`POST`** `http://localhost:9081/api/register/sensor`
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
    client_id  : sensor-service (created in Keycloak)
    username  : endpointaccessuser
    password  : password123
    ```
  - Verify at Kafka console
    ```
    kafka-console-consumer --bootstrap-server localhost:9092 --topic sensor-registrations --from-beginning --max-messages 10  
    ```
- Verify at **MongoDB**
  - Open MongoDB shell
    - `Please enter a MongoDB connection string (Default: mongodb://localhost/): mongodb://root:root123@localhost:27017/admin`
    - `db.sensorRegistrations.find().pretty()`

## Integrate notification service
- The `notification-service` will be integrated to `registration-service`. 
- The `registration-service` to call the `notification-service's endpoint` after successfully saving a sensor registration to the database.
  - **Add RestTemplate to `registration-service`:** 
    - We'll use Spring's `RestTemplate` to make the HTTP call to the `notification-service`.
  - **Configure RestTemplate:** 
    - We'll create a bean for `RestTemplate`.
  - **Modify `SensorRegistrationConsumer`:** 
    - We'll update the consumer to call the `notification-service` after saving to MongoDB.
    - Confuigure notificationServiceUrl in `application.properties`
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
  docker run -p 8081:9081 -e MONGO_HOST=host.docker.internal -e KAFKA_HOST=host.docker.internal -e KAFKA_PORT=29092 command-orchestration
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
