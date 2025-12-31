# Configuration & Environments
- [Configuration sources](#configuration-sources)
  - [HOCON configuration files](#hocon-configuration-files)
  - [Environment variable overrides](#environment-variable-overrides)
  - [System property overrides](#system-property-overrides)
- [ApplicationSetting structure](#applicationsetting-structure)
  - [GrpcServer configuration](#grpcserver-configuration)
  - [Mongodb configuration](#mongodb-configuration)
  - [Keycloak configuration](#keycloak-configuration)
  - [Authorization configuration](#authorization-configuration)
  - [Feature flags](#feature-flags)
- [MongoDB connection](#mongodb-connection)
- [Dapr and external endpoints](#dapr-and-external-endpoints)
- [Docker and deployment](#docker-and-deployment)
  - [Production Dockerfile](#production-dockerfile)
  - [Development Dockerfile](#development-dockerfile)
  - [Environment file](#environment-file)
- [Deployment flow](#deployment-flow)

## Configuration sources
- Configuration uses Typesafe Config library with HOCON (Human-Optimized Config Object Notation) format
- Configuration resolution order: system properties → environment variables → classpath `application.conf` → defaults
- Configuration loading: `ConfigFactory.systemEnvironmentOverrides().withFallback(ConfigFactory.load())`
- External config file: `-Dconfig.file=path/to/application.conf` system property overrides classpath config
- Configuration validation: `gfc` root path must exist, throws `ConfigException.Missing` if absent
- Configuration logging: resolved configuration logged at startup (without secrets) for troubleshooting

## HOCON configuration files
- Primary config: `src/main/dist/etc/application.conf` (packaged in JAR and Docker image)
- MongoDB secrets: `src/main/dist/etc/mongodb/mongodb-secret.conf` (included via `include` directive)
- Configuration includes: MongoDB credentials loaded from separate file for security
- Default values: all configuration properties have defaults defined in `ApplicationSetting` record constructors
- Configuration structure: nested HOCON objects map to nested `ApplicationSetting` records

## Environment variable overrides
- Environment variables override HOCON values using `${?VARIABLE_NAME}` syntax
- MongoDB variables: `MONGODB_DB`, `MONGODB_HOST`, `MONGODB_USERNAME`, `MONGODB_PASSWORD`, `MONGODB_OPTIONS`, `MONGODB_URI_PREFIX`, `MONGODB_SEARCH_STRATEGY`
- gRPC variables: `SMOC_SERVER_PORT`, `MAX_INBOUND_MESSAGE_SIZE`, `BOSS_THREAD_POOL_COUNT`, `WORKER_THREAD_POOL_COUNT`, `LATENCY_LIMITER_THRESHOLD_MILLIS`
- Keycloak variables: `KEYCLOAK_PROFILE`, `KEYCLOAK_ISSUED_FOR`, `KEYCLOAK_SERVER_URL`, `KEYCLOAK_ALLOWED_REALMS`, `KEYCLOAK_ACCESS_KEY`
- Device identifier: `SMOC_DEVICE_IDENTIFIER` (default: `"SerialNumber"`)
- Dapr variables: `DAPR_GRPC_PORT`, `DAPR_API_TIMEOUT_MILLISECONDS`

## System property overrides
- System properties override environment variables and HOCON config
- Config file: `-Dconfig.file=path/to/application.conf` specifies external configuration file
- Logging config: `-Dlogback.configurationFile=path/to/logback.xml` for Logback configuration
- JVM logging: `-Djava.util.logging.config.file=path/to/logging.properties` for JUL configuration
- Log appender: `-Dlog.appender=STDOUT` for console logging in development
- System property format: `-Dproperty.name=value` passed to JVM at startup

## ApplicationSetting structure
- Immutable record hierarchy: `ApplicationSetting` → `Smoc` → nested records for each subsystem
- Type safety: all configuration values strongly typed (no string-based property access)
- Default values: all optional properties have defaults, configuration never null
- Configuration mapping: HOCON objects automatically mapped to record constructors
- Nested structure: `gfc.grpcServer`, `gfc.mongodb`, `gfc.keycloak`, `gfc.authorization`, `gfc.featureFlags`

## GrpcServer configuration
- Port: `listen` (default: `9090`), overridden by `SMOC_SERVER_PORT` environment variable
- Thread pools: `bossThreadpoolCount` (default: CPU cores), `workerThreadpoolCount` (default: CPU cores × 5)
- Message size: `maxInboundMessageSize` (default: `8 MiB`), overridden by `MAX_INBOUND_MESSAGE_SIZE`
- Latency limiter: `latencyLimiterThreshold` (default: `10000 ms`), overridden by `LATENCY_LIMITER_THRESHOLD_MILLIS`
- Thread pool configuration: Netty boss thread pool for connection acceptance, worker pool for request processing

## Mongodb configuration
- Connection URI: constructed from `connection-uri-prefix`, `username`, `password`, `host`, `db`, `options`
- URI prefix: `mongodb+srv://` (default) or `mongodb://` (local), overridden by `MONGODB_URI_PREFIX`
- Database name: `db` (default: `"gfc-dev"`), overridden by `MONGODB_DB`
- Authentication: `authMechanism` (default: `"SCRAM-SHA-256"`), credentials from `mongodb-secret.conf`
- Connection options: `options` string includes auth mechanism, heartbeat, write concern, retry writes, compression
- Heartbeat: `heartbeatFrequencyMS` (default: `10000`), configurable for connection monitoring
- Search strategy: `searchStrategy` enum (`PRIMARY` or `ATLAS`), default `PRIMARY`, overridden by `MONGODB_SEARCH_STRATEGY`
- Write concern: `w=majority` for data durability, `retryWrites=true` for automatic retry

## Keycloak configuration
- Profile: `profile` (default: `"prod"`), `"dev"` disables authentication, overridden by `KEYCLOAK_PROFILE`
- Server URL: `serverUrl` (required), Keycloak base URL, overridden by `KEYCLOAK_SERVER_URL`
- Expected audience: `expectedAudience` (default: `"account"`), JWT `aud` claim validation
- Expected issued for: `expectedIssuedFor` (CSV list), JWT `azp` claim validation, overridden by `KEYCLOAK_ISSUED_FOR`
- Allowed realms: `allowedRealms` (CSV list), realm whitelist for multi-tenant support, overridden by `KEYCLOAK_ALLOWED_REALMS`
- Resource access key: `resourceAccessKey` (required), key for role lookup in token `resourceAccess` map, overridden by `KEYCLOAK_ACCESS_KEY`

## Authorization configuration
- Method roles: `methodRoles` map (method FQN → list of required roles), configured in `authorization.methodRoles` list
- App roles: `appRoles` map (app name → list of methods), legacy mapping for frontend compatibility
- Method FQN format: `"core.api.{service}.{ServiceName}/{MethodName}"` (e.g., `"core.api.device.v1.DeviceService/GetDevice"`)
- Role mapping: each method can require multiple roles (OR logic: user needs any one role)
- Configuration structure: HOCON list of objects with `method` and `role` fields

## Feature flags
- Feature flags: `featureFlags` map (feature name → boolean), configured in `feature-flags` HOCON object
- Feature check: `FeatureFlags.isActive(featureName)` returns boolean if feature enabled
- Default: empty map if no feature flags configured
- Use case: gradual feature rollouts, A/B testing, environment-specific feature toggles

## MongoDB connection
- Connection string format: `mongodb+srv://username:password@host/db?options` or `mongodb://username:password@host/db?options`
- Options string: includes `authMechanism`, `heartbeatFrequencyMS`, `w` (write concern), `retryWrites`, `connectTimeoutMS`, `maxPoolSize`, `compressors`
- Compression: supports `zstd`, `snappy`, `zlib` compressors for network efficiency
- Connection pooling: `maxPoolSize` (default: 250) for concurrent connection management
- Proxy support: `proxyHost`, `proxyPort`, `proxyUsername`, `proxyPassword` for corporate proxy environments
- Health monitoring: `ReadinessHealthIndicator` implements `ServerMonitorListener` for connection health tracking
- Connection URI construction: built from components at runtime, supports both Atlas and local MongoDB

## Dapr and external endpoints
- Dapr sidecar: runs alongside service for service mesh integration, health checks, and distributed tracing
- Dapr gRPC port: `DAPR_GRPC_PORT` environment variable (default: `50012`), used for service-to-service communication
- Dapr API timeout: `DAPR_API_TIMEOUT_MILLISECONDS` (default: `30000`), timeout for Dapr API calls
- Service port: gRPC server listens on port `9090` (configurable via `SMOC_SERVER_PORT`)
- Dapr caller bypass: requests from `device-hub` Dapr caller skip authentication (service-to-service trust)
- Health endpoints: Dapr health check via `DaprHealthServiceImpl`, standard gRPC health via `GrpcHealthServiceImpl`
- Sidecar expectations: Dapr sidecar must be started with `--app-id gfc-service --app-port 9090 --app-protocol grpc --dapr-grpc-port 50012`

## Docker and deployment
- Multi-stage builds: `Dockerfile-dev` uses Maven build stage, `Dockerfile` uses pre-built JAR
- Base images: `eclipse-temurin:25-jre-alpine` (production), `eclipse-temurin:25-jre` (development)
- User security: non-root user `gfc-service` in `apps` group for container security
- Artifact copying: JAR, libs, configuration files, and protobuf descriptor set copied to `/app`
- Port exposure: `9090` (gRPC), `5005` (debug port in dev Dockerfile)
- JVM options: server mode, IPv4 preference, config file path, logging configuration

## Production Dockerfile
- Base image: `eclipse-temurin:25-jre-alpine` (minimal Alpine Linux for smaller image size)
- Artifacts: expects pre-built JAR and libs in `target/` directory
- Files copied: JAR, libs, `application.conf`, `logback.xml`, `logging.properties`, `mongodb-secret.conf`, protobuf descriptor
- Protobuf descriptor: `apis_descriptor_set.pb` copied for gRPC reflection and Envoy proxy configuration
- Working directory: `/app` with non-root user execution
- Command: JVM with config file, logging, server mode, IPv4 preference

## Development Dockerfile
- Multi-stage build: Maven build stage compiles and packages application
- Build stage: copies `gfc-apis` and `gfc-service` source, runs `mvn clean package`
- Runtime stage: `eclipse-temurin:25-jre` base image (not Alpine for better compatibility)
- Debug port: exposes port `5005` for remote debugging with `-agentlib:jdwp`
- Git plugin: `-Dgit.plugin.skip=true` to skip Git commit ID generation in Docker build
- Test skipping: `-DskipTests` to speed up build process

## Environment file
- Development config: `envfile.env` contains local development environment variables
- MongoDB config: connection details for local or proxy-based MongoDB Atlas access
- Keycloak config: development Keycloak server URL, client ID, realm configuration
- Dapr config: Dapr gRPC port and API timeout settings
- JVM options: example JVM options for IntelliJ IDEA run configurations
- Secret management: MongoDB credentials stored in separate `mongodb-secret.conf` file

## Deployment flow
- CI/CD pipeline builds Docker image, configures environment-specific settings, deploys to runtime
- Configuration injection happens at container startup via environment variables
- Service mesh integration via Dapr sidecar for distributed system features
<img src="images/config-1.jpg">

<details>
  <summary>mermaid</summary>

```mermaid

flowchart TD
    subgraph "CI/CD pipeline"
        CI["CI/CD system"]
        BUILD["Docker build"]
        REG["Container registry"]
    end

    subgraph "configuration"
        ENV["Environment config (Kubernetes secrets and ConfigMaps)"]
        CONF["application.conf and environment variables"]
    end

    subgraph "runtime"
        K8S["Kubernetes cluster"]
        POD["Pod: gfc-service"]
        DAPR["Dapr sidecar"]
        APP["gfc-service container"]
    end

    subgraph "external services"
        MDB[("MongoDB")]
        KC["Keycloak"]
    end

    CI -->|"Source code and gfc-apis"| BUILD
    BUILD -->|"Maven build"| BUILD
    BUILD -->|"Docker image creation"| BUILD
    BUILD -->|"Push image"| REG
    REG -->|"Pull image"| K8S

    ENV -->|"Inject env vars"| POD
    K8S -->|"Deploy pod"| POD
    POD -->|"Start container"| APP
    POD -->|"Start sidecar"| DAPR

    APP -->|"Load configuration"| CONF
    APP -->|"Connect"| MDB
    APP -->|"Validate tokens"| KC
    DAPR -->|"Health checks"| APP

    style BUILD fill:#4A90E2,color:#ffffff
    style APP fill:#47A248,color:#ffffff
    style DAPR fill:#0D7377,color:#ffffff
```
</details>
