# Application
- [Project structure](#project-structure)
- [Purpose](#purpose)
- [Key dependencies](#key-dependencies)
- [Build and run](#build-and-run)
## Project structure

| Folder/File | Purpose |
|-------------|---------|
| `src/main/java/com/landisgyr/gfc/` | Core Java application code |
| `src/main/java/com/landisgyr/gfc/di/` | Dependency injection components (Dagger) |
| `src/main/java/com/landisgyr/gfc/grpc/` | gRPC service implementations |
| `src/main/java/com/landisgyr/gfc/server/` | Server bootstrap and configuration |
| `src/main/java/com/landisgyr/gfc/services/` | Business logic services |
| `src/main/java/com/landisgyr/gfc/monitoring/` | Health checks and monitoring |
| `src/main/resources/com/landisgyr/gfc/` | Application resources (configs, properties) |
| `src/main/dist/etc/` | Distribution configs (application.conf, logback.xml) |
| `src/test/` | Test code and resources |
| `pom.xml` | Maven build configuration |
| `Dockerfile` / `Dockerfile-dev` | Container images for prod/dev |

## Purpose
- **Smart meter operations center backend**
- Provides CRUD operations via gRPC
- Handles devices, work orders, organizations, tags
- Integrates with Keycloak for authentication
- Uses MongoDB for persistence
- Runs with Dapr sidecar for microservices features

## Key dependencies
- **gRPC 1.77.0** - RPC framework
- **Dapr SDK 1.16.0** - Distributed application runtime
- **MongoDB Driver 5.6.2** - Database client
- **Keycloak 26.x** - Identity/access management
- **Dagger 2.57.2** - Dependency injection
- **Netty 4.2.8** - Network transport
- **Logback/SLF4J** - Logging
- **Typesafe Config** - Configuration management

## Build and run
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
- Test with grpcurl or 
