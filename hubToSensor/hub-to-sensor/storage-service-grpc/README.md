# Storage service
- [Properties](#properties)
- [How to run](#how-to-run)
- [Test](#test)
## Properties
- [application.properties](src/main/resources/application.properties)
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
- Test it together with `Flexibility bridge service` as it has implemented a gRPC client
- Push data to Broker
  - `POST`: `http://localhost:8081/api/messages`
  - Payload
    ```json
    {
      "sensorId": "sensor-001",
      "operation": "DIRECT-ON",
      "relayNumber": 2,
      "duration": 30,
      "status": "Received"
    }
    ```
- Verify `flexibility bridge service` logs
```
INBOUND HEADERS: streamId=5 headers=GrpcHttp2ResponseHeaders[grpc-status: 0] padding=0 endStream=true ?? Sending to Storage Service via gRPC...
? gRPC call successful. Server message: Record saved successfully with ID: 7
```
- Verify `storage service logs`
```
c.a.s.StorageServiceApplication          : Started StorageServiceApplication in 9.19 seconds (process running for 9.879)
Hibernate: insert into control_requests (duration,operation,relay_number,sensor_id,status) values (?,?,?,?,?)
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
```
