# Command Lifecycle Service
- [Responsibilities](#responsibilities)
- [Kafka topics](#kafka-topics)
## Responsibilities
* Subscribes to `task.created` topic.
* For each device in the task:
  * Generates a DLMS command (in XML format).
  * Publishes the command to Kafka topic `command.generated`.
* Subscribes to `device.responses` topic:
  * Parses and matches the response to a command/task.
  * Updates device status in the DB via internal API or direct write.
  * Publishes parsed response metadata to `orchestrator.responses`.
## Kafka topics

| Topic                    | Direction    | Payload Description                                      |
| ------------------------ | ------------ | -------------------------------------------------------- |
| `task.created`           | 🔽 Subscribe | Task ID and metadata to initiate command generation      |
| `command.generated`      | 🔼 Publish   | Generated DLMS XML command with device ID, task ID       |
| `device.responses`       | 🔽 Subscribe | DLMS device response (raw or decoded)                    |
| `orchestrator.responses` | 🔼 Publish   | Parsed, structured response payload for upstream systems |

 In your current design, the command payload (XML) is not stored—just streamed directly to the Protocol Adapter.

## [XML generation](xmlgeneration/README.md)
