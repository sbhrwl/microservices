# Installation
- Build the project
  - Compile and package the JAR
    ```bash
    mvn clean install -DskipTests=true -D"checkstyle.skip"=true
    ```
  - Optional: update snapshots/releases during build
    ```bash
    mvn -U clean install -DskipTests=true -D"checkstyle.skip"=true
    ```
- Build outputs
  - Packaged JAR is produced at target/iec61968-connector-1.0.jar
