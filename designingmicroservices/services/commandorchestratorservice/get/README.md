# GET
- [Endpoint](#endpoint)
- [Response](#response)
## Endpoint
- `/commands/{taskId}`
  - `taskId`: Unique ID of the command task as path variable

| Attribute     | Value                                       |
| ------------- | ------------------------------------------- |
| Method        | `GET`                                       |
| Endpoint      | `/commands/{taskId}`                        |
| Description   | Fetch the status of a specific command task |
| Auth Required | Yes                                         |
| Produces      | `application/json`                          |

## Response
```json
{
  "taskId": "task-7890",
  "correlationId": "123456-abc",
  "requestedBy": "scheduler-service",
  "commandType": "SET_TOU",
  "scheduledAt": "2025-05-08T02:00:00Z",
  "deviceStatuses": [
    {
      "deviceId": "meter-001",
      "status": "SUCCESS",
      "lastUpdated": "2025-05-08T02:01:10Z"
    },
    {
      "deviceId": "meter-002",
      "status": "PENDING",
      "lastUpdated": "2025-05-08T02:00:00Z"
    },
    {
      "deviceId": "meter-003",
      "status": "FAILED",
      "lastUpdated": "2025-05-08T02:01:15Z",
      "error": "Device unreachable"
    }
  ]
}
```
