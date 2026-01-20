# Checklist
* [Backend](#backend)
* [Gateway](#gateway)
* [Frontend](#frontend)
* [Quick view](#quick-view)
## Backend
* Define proto files
  * Create `.proto` file for gRPC:
    * `UpdatePostcodeRequest`
    * `SensorResponse`
    * `SensorService` with `updateSensorPostcode` rpc
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
    * `findBySensorId(sensorId)`
    * `save(sensor)`
* Build application service
  * Implement `updateSensorPostcode()` logic:
    * Fetch sensor by id
    * Validate business rules
    * Update postcode
    * Persist changes
* Implement grpc server (detailed steps)
  * Accept grpc request
    * Read `UpdatePostcodeRequest` fields
  * Call application service
    * `applicationService.updateSensorPostcode(sensorId, newPostcode)`
  * Build grpc response
    * Map `Sensor` domain object to `SensorResponse`
    * Convert timestamps to required string format
  * Return response
    * `responseObserver.onNext(sensorResponse)`
    * `responseObserver.onCompleted()`
  * Handle errors
    * Map `SensorNotFoundException` → `Status.NOT_FOUND`
    * Map validation errors → `Status.INVALID_ARGUMENT`
    * Map other exceptions → `Status.INTERNAL`
    * Send `responseObserver.onError(...)`
## Gateway
* Setup graphql server
  * Install graphql server packages
  * Configure server entry point
  * Start server on port (e.g., 4000)
* Define graphql schema
  * Create schema with:
    * `updateSensorPostcode(sensorId: String!, newPostcode: String!): Sensor!`
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
    * `newPostcode`
  * Map args from camelCase → snake_case
    * `sensorId` → `sensor_id`
    * `newPostcode` → `new_postcode`
  * Create grpc request object
    * Use generated TS type `UpdatePostcodeRequest`
  * Call backend grpc method
    * `grpcClient.updateSensorPostcode(request)`
  * Handle grpc response
    * Use generated TS type `SensorResponse`
    * Map to graphql model
    * Convert timestamps to iso
  * Handle errors
    * Convert grpc errors to graphql errors
    * Provide readable messages for frontend
## Frontend
* Setup apollo client
  * Install apollo graphql packages
  * Configure apollo module
    * Graphql endpoint url (e.g., `http://localhost:4000/graphql`)
* Create update postcode ui
  * Input fields:
    * `sensorId`
    * `postcode`
  * Update button
* Add client-side validation
  * Validate:
    * Required fields
    * Postcode format
* Implement graphql mutation service
  * Create method:
    * `updateSensorPostcode(sensorId, postcode)`
    * Uses apollo `mutate()`
* Call mutation from component
  * On submit:
    * Call `updateSensorPostcode(...)`
    * Show success message
    * Update displayed sensor data
    * Show error message on failure
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
* [ ] Build update postcode ui
* [ ] Add validation
* [ ] Create graphql mutation service
* [ ] Call mutation from component
