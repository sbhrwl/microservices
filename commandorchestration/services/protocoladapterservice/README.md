# Protocol Adapter Service
- [Responsibilities](#responsibilities)
- [Kafka topics](#kafka-topics)
- [Considerations](#considerations)
## Responsibilities
* Subscribe to Kafka topic `command.generated`
* Convert **XML commands** to **DLMS PDUs**, apply security (encryption, authentication)
* Send commands to DLMS devices over NB-IoT / LTE-M
* Receive device responses
* Decode DLMS PDUs and publish to `device.responses` Kafka topic
## Kafka topics

| Purpose          | Topic Name          | Direction |
| ---------------- | ------------------- | --------- |
| Commands to send | `command.generated` | Subscribe |
| Device responses | `device.responses`  | Publish   |

## Considerations
* DLMS/COSEM protocol handling including frame structure, encryption, invocation counter management.
* Handle timeouts and retries.
* Maintain socket/session management with device as per comm stack.
* Lightweight stateless operation—heavy logic like retries or state persistence is managed upstream.
