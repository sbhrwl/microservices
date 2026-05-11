# Introduction
- [High level flow](#high-level-flow)
- [Startup](#startup)
- [gRPC client to TenantIdInterceptor](#grpc-client-to-tenantidinterceptor)
- [FlexMarketService to CommandProcessor](#flexmarketservice-to-commandprocessor)
## High level flow
```mermaid
flowchart LR
    %% High-level component flow

    classDef inbound fill:#ecfdf5,stroke:#86efac,color:#111827
    classDef app fill:#fefce8,stroke:#fde68a,color:#111827
    classDef outbound fill:#eef2ff,stroke:#c7d2fe,color:#111827
    classDef external fill:#fce7f3,stroke:#f9a8d4,color:#111827

    Client["gRPC Client"] --> Inbound["Inbound gRPC Adapter"]
    Inbound --> App["Application Layer"]
    App --> Outbound["Outbound SOAP Adapter"]
    Outbound --> Datahub["Fingrid Datahub SOAP Endpoint"]

    class Client,Datahub external
    class Inbound inbound
    class App app
    class Outbound outbound
```
## Startup
```mermaid
flowchart LR
    %% Startup flow

    classDef startup fill:#f1f5f9,stroke:#cbd5e1,color:#111827

    Bootstrap["Bootstrap.main()"] --> Main["Main.run()"]
    Main --> Config["Load application.conf"]
    Config --> Dagger["Build Dagger component"]
    Dagger --> Camel["Start Camel context"]
    Dagger --> Grpc["Start gRPC server"]

    class Bootstrap,Main,Config,Dagger,Camel,Grpc startup
```
## gRPC client to TenantIdInterceptor
```mermaid
flowchart LR
    %% Handoff 1: gRPC client to TenantIdInterceptor

    classDef inbound fill:#ecfdf5,stroke:#86efac,color:#111827
    classDef external fill:#fce7f3,stroke:#f9a8d4,color:#111827

    Client["Client calls sendCommand(Empty)<br/>metadata: Tenant-Id"] --> Server["gRPC Server"]
    Server --> Interceptor["TenantIdInterceptor checks metadata"]
    Interceptor --> Present{"Tenant-Id present?"}
    Present -- "No" --> Reject["Reject: INVALID_ARGUMENT"]
    Present -- "Yes" --> Context["Store tenant id in gRPC Context"]

    class Client external
    class Server,Interceptor,Present,Reject,Context inbound
```
## FlexMarketService to CommandProcessor
```mermaid
flowchart LR
    %% Handoff 3: FlexMarketService to CommandProcessor

    classDef inbound fill:#ecfdf5,stroke:#86efac,color:#111827
    classDef app fill:#fefce8,stroke:#fde68a,color:#111827
    classDef outbound fill:#eef2ff,stroke:#c7d2fe,color:#111827
    classDef note fill:#fff7d6,stroke:#eab308,color:#111827

    Service["FlexMarketService"] --> Processor["CommandProcessor.sendCommand(request)"]
    Processor --> Intended["Intended: lookup network by tenant"]
    Processor --> Gap["Current code:<br/>lookup is commented out"]
    Processor --> DispatcherCall["Calls RequestDispatcher.dispatch(xx, null)"]

    class Service inbound
    class Processor,Intended app
    class Gap note
    class DispatcherCall outbound
```
