# Run time
- [Introduction](#Introduction)
- [Bootstrap & configuration](#bootstrap--configuration)
- [Dependency injection wiring](#dependency-injection-wiring)
- [Server startup](#server-startup)
- [Shutdown sequence](#shutdown-sequence)
## Introduction
- Service lifecycle follows **`strict initialization sequence`** `ensuring dependencies wired` before accepting requests
- Entry point is `Bootstrap.main()` which delegates to `Server.run()` for startup logic
- Configuration loading uses `Typesafe Config` with environment variable overrides (`-Dconfig.file` system property)
- Dependency injection happens via `Dagger` at compile time, creating `ApplicationComponent` with 
  - **`GrpcModule`** and 
  - **`AppModule`**
- gRPC server initialization requires
  - `Netty executors` (boss/worker thread pools),
  - `MongoDB client`
  - `Health indicators`
- Graceful shutdown ensures `in-flight requests completion` before termination
## Bootstrap & configuration
- `Bootstrap.main()` entry point initializes logging bridge (`SLF4JBridgeHandler`) and instantiates `Server`
- `Server.run()` loads configuration from `ConfigFactory` (system properties → environment → classpath)
- Configuration validation checks for `gfc` root path, throws `ConfigException.Missing` if absent
<img src="images/runtime-1.jpg">

<details>
  <summary>mermaid</summary>

```mermaid
sequenceDiagram
    participant Operator
    participant JVM
    participant Bootstrap
    participant Server
    participant ConfigFactory
    Operator->>JVM: java -jar gfc-service.jar
    JVM->>Bootstrap: main(String[] args)
    Bootstrap->>Bootstrap: SLF4JBridgeHandler.install()
    Bootstrap->>Server: new Server().run()
    Server->>ConfigFactory: loadConfig()
    ConfigFactory->>ConfigFactory: systemEnvironmentOverrides()
    ConfigFactory->>ConfigFactory: withFallback(ConfigFactory.load())
    ConfigFactory-->>Server: Config
    Server->>Server: validate gfc root path
```
</details>

## Dependency injection wiring
- `DaggerApplicationComponent.factory().create(config)` builds the dependency graph
- `ApplicationComponent` injects dependencies into `Server` instance: `io.grpc.Server`, `MongoClient`, `ReadinessHealthIndicator`, `GitInfoManager`
- `GrpcModule` provides Netty executors and builds `GrpcServerComponent` with all gRPC services
- `GrpcServerComponent` wires all service implementations (`DeviceServiceImpl`, `EventServiceImpl`, etc.) and interceptors
<img src="images/runtime-2.jpg">

<details>
  <summary>mermaid</summary>

```mermaid
flowchart TD
    subgraph "application startup"
        Bootstrap["Bootstrap"]
        Server["Server"]
    end

    subgraph "dependency injection"
        AppComponent["ApplicationComponent"]
        DaggerFactory["DaggerApplicationComponent.factory()"]
    end

    subgraph "grpc infrastructure"
        GrpcModule["GrpcModule"]
        GrpcServerComponent["GrpcServerComponent"]
    end

    subgraph "grpc services"
        DeviceService["DeviceServiceImpl"]
        EventService["EventServiceImpl"]
        Interceptors["gRPC interceptors"]
    end

    Bootstrap -->|"run(config)"| Server

    Server -->|"create(config)"| DaggerFactory
    DaggerFactory -->|"build graph"| AppComponent

    AppComponent -->|"inject"| Server

    AppComponent -->|"provide executors"| GrpcModule
    GrpcModule -->|"build"| GrpcServerComponent

    GrpcServerComponent --> DeviceService
    GrpcServerComponent --> EventService
    GrpcServerComponent --> Interceptors
```
</details>

## Server startup
- `server.start()` binds to configured port (default `9090`) and begins accepting connections
- `GracefulShutdownHook` registered as JVM shutdown hook via `Runtime.addShutdownHook()`
- `server.awaitTermination()` blocks main thread until shutdown signal received
<img src="images/runtime-3.jpg">

<details>
  <summary>mermaid</summary>

```mermaid
sequenceDiagram
    participant Server
    participant GrpcServer
    participant ShutdownHook
    participant Runtime
    Server->>GrpcServer: server.start()
    GrpcServer-->>Server: Server listening on port 9090
    Server->>ShutdownHook: new GracefulShutdownHook()
    Server->>Runtime: addShutdownHook(gracefulShutdownHook)
    Server->>GrpcServer: awaitTermination()
    Note over GrpcServer: Accepting gRPC requests
```
</details>

## Shutdown sequence
- JVM shutdown signal triggers registered `GracefulShutdownHook` thread
- `healthIndicator.applicationShutdownStarted()` marks all services as `NOT_SERVING` via `HealthStatusManager.enterTerminalState()`
- `server.shutdown()` initiates graceful shutdown, stops accepting new requests
- `server.awaitTermination(5, TimeUnit.SECONDS)` waits for in-flight requests to complete
- If termination not complete within 5 seconds, `server.shutdownNow()` forces immediate shutdown
- Final `awaitTermination(2, TimeUnit.SECONDS)` allows resource cleanup
<img src="images/runtime-4.jpg">

<details>
  <summary>mermaid</summary>

```mermaid
sequenceDiagram
    participant JVM
    participant ShutdownHook
    participant HealthIndicator
    participant GrpcServer
    JVM->>ShutdownHook: Shutdown signal received
    ShutdownHook->>HealthIndicator: applicationShutdownStarted()
    HealthIndicator->>HealthIndicator: enterTerminalState()
    HealthIndicator->>HealthIndicator: Set all services NOT_SERVING
    ShutdownHook->>GrpcServer: server.shutdown()
    GrpcServer->>GrpcServer: Stop accepting new requests
    ShutdownHook->>GrpcServer: awaitTermination(5s)
    alt Requests complete within 5s
        GrpcServer-->>ShutdownHook: Terminated
    else Timeout
        ShutdownHook->>GrpcServer: shutdownNow()
        ShutdownHook->>GrpcServer: awaitTermination(2s)
    end
```
</details>
