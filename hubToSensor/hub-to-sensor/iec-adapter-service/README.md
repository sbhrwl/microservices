# Flexibility bridge
- [Properties](#properties)
- [How to run](#how-to-run)
- [Test](#test)
## Properties
- [application.yml](src/main/resources/application.yml)
## How to run
- Create spring boot app with [spring initialiser](https://start.spring.io/)
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
### Request
- Test it together with `Flexibility bridge service` as it has implemented a gRPC client
- Push data to Broker
  - `POST`: `http://localhost:8081/api/messages`
  - Payload
    ```json
    {
      "sensorId": "sensor-001",
      "operation": "DIRECT-ON",
      "relayNumber": 2,
      "duration": 30
    }
    ```
- Verify `Flexibility hub simulator` logs
```
2025-10-02T09:48:59.790+03:00  INFO 13744 --- [           main] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:5672]
2025-10-02T09:48:59.890+03:00  INFO 13744 --- [           main] o.s.a.r.c.CachingConnectionFactory       : Created new connection: rabbitConnectionFactory#633fd91:0/SimpleConnection@193eb1ba [delegate=amqp://admin@127.0.0.1:5672/, localPort=60291]
2025-10-02T09:49:00.038+03:00  INFO 13744 --- [           main] c.a.f.FlexibilityHubSimulatorApplication : Started FlexibilityHubSimulatorApplication in 3.862 seconds (process running for 4.479)
2025-10-02T09:50:23.284+03:00  INFO 13744 --- [nio-8081-exec-2] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2025-10-02T09:50:23.285+03:00  INFO 13744 --- [nio-8081-exec-2] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2025-10-02T09:50:23.287+03:00  INFO 13744 --- [nio-8081-exec-2] o.s.web.servlet.DispatcherServlet        : Completed initialization in 2 ms
```
- Verify `flexibility bridge service` logs
```
2025-10-02T09:50:24.976+03:00  INFO 15148 --- [ntContainer#0-1] c.a.f.service.RequestConsumer            : ?? Saved initial record to Storage Service. Status: Control Requested. Generated ID: 30
2025-10-02T09:50:24.977+03:00  INFO 15148 --- [ntContainer#0-1] c.a.f.service.MessageProducerService     : ? Final payload being sent to connector queue: MessagePayload{recordId='30', sensorId='sensor-001', operation='DIRECT-ON', relayNumber=1, duration=30}
2025-10-02T09:50:25.036+03:00  INFO 15148 --- [ntContainer#0-1] c.a.f.service.MessageProducerService     : ?? Published request ID: 30 for Sensor ID: sensor-001 using key: connector.request
2025-10-02T09:50:25.040+03:00 DEBUG 15148 --- [-worker-ELG-1-2] i.g.n.s.i.grpc.netty.NettyClientHandler  : [id: 0x00aa6143, L:/127.0.0.1:62423 - R:localhost/127.0.0.1:9090] OUTBOUND HEADERS: streamId=5 headers=GrpcHttp2OutboundHeaders[:authority: localhost:9090, :path: /RecordService/updateRecord, :method: POST, :scheme: http, content-type: application/grpc, te: trailers, user-agent: grpc-java-netty/1.62.2, grpc-accept-encoding: gzip] streamDependency=0 weight=16 exclusive=false padding=0 endStream=false
2025-10-02T09:50:25.219+03:00 DEBUG 15148 --- [-worker-ELG-1-2] i.g.n.s.i.grpc.netty.NettyClientHandler  : [id: 0x00aa6143, L:/127.0.0.1:62423 - R:localhost/127.0.0.1:9090] INBOUND HEADERS: streamId=5 headers=GrpcHttp2ResponseHeaders[grpc-status: 0] padding=0 endStream=true
2025-10-02T09:50:25.220+03:00  INFO 15148 --- [ntContainer#0-1] c.a.f.service.RequestConsumer            : ? Updated record status to Sent for protocol conversion for request ID: 30. Publishing successful.
```
- Verify `storage service` logs
```
2025-10-02T09:49:30.796+03:00  INFO 29076 --- [           main] n.d.b.g.s.s.GrpcServerLifecycle          : gRPC Server started, listening on address: *, port: 9090
2025-10-02T09:49:30.806+03:00  INFO 29076 --- [           main] c.a.s.StorageServiceApplication          : Started StorageServiceApplication in 5.485 seconds (process running for 6.224)
Hibernate: insert into control_requests (duration,operation,relay_number,sensor_id,status) values (?,?,?,?,?)
Hibernate: insert into request_change_log (change_description,change_timestamp,record_id) values (?,?,?)
Hibernate: select r1_0.id,r1_0.duration,r1_0.operation,r1_0.relay_number,r1_0.sensor_id,r1_0.status from control_requests r1_0 where r1_0.id=?
Hibernate: insert into request_change_log (change_description,change_timestamp,record_id) values (?,?,?)
Hibernate: update control_requests set duration=?,operation=?,relay_number=?,sensor_id=?,status=? where id=?
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

mydatabase=# select * from request_change_log;
```

### Response
- Success
```xml
<FlexibilityResponse>
    <RequestID>30</RequestID>
    <Status>SUCCESS</Status>
    <Message>Operation DIRECT-ON for sensor-001 completed successfully.</Message>
    <Timestamp>2025-10-02T10:15:00Z</Timestamp>
</FlexibilityResponse>
```
- Error
```xml
<FlexibilityResponse>
    <RequestID>30</RequestID>
    <Status>ERROR</Status>
    <ErrorCode>404</ErrorCode>
    <Message>Target sensor 'sensor-001' not found or offline.</Message>
    <Timestamp>2025-10-02T10:15:05Z</Timestamp>
</FlexibilityResponse>
```