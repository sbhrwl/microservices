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

## Summary

| Service                        | Subscribed Topics                               | Queue Names                                                                                           | 
| ------------------------------ | ----------------------------------------------- | ----------------------------------------------------------------------------------------------------- | 
| **Flexibility Hub Simulator**  | `flexibility-hub.response`                      | `flexibility-hub-simulator-flexibility-hub.response`                                                  | 
| **Flexibility Bridge Service** | `flexibility-hub.request`, `connector.response` | `flexibility-bridge-service-flexibility-hub.request`, `flexibility-bridge-service-connector.response` | 
| **Protocol Adapter Service**   | `connector.request`, `hes.response`             | `protocol-adapter-service-connector.request`, `protocol-adapter-service-hes.response`                 | 
| **HES Simulator**              | `hes.request`                                   | `hes-simulator-hes.request`                                                                           | 
