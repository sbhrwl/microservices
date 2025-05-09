# Task Orchestration Service
## Responsibilities
* Subscribing to **command requests** from `task.requested` [Kafka topic](docs/kafkatopic/README.md) (produced by `CommandOrchestratorService`)
* Creating a **task** in the [database](docs/dbschema/README.md), including device list and metadata
* Publishing **task metadata** to [Kafka topic](docs/kafkatopic/README.md) (`task.created`) for downstream processing (command generation)
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
- `GET` `localhost:9999/api/tasks`
- Kafka consumer
  - Check console for consumer logs
    ```
    2025-05-09 20:52:20.368  INFO 22532 --- [ntainer#0-0-C-1] c.o.t.service.KafkaConsumerService       : Received message from 'task.requested': {"command_id":"681e40d174e31943e23e7b56","requested_by":"userD","device_ids":["meter-001","meter-002","meter-003"],"command_type":"SET_TOU","command_params":{"tou_profile_id":"TOU-2025-TEST"}}
    ```