# gRPC setup
* **Protocol Buffers (`.proto` files):**
  * **Definition:** A language-neutral, platform-neutral, extensible mechanism for serializing structured data.
  * **Essential for:** Defining the data messages (`message`) and the service contract (`service` with `rpc` methods) that both client and server will use for communication.
* **Generated Code (Stubs):**
  * **Definition:** Automated code generation from `.proto` files by the `protoc` compiler with a gRPC plugin.
  * **Essential for:** Providing the necessary classes for both the client (stubs to make remote calls as if they were local) and the server (base classes to implement the defined service methods).
* **gRPC Channel (`ManagedChannel`):**
  * **Definition:** A connection to a gRPC server on a specified host and port.
  * **Essential for:** The client to establish and manage the underlying network connection to the server. It abstracts connection details like pooling and load balancing.
* **gRPC Server:**
  * **Definition:** An application that implements the gRPC service methods defined in the `.proto` file and listens for incoming client requests.
  * **Essential for:** Receiving, processing, and responding to RPC calls from clients.
* **Service Implementation:**
  * **Definition:** The concrete class on the server that extends the generated gRPC service base class and provides the business logic for each RPC method.
  * **Essential for:** Executing the actual work requested by the client.
* **Maven Plugin (`protobuf-maven-plugin` and `build-helper-maven-plugin`):**
  * **Definition:** Maven plugins that automate the compilation of `.proto` files into Java source code and ensure these generated sources are added to the project's build classpath.
  * **Essential for:** Integrating Protobuf and gRPC code generation seamlessly into the Maven build process, preventing "package does not exist" errors.
