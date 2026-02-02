# Apollo server
- [Introduction](#introduction)
- [Structure](#structure)
- [Apollo Server responsibilities](#apollo-server-responsibilities)
- [Startup flow](#startup-flow)
- [Directory-to-responsibility map](#directory-to-responsibility-map)
- [Request lifecycle](#request-lifecycle)
- [Data access paths](#data-access-paths)
- [What Apollo Server does not own](#what-apollo-server-does-not-own)
- [Minimal mental model](#minimal-mental-model)
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
    ResolverMap --> Resolvers
    Resolvers --> Dapr
    Resolvers --> GRPC
    Resolvers --> External
```
</details>

## Structure
```
api-gateway/
│
├── Configuration Files
│   ├── Dockerfile                      # Docker container configuration
│   ├── README.md                       # Project documentation
│   ├── biome.json                      # Biome linter/formatter config
│   ├── codegen.yml                     # GraphQL code generation config
│   ├── package.json                    # NPM dependencies and scripts
│   ├── package-lock.json               # NPM dependency lock file
│   ├── tsconfig.json                   # TypeScript compiler configuration
│   ├── protoc.bat                      # Protocol buffer compilation (Windows)
│   └── protoc.sh                       # Protocol buffer compilation (Linux)
│
├── src/                                # Source code directory
│   ├── index.ts                        # Application entry point
│   ├── resolvers.ts                    # GraphQL resolvers
│   ├── type-defs.ts                    # GraphQL type definitions
│   │
│   ├── common/                         # Shared utilities and configurations
│   │   ├── config.ts                   # Application configuration
│   │   ├── dapr-client.ts              # Dapr client setup
│   │   ├── to-date-time.ts             # DateTime conversion utility
│   │   ├── to-date.ts                  # Date conversion utility
│   │   │
│   │   └── scalars/                    # GraphQL custom scalar types
│   │       ├── date-time.ts            # DateTime scalar
│   │       ├── date.ts                 # Date scalar
│   │       ├── json-map-scalar.ts      # JSON Map scalar
│   │       ├── latitude.ts             # Latitude scalar
│   │       └── longitude.ts            # Longitude scalar
│   │
│   ├── integrations/                   # Third-party integrations
│   │   ├── apollo-escape.ts            # Apollo GraphQL Armor security
│   │   └── apollo-fastify.ts           # Apollo-Fastify integration
│   │
│   ├── resolver-definitions/           # GraphQL resolver implementations
│   │   └── core/
│   │       └── authorization/          # Authorization resolvers
│   │       └── device/                 # Device resolvers
│   │       └── event/                  # Event resolvers
│   │       └── organization/           # Organization resolvers
│   │       └── tag/                    # Tag resolvers
│   │
│   ├── routes/                         # REST API routes
│   │   └── flexibilities-import.route.ts  # File upload endpoint
│   │
│   └── __generated__/                  # Auto-generated code (from protoc/codegen)
│       └── core/
│           └── api/
│               └── device/
│                   └── v1/
│                       ├── device_grpc_pb.ts    # gRPC client stubs
│                       └── device_pb.ts         # Protocol buffer messages
│
└── test/                               # Test files and fixtures
    └── Flexibilities-L540.csv          # Sample CSV test file
```
## Apollo Server responsibilities
- Apollo Server **coordinates**, it does not implement business logic.

| Responsibility    | What it means              | Where in your project    |
| ----------------- | -------------------------- | ------------------------ |
| HTTP exposure     | `/graphql` endpoint        | `index.ts` + Fastify     |
| Schema loading    | API shape                  | `type-defs.ts`           |
| Resolver wiring   | Field → function mapping   | `resolvers.ts`           |
| Execution context | auth, headers, clients     | Fastify → Apollo context |
| Plugins           | security, logging, metrics | `integrations/*`         |

## Startup flow
<img src="images/apollo-server-2.jpg">

<details>
  <summary>mermaid</summary>

```mermaid
flowchart TD
    Config["Configuration (env, config.ts)"]
    Schema["Schema (type-defs.ts)"]
    ResolverMap["Resolver map (resolvers.ts)"]
    Context["Context builder"]
    Apollo["Apollo Server instance"]
    Fastify["Fastify server"]
    HTTP["HTTP listen"]

    Config --> Apollo
    Schema --> Apollo
    ResolverMap --> Apollo
    Context --> Apollo
    Apollo --> Fastify
    Fastify --> HTTP
```
</details>

## Directory-to-responsibility map

| Path                          | Role           | Notes                              |
| ----------------------------- | -------------- | ---------------------------------- |
| `src/index.ts`                | bootstrap      | create Fastify + Apollo            |
| `src/type-defs.ts`            | schema         | types, queries, mutations, scalars |
| `src/resolvers.ts`            | resolver map   | pure mapping, no logic             |
| `src/resolver-definitions/**` | resolver logic | business logic lives here          |
| `src/common/`                 | shared infra   | config, Dapr, scalars              |
| `src/integrations/`           | Apollo plugins | Fastify, Armor                     |
| `src/routes/`                 | REST endpoints | parallel API                       |
| `src/__generated__/`          | gRPC clients   | consumed by resolvers              |

## Request lifecycle
<img src="images/apollo-server-3.jpg">

<details>
  <summary>mermaid</summary>

```mermaid
flowchart TD
    Client["Client"]
    Fastify["Fastify"]
    Apollo["Apollo Server"]
    Schema["Schema validation"]
    ResolverMap["Resolver lookup"]
    Resolver["Resolver execution"]
    Response["GraphQL response"]

    Client --> Fastify
    Fastify --> Apollo
    Apollo --> Schema
    Schema --> ResolverMap
    ResolverMap --> Resolver
    Resolver --> Response
```
</details>

## Data access paths
- Resolvers are the **only entry point** to data sources.
<img src="images/apollo-server-4.jpg">

<details>
  <summary>mermaid</summary>

```mermaid
flowchart TD
    Resolver["Resolver"]
    Dapr["Dapr services"]
    GRPC["gRPC clients"]
    REST["Internal REST routes"]
    External["External APIs"]

    Resolver --> Dapr
    Resolver --> GRPC
    Resolver --> REST
    Resolver --> External
```
</details>

## What Apollo Server does not own
- Apollo Server does **zero domain work**.

| Concern            | Owned by                 |
| ------------------ | ------------------------ |
| Business logic     | resolvers                |
| Data fetching      | Dapr / gRPC / REST       |
| Transport          | Fastify                  |
| Federation routing | Apollo Router (not here) |
| Code generation    | `codegen.yml`, `protoc`  |

## Minimal mental model
```text
Apollo Server =
  schema
+ resolver map
+ context
+ HTTP adapter
```
