# Database Schema
## Document DB based
- Each document represents the latest status of a single command sent to a device:
```json
{
  "deviceId": "meter-001",
  "commandId": "cmd-123456",
  "correlationId": "123456-abc",
  "commandType": "SET_TOU",
  "issuedAt": "2025-05-08T02:00:00Z",
  "status": "SUCCESS",
  "lastUpdated": "2025-05-08T02:01:10Z",
  "error": null
}
```

* `payloadSummary`: optional JSON blob (e.g., registers set, OBIS codes)
* `responseRaw`: optional decoded response string (for troubleshooting)
