# ActiveMQ Spring Boot application with message sent as a JSON payload
- [Project structure](#project-structure)
- [Components used](#components-used)
- [Setup ActiveMQ](#setup-activemq)
- [How to run](#how-to-run)
## Project structure
- This project demonstrates a simple Spring Boot application that integrates with ActiveMQ for message publishing and consumption. 
- It includes REST endpoints for publishing messages and a consumer component for processing incoming messages.
- **`DemoApplication.java`**: The main entry point of the Spring Boot application.
- **`PublishController.java`**: A REST controller that provides an endpoint to publish messages to the ActiveMQ queue.
- **`MessageConsumer.java`**: A component that listens to messages from the ActiveMQ queue and processes them.
- **`SystemMessage.java`**: A model class representing the structure of the messages being sent and received.
- **`JmsConfig.java`**: Configuration class for setting up the JMS listener container factory.
- **`application.properties`**: Configuration file for ActiveMQ connection details.

## Components used
- **Spring Boot**: Provides the framework for building the application.
- **Spring JMS**: Used for integrating with ActiveMQ.
- **ActiveMQ**: A message broker for sending and receiving messages.
- **SLF4J**: Logging framework used for logging messages.
  - `application.properties`
```
logging.level.org.springframework.jms=DEBUG
logging.level.org.apache.activemq=DEBUG
```
## Setup ActiveMQ
- Create a [docker compose](docker-compose.yml) file
- run docker compose
  - This will launch ActiveMQ with the same configuration as the manual `docker run` command
```
docker-compose up -d
```
- Verify ActiveMQ Docker container is running
```
docker ps
```

|Port | Purpose | How to Access|
|---- | ------ | --------|
|8161 | Web Console (HTTP) | browser → http://localhost:8161/admin|

- Update the [`application.properties`](src/main/resources/application.properties) file if the broker URL, username, or password differs.
## How to run
- Build
```
mvn clean install
```
- Run
```
mvn spring-boot:run
```

- API
- **Postman**
  - URL: `http://localhost:8080/publishMessage`
  - Set request body (JSON)
    ```json
    {
        "source": "External System",
        "message": "Pushing a message"
    }
    ```
- **Powershell**
  ```
  Invoke-RestMethod -Uri http://localhost:8080/publishMessage -Method POST -ContentType "application/json" -Body '{"source":"External System","message":"Pushing a message"}'

  Invoke-RestMethod -Uri http://localhost:8080/publishMessage -Method POST -ContentType "application/json" -Body '{
    "source": "External System",
    "message": "Pushing a message"
  }'
  ```
- Verify publishing
```
02-1:1:6,started=true,closed=false} java.lang.Object@5bdc6f02
2025-04-28T12:36:53.394+03:00 DEBUG 31952 --- [nio-8080-exec-1] o.s.j.c.CachingConnectionFactory         : Registering cached JMS MessageProducer for destination [queue://IEC20_IN_1]: ActiveMQMessageProducer { value=ID:FIJYVNB23072-65136-1745833000202-1:1:6:1 }
2025-04-28T12:36:53.402+03:00 DEBUG 31952 --- [nio-8080-exec-1] o.springframework.jms.core.JmsTemplate   : Sending created message: ActiveMQObjectMessage {commandId = 0, responseRequired = false, messageId = null, originalDestination = null, originalTransactionId = null, producerId = null, destination = null, transactionId = null, deliveryTime = 0, expiration = 0, timestamp = 0, arrival = 0, brokerInTime = 0, brokerOutTime = 0, correlationId = null, replyTo = null, persistent = false, type = null, priority = 0, groupID = null, groupSequence = 0, targetConsumerId = null, compressed = false, userID = null, content = org.apache.activemq.util.ByteSequence@5f5007, marshalledProperties = null, dataStructure = null, redeliveryCounter = 0, size = 0, properties = null, readOnlyProperties = false, readOnlyBody = false, droppable = false, jmsXGroupFirstForConsumer = false}
```
- Verify consumer
```
2025-04-30T14:36:36.225+03:00 DEBUG 12372 --- [ntContainer#0-1] o.s.j.l.DefaultMessageListenerContainer  : Received message of type [class org.apache.activemq.command.ActiveMQObjectMessage] from consumer [ActiveMQMessageConsumer { value=ID:FIJYVNB23072-59739-1746012948472-1:1:1:1, started=true }] of session [Cached JMS Session: ActiveMQSession {id=ID:FIJYVNB23072-59739-1746012948472-1:1:1,started=true,closed=false} java.lang.Object@5be0f900]
2025-04-30T14:36:36.231+03:00 DEBUG 12372 --- [ntContainer#0-1] .s.j.l.a.MessagingMessageListenerAdapter : Processing [LazyResolutionMessage [rawMessage=ActiveMQObjectMessage {commandId = 8, responseRequired = true, messageId = ID:FIJYVNB23072-59739-1746012948472-1:1:2:1:2, originalDestination = null, originalTransactionId = null, producerId = ID:FIJYVNB23072-59739-1746012948472-1:1:2:1, destination = queue://IEC20_IN_1, transactionId = null, deliveryTime = 0, expiration = 0, timestamp = 1746012996208, arrival = 0, brokerInTime = 1746012996207, brokerOutTime = 1746012996207, correlationId = null, replyTo = null, persistent = true, type = null, priority = 4, groupID = null, groupSequence = 0, targetConsumerId = null, compressed = false, userID = null, content = org.apache.activemq.util.ByteSequence@e156717, marshalledProperties = null, dataStructure = null, redeliveryCounter = 0, size = 0, properties = null, readOnlyProperties = true, readOnlyBody = true, droppable = false, jmsXGroupFirstForConsumer = false}]]
2025-04-30T14:36:36.235+03:00  INFO 12372 --- [ntContainer#0-1] c.e.d.c.component.MessageConsumer        : Message recieved. SystemMessage [source=External System, message=Pushing a message]
```
