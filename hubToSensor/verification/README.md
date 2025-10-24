# Verification
- [Payload](#payload)
- [Flow](#flow)
- [Request](#request)
- [Response](#response)
  - [Success](#success)
  - [Failure](#failure)
- [Verify from kubectl](#verify-from-kubectl)
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
 
## Request
- Push data to Broker
  - `POST`: `http://localhost:8081/api/messages`
  - **Kubernetes**: `POST`: `http://localhost:30881/api/messages`
  - Payload
    ```json
    {
      "sensorId": "sensor-001",
      "operation": "DIRECT-ON",
      "relayNumber": 2,
      "duration": 30
    }
    ```
- Verify **`Flexibility hub simulator`** logs
```
2025-10-03T13:13:43.897+03:00  INFO 16244 --- [           main] c.a.f.FlexibilityHubSimulatorApplication : Started FlexibilityHubSimulatorApplication in 7.778 seconds (process running for 9.098)
2025-10-03T13:16:40.986+03:00  INFO 16244 --- [nio-8081-exec-1] c.a.f.service.MessagePublisher           : ? Publishing message to hub flexibility-bridge.exchange with routing key flexibility-hub.request. JSON Payload: {"sensorId":"sensor-001","operation":"DIRECT-ON","relayNumber":1,"duration":0}
2025-10-03T13:16:41.046+03:00  INFO 16244 --- [nio-8081-exec-1] c.a.f.service.MessagePublisher           : ? Message successfully published.
```
- Verify **`flexibility bridge service`** logs
```
2025-10-03T13:16:41.136+03:00  INFO 24820 --- [ntContainer#0-1] c.a.f.service.RequestConsumer            : ?? Saved initial record to Storage Service. Status: Control Requested. Generated ID: 42
2025-10-03T13:16:41.137+03:00  INFO 24820 --- [ntContainer#0-1] uestProducerForProtocolConversionService : ? Final payload being sent to connector queue: RequestPayload{recordId='42', sensorId='sensor-001', operation='DIRECT-ON', relayNumber=1, duration=0}
2025-10-03T13:16:41.156+03:00  INFO 24820 --- [ntContainer#0-1] uestProducerForProtocolConversionService : ?? Published request ID: 42 for Sensor ID: sensor-001 using key: connector.request
2025-10-03T13:16:41.182+03:00  INFO 24820 --- [ntContainer#0-1] c.a.f.service.RequestConsumer            : ? Updated record status to Sent for protocol conversion for request ID: 42. Publishing successful.
```
- Verify **`storage service`** logs
```
2025-10-02T09:49:30.796+03:00  INFO 29076 --- [           main] n.d.b.g.s.s.GrpcServerLifecycle          : gRPC Server started, listening on address: *, port: 9090
2025-10-02T09:49:30.806+03:00  INFO 29076 --- [           main] c.a.s.StorageServiceApplication          : Started StorageServiceApplication in 5.485 seconds (process running for 6.224)
Hibernate: insert into control_requests (duration,operation,relay_number,sensor_id,status) values (?,?,?,?,?)
Hibernate: insert into request_change_log (change_description,change_timestamp,record_id) values (?,?,?)
Hibernate: select r1_0.id,r1_0.duration,r1_0.operation,r1_0.relay_number,r1_0.sensor_id,r1_0.status from control_requests r1_0 where r1_0.id=?
Hibernate: insert into request_change_log (change_description,change_timestamp,record_id) values (?,?,?)
Hibernate: select r1_0.id,r1_0.duration,r1_0.operation,r1_0.relay_number,r1_0.sensor_id,r1_0.status from control_requests r1_0 where r1_0.id=?
Hibernate: update control_requests set duration=?,operation=?,relay_number=?,sensor_id=?,status=? where id=?
Hibernate: insert into request_change_log (change_description,change_timestamp,record_id) values (?,?,?)
Hibernate: update control_requests set duration=?,operation=?,relay_number=?,sensor_id=?,status=? where id=?
Hibernate: select r1_0.id,r1_0.duration,r1_0.operation,r1_0.relay_number,r1_0.sensor_id,r1_0.status from control_requests r1_0 where r1_0.id=?
Hibernate: insert into request_change_log (change_description,change_timestamp,record_id) values (?,?,?)
Hibernate: update control_requests set duration=?,operation=?,relay_number=?,sensor_id=?,status=? where id=?
Hibernate: select r1_0.id,r1_0.duration,r1_0.operation,r1_0.relay_number,r1_0.sensor_id,r1_0.status from control_requests r1_0 where r1_0.id=?
Hibernate: insert into request_change_log (change_description,change_timestamp,record_id) values (?,?,?)
Hibernate: update control_requests set duration=?,operation=?,relay_number=?,sensor_id=?,status=? where id=?
```
- Verify **`protocol adapter service`** logs
```
2025-10-03T13:16:41.160+03:00  INFO 31916 --- [ntContainer#0-1] c.a.p.service.RequestConsumer            : ? Received request for Sensor ID: sensor-001 with existing Record ID: 42
2025-10-03T13:16:41.197+03:00  INFO 31916 --- [ntContainer#0-1] c.a.p.service.RequestConsumer            : ?? Updated initial record status to RECEIVED. ID: 42
2025-10-03T13:16:41.197+03:00  INFO 31916 --- [ntContainer#0-1] c.a.p.service.RequestConsumer            : ? Starting protocol conversion for Record ID: 42
2025-10-03T13:16:41.236+03:00  INFO 31916 --- [ntContainer#0-1] c.a.p.service.RequestConsumer            : ? Updated status to 'protocol conversion done' for ID: 42
2025-10-03T13:16:41.242+03:00  INFO 31916 --- [ntContainer#0-1] c.a.p.s.RequestProducerForHESService     : ? Successfully published HES request for Record ID: 42
2025-10-03T13:16:41.298+03:00  INFO 31916 --- [ntContainer#0-1] c.a.p.service.RequestConsumer            : ? Updated status to 'Sent to HES' for ID: 42. Publishing successful.
```
- Verify **`hes simulator`** logs
```
2025-10-11T16:35:46.096Z  INFO 1 --- [           main] c.a.h.HESsimulatorApplication            : Started HESsimulatorApplication in 3.781 seconds (process running for 4.475)
2025-10-11T16:38:16.933Z DEBUG 1 --- [ntContainer#0-1] c.a.h.service.HESSimulatorService        : Full received HES Request XML:
"<HesRequest id=\"sensor-002\">    <RequestID>54</RequestID>    <operation>DIRECT-ON</operation>    <relay>1</relay>    <duration>0</duration></HesRequest>"
2025-10-11T16:38:16.936Z  INFO 1 --- [ntContainer#0-1] c.a.h.service.HESSimulatorService        : 📧 Received HES Request with ID: 54. Starting 60-second simulation delay.
2025-10-11T16:38:16.959Z  INFO 1 --- [ntContainer#0-1] c.a.h.service.HESSimulatorService        : ✅ HES Response sent for ID: 54. Status: ERROR
```
- Verify PostgreSQL
```sql
PS C:\Users\sabharwalr> psql -h localhost -U myuser -d mydatabase
Password for user myuser:

psql (17.6, server 16.9 (Debian 16.9-1.pgdg120+1))
WARNING: Console code page (850) differs from Windows code page (1252)
         8-bit characters might not work correctly. See psql reference
         page "Notes for Windows users" for details.
Type "help" for help.

mydatabase=# select * from control_requests;
 id | duration | operation | relay_number | sensor_id  |   status
----+----------+-----------+--------------+------------+-------------
 42 |        0 | DIRECT-ON |            1 | sensor-001 | Sent to HES
(1 row)

mydatabase=# select * from request_change_log;
 id  |            change_description            |       change_timestamp        | record_id
-----+------------------------------------------+-------------------------------+-----------
 132 | Control Requested                        | 2025-10-03 10:16:41.105162+00 |        42
 133 | Sent for protocol conversion             | 2025-10-03 10:16:41.170938+00 |        42
 134 | message recieved for protocol conversion | 2025-10-03 10:16:41.179569+00 |        42
 135 | protocol conversion done                 | 2025-10-03 10:16:41.212662+00 |        42
 136 | Sent to HES                              | 2025-10-03 10:16:41.262414+00 |        42
```
- [Data API](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/hub-to-sensor/data-api-service/README.md)
  - Test request tracker
  - `GET /requests/{id}/tracker`
  - `http://localhost:8085/api/v1/requests/61/tracker`
  - **Kubernetes**: `http://localhost:30885/api/v1/requests/61/tracker`
- [UI](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/hub-to-sensor/ui-app/README.md)
## Response
### Success
```xml
<FlexibilityResponse>
    <RequestID>30</RequestID>
    <Status>SUCCESS</Status>
    <Message>Operation DIRECT-ON for sensor-001 completed successfully.</Message>
    <Timestamp>2025-10-02T10:15:00Z</Timestamp>
</FlexibilityResponse>
```
- Verify **`protocol adapter service`** logs
```
2025-10-03T13:23:03.141+03:00  INFO 31916 --- [ntContainer#1-1] c.a.p.service.ResponseConsumer           : ?? Updated status to 'Response recieved from HES' for ID: 42
2025-10-03T13:23:03.142+03:00  INFO 31916 --- [ntContainer#1-1] c.a.p.service.ResponseConsumer           : ? Starting protocol conversion (Object to JSON String) for Record ID: 42
2025-10-03T13:23:03.169+03:00  INFO 31916 --- [ntContainer#1-1] c.a.p.service.ResponseConsumer           : ? Updated status to 'Protocol conversion done for response' for ID: 42
2025-10-03T13:23:03.170+03:00  INFO 31916 --- [ntContainer#1-1] c.a.p.service.ResponseProducerToBridge   : Sending final JSON response (ID: 42) to Exchange: flexibility-bridge.exchange with Routing Key: connector.response
2025-10-03T13:23:03.170+03:00  INFO 31916 --- [ntContainer#1-1] c.a.p.service.ResponseProducerToBridge   : ? JSON Payload being sent: {"requestId":"42", "status":"SUCCESS", "message":"Operation DIRECT-ON for sensor-001 completed successfully.", "errorCode":""}
2025-10-03T13:23:03.205+03:00  INFO 31916 --- [ntContainer#1-1] c.a.p.service.ResponseConsumer           : ? Updated status to 'Response sent to Bridge' for ID: 42. Final status: COMPLETED
```
- Verify **`storage service`** logs
```
Hibernate: select r1_0.id,r1_0.duration,r1_0.operation,r1_0.relay_number,r1_0.sensor_id,r1_0.status from control_requests r1_0 where r1_0.id=?
Hibernate: insert into request_change_log (change_description,change_timestamp,record_id) values (?,?,?)
Hibernate: update control_requests set duration=?,operation=?,relay_number=?,sensor_id=?,status=? where id=?
Hibernate: select r1_0.id,r1_0.duration,r1_0.operation,r1_0.relay_number,r1_0.sensor_id,r1_0.status from control_requests r1_0 where r1_0.id=?
Hibernate: insert into request_change_log (change_description,change_timestamp,record_id) values (?,?,?)
Hibernate: update control_requests set duration=?,operation=?,relay_number=?,sensor_id=?,status=? where id=?
```
- Verify **`flexibility bridge service`** logs
```
2025-10-03T13:23:03.209+03:00  INFO 24820 --- [ntContainer#1-1] c.a.f.service.ResponseConsumer           : ? Received and successfully parsed JSON response for RequestID: 42
2025-10-03T13:23:03.238+03:00  INFO 24820 --- [ntContainer#1-1] c.a.f.service.ResponseConsumer           : ?? Updated status to 'Parsed response recieved from Protocol adapter' for ID: 42
2025-10-03T13:23:03.240+03:00  INFO 24820 --- [ntContainer#1-1] c.a.f.service.ResponseConsumer           : Attempting to publish final response (JSON object) for RequestID 42 back to Hub...
2025-10-03T13:23:03.240+03:00  INFO 24820 --- [ntContainer#1-1] c.a.f.service.ResponseProducerToHub      : Attempting to publish final JSON response object (status: SUCCESS) for Request ID 42 to Exchange 'flexibility-bridge.exchange' with Routing Key 'flexibility-hub.response'
2025-10-03T13:23:03.245+03:00  INFO 24820 --- [ntContainer#1-1] c.a.f.service.ResponseProducerToHub      : ? Successfully published final JSON response for Request ID 42 with status SUCCESS.
2025-10-03T13:23:03.246+03:00  INFO 24820 --- [ntContainer#1-1] c.a.f.service.ResponseConsumer           : Request 42 completed successfully.
2025-10-03T13:23:03.291+03:00  INFO 24820 --- [ntContainer#1-1] c.a.f.service.ResponseConsumer           : ? Final status updated for RequestID 42 to Request staus: Completed. Server response: Record status updated to Request staus: Completed for ID: 42```
```
- Verify **`Flexibility hub simulator`** logs
```
2025-10-03T13:23:03.280+03:00  INFO 16244 --- [ntContainer#0-1] c.a.f.service.ResponseConsumer           : =========================================================================================
2025-10-03T13:23:03.281+03:00  INFO 16244 --- [ntContainer#0-1] c.a.f.service.ResponseConsumer           : ? FINAL RESPONSE RECEIVED from Bridge for Request ID: 42
2025-10-03T13:23:03.283+03:00  INFO 16244 --- [ntContainer#0-1] c.a.f.service.ResponseConsumer           :    Status: SUCCESS
2025-10-03T13:23:03.283+03:00  INFO 16244 --- [ntContainer#0-1] c.a.f.service.ResponseConsumer           :    Message: Operation DIRECT-ON for sensor-001 completed successfully.
2025-10-03T13:23:03.284+03:00 ERROR 16244 --- [ntContainer#0-1] c.a.f.service.ResponseConsumer           :    Error Code:
2025-10-03T13:23:03.288+03:00  INFO 16244 --- [ntContainer#0-1] c.a.f.service.ResponseConsumer           :    RAW RESPONSE JSON: MessageResponse{requestId='42', status='SUCCESS', message='Operation DIRECT-ON for sensor-001 completed successfully.', errorCode='', timestamp='null'}
2025-10-03T13:23:03.289+03:00  INFO 16244 --- [ntContainer#0-1] c.a.f.service.ResponseConsumer           : =========================================================================================
```
- Database
```sql
mydatabase=# select * from control_requests;
 id | duration | operation | relay_number | sensor_id  |          status
----+----------+-----------+--------------+------------+--------------------------
 42 |        0 | DIRECT-ON |            1 | sensor-001 | Request staus: Completed
(1 row)


mydatabase=# select * from request_change_log;
 id  |                change_description                 |       change_timestamp        | record_id
-----+---------------------------------------------------+-------------------------------+-----------
 132 | Control Requested                                 | 2025-10-03 10:16:41.105162+00 |        42
 133 | Sent for protocol conversion                      | 2025-10-03 10:16:41.170938+00 |        42
 134 | message recieved for protocol conversion          | 2025-10-03 10:16:41.179569+00 |        42
 135 | protocol conversion done                          | 2025-10-03 10:16:41.212662+00 |        42
 136 | Sent to HES                                       | 2025-10-03 10:16:41.262414+00 |        42
 137 | Response recieved from HES                        | 2025-10-03 10:23:03.118948+00 |        42
 138 | Protocol conversion done for response             | 2025-10-03 10:23:03.149661+00 |        42
 139 | Response sent to Bridge - Final Status: COMPLETED | 2025-10-03 10:23:03.18456+00  |        42
 140 | Parsed response recieved from Protocol adapter    | 2025-10-03 10:23:03.222596+00 |        42
 141 | Request staus: Completed                          | 2025-10-03 10:23:03.257418+00 |        42
```
### Failure
```xml
<FlexibilityResponse>
    <RequestID>30</RequestID>
    <Status>ERROR</Status>
    <ErrorCode>404</ErrorCode>
    <Message>Target sensor 'sensor-001' not found or offline.</Message>
    <Timestamp>2025-10-02T10:15:05Z</Timestamp>
</FlexibilityResponse>
```
- Verify **`protocol adapter service`** logs
```
2025-10-03T13:29:17.428+03:00  INFO 31916 --- [ntContainer#1-1] c.a.p.service.ResponseConsumer           : ? Received object response for RequestID: 42
2025-10-03T13:29:17.475+03:00  INFO 31916 --- [ntContainer#1-1] c.a.p.service.ResponseConsumer           : ?? Updated status to 'Response recieved from HES' for ID: 42
2025-10-03T13:29:17.475+03:00  INFO 31916 --- [ntContainer#1-1] c.a.p.service.ResponseConsumer           : ? Starting protocol conversion (Object to JSON String) for Record ID: 42
2025-10-03T13:29:17.503+03:00  INFO 31916 --- [ntContainer#1-1] c.a.p.service.ResponseConsumer           : ? Updated status to 'Protocol conversion done for response' for ID: 42
2025-10-03T13:29:17.504+03:00  INFO 31916 --- [ntContainer#1-1] c.a.p.service.ResponseProducerToBridge   : Sending final JSON response (ID: 42) to Exchange: flexibility-bridge.exchange with Routing Key: connector.response
2025-10-03T13:29:17.505+03:00  INFO 31916 --- [ntContainer#1-1] c.a.p.service.ResponseProducerToBridge   : ? JSON Payload being sent: {"requestId":"42", "status":"ERROR", "message":"Target sensor 'sensor-001' not found or offline.", "errorCode":"404"}
2025-10-03T13:29:17.531+03:00  INFO 31916 --- [ntContainer#1-1] c.a.p.service.ResponseConsumer           : ? Updated status to 'Response sent to Bridge' for ID: 42. Final status: FAILED (Code: 404, Msg: Target sensor 'sensor-001' not found or offline.)
```
- Verify **`storage service`** logs
```
Hibernate: select r1_0.id,r1_0.duration,r1_0.operation,r1_0.relay_number,r1_0.sensor_id,r1_0.status from control_requests r1_0 where r1_0.id=?
Hibernate: insert into request_change_log (change_description,change_timestamp,record_id) values (?,?,?)
Hibernate: update control_requests set duration=?,operation=?,relay_number=?,sensor_id=?,status=? where id=?
Hibernate: select r1_0.id,r1_0.duration,r1_0.operation,r1_0.relay_number,r1_0.sensor_id,r1_0.status from control_requests r1_0 where r1_0.id=?
Hibernate: insert into request_change_log (change_description,change_timestamp,record_id) values (?,?,?)
Hibernate: update control_requests set duration=?,operation=?,relay_number=?,sensor_id=?,status=? where id=?
Hibernate: select r1_0.id,r1_0.duration,r1_0.operation,r1_0.relay_number,r1_0.sensor_id,r1_0.status from control_requests r1_0 where r1_0.id=?
Hibernate: insert into request_change_log (change_description,change_timestamp,record_id) values (?,?,?)
Hibernate: update control_requests set duration=?,operation=?,relay_number=?,sensor_id=?,status=? where id=?
```
- Verify **`flexibility bridge service`** logs
```
2025-10-03T13:29:17.514+03:00  INFO 24820 --- [ntContainer#1-1] c.a.f.service.ResponseConsumer           : ? Received and successfully parsed JSON response for RequestID: 42
2025-10-03T13:29:17.540+03:00  INFO 24820 --- [ntContainer#1-1] c.a.f.service.ResponseConsumer           : ?? Updated status to 'Parsed response recieved from Protocol adapter' for ID: 42
2025-10-03T13:29:17.541+03:00  INFO 24820 --- [ntContainer#1-1] c.a.f.service.ResponseConsumer           : Attempting to publish final response (JSON object) for RequestID 42 back to Hub...
2025-10-03T13:29:17.541+03:00  INFO 24820 --- [ntContainer#1-1] c.a.f.service.ResponseProducerToHub      : Attempting to publish final JSON response object (status: ERROR) for Request ID 42 to Exchange 'flexibility-bridge.exchange' with Routing Key 'flexibility-hub.response'
2025-10-03T13:29:17.542+03:00  INFO 24820 --- [ntContainer#1-1] c.a.f.service.ResponseProducerToHub      : ? Successfully published final JSON response for Request ID 42 with status ERROR.
2025-10-03T13:29:17.544+03:00 ERROR 24820 --- [ntContainer#1-1] c.a.f.service.ResponseConsumer           : Request 42 failed. Final status: Request staus: Failed (Code: 404, Msg: Target sensor 'sensor-001' not found or offline.)
2025-10-03T13:29:17.567+03:00  INFO 24820 --- [ntContainer#1-1] c.a.f.service.ResponseConsumer           : ? Final status updated for RequestID 42 to Request staus: Failed (Code: 404, Msg: Target sensor 'sensor-001' not found or offline.). Server response: Record status updated to Request staus: Failed (Code: 404, Msg: Target sensor 'sensor-001' not found or offline.) for ID: 42
```
- Verify **`Flexibility hub simulator`** logs
```
2025-10-03T13:29:17.548+03:00  INFO 16244 --- [ntContainer#0-1] c.a.f.service.ResponseConsumer           : =========================================================================================
2025-10-03T13:29:17.549+03:00  INFO 16244 --- [ntContainer#0-1] c.a.f.service.ResponseConsumer           : ? FINAL RESPONSE RECEIVED from Bridge for Request ID: 42
2025-10-03T13:29:17.549+03:00  INFO 16244 --- [ntContainer#0-1] c.a.f.service.ResponseConsumer           :    Status: ERROR
2025-10-03T13:29:17.549+03:00  INFO 16244 --- [ntContainer#0-1] c.a.f.service.ResponseConsumer           :    Message: Target sensor 'sensor-001' not found or offline.
2025-10-03T13:29:17.549+03:00 ERROR 16244 --- [ntContainer#0-1] c.a.f.service.ResponseConsumer           :    Error Code: 404
2025-10-03T13:29:17.550+03:00  INFO 16244 --- [ntContainer#0-1] c.a.f.service.ResponseConsumer           :    RAW RESPONSE JSON: MessageResponse{requestId='42', status='ERROR', message='Target sensor 'sensor-001' not found or offline.', errorCode='404', timestamp='null'}
2025-10-03T13:29:17.551+03:00  INFO 16244 --- [ntContainer#0-1] c.a.f.service.ResponseConsumer           : =========================================================================================
```
- Database
```sql
mydatabase=# select * from control_requests;
 id | duration | operation | relay_number | sensor_id  |                                          status
----+----------+-----------+--------------+------------+------------------------------------------------------------------------------------------
 42 |        0 | DIRECT-ON |            1 | sensor-001 | Request staus: Failed (Code: 404, Msg: Target sensor 'sensor-001' not found or offline.)
(1 row)


mydatabase=# select * from request_change_log;
 id  |                                                change_description                                                 |       change_timestamp        | record_id
-----+-------------------------------------------------------------------------------------------------------------------+-------------------------------+-----------
 132 | Control Requested                                                                                                 | 2025-10-03 10:16:41.105162+00 |        42
 133 | Sent for protocol conversion                                                                                      | 2025-10-03 10:16:41.170938+00 |        42
 134 | message recieved for protocol conversion                                                                          | 2025-10-03 10:16:41.179569+00 |        42
 135 | protocol conversion done                                                                                          | 2025-10-03 10:16:41.212662+00 |        42
 136 | Sent to HES                                                                                                       | 2025-10-03 10:16:41.262414+00 |        42
 137 | Response recieved from HES                                                                                        | 2025-10-03 10:23:03.118948+00 |        42
 138 | Protocol conversion done for response                                                                             | 2025-10-03 10:23:03.149661+00 |        42
 139 | Response sent to Bridge - Final Status: COMPLETED                                                                 | 2025-10-03 10:23:03.18456+00  |        42
 140 | Parsed response recieved from Protocol adapter                                                                    | 2025-10-03 10:23:03.222596+00 |        42
 141 | Request staus: Completed                                                                                          | 2025-10-03 10:23:03.257418+00 |        42
 142 | Response recieved from HES                                                                                        | 2025-10-03 10:29:17.447798+00 |        42
 143 | Protocol conversion done for response                                                                             | 2025-10-03 10:29:17.482692+00 |        42
 144 | Response sent to Bridge - Final Status: FAILED (Code: 404, Msg: Target sensor 'sensor-001' not found or offline.) | 2025-10-03 10:29:17.517319+00 |        42
 145 | Parsed response recieved from Protocol adapter                                                                    | 2025-10-03 10:29:17.524501+00 |        42
 146 | Request staus: Failed (Code: 404, Msg: Target sensor 'sensor-001' not found or offline.)                          | 2025-10-03 10:29:17.553575+00 |        42
```
## Verify from kubectl
- Data API
```
PS C:\Git\microservices\hubToSensor\ingress\trial\orchestrate-hubtosensor-services> kubectl exec -it data-api-546dbcd7c5-wbwfs -n staging -- /bin/sh -c "curl -s localhost:8085/api/v1/requests"
[{"id":65,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Completed"},{"id":71,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Completed"},{"id":66,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Completed"},{"id":61,"duration":0,"operation":"DIRECT-OFF","relayNumber":1,"sensorId":"sensor-004","status":"Request staus: Completed"},{"id":67,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Completed"},{"id":62,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Completed"},{"id":68,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Failed (Code: 404, Msg: Target sensor 'sensor-001' not found or offline.)"},{"id":63,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Failed (Code: 404, Msg: Target sensor 'sensor-001' not found or offline.)"},{"id":69,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Failed (Code: 404, Msg: Target sensor 'sensor-001' not found or offline.)"},{"id":64,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Completed"},{"id":70,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Completed"}]
PS C:\Git\microservices\hubToSensor\ingress\orchestrate-hubtosensor-services> curl -I -k https://fhs.local/data-api/api/v1/requests
```