# POST
- [Endpoint](#endpoint)
- [Request payload](#request-payload)
- [Response](#response)

## Endpoint

| Attribute     | Value                                                                                                                |
| ------------- | -------------------------------------------------------------------------------------------------------------------- |
| Method        | `POST`                                                                                                               |
| Endpoint      | `/tasks`                                                                                                             |
| Description   | Accept a list of devices and command type, generate a task ID, persist the task, and publish task metadata to Kafka. |
| Auth Required | Yes                                                                                                                  |
| Consumes      | `application/json`                                                                                                   |
| Produces      | `application/json`                                                                                                   |

## Request Payload
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

## Response
```json
{
  "taskId": "task-7890",
  "status": "ACCEPTED",
  "message": "Task accepted and published"
}
```
