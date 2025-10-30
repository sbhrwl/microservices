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
- `dapr run --app-id storage-service --app-port 0 --resources-path .\dapr\config-files -- mvn spring-boot:run`
- `dapr run --app-id flexibility-bridge-service --app-port 0 --resources-path .\dapr\config-files -- mvn spring-boot:run`
- `dapr run --app-id protocol-adapter-service --app-port 0 --resources-path .\dapr\config-files -- mvn spring-boot:run`
- `dapr run --app-id flexibility-hub-simulator --app-port 8081 --resources-path .\dapr\config-files -- mvn spring-boot:run`
- `dapr run --app-id hes-simulator --app-port 0 --resources-path .\dapr\config-files -- mvn spring-boot:run`
- `dapr run --app-id data-api-service --app-port 8085 --resources-path .\dapr\config-files -- mvn spring-boot:run`
