# Checklist
* [Backend](#backend)
* [Gateway](#gateway)
* [Frontend](#frontend)
* [Quick view](#quick-view)
## Backend
* Define proto files
  * Create `.proto` file for gRPC:
    * `RegisterSensorRequest`
    * `SensorResponse`
    * `SensorService` with `registerSensor` rpc
* Generate grpc code
  * Generate server stubs from `.proto`
  * Ensure correct package naming and service bindings
* Define the domain model
  * Create `Sensor` entity with:
    * `sensorId`
    * `userEmail`
    * `postcode`
    * `status`
    * `registeredAt`
    * `lastUpdatedAt`
* Create repository layer (mongodb)
  * Create repository interface:
    * `existsBySensorId(sensorId)`
    * `save(sensor)`
    * `findBySensorId(sensorId)`
* Build application service
  * Implement `registerSensor()` logic:
    * Duplicate check
    * Create sensor
    * Save to database
* Implement grpc server (detailed steps)
  * Accept grpc request
    * Read `RegisterSensorRequest` fields
    * Validate request payload
  * Call application service
    * `applicationService.registerSensor(sensorId, userEmail, postcode)`
  * Build grpc response
    * Map `Sensor` domain object to `SensorResponse`
    * Convert timestamps to required string format
  * Return response
    * `responseObserver.onNext(sensorResponse)`
    * `responseObserver.onCompleted()`
  * Handle errors
    * Map exceptions to grpc status codes
    * Send `responseObserver.onError(...)`
## Gateway
* Setup graphql server
  * Install graphql server packages
  * Configure server entry point
  * Start server on port (e.g., 4000)
* Define graphql schema
  * Create schema with:
    * `registerSensor` mutation
    * `Sensor` type
* Generate typescript types from proto
  * Use proto compiler tools (e.g., `protoc` + plugins)
  * Generate:
    * grpc client stubs
    * typescript interfaces
  * Ensure generated types are available in the gateway project
* Setup grpc client
  * Import generated typescript stubs
  * Connect to backend grpc service (e.g., port 9090)
* Implement resolver (detailed steps)
  * Receive graphql arguments:
    * `sensorId`
    * `userEmail`
    * `postcode`
  * Create grpc request object:
    * Use generated TS type `RegisterSensorRequest`
    * Match proto naming (e.g., `sensor_id`)
  * Call backend grpc method:
    * `grpcClient.registerSensor(request)`
  * Handle grpc response:
    * Use generated TS type `SensorResponse`
    * Map to graphql model
    * Convert timestamps to iso
  * Handle errors:
    * Convert grpc errors to graphql errors
    * Provide readable messages for frontend
## Frontend
* Setup apollo client
  * Install apollo graphql packages
  * Configure apollo module:
    * Graphql endpoint url (e.g., `http://localhost:4000/graphql`)
* Create registration form ui
  * Input fields:
    * `sensorId`
    * `userEmail`
    * `postcode`
  * Submit button
* Add client-side validation
  * Validate:
    * Required fields
    * Email format
* Implement graphql mutation service
  * Create method:
    * `registerSensor(sensorId, userEmail, postcode)`
    * Uses apollo `mutate()`
* Call mutation from component
  * On submit:
    * Call `registerSensor(...)`
    * Show success or error messages
    * Reset form
## Quick view
### Backend
* [ ] Define `.proto`
* [ ] Generate grpc code
* [ ] Create domain model
* [ ] Create mongodb repository
* [ ] Build application service
* [ ] Implement grpc server
  * Accept request
  * Call application service
  * Build response
  * Return response
  * Handle errors
### Gateway
* [ ] Setup graphql server
* [ ] Define schema
* [ ] Generate ts types from proto
* [ ] Setup grpc client
* [ ] Implement resolver
  * Receive args
  * Map args from camelCase → snake_case
  * Create grpc `request` using generated ts type
  * Call grpc
  * Handle response using generated ts type
  * Map `response` from snake_case → camelCase
  * Handle errors
### Frontend
* [ ] Setup apollo client
* [ ] Build registration form
* [ ] Add validation
* [ ] Create graphql mutation service
* [ ] Call mutation from component
