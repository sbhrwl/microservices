# Reverse proxy
- [Introduction](#introduction)
- [Benefits](#benefits)
- [Setup](#setup)
## Introduction
- A **reverse proxy** is a server that sits **in front of your application** and forwards client requests to it.
```text
Browser ---> Reverse Proxy ---> Your App
```
## Benefits
- **Security**
  * You can hide your internal application structure (IP, ports, etc.).
  * It allows centralized handling of SSL/TLS (HTTPS).
  * You can filter or block suspicious requests.
- **Load balancing**
  * Distributes incoming traffic to multiple app instances for scalability and high availability.
- **Routing**
  * Based on the path, host, or headers, it can route requests to different microservices or apps.
- **Logging and Monitoring**
  * Easier to track traffic and errors in one place.
- **CORS or Authentication Handling**
  * You can offload some cross-origin or token validation logic to the proxy.
## Setup 
- Create a directory for Nginx config
```bash
mkdir nginx-reverse-proxy
cd nginx-reverse-proxy
```
- Create a file named **`default.conf`** with this content:
  * `host.docker.internal` is used to reach your Spring Boot app running on your host machine from inside the Docker container.
```nginx
server {
    listen 80;

    location /api/ {
        proxy_pass http://host.docker.internal:8081/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```
- Create [`docker-compose.yaml`](nginx-reverse-proxy/docker-compose.yaml)
  - This does the following:
    - Runs the official `nginx:latest image`
    - Maps port `80` of your host to the container's `8085`
    - Mounts the `default.conf` you created into the correct config folder inside the container.
- Run the container
  - `docker-compose up -d`
    * Start the Nginx container in the background on port `80`
    * Expose it on port `8085`.
    * Forward any request to `/api` to your Spring Boot app at `http://host.docker.internal:8081`.
  - Verify: 
    - Once it starts successfully, visit: `http://localhost:8085/api/secure`