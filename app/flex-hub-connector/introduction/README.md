# Introduction
- [High level flow](#high-level-flow)
- [Startup](#startup)
- [gRPC client to TenantIdInterceptor](#grpc-client-to-tenantidinterceptor)
- [TenantIdInterceptor to FlexMarketService](#tenantidinterceptor-to-flexmarketservice)
- [FlexMarketService to CommandProcessor](#flexmarketservice-to-commandprocessor)
- [CommandProcessor to RequestDispatcher](#commandprocessor-to-requestdispatcher)
- [RequestDispatcher to Camel route](#requestdispatcher-to-camel-route)
- [Camel route to SOAP endpoint](#camel-route-to-soap-endpoint)

## High level flow
```mermaid
flowchart TD
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
flowchart TD
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
flowchart TD
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

## TenantIdInterceptor to FlexMarketService
```mermaid
flowchart TD
    %% Handoff 2: TenantIdInterceptor to FlexMarketService

    classDef inbound fill:#ecfdf5,stroke:#86efac,color:#111827
    classDef app fill:#fefce8,stroke:#fde68a,color:#111827
    classDef note fill:#fff7d6,stroke:#eab308,color:#111827

    Context["Tenant id in Context"] --> Service["FlexMarketService.sendCommand()"]
    Service --> ReadTenant["Read Tenant-Id from Context"]
    ReadTenant --> DomainRequest["Create SendMessageRequest"]
    DomainRequest --> Gap["Current gap:<br/>request fields are not populated"]

    class Context,Service,ReadTenant inbound
    class DomainRequest app
    class Gap note
```

## FlexMarketService to CommandProcessor
```mermaid
flowchart TD
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

## CommandProcessor to RequestDispatcher
```mermaid
flowchart TD
    %% Handoff 4: CommandProcessor to RequestDispatcher

    classDef app fill:#fefce8,stroke:#fde68a,color:#111827
    classDef outbound fill:#eef2ff,stroke:#c7d2fe,color:#111827

    Processor["CommandProcessor"] --> Dispatcher["RequestDispatcher.dispatch(...)"]
    Dispatcher --> SoapType["Build SendMessageRequestType"]
    SoapType --> Container["Create MessageContainer"]
    Container --> Payload["Create SendMessageRequestMessageType"]
    Payload --> Event["Add MasterDataMPEventMessage payload"]

    class Processor app
    class Dispatcher,SoapType,Container,Payload,Event outbound
```

## RequestDispatcher to Camel route
```mermaid
flowchart TD
    %% Handoff 5: RequestDispatcher to Camel route

    classDef outbound fill:#eef2ff,stroke:#c7d2fe,color:#111827

    Dispatcher["RequestDispatcher"] --> Producer["ProducerTemplate.requestBodyAndHeaders"]
    Producer --> Direct["Endpoint: direct:soap"]
    Producer --> Header["Header: operationName = sendMessage"]
    Direct --> Route["OutboundCamelRouteBuilder"]

    class Dispatcher,Producer,Direct,Header,Route outbound
```

## Camel route to SOAP endpoint
```mermaid
flowchart TD
    %% Handoff 6: Camel route to SOAP endpoint

    classDef outbound fill:#eef2ff,stroke:#c7d2fe,color:#111827
    classDef external fill:#fce7f3,stroke:#f9a8d4,color:#111827

    Route["Camel route direct:soap"] --> Cxf["CXF SOAP Client"]
    Cxf --> ServiceClass["MarketMessagingB2BInboundServiceV01PortType"]
    ServiceClass --> Endpoint["http://localhost:9090/soap/FGR"]
    Endpoint --> Response["SendMessageResponseType"]
    Response --> Log["Log documentReferenceNumber"]

    class Route,Cxf,ServiceClass,Response,Log outbound
    class Endpoint external
```
