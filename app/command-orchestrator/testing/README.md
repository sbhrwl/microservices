# Testing
- [Overview](#overview)
- [Test structure](#test-structure)
- [In-memory state provider usage](#in-memory-state-provider-usage)
- [Actor state testing approach](#actor-state-testing-approach)

## Overview
- Tests focus on exercising actor behavior with an in-memory Dapr state provider and a stubbed Dapr client, avoiding external dependencies.
## Test structure

| Test area           | Package / location                     | Files                                                           |
| ------------------- | -------------------------------------- | --------------------------------------------------------------- |
| Actor runtime tests | `src/test/java/io/dapr/actors/runtime` | - `DaprInMemoryStateProvider.java` <br> - `DeviceTwinTest.java` |
| Dapr client stub    | `src/test/java/io/dapr/actors/client`  | - `DaprClientStub.java`                                         |


## In-memory state provider usage

| aspect   | details      |
| -------- | ------------ |
| File                        | `src/test/java/io/dapr/actors/runtime/DaprInMemoryStateProvider.java`         |
| Extends                     | `DaprStateAsyncProvider`      |
| Internal state              | Keeps actor state in a static `Map<String, byte[]>` (`stateStore`)       |
| Serializer                  | Uses `DaprObjectSerializer` provided at construction          |
| Key methods                 | - `load(...)`: deserializes state bytes for a composed key; throws if missing <br> - `contains(...)`: checks existence by key <br> - `apply(...)`: applies state changes to the in-memory store <br> - `buildId(...)`: composes unique key from actor type, actor id, and state name |
| Integration in test context | `DeviceTwinTest.createContext(...)` builds `ActorRuntimeContext<DeviceTwin>` with: <br> - `DefaultObjectSerializer` / `JavaSerializer` <br> - `DeviceTwinActorFactory` <br> - `DaprInMemoryStateProvider(new JavaSerializer())`  |

## Actor state testing approach

| Test component   | File                        | Key details                 |
| ---------------- | --------------------------- | --------------------------- |
| Device twin test | `src/test/java/io/dapr/actors/runtime/DeviceTwinTest.java` | - Creates inline reminder configuration via `ConfigFactory.parseString(...)` wrapped in `ApplicationSetting` <br> - Constructs `ActorRuntimeContext` with `DaprInMemoryStateProvider` <br> - Creates an `ActorProxy` wired to a mocked Dapr client: <br>   • `DaprClientStub` mocked with Mockito; `invoke(...)` forwards to test manager’s `invokeMethod(...)` <br>   • Actor explicitly activated via `manager.activateActor(actorId).block()` <br>   • `ActorProxyImplForTests` created with `DefaultObjectSerializer` and mocked client <br> - Allows invoking actor methods through the proxy and observing effects on in-memory state without external services |
| Dapr client stub | `src/test/java/io/dapr/actors/client/DaprClientStub.java`  | Implements `DaprClient`; used in tests for mocking and routing invocations back to the in-process actor      |
