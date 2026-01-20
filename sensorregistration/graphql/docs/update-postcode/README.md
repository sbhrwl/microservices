# Update postcode
* [Introduction](#introduction)
* [Architecture overview](#architecture-overview)
* [Frontend update initiation](#frontend-update-initiation)
* [Gateway mutation resolution](#gateway-mutation-resolution)
* [Backend business logic and persistence](#backend-business-logic-and-persistence)
* [Return journey response propagation](#return-journey-response-propagation)
* [Error flows business rule violations](#error-flows-business-rule-violations)
* [Performance analysis](#performance-analysis)
* [Idempotency considerations](#idempotency-considerations)
* [Concurrency handling](#concurrency-handling)
* [Monitoring and observability](#monitoring-and-observability)
* [Security considerations](#security-considerations)
* [Best practices and lessons learned](#best-practices-and-lessons-learned)
## Introduction
* Postcode update is a partial entity update
* Modifies existing data with strict business rules
* [Emphasizes](summary/README.md) consistency, auditability, and validation
* Key themes
  * Optimistic concurrency
  * Business rule validation
  * Audit trails
  * Conditional idempotency
## Architecture overview
* End-to-end flow
  * Angular frontend
  * GraphQL gateway
  * gRPC backend
  * MongoDB persistence
* Characteristics
  * GraphQL operation: mutation
  * Database operation: `findAndModify`
  * Validation: frontend, gateway, backend, domain
## Frontend update initiation
### User interaction angular component
```typescript
updateSensor() {
  if (!this.updateForm.sensorId || !this.updateForm.postcode) {
    this.error = 'All fields are required';
    return;
  }
  this.loading = true;
  this.clearMessages();
  this.graphqlService.updateSensorPostcode(
    this.updateForm.sensorId,
    this.updateForm.postcode
  ).subscribe({
    next: (sensor) => {
      this.loading = false;
      this.successMessage = `Sensor ${sensor.sensorId} updated successfully!`;
      this.currentSensor = sensor;
      this.updateForm = { sensorId: '', postcode: '' };
    },
    error: (err) => {
      this.loading = false;
      this.error = err.message || 'Failed to update sensor';
    }
  });
}
```
* Responsibilities
  * Input validation
  * UI state management
  * Success and error handling
* UX choice
  * Form cleared on success
  * Form preserved on error
### Graphql service layer
```typescript
updateSensorPostcode(sensorId: string, postcode: string): Observable<Sensor> {
  const mutation = `
    mutation UpdateSensorPostcode($sensorId: String!, $newPostcode: String!) {
      updateSensorPostcode(sensorId: $sensorId, newPostcode: $newPostcode) {
        sensorId
        userEmail
        postcode
        status
        registeredAt
      }
    }
  `;
  return this.http.post<GraphQLResponse<{ updateSensorPostcode: Sensor }>>(this.graphqlUrl, {
    query: mutation,
    variables: { sensorId, newPostcode: postcode }
  }).pipe(
    map(response => {
      if (response.errors) {
        throw new Error(response.errors[0].message);
      }
      return response.data.updateSensorPostcode;
    })
  );
}
```
* Mutation traits
  * Explicit state change
  * Returns updated entity
  * Errors mapped to exceptions

## Gateway mutation resolution
### Schema definition
```graphql
type Mutation {
  updateSensorPostcode(sensorId: String!, newPostcode: String!): Sensor!
}
```

* Design choices
  * Non-nullable return
  * Required parameters
  * Clear naming

### Resolver orchestration
```typescript
updateSensorPostcode: async (_: any, args) => {
  const response = await grpcClient.updateSensorPostcode({
    sensor_id: args.sensorId,
    new_postcode: args.newPostcode
  });
  return toGraphQLSensor(response);
}
```

* Responsibilities
  * Protocol translation
  * Error mapping
  * Data transformation
  * Logging
## Backend business logic and persistence
### gRPC service
* Maps domain exceptions to gRPC status codes
```java
public void updateSensorPostcode(UpdatePostcodeRequest request, StreamObserver<SensorResponse> responseObserver) {
  Sensor sensor = applicationService.updateSensorPostcode(request.getSensorId(), request.getNewPostcode());
  responseObserver.onNext(toProto(sensor));
  responseObserver.onCompleted();
}
```
### Application service
* Orchestrates read, validate, write
* Defines transaction boundary
```java
@Transactional
public Sensor updateSensorPostcode(String sensorId, String newPostcode) {
  Sensor sensor = getSensor(sensorId);
  sensor.updatePostcode(newPostcode);
  return sensorRepository.save(sensor);
}
```
### Domain entity
* Enforces business rules
* Maintains audit timestamp
```java
public void updatePostcode(String newPostcode) {
  if (this.postcode.equals(newPostcode)) {
    throw new IllegalArgumentException("Postcode must change");
  }
  this.postcode = newPostcode;
  this.lastUpdatedAt = LocalDateTime.now();
}
```
### Repository layer
* MongoDB atomic update via findOneAndUpdate
```java
Optional<Sensor> findBySensorId(String sensorId);
```
## Return journey response propagation
* MongoDB returns updated document
* gRPC sends SensorResponse
* GraphQL maps to camelCase
* Frontend updates UI state
## Error flows business rule violations
* Sensor not found
  * Repository returns empty
  * Domain throws exception
  * gRPC NOT_FOUND
  * GraphQL SENSOR_NOT_FOUND
* Postcode unchanged
  * Domain validation fails
  * gRPC INVALID_ARGUMENT
  * GraphQL INVALID_POSTCODE_UPDATE
## Performance analysis
* Typical latency ~120ms
* Breakdown
  * HTTP frontend to gateway
  * gRPC gateway to backend
  * MongoDB read and update
* Update slower than read, faster than create
## Idempotency considerations
* Update is conditionally idempotent
* Same postcode repeated call fails
* Trade-off
  * Enforce change for clarity
  * Accept non-idempotency
## Concurrency handling
* Default behavior: last write wins
* Improvement
  * Add @Version field
  * Enable optimistic locking
* Conflicting updates detected and rejected
## Monitoring and observability
* Metrics
  * Update latency
  * Success vs failure
  * Validation error counts
* Distributed tracing
  * Single trace id across layers
## Security considerations
* Authorization at gateway
* Ownership checks before update
* Input sanitization and format validation
## Best practices and lessons learned
* Enforce rules in domain layer
* Update timestamps automatically
* Return updated entity
* Wrap updates in transactions
