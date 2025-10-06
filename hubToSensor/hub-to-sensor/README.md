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
