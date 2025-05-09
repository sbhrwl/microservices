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
  ```
  {
    "correlationId": "123456-abc",
    "requestedBy": "scheduler-service",
    "deviceIds": ["meter-001", "meter-002", "meter-003"],
    "commandType": "SET_TOU",
    "commandParams": {
      "touProfileId": "TOU-2025-TEST"
    }
  } 
  ```
