# ActiveMQ Spring Boot application with message sent as query parameter
- [Setup ActiveMQ](#setup-activemq)
- [How to run](#how-to-run)
- The REST API is defined in ActiveMQController.
- `POST` `/v1/publish?message=<your_message>`
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
  - The application will start on port `2012` configured in [`application.yaml`](src/main/resources/application.yaml).
  - **Publish API**
    - URL: `http://localhost:2012/`
    - Endpoint: `http://localhost:2012/publish`
    - Query parameter `http://localhost:2012/publish?message=HelloActiveMQ`
- **Postman**
  - POST request: `http://localhost:2012/publish?message=HelloActiveMQ`
- **Powershell**
  ```
  Invoke-WebRequest -Uri "http://localhost:2012/publish?message=HelloActiveMQ" -Method POST
  ```
- **curl**
  ```
  curl --location --request POST 'http://localhost:2012/publish?message=HelloActiveMQ'
  ```
- Verify publishing
  - Check the ActiveMQ console (usually at http://localhost:8161/admin) to verify the message was sent to the IEC20_IN_1 queue.
    ```
    2025-04-30T14:22:14.060+03:00 DEBUG 45304 --- [nio-8080-exec-3] o.springframework.jms.core.JmsTemplate   : Sending created message: ActiveMQObjectMessage {commandId = 0, responseRequired = false, messageId = null, originalDestination = null, originalTransactionId = null, producerId = null, destination = null, transactionId = null, deliveryTime = 0, expiration = 0, timestamp = 0, arrival = 0, brokerInTime = 0, brokerOutTime = 0, correlationId = null, replyTo = null, persistent = false, type = null, priority = 0, groupID = null, groupSequence = 0, targetConsumerId = null, compressed = false, userID = null, content = org.apache.activemq.util.ByteSequence@7379df2d, marshalledProperties = null, dataStructure = null, redeliveryCounter = 0, size = 0, properties = null, readOnlyProperties = false, readOnlyBody = false, droppable = false, jmsXGroupFirstForConsumer = false}
    ```
- Verify consumer
  - The consumer logs the received message. 
  - After sending the message, check the console where your Spring Boot application is running.
    ```
    2025-04-30T14:22:14.085+03:00 DEBUG 45304 --- [ntContainer#0-1] o.s.j.l.DefaultMessageListenerContainer  : Received message of type [class org.apache.activemq.command.ActiveMQObjectMessage] from consumer [ActiveMQMessageConsumer { value=ID:FIJYVNB23072-51776-1746012074966-1:1:1:1, started=true }] of session [Cached JMS Session: ActiveMQSession {id=ID:FIJYVNB23072-51776-1746012074966-1:1:1,started=true,closed=false} java.lang.Object@7959a6b2]
    2025-04-30T14:22:14.097+03:00 DEBUG 45304 --- [ntContainer#0-1] .s.j.l.a.MessagingMessageListenerAdapter : Processing [LazyResolutionMessage [rawMessage=ActiveMQObjectMessage {commandId = 8, responseRequired = true, messageId = ID:FIJYVNB23072-51776-1746012074966-1:1:2:1:2, originalDestination = null, originalTransactionId = null, producerId = ID:FIJYVNB23072-51776-1746012074966-1:1:2:1, destination = queue://IEC20_IN_1, transactionId = null, deliveryTime = 0, expiration = 0, timestamp = 1746012134061, arrival = 0, brokerInTime = 1746012134056, brokerOutTime = 1746012134056, correlationId = null, replyTo = null, persistent = true, type = null, priority = 4, groupID = null, groupSequence = 0, targetConsumerId = null, compressed = false, userID = null, content = org.apache.activemq.util.ByteSequence@13663d19, marshalledProperties = null, dataStructure = null, redeliveryCounter = 0, size = 0, properties = null, readOnlyProperties = true, readOnlyBody = true, droppable = false, jmsXGroupFirstForConsumer = false}]]
    2025-04-30T14:22:14.104+03:00  INFO 45304 --- [ntContainer#0-1] c.e.d.c.component.MessageConsumer        : Message recieved. SystemMessage [source=External System, message=Pushing a message]
    ```
