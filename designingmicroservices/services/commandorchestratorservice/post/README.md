# POST
- [Endpoint](#endpoint)
- [Request payload](#request-payload)
- [Response](#response)

## Endpoint
| Attribute     | Value                                                   |
| ------------- | ------------------------------------------------------- |
| Method        | `POST`                                                  |
| Endpoint      | `/commands`                                             |
| Description   | Submit a new command request targeting multiple devices |
| Auth Required | Yes (e.g., Bearer token)                                |
| Consumes      | `application/json`                                      |
| Produces      | `application/json`                                      |
| Sync/Async    | Asynchronous — returns taskId immediately               |

## Request payload
```json
{
  "correlationId": "123456-abc",
  "requestedBy": "scheduler-service",
  "deviceIds": ["meter-001", "meter-002", "meter-003"],
  "commandType": "SET_TOU",
  "commandParams": {
    "touProfileId": "TOU-2025-WINTER"
  },
  "scheduledAt": "2025-05-08T02:00:00Z"
}
```

## Response

```json
{
  "taskId": "task-7890",
  "status": "accepted",
  "message": "Command task created and sent for processing."
}
```
