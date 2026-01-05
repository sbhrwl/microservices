## Runtime
- [Overview](#overview)
- [Actor lifecycle handling](#actor-lifecycle-handling)
- [Reminder and timer flows](#reminder-and-timer-flows)
- [Runtime interactions with dapr](#runtime-interactions-with-dapr)

## Overview
- The service configures Dapr’s ActorRuntime at startup, registers actors, and starts a Reactor Netty HTTP server to receive Dapr callbacks
- Dapr sidecar invokes HTTP endpoints for actor configuration, method calls, reminders, timers, and deactivation
- Actor activation/deactivation hooks (onActivate/onDeactivate) are provided in actors and executed by the runtime

```mermaid
flowchart TD
  A["Startup"] --> B["'DeviceHub.run()'"]
  B --> C["Load config via 'ConfigFactory.load()'"]
  C --> D["Configure 'ActorRuntime' (timeouts, scan, drain, partitions)"]
  D --> E["Configure Dapr default Jackson ObjectMapper"]
  E --> F["Register actors (e.g., 'DeviceTwin')"]
  F --> G["Start Reactor Netty HTTP server (routes)"]
```

## Actor lifecycle handling
- activation
  - Actors are registered with ActorRuntime before the server starts
  - On the first invocation for a given actor id, the runtime activates the actor and calls its onActivate hook
- deactivation
  - Endpoint: DELETE /actors/{type}/{id}
  - ActorCallbackController delegates to ActorRuntime.deactivate(type, id)
  - The actor’s onDeactivate hook is invoked by the runtime during deactivation
- configuration fetch
  - Endpoint: GET /dapr/config
  - Returns serialized ActorRuntime configuration to the Dapr sidecar

```mermaid
sequenceDiagram
  autonumber
  participant S as "Dapr sidecar"
  participant H as "HTTP server"
  participant C as "ActorCallbackController"
  participant R as "ActorRuntime"
  participant A as "Actor instance"

  S->>H: "PUT '/actors/{type}/{id}/method/{method}'"
  H->>C: "route to handler"
  C->>R: "invoke(type, id, method, payload)"
  Note over R: "Activate actor if not active"
  R->>A: "onActivate (first time only)"
  R->>A: "dispatch method"
  A-->>R: "response"
  R-->>C: "bytes"
  C-->>S: "HTTP 200 + bytes"
```

## Reminder and timer flows
- reminder delivery
  - Endpoint: PUT /actors/{type}/{id}/method/remind/{reminder}
  - Body is forwarded to ActorRuntime.invokeReminder
  - On error, the handler logs a warning and propagates the error (server error response)
- timer delivery
  - Endpoint: PUT /actors/{type}/{id}/method/timer/{timer}
  - Body is forwarded to ActorRuntime.invokeTimer
  - On error, the handler logs a warning and propagates the error (server error response)
- reminders storage
  - The number of reminders storage partitions is set on ActorRuntime configuration at startup

```mermaid
sequenceDiagram
  autonumber
  participant S as "Dapr sidecar"
  participant H as "HTTP server"
  participant C as "ActorCallbackController"
  participant R as "ActorRuntime"
  participant A as "Actor instance"

  S->>H: "PUT '/actors/{t}/{id}/method/remind/{rem}'"
  H->>C: "route to handler"
  C->>R: "invokeReminder(t, id, rem, bytes)"
  R->>A: "on reminder(rem, bytes)"
  A-->>R: "ok"
  R-->>C: "complete"
  C-->>S: "HTTP 200 (empty body)"
```

## Runtime interactions with dapr
- configuration exchange
  - Dapr sidecar calls GET /dapr/config; the service returns ActorRuntime configuration bytes
- method invocation
  - Endpoint: PUT /actors/{type}/{id}/method/{method}
  - Body is forwarded to ActorRuntime.invoke; on handler error, the service returns a 200 with the bytes of the string "error"
- deactivation
  - Endpoint: DELETE /actors/{type}/{id}; ActorRuntime handles deactivation
- server and shutdown
  - Reactor Netty server runs on the configured host/port with registered routes
  - A shutdown hook is added to dispose the server gracefully when the JVM 
