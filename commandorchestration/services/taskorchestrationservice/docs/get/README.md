# GET
- [Endpoint](#endpoint)
- [Response](#response)
  - [Response codes](#response-codes)
## Endpoint
- `GET /tasks/{taskId}`
  - Retrieve the status and details of a specific task by `taskId`, including per-device command state if available.

## Response
```json
{
  "taskId": "task-7890",
  "correlationId": "req-20250508-1234",
  "commandType": "SET_TOU",
  "scheduledAt": "2025-05-08T02:00:00Z",
  "status": "IN_PROGRESS",
  "devices": [
    {
      "deviceId": "meter-001",
      "commandStatus": "SENT",
      "lastUpdated": "2025-05-08T02:01:12Z"
    },
    {
      "deviceId": "meter-002",
      "commandStatus": "SUCCESS",
      "lastUpdated": "2025-05-08T02:02:15Z"
    },
    {
      "deviceId": "meter-003",
      "commandStatus": "FAILED",
      "error": "Timeout",
      "lastUpdated": "2025-05-08T02:03:05Z"
    }
  ]
}
```

### Response codes

| Code | Description                              |
| ---- | ---------------------------------------- |
| 200  | Task found and returned                  |
| 404  | Task not found                           |
| 500  | Internal server error (e.g., DB failure) |
