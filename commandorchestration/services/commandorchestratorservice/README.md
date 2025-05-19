# Command Orchestrator Service
- [Responsibilities](#responsibilities)
- [Kafka setup](#kafka-setup)
- [MongoDB setup](#mongodb-setup)
- [ProtoBuf](#protobuf)
- [Implementation](commandorchestratorservice/README.md)
## Responsibilities
* Accepting **command requests** from [upstream applications](docs/post/README.md)
* Creating a record for requested command in [database](docs/dbschema/README.md)
* Sending requests to [Kafka topic](docs/kafkatopic/README.md) (`task.requested`)
* Accept **command responses** from downstream services (via Kafka topic `orchestrator.responses`)

## Kafka setup
- Create a [`docker-compose.yml`](kafkasetup/docker-compose.yml)
- Run docker compose: `docker-compose up -d`
- Varify: `docker-compose logs kafka`
- Log in to container via `Docker Desktop` or `console`
  - `docker exec -it <kafka_container_id_or_name> /bin/bash`
- Verify topics: `kafka-topics --bootstrap-server localhost:9092 --list`
- View messages from a topic
  - If you don’t want to read from the beginning but only new messages, omit the `--from-beginning` flag
  ```
  kafka-console-consumer --bootstrap-server localhost:9092 --topic task.requested --from-beginning --max-messages 10
  ```
## MongoDB setup
- Create a [`docker-compose.yml`](mongodbsetup/docker-compose.yml)
- Run docker compose: `docker-compose up -d`
- Verify MySQL Docker container is running: `docker ps`
- [Download MongoDB shell](https://www.mongodb.com/try/download/shell)
- Open MongoDB shell
  ```bash
  Please enter a MongoDB connection string (Default: mongodb://localhost/): mongodb://root:root123@localhost:27017/admin
  ```
- To confirm the data, query the collection:
  ```
  db.command_requests.find().pretty()
  ```
## ProtoBuf
- [Project](commandorchestratorservicewithproto/README.md) generates proto with `mvn clean install -U` without proto related changes in KafkaProducerService
```markdown
commandorchestratorservice
└── src
    └── main
        ├── java
        │   └── com
        │       └── org
        │           └── cmdorch
        │               ├── document
        │               ├── restcontroller
        │               └── service
        └── proto
            └── command_request.proto   ← Add your .proto file here
```
- Add `dependencies` and `plugin` to `pom.xml`
- Run the project `mvn spring-boot:run`
- Verify generated Protobuf Java classes
  - Check that `.java` files were generated from `.proto` under `target/generated-sources/protobuf/java/`
<img src="images/protobuf.jpg">  

### Changes
- Change 1
  - From: `private final KafkaTemplate<String, CommandRequest> kafkaTemplate;`
  - To:  `private final KafkaTemplate<String, CommandRequestProto> kafkaTemplate;`
- Change 2
  - From: `kafkaTemplate.send(topicName, commandRequest.getcommandId(), commandRequest);`
  - To:
    ```java
    CommandRequestProto proto = CommandRequestProto.newBuilder()
            .setCommandId(commandRequest.getcommandId())
            .setRequestedBy(commandRequest.getRequestedBy())
            .addAllDeviceIds(commandRequest.getDeviceIds())
            .setCommandType(commandRequest.getCommandType())
            .setTouProfileId(commandRequest.getCommandParams().getTouProfileId())
            .build();

    kafkaTemplate.send(topicName, proto.getCommandId(), proto);
    ```
