# README.md

# Spring Boot Post Service

This project is a Spring Boot application that handles POST requests for command submissions targeting multiple devices. It accepts a JSON payload and processes the command asynchronously.

## Project Structure

```
spring-boot-post-service
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── postservice
│   │   │               ├── PostServiceApplication.java
│   │   │               ├── controller
│   │   │               │   └── CommandController.java
│   │   │               └── model
│   │   │                   └── CommandRequest.java
│   │   └── resources
│   │       ├── application.properties
│   │       └── static
│   └── test
│       └── java
│           └── com
│               └── example
│                   └── postservice
│                       └── PostServiceApplicationTests.java
├── pom.xml
└── README.md
```

## Setup Instructions

1. Clone the repository:
   ```
   git clone <repository-url>
   ```

2. Navigate to the project directory:
   ```
   cd spring-boot-post-service
   ```

3. Build the project using Maven:
   ```
   mvn clean install
   ```

4. Run the application:
   ```
   mvn spring-boot:run
   ```

## Usage

To submit a command, send a POST request to the `/commands` endpoint with the following JSON payload:

```json
{
  "correlationId": "123456-abc",
  "requestedBy": "scheduler-service",
  "deviceIds": ["meter-001", "meter-002", "meter-003"],
  "commandType": "SET_TOU",
  "commandParams": {
    "touProfileId": "TOU-2025-WINTER"
  },
  "scheduledAt": "2025-05-08T02:00:00Z"
}
```

## Response

The response will include a task ID and status indicating whether the command was accepted for processing.

```json
{
  "taskId": "task-7890",
  "status": "accepted",
  "message": "Command task created and sent for processing."
}
```

## License

This project is licensed under the MIT License.