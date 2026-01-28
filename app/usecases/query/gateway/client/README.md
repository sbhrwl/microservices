## 
* [what this client does](#what-this-client-does)
* [key concepts](#key-concepts)
* [project structure](#project-structure)
* [initialization flow](#initialization-flow)
* [making grpc calls](#making-grpc-calls)
* [authentication and metadata](#authentication-and-metadata)
* [singleton pattern](#singleton-pattern)
* [usage example](#usage-example)
* [common pitfalls](#common-pitfalls)
## what this client does
* Wraps a **gRPC Flexibility service** behind a clean TypeScript API
* Uses **Dapr** to proxy gRPC calls between services
* Handles:
  * Lazy initialization
  * Metadata creation (auth + Dapr app-id)
  * Promise-based APIs instead of callbacks
  * Singleton lifecycle management
* Think of it as a **polite concierge** that opens the Dapr door, speaks gRPC fluently, and hands you simple async methods.
## key concepts
* **Dapr gRPC proxy**
  * Lets you call another service by `app-id`
  * Removes direct service discovery concerns
* **Lazy initialization**
  * Client is created only when first used
* **Singleton**
  * One shared client instance across the app
* **Callback → Promise bridge**
  * Converts gRPC callbacks into `async/await`
## project structure
* Relevant imports:
* Generated gRPC types and client
* `FlexibilityServiceClient`
  * Request/response types
* Dapr helpers
  * `createDaprProxy`
  * `createDaprMetadata`
* Key files:
* `flexibility-client.ts` → this wrapper
* `dapr-client.ts` → shared Dapr utilities
* `__generated__/` → protobuf-generated code
## initialization flow
How the client boots up:

1. First API call triggers `getProxy()`
2. `initialize()` runs once
3. `createDaprProxy(FlexibilityServiceClient)` is called
4. Proxy is cached for future calls

Safety features:

* Multiple concurrent calls wait on the same `initPromise`
* Failed initialization resets state so retries work
## making grpc calls
Each service method follows the same recipe:

* Ensure the proxy is initialized
* Create gRPC metadata
* Call the gRPC method
* Wrap the callback in a `Promise`

Example pattern:

* `queryFlexibilities`
* `getFlexibility`
* `uploadCsv`
* `confirmUpload`

This gives you clean usage like:

```ts
await client.queryFlexibilities(request, token);
```

No callbacks. No ceremony. Just async flow.

---

## authentication and metadata

Metadata is created centrally:

* Includes:

  * Dapr target `app-id`
  * Optional auth token
* Built via `createDaprMetadata(appId, token)`

Why this matters:

* Keeps security logic out of business code
* Makes auth changes easy and consistent

---

## singleton pattern

Why a singleton is used:

* gRPC clients are expensive to create
* Dapr proxy setup should happen once
* Ensures consistent configuration

How it works:

* `getFlexibilityClient()` returns the same instance
* `closeFlexibilityClient()` resets everything

Perfect for:

* Web backends
* Workers
* Long-running services

---

## usage example

Typical flow in application code:

* Get the client
* Call a method
* Forget about setup details

```ts
const client = getFlexibilityClient();
const result = await client.getFlexibility({ id }, token);
```

That’s it. No manual initialization required.

---

## common pitfalls

* Forgetting Dapr sidecar running

  * gRPC calls will fail silently or timeout
* Wrong `DAPR_FLEXIBILITY_APP_ID`

  * Defaults to `"gfc-core"`
* Calling `close()` too early

  * Forces re-initialization on next call

---

If you want, next we can:

* Add retries and timeouts
* Refactor to a base gRPC client
* Diagram the Dapr → gRPC call path
* Convert this into a README for your repo
