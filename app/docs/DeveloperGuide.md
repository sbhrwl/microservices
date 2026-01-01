# Developer Guide
- [Build and run](#build-and-run)
## Build and run
- **Compile**: `mvn clean compile`,
- **Build:**
```bash
mvn clean install -DskipTests=true -D"checkstyle.skip"=true
```
- **Run (with Dapr):**
```bash
# Set environment
$env:DAPR_GRPC_PORT=50012

# Single command
dapr run --enable-profiling --app-id gfc-service --app-port 9090 --app-protocol grpc --dapr-grpc-port 50012 -- mvn exec:java -D"config.file"=src/main/dist/etc/application.conf -D"logback.configurationFile"=src/main/dist/etc/logback.xml -D"log.appender"=STDOUT
```
- **Access:**
  - gRPC endpoint: `localhost:9090`
  - Test with `grpcurl`
- **Adding a new gRPC service**:
  - Protobuf, `*ServiceImpl`, Dagger wiring (`GrpcServerComponent`, modules), DAOs, domain objects.
- **Extending queries**:
  - Adding new filters, updating `AggregationPipelineBuilder` and filter visitors.
- Config hints, dependencies like MongoDB/Dapr/Keycloak.