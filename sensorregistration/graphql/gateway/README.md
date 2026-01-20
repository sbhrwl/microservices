# Sensor GraphQL gateway
- TypeScript GraphQL gateway for the sensor gRPC service.
## Prerequisites
- Node.js 18+
- Running gRPC backend on localhost:9090
## Setup
- Install dependencies: `npm install`
- Generate TypeScript from proto: `npm run generate-proto`
  - Make sure directory exists: `mkdir src\grpc\generated`
- Build: `npm run build`
- Run: `npm start`
## GraphQL Playground
- Access at: `http://localhost:4000`
- Register Sensor
```graphql
mutation {
  registerSensor(
    sensorId: "SENSOR-001"
    userEmail: "user@example.com"
    postcode: "12345"
  ) {
    sensorId
    userEmail
    postcode
    status
    registeredAt
  }
}
```
- Get Sensor
```graphql
query {
  sensor(sensorId: "SENSOR-001") {
    sensorId
    userEmail
    postcode
    status
    lastUpdatedAt
  }
}
```
- List User's Sensors
```graphql
query {
  sensorsByUser(userEmail: "user@example.com") {
    sensorId
    postcode
    status
  }
}
```
- Update Postcode
```graphql
mutation {
  updateSensorPostcode(
    sensorId: "SENSOR-001"
    newPostcode: "54321"
  ) {
    sensorId
    postcode
    lastUpdatedAt
  }
}
```