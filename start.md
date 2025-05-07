# Goals of transition
* Motivation for shift to microservices
  * Better scalability?
  * Independent deployments?
  * Fault isolation?
  * Tech stack modernization?
  * Integration with cloud-native platforms?
## Communication between these microservices
* **Event-driven** (e.g., using Pub/Sub to trigger each stage asynchronously), or
* **Request-response** (e.g., one microservice calling the next via REST or gRPC)?
## Approach for desiging microservices
* Services to be bounded strictly by **function** (e.g., `parsing, generating, protocol conversion`)?
  **Pros:**
  * High cohesion and single responsibility.
  * Easier to scale individual pieces (e.g., only scale the Gateway if protocol load is high).
  * Teams can own narrow technical areas.
  **Cons:**
  * Requires more coordination between services.
  * More inter-service communication (potential latency/debug complexity).
  **Best When:**
  * You expect uneven loads across stages.
  * You have clear technical boundaries and DevOps maturity.
* Services to be composed around **use-cases** (e.g., “`Command Lifecycle Service`” handling multiple stages)?
  **Pros:**
  * Fewer services, more end-to-end ownership.
  * Good for early-stage systems or if your team is small.
  * Less orchestration/communication overhead.
  **Cons:**
  * Can grow into a monolith if not managed well.
  * Harder to isolate performance bottlenecks.
  **Best When:**
  * Your use cases are strongly coupled.
  * Simpler ops and release cycles are a priority.
* Best practices
  * Start with **coarser-grained services** and break them down as you scale.
  * Use **domain boundaries** (bounded contexts in DDD) to guide decomposition.
  * Observe runtime behavior before splitting (e.g., if `Command Generator` spikes under load, consider isolating XML creation).
  * Services that interact with **infrastructure or protocols** (e.g., Protocol Gateway) are often better as independent functions.
## Architecture
### Request
```plantuml
@startuml
title Request flow

box "Orchestrator Layer" #LightBlue
  actor CommandOrchestratorService
end box

queue "Kafka: command.orchestrator.requests" as KafkaGFC

box "Task Management Layer" #LightGreen
  participant TaskOrchestrationService
end box

database "Task DB" as TaskDB
queue "Kafka: task.created" as KafkaTask

box "Command Lifecycle Layer" #Orange
  participant CommandLifecycleService
end box

queue "Kafka: command.generated" as KafkaCommand

box "Protocol Layer" #PeachPuff
  participant ProtocolAdapterService
end box

participant "DLMS Device" as Device

== Step 1: Command Published ==
CommandOrchestratorService -> KafkaGFC: Publish (device list)

== Step 2: Task Creation ==
TaskOrchestrationService -> KafkaGFC: Subscribe
TaskOrchestrationService -> TaskDB: Insert task + device list
TaskOrchestrationService -> KafkaTask: Publish (taskId)

== Step 3: Command Generation ==
CommandLifecycleService -> KafkaTask: Subscribe
CommandLifecycleService -> TaskDB: Fetch task + devices
CommandLifecycleService -> KafkaCommand: Publish (XML command)

== Step 4: DLMS PDU Delivery ==
ProtocolAdapterService -> KafkaCommand: Subscribe
ProtocolAdapterService -> Device: Send DLMS PDU

@enduml
```
### Response
```plantuml
@startuml
title Response flow

participant "DLMS Device" as Device
box "Protocol Layer" #FFDAB9
  participant ProtocolAdapterService
end box

queue "Kafka: device.responses" as KafkaResponse

box "Command Lifecycle Layer" #Orange
  participant CommandLifecycleService
end box

queue "Kafka: command.response" as KafkaCommandResponse
database "Task DB" as TaskDB

box "Task Management Layer" #LightGreen
  participant TaskOrchestrationService
end box

queue "Kafka: command.orchestrator.responses" as KafkaOrchestratorResponse

box "Orchestrator Layer" #LightBlue
  actor CommandOrchestratorService
end box

== Step 1: Device Sends Response ==
Device -> ProtocolAdapterService: DLMS PDU response
ProtocolAdapterService -> KafkaResponse: Publish (decoded response)

== Step 2: Process Device Response ==
CommandLifecycleService -> KafkaResponse: Subscribe
CommandLifecycleService -> TaskDB: Update command status
CommandLifecycleService -> KafkaCommandResponse: Publish (parsed metadata)

== Step 3: Response Routing ==
TaskOrchestrationService -> KafkaCommandResponse: Subscribe
TaskOrchestrationService -> KafkaOrchestratorResponse: Push (response summary)

== Step 4: Final Delivery ==
CommandOrchestratorService -> KafkaOrchestratorResponse: Subscribe

@enduml
```

## Services
| **Service Name**               | **Role / Responsibility**                                                               |
| ------------------------------ | --------------------------------------------------------------------------------------- |
| **CommandOrchestratorService** | Accepts commands from upstream apps; sends and receives status/responses                |
| **TaskOrchestrationService**   | Parses incoming requests; creates and manages tasks with list of devices                |
| **CommandLifecycleService**    | Generates device-specific commands; handles device responses and updates task status    |
| **ProtocolAdapterService**     | Converts commands from XML to DLMS PDU and vice versa; interfaces directly with devices |
| **UpstreamIntegrationService** | Pushes parsed and formatted responses back to orchestrator or external systems          |


