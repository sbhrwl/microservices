# Run service as a Docker container
- [Docker commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Docker/commands/README.md)
## Build your app
- For Spring Boot use below command to **create** a `.jar` file.
  - `./mvnw clean package` or `./gradlew build` 
## Create a Dockerfile in your project root
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```
## Build the Docker image
```bash
docker build -t microservice-get-message .
```
## Build and tag
```
docker build -t image_name:tag .

docker build -t microservice-get-message:v1.0.0 .
docker build -t microservice-get-message:latest .
```
## Run the container
### Option 1
- `docker run` command
```bash
docker run -p 8080:9999 microservice-get-message
```
### Option 2
-  `docker-compose.yml` followed by `docker-compose up`
```
version: "3.9"

services:
  java-app:
    image: microservice-get-message        # use the already built image
    ports:
      - "8080:9999"             # map container port to host
    restart: unless-stopped
```
## Verify it's running
  * Send Get request from Postman `http://localhost:8080/message/generate`
