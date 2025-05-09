# Command Orchestrator Service
## How to run
- Build 
```bash
mvn clean install

mvn clean install -DskipTests
```
- Run
```bash
mvn spring-boot:run
```

## Test
- request "POST" `localhost:9999/api/commands`
- with request body as JSON payload
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
