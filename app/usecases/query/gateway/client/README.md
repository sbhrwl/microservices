# Client
* [What this client does](#what-this-client-does)
* [Concepts](#concepts)
* [Project structure](#project-structure)
* [Initialization flow](#initialization-flow)
* [Making grpc calls](#making-grpc-calls)
* [Authentication and metadata](#authentication-and-metadata)
* [Singleton pattern](#singleton-pattern)
* [Usage example](#usage-example)
* [Common pitfalls](#common-pitfalls)
## What this client does
* Wraps a **gRPC Flexibility service** behind a clean TypeScript API
* Uses **Dapr** to proxy gRPC calls between services
* Handles:
  * Lazy initialization
  * Metadata creation (auth + Dapr app-id)
  * Promise-based APIs instead of callbacks
  * Singleton lifecycle management
* Think of it as a **polite concierge** that opens the Dapr door, speaks gRPC fluently, and hands you simple async methods.
## Concepts
* **Dapr gRPC proxy**
  * Lets you call another service by `app-id`
  * Removes direct service discovery concerns
* **Lazy initialization**
  * Client is created only when first used
* **Singleton**
  * One shared client instance across the app
* **Callback → Promise bridge**
  * Converts gRPC callbacks into `async/await`
## Project structure
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
## Initialization flow
* How the client boots up:
  * First API call triggers `getProxy()`
  * `initialize()` runs once
  * `createDaprProxy(FlexibilityServiceClient)` is called
  * Proxy is cached for future calls
* Safety features:
  * Multiple concurrent calls wait on the same `initPromise`
  * Failed initialization resets state so retries work
## Making grpc calls
* Each service method follows the same recipe:
* Ensure the proxy is initialized
* Create gRPC metadata
* Call the gRPC method
* Wrap the callback in a `Promise`
* Example pattern:
  * `queryFlexibilities`
  * `getFlexibility`
  * `uploadCsv`
  * `confirmUpload`
* This gives you clean usage like:
```ts
await client.queryFlexibilities(request, token);
```
* No callbacks. No ceremony. Just async flow.
## Authentication and metadata
* Metadata is created centrally:
* Includes:
  * Dapr target `app-id`
  * Optional auth token
* Built via `createDaprMetadata(appId, token)`
* Why this matters:
  * Keeps security logic out of business code
  * Makes auth changes easy and consistent
## Singleton pattern
* Why a singleton is used:
  * gRPC clients are expensive to create
  * Dapr proxy setup should happen once
  * Ensures consistent configuration
* How it works:
  * `getFlexibilityClient()` returns the same instance
  * `closeFlexibilityClient()` resets everything
  * Perfect for:
    * Web backends
    * Workers
    * Long-running services
## Usage example
* Typical flow in application code:
  * Get the client
  * Call a method
  * Forget about setup details
```ts
const client = getFlexibilityClient();
const result = await client.getFlexibility({ id }, token);
```
* That’s it. No manual initialization required.
## Common pitfalls
* Forgetting Dapr sidecar running
  * gRPC calls will fail silently or timeout
* Wrong `DAPR_FLEXIBILITY_APP_ID`
  * Defaults to `"gfc-core"`
* Calling `close()` too early
  * Forces re-initialization on next call
## Next
* Add retries and timeouts
* Refactor to a base gRPC client
* Diagram the Dapr → gRPC call path
* Convert this into a README for your repo
