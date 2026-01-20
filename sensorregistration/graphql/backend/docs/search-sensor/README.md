## Search sensor
- [Introduction](#introduction)
- [Architecture overview](#architecture-overview)
- [Frontend search initiation](#frontend-search-initiation)
- [Gateway query resolution](#gateway-query-resolution)
- [Backend data retrieval](#backend-data-retrieval)
- [Response propagation](#response-propagation)
- [Error flow](#error-flow)
- [Performance analysis](#performance-analysis)
- [Comparison: search vs. registration](#comparison-search-vs-registration)
- [Monitoring and observability](#monitoring-and-observability)
- [Security considerations](#security-considerations)
- [Best practices and lessons learned](#best-practices-and-lessons-learned)
- [Conclusion](#conclusion)
## Introduction
- Demonstrates the read path through the microservices architecture
- Highlights separation of concerns, protocol translation, and error handling
- Emphasizes handling of non-existent data as a first-class scenario
## Architecture overview
```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│   Angular   │─────▶│   GraphQL   │─────▶│    gRPC     │─────▶│   MongoDB   │
│   Frontend  │ HTTP │   Gateway   │ gRPC │   Backend   │      │   Database  │
│  (Query)    │      │ (Query)     │      │ (FindById)  │      │ (findOne)   │
└─────────────┘      └─────────────┘      └─────────────┘      └─────────────┘
```
- Differences from registration
  - GraphQL query instead of mutation
  - Read-only database access
  - Idempotent and retry-safe
## Frontend search initiation
### User interaction (Angular component)
```typescript
// File: c:\Git\microservices\sensorregistration\graphql\frontend\src\app\app.component.ts
searchSensor() {
  if (!this.searchSensorId) {
    this.error = 'Sensor ID is required';
    return;
  }
  this.loading = true;
  this.clearMessages();
  this.graphqlService.getSensor(this.searchSensorId).subscribe({
    next: (sensor) => {
      this.loading = false;
      this.currentSensor = sensor;
      this.successMessage = 'Sensor found!';
    },
    error: (err) => {
      this.loading = false;
      this.currentSensor = null;
      this.error = err.message || 'Sensor not found';
    }
  });
}
````

* Responsibilities
  * Input validation
  * UI state management
  * Clearing stale data on error
## Gateway query resolution
### GraphQL service layer
```typescript
// File: c:\Git\microservices\sensorregistration\graphql\frontend\src\app\services\graphql.service.ts
getSensor(sensorId: string): Observable<Sensor> {
  const query = `
    query GetSensor($sensorId: String!) {
      sensor(sensorId: $sensorId) {
        sensorId
        userEmail
        postcode
        status
        registeredAt
      }
    }
  `;
  return this.http.post<GraphQLResponse<{ sensor: Sensor }>>(this.graphqlUrl, {
    query,
    variables: { sensorId }
  }).pipe(
    map(response => {
      if (response.errors) {
        throw new Error(response.errors[0].message);
      }
      return response.data.sensor;
    })
  );
}
```
### GraphQL schema
```graphql
// File: c:\Git\microservices\sensorregistration\graphql\gateway\src\schema.graphql
type Query {
  sensor(sensorId: String!): Sensor
  sensorsByUser(userEmail: String!): [Sensor!]!
}
type Sensor {
  sensorId: String!
  userEmail: String!
  postcode: String!
  status: String!
  registeredAt: String!
  lastUpdatedAt: String
}
```
### GraphQL resolver
```typescript
// File: c:\Git\microservices\sensorregistration\graphql\gateway\src\resolvers\sensorResolver.ts
export const resolvers = {
  Query: {
    sensor: async (_: any, args: { sensorId: string }): Promise<Sensor | null> => {
      try {
        const response = await grpcClient.getSensor({ sensor_id: args.sensorId });
        return toGraphQLSensor(response);
      } catch (error: any) {
        if (error.code === grpc.status.NOT_FOUND) {
          return null;
        }
        throw new GraphQLError(error.message || 'Failed to fetch sensor', {
          extensions: { code: error.code || 'INTERNAL_SERVER_ERROR' }
        });
      }
    }
  }
};
```
## Backend data retrieval
### gRPC service
```java
// File: c:\Git\microservices\sensorregistration\graphql\backend\src\main\java\com\example\sensor\grpc\SensorGrpcService.java
@Override
public void getSensor(GetSensorRequest request,
                     StreamObserver<SensorResponse> responseObserver) {
    try {
        Sensor sensor = applicationService.getSensor(request.getSensorId());
        responseObserver.onNext(toProto(sensor));
        responseObserver.onCompleted();
    } catch (SensorNotFoundException e) {
        responseObserver.onError(Status.NOT_FOUND
            .withDescription(e.getMessage())
            .asRuntimeException());
    } catch (Exception e) {
        responseObserver.onError(Status.INTERNAL
            .withDescription("Internal server error")
            .asRuntimeException());
    }
}
```
### Application service
```java
// File: c:\Git\microservices\sensorregistration\graphql\backend\src\main\java\com\example\sensor\application\SensorApplicationService.java
@Transactional(readOnly = true)
public Sensor getSensor(String sensorId) {
    return sensorRepository.findBySensorId(sensorId)
        .orElseThrow(() -> new SensorNotFoundException(
            "Sensor with ID '" + sensorId + "' not found"
        ));
}
```

### Repository
```java
// File: c:\Git\microservices\sensorregistration\graphql\backend\src\main\java\com\example\sensor\repository\SensorRepository.java
public interface SensorRepository extends MongoRepository<Sensor, String> {
    Optional<Sensor> findBySensorId(String sensorId);
}
```

## Response propagation
### GraphQL success response
```json
{
  "data": {
    "sensor": {
      "sensorId": "SENSOR-001",
      "userEmail": "user@example.com",
      "postcode": "12345",
      "status": "ACTIVE",
      "registeredAt": "2024-01-15T10:30:00Z"
    }
  }
}
```

### GraphQL not found response
```json
{
  "data": {
    "sensor": null
  }
}
```

## Error flow

```
MongoDB empty result
→ Optional.empty()
→ SensorNotFoundException
→ gRPC NOT_FOUND
→ GraphQL null
→ UI displays "Sensor not found"
```

## Performance analysis
* Typical latency: ~80ms
* Faster than registration due to:
  * No writes
  * Indexed lookup
  * Read-only transaction

## Comparison: search vs. registration

| Aspect       | Search | Registration |
| ------------ | ------ | ------------ |
| GraphQL type | Query  | Mutation     |
| Idempotent   | Yes    | No           |
| Caching      | Yes    | No           |
| Database     | Read   | Write        |

## Monitoring and observability
* Track cache hit rate
* Track gRPC latency
* Track search hit vs miss

## Security considerations
* Validate input at frontend, gateway, and backend
* Enforce authorization at gateway resolver

## Best practices and lessons learned
* Return null for not found in queries
* Use read-only transactions
* Index frequently queried fields
* Perform defensive data transformation

## Conclusion
* Search path optimizes for reads and UX
* Not found is a valid state, not an error
* Architecture remains consistent across read and write paths