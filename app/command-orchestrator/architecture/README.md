## Architecture
- [Overview](#overview)
- [Actor-based design](#actor-based-design)
- [Dapr sidecar interaction](#dapr-sidecar-interaction)
- [HTTP callback server](#http-callback-server)
- [State store usage](#state-store-usage)
- [High-level component interactions](#high-level-component-interactions)

## Overview
- **Actor-hosting `microservice`** that runs alongside a Dapr sidecar
  - Actors encapsulate per-device state and behavior
- Reactor Netty HTTP server 
  - Exposes Dapr-required callbacks and
  - Forwards to Dapr’s ActorRuntime
- PostgreSQL is used as the Dapr state store for durable actor state
- Configuration is provided via `Typesafe Config`
  - Defaults and structure are defined in `ApplicationSetting`
## Actor-based design
- Actors are registered at startup with Dapr’s `ActorRuntime` and created on-demand per `actor id`
- `Per-actor state` is accessed through `Dapr’s state abstractions`
  - `Serialization` uses the SDK-configured `ObjectMapper`
- **Reminders** and **timers** are supported via `ActorRuntime`
  - Scheduling parameters (|initial delay`, `period`, `partitions`, |execution window`) are configurable in `ApplicationSetting`
- Example domain model present:
  - `DeviceConfiguration` with `builder` and `getters/setters` used by device actors
## Dapr sidecar interaction
- Sidecar invokes the service’s HTTP callbacks to:

| Purpose                           | HTTP method | Endpoint                                       |
| --------------------------------- | ----------- | ---------------------------------------------- |
| Fetch actor runtime configuration | GET         | `/dapr/config`                                 |
| Deactivate an actor               | DELETE      | `/actors/{type}/{id}`                          |
| Invoke an actor method            | PUT         | `/actors/{type}/{id}/method/{method}`          |
| Deliver reminders                 | PUT         | `/actors/{type}/{id}/method/remind/{reminder}` |
| Deliver timers                    | PUT         | `/actors/{type}/{id}/method/timer/{timer}`     |

- `ActorCallbackController` receives these requests and delegates to `ActorRuntime` for execution
- Actor runtime configuration is serialized and returned to the sidecar by `GET` `/dapr/config`

## HTTP callback server
- Implemented with Reactor Netty
  - Routes are registered by `RoutingService` implementations (e.g., `ActorCallbackController`)
- Server binds to the configured interface and port (defaults: `0.0.0.0:3501`) and uses dedicated `selector/worker` event loops
- Graceful shutdown is handled by adding a JVM shutdown hook to dispose the Reactor Netty server
## State store usage
- Dapr persists actor state to PostgreSQL configured via the component YAML under `components/postgresql.yaml`
- State operations are performed by the ActorRuntime through the Dapr SDK
  - No direct JDBC usage is present in the service code
- Tests include an `in-memory` Dapr state provider to facilitate isolated actor behavior verification

## High-level component interactions
<img src="images/arch-1.jpg">

<details>
  <summary>mermaid</summary>

```mermaid
flowchart TD
  A["Client or integration"] --> B["Dapr sidecar"]
  subgraph SVC["device-hub service"]
    D["Reactor Netty 'HTTP server'"]
    E["'ActorCallbackController'"]
    F["Dapr 'ActorRuntime' (SDK)"]
    G["Device actors (e.g., 'DeviceTwin')"]
  end
  subgraph STATE["Dapr state store"]
    H["PostgreSQL"]
  end

  B --> D
  D --> E
  E --> F
  F --> G
  F --> H
```
</details>

<img src="images/arch-2.jpg">

<details>
  <summary>mermaid</summary>

```mermaid
flowchart TD
  C[[Client or integration]]
  D[[Dapr sidecar]]
  H[HTTP server]
  Ctl[ActorCallbackController]
  R[[Actor runtime]]
  A[Device actor]
  S[(State store PostgreSQL)]

  C -->|Invoke actor method| D
  D -->|HTTP callback| H
  H --> Ctl
  Ctl --> R
  R --> A
  A --> R
  R --> S
  R --> Ctl
  Ctl --> D
  D --> C
```
</details>
