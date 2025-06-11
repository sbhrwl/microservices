# Road to real time APIs
- [HTTP and gRPC](#http-and-grpc)
  - [Streaming support with gRPC](#streaming-support-with-grpc)
  - [Scenarios where gRPC shines](#scenarios-where-grpc-shines)
  - [HTTP usage](#http-usage)
  - [Language diversity with protobuffers](#language-diversity-with-protobuffers)
- [Real time APIs with HTTP](#real-time-apiswith-http)
  - [HTTP/3](#http/3)
  - [WebTransport](#webtransport)
    - [WebSockets](#websockets)
      - [Challenges with websockets](#challenges-with-websockets)
    - [How WebTransport helps](#how-webtransport-helps)
- [gRPC Vs WebTransport](#grpc-vs-webtransport)
## HTTP and gRPC
- Both HTTP and [gRPC](https://github.com/sbhrwl/system_design/blob/main/docs/services/grpc/README.md) can be used to build APIs.
- When you use HTTP for an API (often called a REST API), you're usually sending data as `text` (like JSON or XML).
- With **gRPC**, instead of text, you define your data structures using `Protocol Buffers`. 
  - This is like creating a highly organized, **language-neutral blueprint** for the messages your applications will send.
### Streaming support with gRPC
- **Streaming** = `Multiple messages over one connection`
- HTTP is mostly a request-response model (client asks, server answers).
- gRPC, built on HTTP/2, supports various types of communication:
  * **Unary**: Still request-response, like HTTP.
  * **Server streaming**: Client sends a request, server sends back multiple responses.
  * **Client streaming**: Client sends multiple requests, server sends one response.
  * **Bidirectional streaming**: Client and server both send multiple messages back and forth simultaneously.
### Scenarios where gRPC shines
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
### Language diversity with protobuffers
- Protocol Buffers, is designed to handle "polyglot" environment well
  - If you have a system where different parts are written in different programming languages (e.g., one service in Python, another in Java, another in Go), and you need them to communicate seamlessly.
- Protocol Buffers are `language-agnostic`. 
  - You define your service and message structures once in a .proto file, and then you can generate client and server code in numerous languages (Java, Python, Go, C#, Node.js, etc.). 
  - This ensures that all services, regardless of their implementation language, speak the exact same "language" when communicating
## Real time APIs with HTTP
- Based on `HTTP3` and `WebTransport`
  - We already know HTTP and how it traditionally works (request-response).
  - We also understand gRPC's streaming capabilities
  - Now, imagine we want `to make HTTP itself better for real-time communication`, similar to how gRPC does streaming, but still `keeping it HTTP-based for web browser compatibility`.
  - This is where HTTP/3 and WebTransport come in.
### HTTP/3
- HTTP/1.1: The classic HTTP 
- HTTP/2: used by gRPC 
- **HTTP/3**
  - It is the next major version of the HTTP protocol.
  - HTTP/3 runs on top of a new transport protocol called **QUIC (Quick UDP Internet Connections)**, instead of `TCP` (which HTTP/1.1 and HTTP/2 use).
    - Think of TCP as a very reliable but sometimes slower delivery service, and QUIC as a `faster, more flexible` one.
### WebTransport
- WebTransport is an improvement for real-time
#### WebSockets
- Imagine you're on a website, and you want to see live updates, like a chat room or stock prices changing instantly.
- With traditional HTTP/1.1,
  - You'd have to constantly ask the server **"Are there any new messages?"** (`polling`), which is inefficient. `Or`
  - The server would have to **keep a request open for a long time** until it had something to send (`long polling`).
- WebSockets were designed to solve this.
  - When you open a WebSocket connection, it's like **opening a `persistent, two-way street` between your browser and the server**. 
  - Once established, both the client and the server can send messages to each other whenever they want, without constantly initiating new requests. 
  - This is great for real-time communication.
- WebSockets revolutionized real-time communication on the web. 
- They are widely used for chat apps, online games, notifications, etc.
##### Challenges with websockets
- WebSockets themselves still run over `TCP`. 
  - While they offer two-way communication, they provide a single, ordered stream of messages. 
  - If one message gets stuck, everything behind it also gets stuck (**"head-of-line blocking"** issue).
- This is where WebTransport comes in, especially when combined with HTTP/3.
### How WebTransport helps
- WebTransport is an API that allows web applications to send and receive data using HTTP/3 (and thus QUIC). 
- It's designed to provide the benefits of QUIC (like no head-of-line blocking for different streams, faster connection setup) directly to the browser.
- WebTransport runs over **QUIC (which uses UDP but adds reliability)**, it gains several key benefits over WebSockets (which run over TCP):
  - **Multiple, independent streams:**
    - Unlike WebSockets' single stream, WebTransport can open multiple *independent* streams within a single connection.
    - This means if one stream experiences packet loss, the others aren't affected by "head-of-line blocking."
  - **Unreliable datagrams:**
     - WebTransport also offers an optional "datagram" mode.
     - This allows for sending small, unreliable, unordered messages, which is perfect for very low-latency scenarios where losing an occasional update is okay (like a game sending player positions) and you don't want the overhead of guaranteed delivery.
  - **Faster connection setup:**
    - Thanks to QUIC's design.
- So, in essence, `WebTransport` offers a more flexible and potentially higher-performance way to do real-time communication in browsers than WebSockets, especially for complex applications that might benefit from multiple independent data streams or ultra-low-latency unreliable messaging.
Perfect answer! You've nailed the primary strengths.

## gRPC Vs WebTransport
* **gRPC:** Is exceptionally well-suited for **internal service-to-service communication**.
  * **Reasoning:** Its binary nature, Protocol Buffer contracts, and efficient HTTP/2 foundation make it ideal for high-performance, low-latency communication *between trusted services* in a data center or within a microservices architecture.
  * Browser compatibility, while possible with gRPC-Web, is an add-on, not its core design goal.
* **WebTransport (with HTTP/3):** Is designed for **browser-to-server real-time communication**.
  * **Reasoning:** Its native browser support, ability to handle multiple independent streams, and foundation on HTTP/3/QUIC make it excellent for building highly performant, flexible, and real-time web applications that need efficient two-way data flow directly from the browser.

| Feature                   | gRPC (Protocol Buffers, HTTP/2)                     | WebTransport (HTTP/3, QUIC)                                  |
| :------------------------ | :-------------------------------------------------- | :----------------------------------------------------------- |
| **Primary Use Case** | Internal service-to-service, microservices         | Browser-to-server real-time apps                             |
| **Browser Compatibility** | Requires proxy (gRPC-Web)                           | Native W3C standard, direct browser support                  |
| **Data Format** | Binary (Protocol Buffers)                           | Binary (common, but flexible)                                |
| **Debugging** | Requires special tools to decode                    | Requires knowledge of custom binary format (if used)         |
| **API Contract/Schema** | Strict (Protocol Buffers), code generation          | Flexible, user-defined (transport only)                      |
| **Streaming Capabilities**| Unary, Server Stream, Client Stream, Bi-directional | Multiple independent streams, unreliable datagrams           |
| **Underlying Protocol** | HTTP/2 over TCP                                     | HTTP/3 over QUIC (built on UDP)                              |
| **Head-of-Line Blocking** | Possible (within HTTP/2 streams)                    | Minimized (due to QUIC's stream multiplexing)                |
