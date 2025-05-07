# GET
- [Endpoint](#endpoint)
- [Response](#response)
  - [Response codes](#response-codes)
## Endpoint
- `GET /commands/{taskId}`
  - `taskId`: Unique ID of the command task as path variable

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

### Response codes

| Code | Description                              |
| ---- | ---------------------------------------- |
| 200  | Task for creating commands found and returned |
| 404  | Task for creating commands not found          |
| 500  | Internal server error                         |
