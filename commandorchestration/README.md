# Command orchestration 
- [Introduction](introduction/README.md)
- [Architecture](architecture/README.md)
- [Services](command-orchestration/README.md)
  - [Task orchestrator](command-orchestration/task-orchestrator/README.md)
  - [Command orchestrator](command-orchestration/command-orchestrator/README.md)
  - [Protocol gateway](command-orchestration/protocol-gateway/README.md)
  - [Sensor simulator](command-orchestration/sensor-simulator/README.md)
- [Containers](containers/README.md)
- [Kubernetes](kubernetes/README.md)
- [Helm charts](helmcharts/README.md)
- [Horizontal Pod Autoscalar](hpa/README.md)
- [Deployment across environments](deploymentacrossenv/README.md)
- [Verification](#verification)
## Verification
- Task orchestrator
```
[Task orchestrator] [ntainer#0-0-C-1] c.e.taskservice.service.KafkaProducer    : Message sent successfully for key: task-0011
[Task orchestrator] [ntainer#0-0-C-1] c.e.taskservice.service.TaskService      : Task message sent to Kafka for task ID: task-0011
```
- Command orchestrator
```
[Command orchestrator] [ntainer#0-0-C-1] c.e.c.consumer.TaskMessageConsumer       : Received Kafka message with key: task-0011 from topic: task-topic
[Command orchestrator] [ntainer#0-0-C-1] c.e.c.consumer.TaskMessageConsumer       : Successfully deserialized TaskMessage: taskId: "task-0011"
commandArgs: "arg1"
commandArgs: "arg2"
sensorList: "sensor3"
sensorList: "sensor4"
[Command orchestrator] [ntainer#0-0-C-1] c.e.c.s.CommandOrchestrationService      : Orchestrating commands for Task ID: task-0011 with 2 sensors.
[Command orchestrator] [ntainer#0-0-C-1] c.e.c.s.CommandOrchestrationService      : Saved command to MongoDB for sensor sensor3 with ID: 68467fe0e7b5b607348d7302
[Command orchestrator] [ntainer#0-0-C-1] c.e.c.service.KafkaProducer              : Sending message to Kafka topic: command-topic with key: sensor3
[Command orchestrator] [ntainer#0-0-C-1] c.e.c.service.KafkaProducer              : Message payload (Base64 encoded): Chg2ODQ2N2ZlMGU3YjViNjA3MzQ4ZDczMDISCXRhc2stMDAxMRoHc2Vuc29yMyoEYXJnMSoEYXJnMg==
[Command orchestrator] [ntainer#0-0-C-1] c.e.c.service.KafkaProducer              : Message sent successfully for key: sensor3
[Command orchestrator] [ntainer#0-0-C-1] c.e.c.s.CommandOrchestrationService      : Command for sensor sensor3 is ready for execution.
[Command orchestrator] [ntainer#0-0-C-1] c.e.c.s.CommandOrchestrationService      : Saved command to MongoDB for sensor sensor4 with ID: 68467fe0e7b5b607348d7303
[Command orchestrator] [ntainer#0-0-C-1] c.e.c.service.KafkaProducer              : Sending message to Kafka topic: command-topic with key: sensor4
[Command orchestrator] [ntainer#0-0-C-1] c.e.c.service.KafkaProducer              : Message payload (Base64 encoded): Chg2ODQ2N2ZlMGU3YjViNjA3MzQ4ZDczMDMSCXRhc2stMDAxMRoHc2Vuc29yNCoEYXJnMSoEYXJnMg==
[Command orchestrator] [ntainer#0-0-C-1] c.e.c.service.KafkaProducer              : Message sent successfully for key: sensor4
[Command orchestrator] [ntainer#0-0-C-1] c.e.c.s.CommandOrchestrationService      : Command for sensor sensor4 is ready for execution.
[Command orchestrator] [ntainer#0-0-C-1] c.e.c.s.CommandOrchestrationService      : Finished orchestrating commands for Task ID: task-0011.
```
- Protocol gateway
```
[Protocol gateway] [ntainer#0-0-C-1] c.e.p.service.CommandMessageListener     : Received CommandMessage: ID=68467fe0e7b5b607348d7302, SensorID=sensor3, Type=START, Args=[arg1, arg2]
[Protocol gateway] [ntainer#0-0-C-1] c.e.p.protocol.DlmsProtocolConverter     : Converting command with ID 68467fe0e7b5b607348d7302 for DLMS protocol
[Protocol gateway] [ntainer#0-0-C-1] c.e.p.service.CommandMessageListener     : Converted command bytes: 68467fe0e7b5b607348d7302
[Protocol gateway] [ntainer#0-0-C-1] c.e.p.service.CommandMessageListener     : Sensor simulator response: Simulated sensor received payload of length 24
[Protocol gateway] [ntainer#0-0-C-1] c.e.p.service.CommandMessageListener     : Received CommandMessage: ID=68467fe0e7b5b607348d7303, SensorID=sensor4, Type=START, Args=[arg1, arg2]
[Protocol gateway] [ntainer#0-0-C-1] c.e.p.protocol.DlmsProtocolConverter     : Converting command with ID 68467fe0e7b5b607348d7303 for DLMS protocol
[Protocol gateway] [ntainer#0-0-C-1] c.e.p.service.CommandMessageListener     : Converted command bytes: 68467fe0e7b5b607348d7303
[Protocol gateway] [ntainer#0-0-C-1] c.e.p.service.CommandMessageListener     : Sensor simulator response: Simulated sensor received payload of length 24
```
- Sensor simulator
```
[Sensor simulator] [ntainer#0-0-C-1] c.e.s.c.SensorSimulatorController        : Received sensor simulation request with payload: 68467fe0e7b5b607348d7302
[Sensor simulator] [ntainer#0-0-C-1] c.e.s.c.SensorSimulatorController        : Received sensor simulation request with payload: 68467fe0e7b5b607348d7303
```
