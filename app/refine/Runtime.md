# Run time
- [Startup sequence](#runtime-overview--startup-sequence)
- [Startup flow](#startup-flow)
- [Dependency injection wiring](#dependency-injection-wiring)
- [Shutdown sequence](#shutdown-sequence)
## Runtime overview & startup sequence
- Service lifecycle follows a strict initialization sequence ensuring all dependencies are wired before accepting requests
- Entry point is `Bootstrap.main()` which delegates to `Server.run()` for actual startup logic
- Configuration loading uses `Typesafe Config` with environment variable overrides (`-Dconfig.file` system property)
- Dependency injection happens via `Dagger` at compile time, creating `ApplicationComponent` with `GrpcModule` and `AppModule`
- gRPC server initialization requires Netty executors (boss/worker thread pools), MongoDB client, and health indicators
- Graceful shutdown ensures in-flight requests complete before termination
## Startup flow
- `Bootstrap.main()` entry point initializes logging bridge (`SLF4JBridgeHandler`) and instantiates `Server`
- `Server.run()` loads configuration from `ConfigFactory` (system properties → environment → classpath)
- Configuration validation checks for `gfc` root path, throws `ConfigException.Missing` if absent
- `DaggerApplicationComponent.factory().create(config)` builds the dependency graph
- `ApplicationComponent` injects dependencies into `Server` instance: `io.grpc.Server`, `MongoClient`, `ReadinessHealthIndicator`, `GitInfoManager`
- `GrpcModule` provides Netty executors and builds `GrpcServerComponent` with all gRPC services
- `GrpcServerComponent` wires all service implementations (`DeviceServiceImpl`, `EventServiceImpl`, etc.) and interceptors
- `server.start()` binds to configured port (default `9090`) and begins accepting connections
- `GracefulShutdownHook` registered as JVM shutdown hook via `Runtime.addShutdownHook()`
- `server.awaitTermination()` blocks main thread until shutdown signal received
```mermaid
sequenceDiagram
    participant Operator
    participant JVM
    participant Bootstrap
    participant Server
    participant ConfigFactory
    participant ApplicationComponent
    participant GrpcModule
    participant GrpcServerComponent
    participant GrpcServer
    participant ShutdownHook
    Operator->>JVM: java -jar gfc-service.jar
    JVM->>Bootstrap: main(String[] args)
    Bootstrap->>Bootstrap: SLF4JBridgeHandler.install()
    Bootstrap->>Server: new Server().run()
    Server->>ConfigFactory: loadConfig()
    ConfigFactory-->>Server: Config
    Server->>ApplicationComponent: DaggerApplicationComponent.factory().create(config)
    ApplicationComponent->>GrpcModule: provideGrpcServer()
    GrpcModule->>GrpcModule: Create Netty executors
    GrpcModule->>GrpcServerComponent: Builder.nettyServerModule().build()
    GrpcServerComponent->>GrpcServerComponent: Wire all gRPC services
    GrpcServerComponent-->>GrpcModule: io.grpc.Server
    GrpcModule-->>ApplicationComponent: io.grpc.Server
    ApplicationComponent->>Server: inject(this)
    Server->>GrpcServer: server.start()
    GrpcServer-->>Server: Server listening on port
    Server->>ShutdownHook: addShutdownHook(gracefulShutdownHook)
    Server->>GrpcServer: awaitTermination()
    Note over GrpcServer: Accepting requests
```
## Dependency injection wiring
- `ApplicationComponent` is the root component with `@AppScope` lifecycle
- `GrpcModule` provides Netty thread pool executors (boss/worker) and builds `GrpcServerComponent`
- `AppModule` provides MongoDB client, health status managers, and application settings
- `GrpcServerComponent` is a subcomponent that wires all gRPC service implementations
- Each gRPC service (`*ServiceImpl`) has its own Dagger-generated service module
- Interceptors (`AuthorizationInterceptor`, `ExceptionHandlerInterceptor`, `LatencyLimiterInterceptor`) are provided via `InterceptorsModule`
- All dependencies are resolved at compile time by Dagger, ensuring type safety
## Shutdown sequence
- JVM shutdown signal triggers registered `GracefulShutdownHook` thread
- `healthIndicator.applicationShutdownStarted()` marks all services as `NOT_SERVING` via `HealthStatusManager.enterTerminalState()`
- `server.shutdown()` initiates graceful shutdown, stops accepting new requests
- `server.awaitTermination(5, TimeUnit.SECONDS)` waits for in-flight requests to complete
- If termination not complete within 5 seconds, `server.shutdownNow()` forces immediate shutdown
- Final `awaitTermination(2, TimeUnit.SECONDS)` allows resource cleanup
- Health status managers enter terminal state, preventing new health check requests from returning `SERVING`
