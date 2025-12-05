# Services
- [Flexibility Hub simulator](flexibility-hub-simulator/README.md)
- [GFC service](flexibility-bridge-service/README.md)
- [Control orchestrator gRPC](storage-service-grpc/README.md)
- [Protocol adapter service](protocol-adapter-service/README.md)
- [HES-AIM simulator](hes-simulator/README.md)
- [Local testing](#local-testing)
- [State store](#state-store)
## Local testing
- As with Dapr local builds error out with `8080 not available`, Explicitly add port in `application.yaml` for each service
- `dapr run --app-id flexibility-hub-simulator --app-port 8081 --resources-path .\dapr\config-files -- mvn spring-boot:run`
- `dapr run --app-id storage-service --app-port 8086 --resources-path .\dapr\config-files -- mvn spring-boot:run`
- `dapr run --app-id flexibility-bridge-service --app-port 8082 --resources-path .\dapr\config-files -- mvn spring-boot:run`
- `dapr run --app-id protocol-adapter-service --app-port 8083 --resources-path .\dapr\config-files -- mvn spring-boot:run`
- `dapr run --app-id hes-simulator --app-port 8084 --resources-path .\dapr\config-files -- mvn spring-boot:run`

| Service                        | Subscribed Topics                               | Queue Names                                                                                           | 
| ------------------------------ | ----------------------------------------------- | ----------------------------------------------------------------------------------------------------- | 
| **Flexibility Hub Simulator**  | `flexibility-hub.response`                      | `flexibility-hub-simulator-flexibility-hub.response`                                                  | 
| **Flexibility Bridge Service** | `flexibility-hub.request`, `connector.response` | `flexibility-bridge-service-flexibility-hub.request`, `flexibility-bridge-service-connector.response` | 
| **Protocol Adapter Service**   | `connector.request`, `hes.response`             | `protocol-adapter-service-connector.request`, `protocol-adapter-service-hes.response`                 | 
| **HES Simulator**              | `hes.request`                                   | `hes-simulator-hes.request`                                                                           | 

## State store
- Verify PostgreSQL
```sql
PS C:\Users\sabharwalr> psql -h localhost -U myuser -d mydatabase
Password for user myuser:

psql (17.6, server 16.9 (Debian 16.9-1.pgdg120+1))
WARNING: Console code page (850) differs from Windows code page (1252)
         8-bit characters might not work correctly. See psql reference
         page "Notes for Windows users" for details.
Type "help" for help.

mydatabase=# SELECT * FROM control_requests;
mydatabase=# SELECT * FROM request_change_log;
mydatabase=# SELECT * FROM state;
```
