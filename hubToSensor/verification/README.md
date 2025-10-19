# Verification

## Payload
```json
{
  "sensorId": "sensor-001",
  "operation": "DIRECT-ON",
  "relayNumber": 2,
  "duration": 30
}
```
## Flow
```
Flex Hub Simulator
       |
       v
+---------------------+
|      Broker         |
+---------------------+
       |
       v
+---------------------+
|       Bridge        |
| - Insert request    |
|   into DB           |
| - Update status:    |
|   Sent for protocol |
+---------------------+
       |
       v
+---------------------+
|      Broker         |
+---------------------+
       |
       v
+---------------------+
| Protocol Adapter     |
| - Update status:     |
|   Request received   |
| - Protocol conversion|
| - Update status:     |
|   Sent to HES        |
+---------------------+
       |
       v
+---------------------+
| HES Simulator        |
| - Executes command   |
| - Sends response     |
+---------------------+
       |
       v
+---------------------+
| Protocol Adapter     |
| - Update status:     |
|   Response received  |
| - Forward to Bridge  |
+---------------------+
       |
       v
+---------------------+
| Bridge               |
| - Update status:     |
|   Request success/fail|
| - Forward response    |
+---------------------+
       |
       v
Flex Hub Simulator
```