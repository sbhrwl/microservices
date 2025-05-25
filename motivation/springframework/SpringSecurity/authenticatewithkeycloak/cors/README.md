# CORS

## Introduction
- **CORS (Cross-Origin Resource Sharing)** is a security feature built into browsers.
- It **controls whether a web page running at one origin (like `http://my-ui.com`)** can **make requests to a different origin** (like `http://api-server.com`).
- Spring Security `blocks CORS` by default unless explicitly configured.
  - So you need to configure **CORS rules** to allow trusted domains (like your frontend) to call the backend.
## Why is it needed
- By default, browsers **block** requests made from one domain to another if the response doesn't explicitly allow it.
- Example:
  * Your frontend is running on `http://localhost:3000`
  * Your Spring Boot backend is on `http://localhost:8081`
  * If the backend doesn't allow cross-origin requests, the browser blocks it.
- [Example from `sensor registration` service](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/sensor-registration/sensor-service/src/main/java/com/example/sensorservice/config/SecurityConfig.java)
