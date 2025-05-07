# GET
Here’s a clear API contract for the `GET /tasks/{taskId}` endpoint, which is exposed by **TaskOrchestrationService** to let upstream services (like `CommandOrchestratorService`) or users query task status and device-level progress.

---

## 🔎 `GET /tasks/{taskId}`

### ✅ Purpose

Retrieve the status and details of a specific task by `taskId`, including per-device command state if available.

---

### 🔗 Endpoint

```
GET /tasks/{taskId}
```

### 🔄 Path Parameters

| Parameter | Type   | Description                   |
| --------- | ------ | ----------------------------- |
| taskId    | string | Unique identifier of the task |

---

### 📥 Example Request

```
GET /tasks/task-7890
```

---

### 📤 Response: 200 OK

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

---

### 🔁 Response Codes

| Code | Description                              |
| ---- | ---------------------------------------- |
| 200  | Task found and returned                  |
| 404  | Task not found                           |
| 500  | Internal server error (e.g., DB failure) |

---

Would you like me to define the **internal schema** or collection layout this endpoint will use?
