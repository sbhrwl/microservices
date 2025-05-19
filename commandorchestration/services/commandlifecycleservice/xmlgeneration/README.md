# XML generation
- [Process](#process)
- [Template based XML generation](#template-based-xml-generation)
- [Command generation logic per device](#command-generation-logic-per-device)
- [Publishing to Kafka](#publishing-to-kafka)
  - [Kafka topic config](#kafka-topic-config)
## Process
- Subscribe to `task.created`
- Fetch task & device list from DB
- For each device:
  - Generate XML using templating
  - Assign unique `commandId`
  - Publish to Kafka (`command.generated`)

## Template based XML generation
- Use pre-defined XML templates with placeholders.
- This ensures:
  - Reusability
  - Maintainability
  - Easier updates when protocol specs change

```xml
<Command>
  <DeviceId>{{deviceId}}</DeviceId>
  <CommandType>ON</CommandType>
  <Timestamp>{{timestamp}}</Timestamp>
</Command>
```

- Use a templating engine like:
* **Mustache** (lightweight, good for config-driven formats)
* **Freemarker** (Java-specific, powerful)
* **Handlebars**, **Jinja2** (Python), etc.

## Command generation logic per device
- For each device:
  - Prepare a context object with deviceId, taskId, timestamp, etc.
  - Fill in the template to produce the final XML string.

## Publishing to Kafka
- `command.generated`
- Each generated XML should be wrapped in a Kafka-friendly JSON payload:
```json
{
  "taskId": "123e4567-e89b-12d3-a456-426614174000",
  "deviceId": "MTR001",
  "commandId": "CMD-54321",
  "xmlPayload": "<Command>...</Command>",
  "timestamp": "2025-05-07T12:03:00Z"
}
```

### Kafka topic config
- **Topic:** `command.generated`
- **Key:** `deviceId` (for ordered delivery if needed)
- **Value:** JSON as above
- **Partitioning strategy:** deviceId-based if ordering is needed per meter
