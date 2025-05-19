# DB schema
- [Document DB based](#document-db-based)
  - [Collection](#collection)
  - [Index](#index)
  - [Benefits](#benefits)
- [Relational schema](#relational-schema)
  - [Table](#table)
  - [Pros and Cons](#pros-and-cons)
## Document DB based
### Collection
- `tasks`
  - Each document represents a full task, including metadata and all devices it targets.
    ```json
    {
      "taskId": "task-7890",
      "commandId": "681dce4358cba50614cee672",
      "commandType": "SET_TOU",
      "scheduledAt": "2025-05-08T02:00:00Z",
      "status": "IN_PROGRESS", // or QUEUED, COMPLETED, FAILED
      "createdAt": "2025-05-08T01:59:00Z",
      "updatedAt": "2025-05-08T02:03:00Z",
      "devices": [
        {
          "deviceId": "meter-001",
          "commandStatus": "SENT",       // or PENDING, SUCCESS, FAILED
          "error": null,
          "lastUpdated": "2025-05-08T02:01:12Z"
        },
        {
          "deviceId": "meter-002",
          "commandStatus": "FAILED",
          "error": "Timeout",
          "lastUpdated": "2025-05-08T02:03:05Z"
        }
      ]
    }
    ```

### Index
* `taskId` (unique, indexed)
* `commanId` (searchable)
* `devices.deviceId` (for fast lookup by device)

### Benefits
* Keeps the task and all device statuses in a **single document** (ideal for fast reads for GET `/tasks/{taskId}`)
* Allows incremental updates per device as responses arrive
* Avoids joins if using MongoDB / Firestore / etc.

## Relational schema
### Table
- `tasks`
  - **Primary Key:** `task_id`
  - **Index:** `correlation_id`

| Column           | Type      | Description                                |
| ---------------- | --------- | ------------------------------------------ |
| `task_id`        | VARCHAR   | Primary key (UUID or generated ID)         |
| `command_id` | VARCHAR   | Correlates with upstream request           |
| `command_type`   | VARCHAR   | Type of DLMS command (e.g. SET\_TOU)       |
| `scheduled_at`   | TIMESTAMP | When command should be executed            |
| `status`         | VARCHAR   | `QUEUED`, `IN_PROGRESS`, `COMPLETED`, etc. |
| `created_at`     | TIMESTAMP | When task was created                      |
| `updated_at`     | TIMESTAMP | Last updated timestamp                     |

- `task_devices`
  - **Foreign Key:** `task_id → tasks.task_id`
  - **Index:** `device_id`, `task_id`

| Column           | Type      | Description                                  |
| ---------------- | --------- | -------------------------------------------- |
| `id`             | SERIAL    | Primary key                                  |
| `task_id`        | VARCHAR   | Foreign key to `tasks.task_id`               |
| `device_id`      | VARCHAR   | Meter/device ID                              |
| `command_status` | VARCHAR   | `PENDING`, `SENT`, `SUCCESS`, `FAILED`, etc. |
| `error_message`  | TEXT      | Error details, if any                        |
| `last_updated`   | TIMESTAMP | Last status update                           |

### Pros and Cons
| Pros                                                | Cons                                           |
| --------------------------------------------------- | ---------------------------------------------- |
| Easy to normalize and enforce referential integrity | Requires joins for retrieving full task status |
| Good for large-scale analytics with SQL             | Updates per device may need transactions       |
