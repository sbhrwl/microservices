# Notification service
- [Properties](#properties)
- [How to run](#how-to-run)
- [Test notification service](#test-notification-service)
## Properties
- [application.properties](src/main/resources/application.properties)
- Configure `mailtrap`
<img src="images/mailtrap.jpg">

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

## Test notification service
- Send request from `sensor service`
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
- Verify **email**