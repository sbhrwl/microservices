# Application
- [Project structure](#project-structure)
- [Purpose](#purpose)
- [Dependencies](#dependencies)
- [Build and run](#build-and-run)
- [Refine](refine/README.md)
## Project structure

| Folder/File | Purpose |
|-------------|---------|
| `gfc/` | Core Java application code |
| `gfc/di/` | Dependency injection components using Dagger 2; defines application-wide and gRPC server components with module bindings |
| `gfc/grpc/` | gRPC service implementations (Device, Authorization, Event, Organization, Tag, Revision services) and interceptors |
| `gfc/server/` | Server bootstrap, configuration loading, graceful shutdown hooks, and main application runner |
| `gfc/services/` | Business logic services (queries, commands, authorization, git info management) |
| `gfc/dao/` | Data Access Objects for MongoDB operations and authorization role mappings |
| `gfc/domain/` | Domain models and business entities (Device, Organization, OperationContext, Group, etc.) |
| `gfc/keycloak/` | Keycloak integration utilities for JWT token validation, security context, and RBAC |
| `gfc/rbac/` | Role-Based Access Control logic and permission checking |
| `gfc/monitoring/` | Health checks (readiness/liveness), MongoDB connection monitoring, Dapr health integration |
| `gfc/exceptions/` | Custom exception classes (AccessDeniedException, ResourceNotFoundException, etc.) |
| `src/main/resources/com/landisgyr/gfc/` | Application resources: manufacturer mappings, configuration files, static data |
| `src/main/dist/etc/` | Distribution configs: `application.conf` (Typesafe Config), `logback.xml` (logging configuration) |
| `src/test/` | Unit and integration tests with test-specific configurations |
| `pom.xml` | Maven build configuration with dependencies, plugins, and build profiles |
| `Dockerfile` / `Dockerfile-dev` | Container images for production and development environments |
| `envfile.env` | Environment variables for containerized deployments |

## Purpose
- Provides CRUD operations via gRPC
- Handles devices, work orders, organizations, tags
- Integrates with Keycloak for authentication
- Uses MongoDB for persistence
- Runs with Dapr sidecar for microservices features

## Dependencies
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
