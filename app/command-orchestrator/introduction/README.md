# Introduction
- [Overview](#overview)
- [Purpose and scope](#purpose-and-scope)
- [Problem statement](#problem-statement)
- [Core technologies](#core-technologies)
## Overview
- Device-hub is a Java microservice that **hosts device `actors`** using Dapr’s actor runtime
- It exposes the HTTP callbacks required by Dapr to manage actor lifecycle, method invocations, reminders, and timers
- Actor state is persisted via Dapr to a PostgreSQL state store configured under components
- The service is packaged as an executable JAR and runs alongside a Dapr sidecar in self-hosted or cluster environments
## Purpose
- Provide an application host for device actors (for example, DeviceTwin) that encapsulate device `state` and `behavior`
- Bridge external integrations (such as an IEC61968 connector) to device actors through Dapr’s standardized invocation model
- Offer operational endpoints required by Dapr (actor config, deactivate, method, reminder, timer) via a **Reactor `Netty HTTP server`**
- Support scheduled device workflows through actor reminders and timers with configurable defaults
- Configure runtime behavior through `Typesafe Config`, including HTTP server settings and actor runtime parameters
- Persist state reliably to PostgreSQL via the Dapr state store component
## Problem statement
- Managing device state and long-lived operations across distributed components is complex and error-prone
- A consistent model is needed to:
  - Maintain per-device state reliably across restarts
  - Execute device operations, reminders, and timers in a resilient, isolated way
  - Integrate cleanly with upstream systems without exposing internal actor management details
- Device-hub addresses this by hosting device actors via Dapr’s actor model, exposing only the required callback endpoints, and delegating durable state to a standard PostgreSQL-backed state store
## Core technologies
- `java` and `maven`
  - Primary language and build system; produces an executable JAR and manages dependencies and plugins
- `Dapr sdk` (actors)
  - Provides the actor runtime, lifecycle management, reminders, timers, and state abstractions
- `Reactor netty http`
  - Non-blocking HTTP server used for Dapr callback endpoints
- `grpc and reactor grpc`
  - Reactive gRPC stubs for service-to-service communication; code generation configured via protobuf-maven-plugin
- `Dagger`
  - Dependency injection for compile-time wiring of components
- `Typesafe config`
  - Hierarchical configuration with defaults for HTTP server, actor runtime, and reminders
- `Postgresql` (dapr state store)
  - Durable persistence for actor state via Dapr’s PostgreSQL component configuration
- `slf4j` and `logback` (with `logstash encoder`)
  - Structured application logging with JSON encoding support
- `Jackson (jsr310)`
  - JSON serialization support, including Java time types
- `caffeine`
  - In-memory caching utilities 
