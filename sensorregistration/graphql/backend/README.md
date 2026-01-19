# Sensor backend service
- gRPC-based sensor management service with MongoDB persistence.

domain/          - Pure business logic (no Spring)
application/     - Use cases and workflows
repository/      - Data access contracts and MongoDB impl
grpc/            - gRPC service adapters
proto/           - Protocol buffer definitions

## Prerequisites
- Java 17
- Maven 3.8+
- MongoDB 6.0+ running on localhost:27017
## Build and run
- Build: `mvn clean install`
- Run: `mvn spring-boot:run`

## gRPC Server
- Port: 9090
- Service: sensor.SensorService

## Error Mapping
|Condition| gRPC Status         |
|--|---------------------|
|Sensor not found	| NOT_FOUND           |
|Duplicate sensorId	| ALREADY_EXISTS      |
|Postcode unchanged	| INVALID_ARGUMENT    |
|No data found	| FAILED_PRECONDITION |
|Internal error	| INTERNAL            |