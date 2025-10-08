# Data API service
- [APIs](#apis)
- [Implementation order](#implementation-order)
- [Test control request](#test-control-request)
- [Test control request by ID](#test-control-request-by-id)
- [Test request change log](#test-request-change-log)
- [Test request tracker](#test-request-tracker)
- [Swagger API documentation](#swagger-api-documentation)
## APIs
| API Name	| Endpoint	| Return Type (DTO)	| Implementation Focus (Java/Spring Boot)|
| --------- | --------- | ----------------- | -------------------------------------- | 
| Request Details	| `GET /requests/{id}`	| `ControlRequestDTO`	| `ControlRequest` Entity -> Service maps to `ControlRequestDTO`.| 
| Request Status Details	| `GET /requests/{id}/logs`	| `List<ChangeLogDTO>`	| `RequestChangeLog` Entity -> Service fetches logs -> maps to `List<ChangeLogDTO>`.| 
| Request Tracker	| `GET /requests/{id}/tracker`	| `RequestTrackerDTO`	| JPA `@OneToMany` Fetch -> Service combines/maps to `RequestTrackerDTO`.| 
| Error Handling	| All endpoints	| HTTP 404 + JSON Error Body	| Global `@ControllerAdvice` to catch `ResourceNotFoundException`.| 

## Implementation order
- **Model/Entity** (JPA Classes)
- **Repository** (Spring Data JPA Interfaces)
- **DTO** (Data Transfer Classes)
- **Service** (Business Logic)
- **Controller** (REST Endpoints)
- `Entity` → `Repository` → `DTO` → `Service` → `Controller`
## Test control request
- `GET /requests/`
- `http://localhost:8085/api/v1/requests`
## Test control request by ID
- `GET /requests/{id}`
- `http://localhost:8085/api/v1/requests/42`
## Test request change log
- `GET /requests/{id}/logs`
- `http://localhost:8085/api/v1/requests/42/logs`
## Test request tracker
- `GET /requests/{id}/tracker`
- `http://localhost:8085/api/v1/requests/42/tracker`
## Swagger API documentation
- [Swagger API](https://github.com/sbhrwl/system_design/blob/main/docs/services/swaggerapi/README.md)
