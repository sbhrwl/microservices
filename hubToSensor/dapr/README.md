# DAPR
- [Introduction](introduction/README.md)
- [Architecture](architecture/README.md)
- [Prerequisites](prerequisites/README.md)
- [Services](hub-to-sensor/README.md)
  - [Flexibility Hub simulator](hub-to-sensor/flexibility-hub-simulator/README.md)
  - [Flexibility bridge service](hub-to-sensor/flexibility-bridge-service/README.md)
  - [Storage service gRPC](hub-to-sensor/storage-service-grpc/README.md)
  - [Protocol adapter service](hub-to-sensor/protocol-adapter-service/README.md)
  - [HES simulator](hub-to-sensor/hes-simulator/README.md)
- [Helm charts](helmcharts/README.md)
---

Got it — here are **clean, separate tables for each client**, extracted from the logs you provided.
**After the tables, I’ll highlight the core issue.**

---

# ✅ Flex Hub Simulator

| Timestamp    | Component        | Event                                                              |
| ------------ | ---------------- | ------------------------------------------------------------------ |
| 14:45:49.889 | MessagePublisher | Message successfully published to Dapr pub/sub                     |
| 14:45:50.940 | ResponseConsumer | FINAL RESPONSE RECEIVED for Request ID 93                          |
| 14:45:50.941 | ResponseConsumer | Status SUCCESS                                                     |
| 14:45:50.941 | ResponseConsumer | Message: Operation DIRECT-ON for sensor-001 completed successfully |
| 14:45:50.941 | ResponseConsumer | ErrorCode: *(empty)*                                               |
| 14:45:50.941 | ResponseConsumer | RAW JSON: status SUCCESS, message DIRECT-ON for sensor-001         |
| 14:45:56     | Zipkin           | Connection refused (Zipkin not running)                            |

---

# ✅ Flex Bridge

| Timestamp    | Component                     | Event                                          |
| ------------ | ----------------------------- | ---------------------------------------------- |
| 14:45:49.928 | RequestConsumer               | Received request for sensor-002                |
| 14:45:50.125 | RequestConsumer               | Saved Control Request → ID 93                  |
| 14:45:50.162 | RequestConsumer               | Created initial change request log             |
| 14:45:50.162 | ProducerForProtocolConversion | Publishing payload to connector.request        |
| 14:45:50.187 | ProducerForProtocolConversion | Published request ID 93                        |
| 14:45:50.233 | RequestConsumer               | Updated status → Sent for protocol conversion  |
| 14:45:50.806 | ResponseConsumer              | Received & parsed response for ID 93           |
| 14:45:50.917 | ResponseProducerToHub         | Publishing final response to hub               |
| 14:45:50.930 | ResponseProducerToHub         | Successfully published response                |
| 14:45:50.976 | ResponseConsumer              | Final status updated to Completed successfully |

---

# ✅ Protocol Adapter

| Timestamp    | Component        | Event                                          |
| ------------ | ---------------- | ---------------------------------------------- |
| 14:45:50.247 | RequestConsumer  | Received request for ID 93                     |
| 14:45:50.364 | RequestConsumer  | Updated status → RECEIVED                      |
| 14:45:50.364 | RequestConsumer  | Started protocol conversion                    |
| 14:45:50.413 | RequestConsumer  | Protocol conversion done                       |
| 14:45:50.441 | ProducerForHES   | Published HES request                          |
| 14:45:50.492 | RequestConsumer  | Updated status → Sent to HES                   |
| 14:45:50.669 | ResponseConsumer | Received HES response                          |
| 14:45:50.718 | ResponseConsumer | Updated → Response received from HES           |
| 14:45:50.779 | ResponseConsumer | Protocol conversion done (response)            |
| 14:45:50.781 | ProducerToBridge | Sent JSON response: `DIRECT-ON for sensor-001` |
| 14:45:50.868 | ResponseConsumer | Status updated → COMPLETED                     |

---

# ✅ HES Simulator

| Timestamp    | Component           | Event                                                                 |
| ------------ | ------------------- | --------------------------------------------------------------------- |
| 14:45:50.516 | HESSimulatorService | Received request XML (operation DIRECT-OFF, relay 1)                  |
| 14:45:50.519 | HESSimulatorService | Starting 60-second delay (NOTE: but response is sent instantly → BUG) |
| 14:45:50.541 | HESSimulatorService | Prepared response XML → **Message says DIRECT-ON for sensor-001**     |
| 14:45:50.664 | HESSimulatorService | Sent HES response, Status SUCCESS                                     |

---

# ❗ Storage Service (summarized — many entries)

### Major Observations

| Record ID | Operation  | Notes                                                         |
| --------- | ---------- | ------------------------------------------------------------- |
| **93**    | DIRECT-OFF | Correct ID, repeatedly updated                                |
| **94**    | DIRECT-OFF | Created incorrectly — SHOULD NOT exist (duplicate saveRecord) |
| **95**    | DIRECT-OFF | Another unexpected new record — SHOULD NOT exist              |

Storage service is incorrectly creating new ControlRequest rows every time instead of only updating.

---

# 🚨 ROOT CAUSE ANALYSIS (Clear & Simple)

### **1️⃣ Wrong message returned by HES**

The HES simulator **receives DIRECT-OFF**, but responds:

```
Operation DIRECT-ON for sensor-001
```

→ This is why Flex Hub logs show wrong info.

### **2️⃣ Sensor ID is wrong in HES response**

Request: sensor-002
Response: sensor-001

### **3️⃣ Storage service creates multiple records (93, 94, 95)**

This is clearly wrong — it should only ever have:

* **One ControlRequest** per requestId
* Subsequent calls should be **updateRecord**, NOT saveRecord

This explains DB pollution and inconsistent state.

### **4️⃣ Zipkin connection refused**

Not critical, but confirms tracing is failing ( harmless for functionality ).

---

# ✅ NEXT STEP (so we can fix fast)

Tell me:

**Do you want me to build a single consolidated timeline table (end-to-end across all clients) to visualize the whole life of Request 93?**

This will help spot race conditions instantly.

Just say **YES**.
