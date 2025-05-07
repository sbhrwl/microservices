# Kafka topic
- [Subscribe to topic `task.requested`](#subscribe-to-topic-task.requested)
  - [Actions performed](#actions-performed)
- [Publish to topic `task.created`](#publish-to-topic-task.created)
## Subscribe to topic task.requested
- `task.requested`
  * **Type**: `Topic`
  * **Direction**: `Produce` (by CommandOrchestratorService)
  * **Purpose**: Broadcasts new command requests to downstream systems (TaskOrchestrationService)
  * **Partition Key**: `taskId`
## Message format for request
- **Incoming payload** produced by `CommandOrchestratorService`
  ```json
  {
    "correlationId": "req-20250508-1234",
    "commandType": "SET_TOU",
    "scheduledAt": "2025-05-08T02:00:00Z",
    "devices": [
      "meter-001",
      "meter-002",
      "meter-003"
    ],
    "requestedBy": "CommandOrchestratorService"
  }
  ```

### Actions performed
* Create a new `task` entry in internal DB
* Assign a `taskId` (e.g., `task-7890`)
* Persist device list and metadata
* Publish to `task.created`

## Publish to topic task.created
- **Outgoing payload** for `CommandLifecycleService`
  ```json
  {
    "taskId": "task-7890",
    "correlationId": "req-20250508-1234",
    "commandType": "SET_TOU",
    "scheduledAt": "2025-05-08T02:00:00Z"
  }
  ```
