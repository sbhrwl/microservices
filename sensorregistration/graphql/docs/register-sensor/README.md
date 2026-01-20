# Sensor registration
* [Introduction](#introduction)
* [Architecture overview](#architecture-overview)
* [Frontend initiation](#frontend-initiation)
* [Gateway and protocol translation](#gateway-and-protocol-translation)
* [Backend domain and persistence](#backend-domain-and-persistence)
* [Response propagation](#response-propagation)
* [Error flow](#error-flow)
* [Performance considerations](#performance-considerations)
* [Monitoring and observability](#monitoring-and-observability)
* [Security considerations](#security-considerations)
* [Scalability patterns](#scalability-patterns)
* [Lessons learned and best practices](#lessons-learned-and-best-practices)
* [Conclusion](#conclusion)

## Introduction
* [Walkthrough](summary/README.md) of a single business operation across a microservices architecture
* Focus on a sensor registration flow
* Demonstrates interaction between:
  * Angular
  * GraphQL
  * gRPC
  * MongoDB
## Architecture overview
```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│   Angular   │─────▶│   GraphQL   │─────▶│    gRPC     │─────▶│   MongoDB   │
│   Frontend  │ HTTP │   Gateway   │ gRPC │   Backend   │      │   Database  │
│Port: 4200?) │      │ (Port: 4000)│      │ (Port: 9090)│      │ (Port: 27017)│
└─────────────┘      └─────────────┘      └─────────────┘      └─────────────┘
```

* Key architectural decisions:
  * Frontend to gateway uses HTTP and GraphQL
  * Gateway to backend uses gRPC
  * Backend to database uses MongoDB
## Frontend initiation
### User interaction (Angular component)
* [app.component.ts](../../frontend/src/app/app.component.ts)
```typescript
// File: c:\Git\microservices\sensorregistration\graphql\frontend\src\app\app.component.ts
registerSensor() {
  if (!this.registerForm.sensorId || !this.registerForm.userEmail || !this.registerForm.postcode) {
    this.error = 'All fields are required';
    return;
  }

  this.loading = true;
  this.clearMessages();

  this.graphqlService.registerSensor(
    this.registerForm.sensorId,
    this.registerForm.userEmail,
    this.registerForm.postcode
  ).subscribe({
    next: (sensor) => {
      this.loading = false;
      this.successMessage = `Sensor ${sensor.sensorId} registered successfully!`;
      this.currentSensor = sensor;
      this.registerForm = { sensorId: '', userEmail: '', postcode: '' };
    },
    error: (err) => {
      this.loading = false;
      this.error = err.message || 'Failed to register sensor';
    }
  });
}
```
* Responsibilities:
  * Client-side validation
  * UI state management
  * Delegation to service layer
  * Reactive error handling
### GraphQL service layer
* [graphql.service.ts](../../frontend/src/app/services/graphql.service.ts)
```typescript
// File: c:\Git\microservices\sensorregistration\graphql\frontend\src\app\services\graphql.service.ts
registerSensor(sensorId: string, userEmail: string, postcode: string): Observable<Sensor> {
  const mutation = `
    mutation RegisterSensor($sensorId: String!, $userEmail: String!, $postcode: String!) {
      registerSensor(sensorId: $sensorId, userEmail: $userEmail, postcode: $postcode) {
        sensorId
        userEmail
        postcode
        status
        registeredAt
      }
    }
  `;

  return this.http.post<GraphQLResponse<{ registerSensor: Sensor }>>(this.graphqlUrl, {
    query: mutation,
    variables: { sensorId, userEmail, postcode }
  }).pipe(
    map(response => {
      if (response.errors) {
        throw new Error(response.errors[0].message);
      }
      return response.data.registerSensor;
    })
  );
}
```
* Key points:
  * Typed GraphQL mutation
  * Explicit field selection
  * HTTP POST transport
  * Error mapping from GraphQL response
## Gateway and protocol translation
### GraphQL schema definition
* [schema.graphql](../../gateway/src/schema.graphql)
```graphql
// File: c:\Git\microservices\sensorregistration\graphql\gateway\src\schema.graphql
type Mutation {
  registerSensor(
    sensorId: String!
    userEmail: String!
    postcode: String!
  ): Sensor!
  
  updateSensorPostcode(
    sensorId: String!
    postcode: String!
  ): Sensor!
}
```
* Schema acts as:
  * API contract
  * Validation layer
  * Type boundary
### GraphQL resolver
* [sensorResolver.ts](../../gateway/src/resolvers/sensorResolver.ts)
```typescript
// File: c:\Git\microservices\sensorregistration\graphql\gateway\src\resolvers\sensorResolver.ts
async registerSensor(_: any, args: RegisterSensorArgs): Promise<Sensor> {
  const { sensorId, userEmail, postcode } = args;
  
  // Call gRPC backend
  const response = await grpcClient.registerSensor({
    sensor_id: sensorId,
    user_email: userEmail,
    postcode: postcode
  });
  
  // Transform gRPC response to GraphQL format
  return toGraphQLSensor(response);
}

function toGraphQLSensor(response: any) {
  console.log('Converting response to GraphQL:', response);
  
  return {
    sensorId: response.sensor_id || response.sensorId,
    userEmail: response.user_email || response.userEmail,
    postcode: response.postcode,
    status: response.status,
    registeredAt: toISOString(response.registered_at || response.registeredAt),
    lastUpdatedAt: toISOString(response.last_updated_at || response.lastUpdatedAt)
  };
}
```
* Responsibilities:
  * Protocol translation
  * Naming conversion
  * Data mapping
  * Observability hooks
## Backend domain and persistence
### gRPC service entry point
```java
// File: c:\Git\microservices\sensorregistration\graphql\backend\src\main\java\com\example\sensor\grpc\SensorGrpcService.java
@Override
public void registerSensor(RegisterSensorRequest request, 
                           StreamObserver<SensorResponse> responseObserver) {
    try {
        Sensor sensor = applicationService.registerSensor(
            request.getSensorId(),
            request.getUserEmail(),
            request.getPostcode()
        );
        
        responseObserver.onNext(toProto(sensor));
        responseObserver.onCompleted();
        
    } catch (SensorAlreadyExistsException e) {
        log.warn("Sensor already exists: {}", e.getMessage());
        responseObserver.onError(Status.ALREADY_EXISTS
            .withDescription(e.getMessage())
            .asRuntimeException());
            
    } catch (IllegalArgumentException e) {
        log.warn("Invalid input: {}", e.getMessage());
        responseObserver.onError(Status.INVALID_ARGUMENT
            .withDescription(e.getMessage())
            .asRuntimeException());
            
    } catch (Exception e) {
        log.error("Internal error during sensor registration", e);
        responseObserver.onError(Status.INTERNAL
            .withDescription("Internal server error")
            .asRuntimeException());
    }
}
```
### Application service layer
```java
// File: c:\Git\microservices\sensorregistration\graphql\backend\src\main\java\com\example\sensor\application\SensorApplicationService.java
@Service
public class SensorApplicationService {
    
    private final SensorRepository repository;
    
    @Transactional
    public Sensor registerSensor(String sensorId, String userEmail, String postcode) {
        if (repository.existsBySensorId(sensorId)) {
            throw new SensorAlreadyExistsException("Sensor " + sensorId + " already registered");
        }
        
        Sensor sensor = Sensor.create(sensorId, userEmail, postcode);
        return repository.save(sensor);
    }
}
```
### Domain entity
```java
// File: c:\Git\microservices\sensorregistration\graphql\backend\src\main\java\com\example\sensor\domain\Sensor.java
@Document(collection = "sensorRegistrations")
public class Sensor {
    
    @Id
    private String id;
    
    private String sensorId;
    private String userEmail;
    private String postcode;
    private SensorStatus status;
    private LocalDateTime registeredAt;
    private LocalDateTime lastUpdatedAt;
    
    public static Sensor create(String sensorId, String userEmail, String postcode) {
        validateInputs(sensorId, userEmail, postcode);
        
        Sensor sensor = new Sensor();
        sensor.sensorId = sensorId;
        sensor.userEmail = userEmail;
        sensor.postcode = postcode;
        sensor.status = SensorStatus.ACTIVE;
        sensor.registeredAt = LocalDateTime.now();
        sensor.lastUpdatedAt = LocalDateTime.now();
        
        return sensor;
    }
    
    private static void validateInputs(String sensorId, String userEmail, String postcode) {
        if (sensorId == null || sensorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Sensor ID cannot be empty");
        }
        if (userEmail == null || !userEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (postcode == null || postcode.trim().isEmpty()) {
            throw new IllegalArgumentException("Postcode cannot be empty");
        }
    }
}
```
### MongoDB repository
```java
// File: c:\Git\microservices\sensorregistration\graphql\backend\src\main\java\com\example\sensor\repository\SensorRepository.java
public interface SensorRepository extends MongoRepository<Sensor, String> {
    
    boolean existsBySensorId(String sensorId);
    
    Optional<Sensor> findBySensorId(String sensorId);
    
    List<Sensor> findByUserEmail(String userEmail);
}
```
## Response propagation
### gRPC response
```protobuf
message SensorResponse {
  string sensor_id = 1;
  string user_email = 2;
  string postcode = 3;
  string status = 4;
  string registered_at = 5;
  string last_updated_at = 6;
}
```
### GraphQL response
```json
{
  "data": {
    "registerSensor": {
      "sensorId": "SENSOR-001",
      "userEmail": "user@example.com",
      "postcode": "12345",
      "status": "ACTIVE",
      "registeredAt": "2024-01-15T10:30:00Z"
    }
  }
}
```
## Error flow
* Duplicate detection at domain layer
* gRPC status mapping
* GraphQL error translation
* Frontend user feedback
## Conclusion
* Demonstrates an end-to-end request across protocol boundaries
* Shows how each layer adds value without leaking concerns
* Serves as a reference trace for:
  * debugging
  * onboarding
  * architecture reviews

## Next
* turn this into a **formal architecture decision record**
* extract **sequence diagrams (PlantUML or Mermaid TD)**
* tighten it for **engineering handbook style**
* split it into **frontend / gateway / backend deep dives**
