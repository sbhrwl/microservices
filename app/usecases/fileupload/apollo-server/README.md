# Apollo server
- [Introduction](#introduction)
## Introduction
- Top to bottom is `request flow`
- Left to right is `responsibility split`
- Apollo Server is the `orchestrator`, not the business logic
- `Resolvers` are the only place where **real work** happens
- `Fastify` is just the `transport shell`
<img src="images/apollo-server-1.jpg">

<details>
  <summary>mermaid</summary>

```mermaid
flowchart TD
    Client["Client (Web / Mobile / Service)"]

    subgraph HTTP["HTTP layer"]
        Fastify["Fastify server"]
        Routes["REST routes"]
    end

    subgraph Apollo["Apollo Server"]
        Schema["Type definitions (type-defs.ts)"]
        ResolverMap["Resolver map (resolvers.ts)"]
        Context["Context (auth, headers, clients)"]
        Plugins["Plugins (security, logging)"]
    end

    subgraph Logic["Resolver logic"]
        Resolvers["Resolver implementations (resolver-definitions)"]
    end

    subgraph Data["Data sources"]
        Dapr["Dapr services"]
        GRPC["gRPC clients (generated)"]
        External["External APIs"]
    end

    Client --> Fastify
    Client --> Routes

    Fastify --> Apollo
    Apollo --> Schema
    Apollo --> ResolverMap
    Apollo --> Context
    Apollo --> Plugins

    ResolverMap --> Resolvers
    Resolvers --> Dapr
    Resolvers --> GRPC
    Resolvers --> External
```
</details>
