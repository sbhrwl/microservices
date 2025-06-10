# Introduction 
- Both HTTP and [gRPC](https://github.com/sbhrwl/system_design/blob/main/docs/services/grpc/README.md) can be used to build APIs.
- When you use HTTP for an API (often called a REST API), you're usually sending data as `text` (like JSON or XML).
- With **gRPC**, instead of text, you define your data structures using `Protocol Buffers`. 
  - This is like creating a highly organized, **language-neutral blueprint** for the messages your applications will send.
## Streaming support
- **Streaming** = `Multiple messages over one connection`
- HTTP is mostly a request-response model (client asks, server answers).
- gRPC, built on HTTP/2, supports various types of communication:
  * **Unary**: Still request-response, like HTTP.
  * **Server streaming**: Client sends a request, server sends back multiple responses.
  * **Client streaming**: Client sends multiple requests, server sends one response.
  * **Bidirectional streaming**: Client and server both send multiple messages back and forth simultaneously.
## Scenarios where gRPC often shines
* **Streaming** (video/audio, live data feeds): Constant, high-throughput data transfer.
* **Gaming**: Low latency for player actions and updates.
* **Backend services communication**: Often need very efficient, high-volume communication between services `within a data center`.