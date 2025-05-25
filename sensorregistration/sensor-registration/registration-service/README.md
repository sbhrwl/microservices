# Registration service
- [Properties](#properties)
- [How to run](#how-to-run)
- [Test](#test)
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
    - Connect: `Please enter a MongoDB connection string (Default: mongodb://localhost/): mongodb://root:root123@localhost:27017/admin`
    - Check available Databses: `show dbs`
    - Swicth to DB: `use sensorregistration`
    - Query documents in the collections: `db.sensorRegistrations.find().pretty()`
      - Find a sensor: `db.sensorRegistrations.findOne({ sensorId: "sensor789" })`    
    - Delete documents from the collections: `db.sensorRegistrations.deleteMany({})`
      - Delete a sensor: `db.sensorRegistrations.deleteOne({ sensorId: "sensor789" })`

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
