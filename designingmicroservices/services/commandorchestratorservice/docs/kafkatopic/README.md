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
  "taskId": "task-7890",
  "correlationId": "123456-abc",
  "requestedBy": "scheduler-service",
  "deviceIds": ["meter-001", "meter-002", "meter-003"],
  "commandType": "SET_TOU",
  "commandParams": {
    "touProfileId": "TOU-2025-WINTER"
  },
  "scheduledAt": "2025-05-08T02:00:00Z",
  "createdAt": "2025-05-07T16:00:00Z"
}
```

## Task response
- `orchestrator.responses`
  * **Type**: `Topic`
  * **Direction**: `Consume` (by CommandOrchestratorService)
  * **Purpose**: Receives final device-level command execution results (via TaskOrchestrationService)
  * **Partition Key**: `correlationId` or `deviceId`

### Message format for response
```json
{
  "taskId": "task-7890",
  "deviceId": "meter-001",
  "status": "SUCCESS",
  "receivedAt": "2025-05-08T02:01:10Z",
  "responsePayload": {
    "dlmsResultCode": 0,
    "deviceTime": "2025-05-08T02:00:59Z"
  }
}
```
