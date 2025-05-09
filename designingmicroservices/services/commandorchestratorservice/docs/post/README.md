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

## Response

```json
{
  "command_id": "681dce4358cba50614cee672",
  "taskId": "678dce4358cba50614cee698",
  "status": "accepted",
  "message": "Command task created and sent for processing."
}
```
