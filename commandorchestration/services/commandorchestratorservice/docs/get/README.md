# GET
- [Endpoint](#endpoint)
- [Response](#response)
  - [Response codes](#response-codes)
## Endpoint
- `GET /commands/{commandId}`

## Response
```json
{
  "commandId": "cmd-123456",
  "deviceId": "meter-001",
  "commandId": "123456-abc",
  "commandType": "SET_TOU",
  "status": "SUCCESS",
  "issuedAt": "2025-05-08T02:00:00Z",
  "lastUpdated": "2025-05-08T02:01:10Z",
  "error": null
}
```

```json
{
  "taskId": "task-7890",
  "commandId": "123456-abc",
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
