# Flexibility Hub simulator
- [Properties](#properties)
- [How to run](#how-to-run)
- [Test](#test)
## Properties
- [application.yml](src/main/resources/application.yml)
## How to run
- Create spring boot app with [spring initialiser](https://start.spring.io/)
- Build 
```bash
mvn clean install

mvn clean install -DskipTests
```
- Run
```bash
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
mvn spring-boot:run "-Dspring-boot.run.profiles=prod"
```

## Test
- Push data to Broker
  - `POST`: `http://localhost:8081/api/messages`
  - Payload
    ```json
    {
      "sensorId": "sensor-001",
      "operation": "DIRECT-ON",
      "relayNumber": 2,
      "duration": 30
    }
    ```
## Configuration
- `RabbitMQ` for `dev` and `PubSub` for `prod`
  - RabbitMQ → 1 exchange, 2 queues (request, response)
  - Pub/Sub → 2 topics, 2 subscriptions (request, response)