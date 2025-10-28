# Services
- [Flexibility Hub simulator](flexibility-hub-simulator/README.md)
  - [Production version](flexibility-hub-simulator-prod/README.md)
- [Flexibility bridge service](flexibility-bridge-service/README.md)
- [Storage service gRPC](storage-service-grpc/README.md)
  - [Storage service restful](storage-service/README.md)
- [Protocol adapter service](protocol-adapter-service/README.md)
- [HES simulator](hes-simulator/README.md)
- [Data API service](data-api-service/README.md)
- [UI app](ui-app/README.md)
- [Local testing](#local-testing)
## Local testing
* Runing local builds or JAR creation.
* 🧱 **Build phase (Maven/Gradle)** —
  * Dapr has **no effect**. You’re just packaging your Java code.
  → `mvn clean package` or `gradle build` works exactly the same.
* 💻 **Local testing** —
  * You have two options:
  1. **Run without Dapr:**
     * Your code should gracefully handle the absence of Dapr (e.g., disable Dapr beans or mock pub/sub/state).
     * Perfect for quick local dev.
  2. **Run with Dapr locally:**
     * Start RabbitMQ + Postgres in Docker.
     * Use Dapr CLI to run sidecars locally with your app:
       ```bash
       dapr run --app-id flexibility-hub-simulator --app-port 8081 --dapr-http-port 3500 -- mvn spring-boot:run
       ```
     * This emulates Kubernetes behavior.
* 🧩 **Jar creation and pipelines** —
  * Dapr is **runtime**, not a compile-time dependency. You just include the SDK (like any library).
* ✅ Bottom line:
  * You’ll **still build, test, and run JARs** normally. Dapr only comes into play when your service actually runs and interacts with other components.
  * Would you like me to show how to make your Spring Boot service **detect Dapr presence dynamically** (so it can run with or without it)?

<details>
  <summary>Parent pom.xml</summary>

  ## Parent pom.xml
  - When we are *building* these standalone services using a build tool like Maven, a multi-module project structure with a parent `pom.xml` offers significant advantages during the **development and build process**:
  Think of it this way:
  * **Individual Services (Standalone at Runtime):** 
    - At the end of our development and build process, each service (`sensor-service.jar`, `registration-service.jar`, etc.) will be a self-contained application that can be run and deployed independently. 
    - They don't *need* the parent `pom.xml` or the other service modules to run.
  * **Maven Multi-Module Project (Convenience During Development):** 
    - The parent `pom.xml` exists purely for **developer convenience and build management during the development phase.** 
    - It helps us:
      * **Manage Dependencies Consistently:** Ensure all our services use compatible versions of libraries.
      * **Build All Services Together:** Compile, test, and package all our services with a single Maven command from the parent directory.
      * **Establish Build Configurations:** Define common build settings that all services can inherit.
      * **Organize the Project:** Provide a clear structure in our codebase.
  * **Runtime:** Each microservice is a standalone application.
  * **Development/Build:** The parent `pom.xml` in a multi-module Maven project is a tool to help manage and build these independent services in a cohesive way.  
</details>
