# Services
- [Flexibility Hub simulator](flexibility-hub-simulator/README.md)
- [Flexibility bridge service](flexibility-bridge-service/README.md)
- [Storage service gRPC](storage-service-grpc/README.md)
- [Protocol adapter service](protocol-adapter-service/README.md)
- [HES simulator](hes-simulator/README.md)
- [Data API service](data-api-service/README.md)
- [UI app](ui-app/README.md)
- [Local testing](#local-testing)
- [State](#state)
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

## State
- Verify PostgreSQL
```sql
PS C:\Users\sabharwalr> psql -h localhost -U myuser -d mydatabase
Password for user myuser:

psql (17.6, server 16.9 (Debian 16.9-1.pgdg120+1))
WARNING: Console code page (850) differs from Windows code page (1252)
         8-bit characters might not work correctly. See psql reference
         page "Notes for Windows users" for details.
Type "help" for help.

mydatabase=# SELECT * FROM state;
```

| Key                                                               | Value                                                                                                                                                                                                                  | IsBinary | InsertDate                    | UpdateDate                    | ExpireDate |
| ----------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- | ----------------------------- | ----------------------------- | ---------- |
| storage-service||record:1761910463782                             | `eyJpZCI6IjE3NjE5MTA0NjM3ODIiLCJzZW5zb3JJZCI6InNlbnNvci0wMDQiLCJvcGVyYXRpb24iOiJESVJFQ1QtT0ZGIiwicmVsYXlOdW1iZXIiOjEsImR1cmF0aW9uIjowLCJzdGF0dXMiOiJTZW50IGZvciBwcm90b2NvbCBjb252ZXJzaW9uIn0=`                         | t        | 2025-10-31 11:38:01.1916+00   | 2025-10-31 11:38:01.255705+00 |            |
| storage-service||requestchangelog:1761910463782                   | `eyJyZWNvcmRJZCI6IjE3NjE5MTA0NjM3ODIiLCJjaGFuZ2VEZXNjcmlwdGlvbiI6IkNvbnRyb2wgUmVxdWVzdGVkIiwiY2hhbmdlVGltZXN0YW1wIjoxNzYxOTEwNjgxMTk2fQ==`                                                                             | t        | 2025-10-31 11:38:01.199032+00 |                               |            |
| storage-service||requestchangelog:1761910463782_log_1761910681262 | `eyJyZWNvcmRJZCI6IjE3NjE5MTA0NjM3ODIiLCJjaGFuZ2VEZXNjcmlwdGlvbiI6IlN0YXR1cyB1cGRhdGVkIHRvIFNlbnQgZm9yIHByb3RvY29sIGNvbnZlcnNpb24iLCJjaGFuZ2VUaW1lc3RhbXAiOjE3NjE5MTA2ODEyNjJ9`                                         | t        | 2025-10-31 11:38:01.265917+00 |                               |            |
| storage-service||record:1761910463783                             | `eyJpZCI6IjE3NjE5MTA0NjM3ODMiLCJzZW5zb3JJZCI6InNlbnNvci0wMDQiLCJvcGVyYXRpb24iOiJESVJFQ1QtT0ZGIiwicmVsYXlOdW1iZXIiOjEsImR1cmF0aW9uIjowLCJzdGF0dXMiOiJJbml0aWFsIExvZyBFbnRyeSJ9`                                         | t        | 2025-10-31 11:38:01.21094+00  |                               |            |
| storage-service||requestchangelog:1761910463783                   | `eyJyZWNvcmRJZCI6IjE3NjE5MTA0NjM3ODMiLCJjaGFuZ2VEZXNjcmlwdGlvbiI6IkNvbnRyb2wgUmVxdWVzdGVkIiwiY2hhbmdlVGltZXN0YW1wIjoxNzYxOTEwNjgxMjE1fQ==`                                                                             | t        | 2025-10-31 11:38:01.218565+00 |                               |            |
| storage-service||record:1761910463784                             | `eyJpZCI6IjE3NjE5MTA0NjM3ODQiLCJzZW5zb3JJZCI6InNlbnNvci0wMDQiLCJvcGVyYXRpb24iOiJESVJFQ1QtT0ZGIiwicmVsYXlOdW1iZXIiOjEsImR1cmF0aW9uIjowLCJzdGF0dXMiOiJTdGF0dXMgdXBkYXRlZCB0byBTZW50IGZvciBwcm90b2NvbCBjb252ZXJzaW9uIn0=` | t        | 2025-10-31 11:38:01.293423+00 |                               |            |
| storage-service||requestchangelog:1761910463784                   | `eyJyZWNvcmRJZCI6IjE3NjE5MTA0NjM3ODQiLCJjaGFuZ2VEZXNjcmlwdGlvbiI6IkNvbnRyb2wgUmVxdWVzdGVkIiwiY2hhbmdlVGltZXN0YW1wIjoxNzYxOTEwNjgxMjk5fQ==`                                                                             | t        | 2025-10-31 11:38:01.305079+00 |                               |            |

- Decoded `Base64` values

| Key                                                               | Value (decoded JSON)                                                                                                                                                  | IsBinary | InsertDate                    | UpdateDate                    | ExpireDate |
| ----------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- | ----------------------------- | ----------------------------- | ---------- |
| storage-service||record:1761910463782                             | `json {"id":"1761910463782","sensorId":"sensor-004","operation":"DIRECT-OFF","relayNumber":1,"duration":0,"status":"Sent for protocol conversion"}`                   | t        | 2025-10-31 11:38:01.1916+00   | 2025-10-31 11:38:01.255705+00 |            |
| storage-service||requestchangelog:1761910463782                   | `json {"recordId":"1761910463782","changeDescription":"Control Requested","changeTimestamp":1761910681196}`                                                           | t        | 2025-10-31 11:38:01.199032+00 |                               |            |
| storage-service||requestchangelog:1761910463782_log_1761910681262 | `json {"recordId":"1761910463782","changeDescription":"Status updated to Sent for protocol conversion","changeTimestamp":1761910681262}`                              | t        | 2025-10-31 11:38:01.265917+00 |                               |            |
| storage-service||record:1761910463783                             | `json {"id":"1761910463783","sensorId":"sensor-004","operation":"DIRECT-OFF","relayNumber":1,"duration":0,"status":"Initial Log Entry"}`                              | t        | 2025-10-31 11:38:01.21094+00  |                               |            |
| storage-service||requestchangelog:1761910463783                   | `json {"recordId":"1761910463783","changeDescription":"Control Requested","changeTimestamp":1761910681215}`                                                           | t        | 2025-10-31 11:38:01.218565+00 |                               |            |
| storage-service||record:1761910463784                             | `json {"id":"1761910463784","sensorId":"sensor-004","operation":"DIRECT-OFF","relayNumber":1,"duration":0,"status":"Status updated to Sent for protocol conversion"}` | t        | 2025-10-31 11:38:01.293423+00 |                               |            |
| storage-service||requestchangelog:1761910463784                   | `json {"recordId":"1761910463784","changeDescription":"Control Requested","changeTimestamp":1761910681299}`                                                           | t        | 2025-10-31 11:38:01.305079+00 |                               |            |
