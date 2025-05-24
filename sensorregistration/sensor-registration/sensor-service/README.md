# Sensor service
- [Properties](#properties)
- [How to run](#how-to-run)
- [Test](#test)
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

## Test
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
