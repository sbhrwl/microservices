# Sensor service
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
```local
# Server Configuration
server.port=9082

# Keycloak configuration
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/master
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/realms/master/protocol/openid-connect/certs
spring.security.oauth2.client.registration.keycloak.client-id=sensor-service
# spring.security.oauth2.client.registration.keycloak.client-secret=YOUR_CLIENT_SECRET # Only if your client has a secret
spring.security.oauth2.client.registration.keycloak.provider=keycloak
spring.security.oauth2.client.provider.keycloak.issuer-uri=http://localhost:8080/realms/master

# Kafka configuration
spring.kafka.producer.bootstrap-servers=${KAFKA_HOST:localhost}:${KAFKA_PORT:29092}
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
kafka.topic.sensor-registration=sensor-registrations

# CORS configuration
cors.allowed-origins=http://localhost:9081
```

```container
# Server Configuration
server.port=${SERVER_PORT:9082}

# Keycloak configuration
spring.security.oauth2.resourceserver.jwt.issuer-uri=${KEYCLOAK_ISSUER_URI:http://host.docker.internal:8080/realms/master}
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${KEYCLOAK_JWK_SET_URI:http://host.docker.internal:8080/realms/master/protocol/openid-connect/certs}
spring.security.oauth2.client.registration.keycloak.client-id=${KEYCLOAK_CLIENT_ID:sensor-service}
spring.security.oauth2.client.registration.keycloak.provider=${KEYCLOAK_PROVIDER:keycloak}
spring.security.oauth2.client.provider.keycloak.issuer-uri=${KEYCLOAK_ISSUER_URI:http://host.docker.internal:8080/realms/master}
# Uncomment and set secret if needed
# spring.security.oauth2.client.registration.keycloak.client-secret=${KEYCLOAK_CLIENT_SECRET:}

# Kafka configuration
spring.kafka.producer.bootstrap-servers=${KAFKA_HOST:localhost}:${KAFKA_PORT:29092}
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
kafka.topic.sensor-registration=${KAFKA_SENSOR_REG_TOPIC:sensor-registrations}

# CORS configuration
cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://host.docker.internal:9081}
```
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
