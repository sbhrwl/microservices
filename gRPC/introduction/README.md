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
## Scenarios where gRPC shines
* **Streaming** (video/audio, live data feeds): Constant, high-throughput data transfer.
* **Gaming**: Low latency for player actions and updates.
* **Chat messages**
* **Real-time analytics**
* **Internal backend services communication**: Often need very efficient, high-volume communication between services `within a data center`.
### HTTP usage
- HTTP/REST has a much more mature and widely supported ecosystem, especially for **public-facing APIs**
  * Browser compatibility: All web browsers natively speak HTTP and understand JSON. gRPC requires a proxy layer (like gRPC-Web) to work in browsers, adding complexity.
  * Tooling: A vast array of tools, libraries, and documentation exist for building and testing REST APIs in virtually every programming language.
  * Simplicity for simple cases: For basic **CRUD** (Create, Read, Update, Delete) operations, REST can be very straightforward to implement and understand.
- So, when ease of development, broad client compatibility (especially web browsers), and a mature ecosystem are priorities, HTTP/REST often wins.
## Language diversity
- Protocol Buffers, is designed to handle "polyglot" environment well
  - If you have a system where different parts are written in different programming languages (e.g., one service in Python, another in Java, another in Go), and you need them to communicate seamlessly.
- Protocol Buffers are `language-agnostic`. 
  - You define your service and message structures once in a .proto file, and then you can generate client and server code in numerous languages (Java, Python, Go, C#, Node.js, etc.). 
  - This ensures that all services, regardless of their implementation language, speak the exact same "language" when communicating
## Real time APIs with HTTP
- Based on HTTP3 and WebTransport
  - You already know HTTP and how it traditionally works (request-response).
  - You also about gRPC's streaming capabilities
  - Now, imagine we want `to make HTTP itself better for real-time communication`, similar to how gRPC does streaming, but still `keeping it HTTP-based for web browser compatibility`.
  - This is where HTTP/3 and WebTransport come in.
