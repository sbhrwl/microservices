# Kafka topic
- [Kafka topic structure](#kafka-topic-structure)
  - [Task requested](#task-requested)
    - [Message format for request](#message-format-for-request)
  - [Task response](#task-response)
    - [Message format for response](#message-format-for-response)
## Task requested
- `task.requested`
  * **Type**: `Topic`
  * **Direction**: `Produce` (by CommandOrchestratorService)
  * **Purpose**: Broadcasts new command requests to downstream systems (TaskOrchestrationService)
  * **Partition Key**: `taskId`
### Message format for request
```json
{
"command_type": "SET_TOU",
"command_params": {
    "tou_profile_id": "TOU-2025-TEST"
},
"device_ids": [
    "meter-001",
    "meter-002",
    "meter-003"
],
"requested_by": "userB"
}
```

## Task response
- `orchestrator.responses`
  * **Type**: `Topic`
  * **Direction**: `Consume` (by CommandOrchestratorService)
  * **Purpose**: Receives final device-level command execution results (via TaskOrchestrationService)
  * **Partition Key**: `commandId` or `deviceId`

### Message format for response
```json
{
  "taskId": "689dce4358cba50614cee678",
  "command_id": "681dce4358cba50614cee672",
  "deviceId": "meter-001",
  "status": "SUCCESS",
  "receivedAt": "2025-05-08T02:01:10Z",
  "responsePayload": {
    "dlmsResultCode": 0,
    "deviceTime": "2025-05-08T02:00:59Z"
  }
}
```

## Consumer group id
- In [`application.properties`](https://github.com/sbhrwl/microservices/blob/main/designingmicroservices/services/commandorchestratorservice/commandorchestrationservice/src/main/resources/application.properties) file, we have defined `spring.kafka.consumer.group-id` property.
  - This property specifies the consumer group ID for Kafka consumers in your application.
- What is a **Consumer Group** in Kafka?
  - A consumer group is a group of Kafka consumers that work together to consume messages from Kafka topics. 
  - Each consumer in the group processes a subset of the partitions in the topic, ensuring that messages are processed in parallel without duplication.

- Purpose of spring.kafka.consumer.group-id=command-orchestrator-group:
  - **Group Identification**
    - This property identifies the consumer group that your application belongs to. In this case, the group is named command-orchestrator-group.
  - **Load Balancing**
    - Kafka ensures that each partition of a topic is consumed by only one consumer in the group. 
    - If multiple instances of your application are running, they will share the workload.
  - **Offset Management**
    - Kafka tracks the offsets (i.e., the position of the last consumed message) for each consumer group. 
    - This allows consumers to resume from where they left off in case of a restart.
- Example:
  - If you have a Kafka topic with 3 partitions and 3 consumers in the command-orchestrator-group, each consumer will process messages from one partition.
  - If a new consumer joins the group, Kafka will rebalance the partitions among the consumers.
