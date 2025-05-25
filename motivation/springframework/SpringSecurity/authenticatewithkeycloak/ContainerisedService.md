# Run service as a Docker container
- [Docker commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Docker/commands/README.md)
- [Build your app](#build-your-app)
- [Create Dockerfile in your project root](#create-dockerfile-in-your-project-root)
- [Build Docker image](#build-docker-image)
- [Run Docker image as a Docker container](#run-docker-image-as-a-docker-container)
- [Test container](#test-container)

## Build your app
- For Spring Boot use below command to **create a `.jar`** file.
  - `mvn clean package`
## Create Dockerfile in your project root
- [Dockerfile](Dockerfile)
## Build Docker image
```bash
docker build -t secured-endpoint .

# For pushing image to Docker registry, build the image with the full tag directly
docker build -t sbhrwldocker/secured-endpoint:v1.0.0 .
```
## Run Docker image as a Docker container
### Option 1
- `docker run` command
  ```bash
  docker run -p 8081:8081 secured-endpoint
  ```
### Option 2
- `docker-compose.yml` followed by `docker-compose up`
  ```
  version: "3.9"
  
  services:
    java-app:
      image: secured-endpoint     # use the already built image
      ports:
        - "8081:8081"             # map container port to host
      restart: unless-stopped
  ```
## Test container
- Send `GET` request from **Postman** `http://localhost:8081/public`
