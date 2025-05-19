# Project structure
- [Create project structure](#create-project-structure)
- [Parent pom.xml](#parent-pom.xml)
```
sensor-registration/
├── sensor-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/sensorservice/
│   │   │   └── resources/
│   │   └── test/
│   │       ├── java/
│   │       └── resources/
│   └── pom.xml
├── notification-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/notificationservice/
│   │   │   └── resources/
│   │   └── test/
│   │       ├── java/
│   │       └── resources/
│   └── pom.xml
├── registration-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/registrationservice/
│   │   │   └── resources/
│   │   └── test/
│   │       ├── java/
│   │       └── resources/
│   └── pom.xml
├── ui-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/uiservice/
│   │   │   ├── resources/
│   │   │   │   ├── static/
│   │   │   │   └── templates/
│   │   └── test/
│   │       ├── java/
│   │       └── resources/
│   └── pom.xml
└── pom.xml (the parent pom we just created)
```
## Create project structure
-  **Create the root folder:**
    ```bash
    mkdir sensor-registration
    cd sensor-registration
    ```
-  **Create the parent `pom.xml`:**
    Inside the `sensor-registration` directory, create a file named `pom.xml` and paste the XML content I provided earlier into it.

-  **Create the sub-module folders:**
    ```bash
    mkdir sensor-service
    mkdir registration-service
    mkdir notification-service
    mkdir ui-service
    ```
-  **Create the standard Maven structure within each sub-module folder:**
   - For example, for `sensor-service`:
    ```bash
    mkdir -p sensor-service/src/main/java/com/example/sensorservice
    mkdir -p sensor-service/src/main/resources
    mkdir -p sensor-service/src/test/java
    mkdir -p sensor-service/src/test/resources
    touch sensor-service/pom.xml
    ```

- Repeat this for `registration-service`, `notification-service`, and `ui-service`, replacing `sensorservice` with the appropriate service name in the `java` directory path.
- After running these commands (or performing the equivalent actions in your IDE's file explorer), you should have the folder structure as I described previously. 
  - Each service folder will initially contain only the standard `src` structure and an empty `pom.xml`.
- The parent `pom.xml` in the `sensor-registration` directory is what ties these sub-projects together as a multi-module Maven project.

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
