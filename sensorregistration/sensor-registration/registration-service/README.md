# Registration service
- [Properties](#properties)
- [How to run](#how-to-run)
- [Test registration service](#test-registration-service)
- [Integrate notification service](#integrate-notification-service)
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
- Container
```
mvn clean package
docker build -t registration-service .
docker run -p 9083:9083 -e SERVER_PORT=9083 -e MONGO_HOST=host.docker.internal -e MONGO_PORT=27017 -e MONGO_USERNAME=root -e MONGO_PASSWORD=root123 -e KAFKA_HOST=host.docker.internal -e KAFKA_PORT=29092 -e SPRING_KAFKA_CONSUMER_BOOTSTRAP-SERVERS=host.docker.internal:29092 --name registration-service registration-service
```
## Test registration service
- Send request from `sensor service`
  - Get Access token
  - request **`POST`** `http://localhost:9083/api/register/sensor`
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
- `registration service` creates a document in MongoDB
  - Verify at **MongoDB**
  - Open MongoDB shell
    - `Please enter a MongoDB connection string (Default: mongodb://localhost/): mongodb://root:root123@localhost:27017/admin`
    - `db.sensorRegistrations.find().pretty()`

## Integrate notification service
- The `notification-service` will be integrated to `registration-service`. 
- The `registration-service` to call the `notification-service's endpoint` after successfully saving a sensor registration to the database.
  - **Add RestTemplate to `registration-service`:** 
    - Use Spring's `RestTemplate` to make the HTTP call to the `notification-service`.
  - **Configure RestTemplate:** 
    - Create a **bean** for `RestTemplate`.
  - **Modify `SensorRegistrationConsumer`:** 
    - Update the consumer to call the `notification-service` after saving to MongoDB.
    - Configure **`notificationServiceUrl`** in `application.properties`
