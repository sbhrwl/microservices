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
    "command_id": "681dce4358cba50614cee672",
    "requested_by": "userA",
    "device_ids": [
      "meter-001",
      "meter-002",
      "meter-003"
    ],
    "command_type": "SET_TOU",
    "command_params": {
      "tou_profile_id": "TOU-2025-TEST"
    }
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
    "commandId": "681dce4358cba50614cee672",
    "commandType": "SET_TOU",
    "scheduledAt": "2025-05-08T02:00:00Z"
  }
  ```
