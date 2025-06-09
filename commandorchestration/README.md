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
2025-06-09T09:31:59.607+03:00  INFO 10664 --- [nio-9081-exec-3] c.e.taskservice.service.KafkaProducer    : Message sent successfully for key: task-0011
2025-06-09T09:31:59.607+03:00  INFO 10664 --- [nio-9081-exec-3] c.e.taskservice.service.TaskService      : Task message sent to Kafka for task ID: task-0011
```
- Command orchestrator
```
2025-06-09T09:32:00.683+03:00  INFO 34476 --- [Command orchestrator] [ntainer#0-0-C-1] c.e.c.service.KafkaProducer              : Message sent successfully for key: sensor3
2025-06-09T09:32:00.684+03:00  INFO 34476 --- [Command orchestrator] [ntainer#0-0-C-1] c.e.c.s.CommandOrchestrationService      : Command for sensor sensor3 is ready for execution.
2025-06-09T09:32:00.697+03:00  INFO 34476 --- [Command orchestrator] [ntainer#0-0-C-1] c.e.c.s.CommandOrchestrationService      : Saved command to MongoDB for sensor sensor4 with ID: 68467fe0e7b5b607348d7303
2025-06-09T09:32:00.698+03:00  INFO 34476 --- [Command orchestrator] [ntainer#0-0-C-1] c.e.c.service.KafkaProducer              : Sending message to Kafka topic: command-topic with key: sensor4
2025-06-09T09:32:00.699+03:00  INFO 34476 --- [Command orchestrator] [ntainer#0-0-C-1] c.e.c.service.KafkaProducer              : Message payload (Base64 encoded): Chg2ODQ2N2ZlMGU3YjViNjA3MzQ4ZDczMDMSCXRhc2stMDAxMRoHc2Vuc29yNCoEYXJnMSoEYXJnMg==
2025-06-09T09:32:00.700+03:00  INFO 34476 --- [Command orchestrator] [ntainer#0-0-C-1] c.e.c.service.KafkaProducer              : Message sent successfully for key: sensor4
2025-06-09T09:32:00.700+03:00  INFO 34476 --- [Command orchestrator] [ntainer#0-0-C-1] c.e.c.s.CommandOrchestrationService      : Command for sensor sensor4 is ready for execution.
2025-06-09T09:32:00.701+03:00  INFO 34476 --- [Command orchestrator] [ntainer#0-0-C-1] c.e.c.s.CommandOrchestrationService      : Finished orchestrating commands for Task ID: task-0011.
```
- Protocol gateway
```
2025-06-09T09:32:00.841+03:00  INFO 37420 --- [protocolgateway] [ntainer#0-0-C-1] c.e.p.service.CommandMessageListener     : Received CommandMessage: ID=68467fe0e7b5b607348d7302, SensorID=sensor3, Type=START, Args=[arg1, arg2]
2025-06-09T09:32:00.841+03:00  INFO 37420 --- [protocolgateway] [ntainer#0-0-C-1] c.e.p.protocol.DlmsProtocolConverter     : Converting command with ID 68467fe0e7b5b607348d7302 for DLMS protocol
2025-06-09T09:32:00.842+03:00  INFO 37420 --- [protocolgateway] [ntainer#0-0-C-1] c.e.p.service.CommandMessageListener     : Converted command bytes: 68467fe0e7b5b607348d7302
2025-06-09T09:32:01.161+03:00  INFO 37420 --- [protocolgateway] [ntainer#0-0-C-1] c.e.p.service.CommandMessageListener     : Sensor simulator response: Simulated sensor received payload of length 24
2025-06-09T09:32:01.176+03:00  INFO 37420 --- [protocolgateway] [ntainer#0-0-C-1] c.e.p.service.CommandMessageListener     : Received CommandMessage: ID=68467fe0e7b5b607348d7303, SensorID=sensor4, Type=START, Args=[arg1, arg2]
2025-06-09T09:32:01.176+03:00  INFO 37420 --- [protocolgateway] [ntainer#0-0-C-1] c.e.p.protocol.DlmsProtocolConverter     : Converting command with ID 68467fe0e7b5b607348d7303 for DLMS protocol
2025-06-09T09:32:01.177+03:00  INFO 37420 --- [protocolgateway] [ntainer#0-0-C-1] c.e.p.service.CommandMessageListener     : Converted command bytes: 68467fe0e7b5b607348d7303
2025-06-09T09:32:01.200+03:00  INFO 37420 --- [protocolgateway] [ntainer#0-0-C-1] c.e.p.service.CommandMessageListener     : Sensor simulator response: Simulated sensor received payload of length 24
```
- Sensor simulator
```
2025-06-09T09:32:01.108+03:00  INFO 21300 --- [nio-9084-exec-1] c.e.s.c.SensorSimulatorController        : Received sensor simulation request with payload: 68467fe0e7b5b607348d7302
2025-06-09T09:32:01.196+03:00  INFO 21300 --- [nio-9084-exec-2] c.e.s.c.SensorSimulatorController        : Received sensor simulation request with payload: 68467fe0e7b5b607348d7303
```
