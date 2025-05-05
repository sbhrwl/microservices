# Create a container for the app
- [Docker commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Docker/commands/README.md)
* **Build your Spring Boot app**
  * Run: `./mvnw clean package` or `./gradlew build` to **create** a `.jar` file.
* **Create a Dockerfile** in your project root:
  ```dockerfile
  FROM openjdk:17-jdk-slim
  COPY target/*.jar app.jar
  ENTRYPOINT ["java", "-jar", "/app.jar"]
  ```

* **Build the Docker image**
  * Run:
  ```bash
  docker build -t my-spring-app .
  ```

* **Build and tag**
  ```
  docker build -t image_name:tag .
  
  docker build -t my-spring-app:v1.0.0 .
  docker build -t my-spring-app:latest .
  ```
* **Run the container**
  * Option 1: `docker run` command
  ```bash
  docker run -p 8080:9999 my-spring-app
  ```
  * Option 2: `docker-compose.yml` followed by `docker-compose up`
  ```
  version: "3.9"
  
  services:
    java-app:
      image: my-spring-app        # use the already built image
      ports:
        - "8080:8080"             # map container port to host
      restart: unless-stopped
  ```
* **Verify it's running**
  * Send Get request from Postman `http://localhost:8080/message/generate`