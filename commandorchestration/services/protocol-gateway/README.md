# Protocol gateway
- Generate the protos:
```
mvn clean compile
```
- Build the project:
```bash
mvn clean install
```
- Run the application:
```bash
mvn spring-boot:run
```

## Test

## Explanation
- The Kafka listener (CommandMessageListener) receives a message.
- It deserializes the message to a CommandMessage object.
- It uses the command type inside the message to select the correct ProtocolConverter (dlms, lorawan, etc.).
- It then converts the command payload accordingly.
