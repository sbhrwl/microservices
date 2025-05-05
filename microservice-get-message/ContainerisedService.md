# Run service as a Docker container
- [Docker commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Docker/commands/README.md)
## Build your app
- For Spring Boot use below command to **create a `.jar`** file.
  - `./mvnw clean package` or
  - `./gradlew build` 
## Create Dockerfile in your project root
- [Dockerfile](Dockerfile)
## Build Docker image
```bash
docker build -t microservice-get-message .
```
## Run Docker image as a Docker container
### Option 1
- `docker run` command
  ```bash
  docker run -p 8080:9999 microservice-get-message
  ```
### Option 2
- `docker-compose.yml` followed by `docker-compose up`
  ```
  version: "3.9"
  
  services:
    java-app:
      image: microservice-get-message        # use the already built image
      ports:
        - "8080:9999"             # map container port to host
      restart: unless-stopped
  ```
## Test
- Send `GET` request from **Postman** `http://localhost:8080/message/generate`
