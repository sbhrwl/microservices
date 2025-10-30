# Services
- [Flexibility Hub simulator](flexibility-hub-simulator/README.md)
- [Flexibility bridge service](flexibility-bridge-service/README.md)
- [Storage service gRPC](storage-service-grpc/README.md)
- [Protocol adapter service](protocol-adapter-service/README.md)
- [HES simulator](hes-simulator/README.md)
- [Data API service](data-api-service/README.md)
- [UI app](ui-app/README.md)
- [Local testing](#local-testing)
## Local testing
- As with Dapr local builds error out with `8080 not available`, Explicitly add port in `application.yaml` for each service
- `dapr run --app-id flexibility-hub-simulator --app-port 8081 --resources-path .\dapr\config-files -- mvn spring-boot:run`
- `dapr run --app-id storage-service --app-port 8086 --resources-path .\dapr\config-files -- mvn spring-boot:run`
- `dapr run --app-id flexibility-bridge-service --app-port 8082 --resources-path .\dapr\config-files -- mvn spring-boot:run`
- `dapr run --app-id protocol-adapter-service --app-port 8083 --resources-path .\dapr\config-files -- mvn spring-boot:run`
- `dapr run --app-id hes-simulator --app-port 8084 --resources-path .\dapr\config-files -- mvn spring-boot:run`
- `dapr run --app-id data-api-service --app-port 8085 --resources-path .\dapr\config-files -- mvn spring-boot:run`
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
## Summary

| Service                        | Subscribed Topics                               | Queue Names                                                                                           | 
| ------------------------------ | ----------------------------------------------- | ----------------------------------------------------------------------------------------------------- | 
| **Flexibility Hub Simulator**  | `flexibility-hub.response`                      | `flexibility-hub-simulator-flexibility-hub.response`                                                  | 
| **Flexibility Bridge Service** | `flexibility-hub.request`, `connector.response` | `flexibility-bridge-service-flexibility-hub.request`, `flexibility-bridge-service-connector.response` | 
| **Protocol Adapter Service**   | `connector.request`, `hes.response`             | `protocol-adapter-service-connector.request`, `protocol-adapter-service-hes.response`                 | 
| **HES Simulator**              | `hes.request`                                   | `hes-simulator-hes.request`                                                                           | 
