# REST APIs via gRPC-Gateway
- [Overview](#overview)
- [How it works](#how-it-works)
- [Available REST endpoints](#available-rest-endpoints)
  - [Device Service](#device-service)
  - [Event Service](#event-service)
  - [Organization Service](#organization-service)
- [HTTP method mapping](#http-method-mapping)
- [Request/Response format](#requestresponse-format)
  - [Request format](#request-format)
  - [Response format](#response-format)
- [Field naming in REST](#field-naming-in-rest)
- [URL path parameters](#url-path-parameters)
- [Error handling](#error-handling)
- [Why only some endpoints have REST](#why-only-some-endpoints-have-rest)
- [Protocol selection guide](#protocol-selection-guide)
## Overview
- The GFC APIs project exposes select gRPC endpoints as RESTful HTTP APIs using **gRPC-Gateway**.
- Achieved through `google.api.http` annotations in proto definitions, automatically generating REST endpoints alongside gRPC services.
## How it works
- gRPC-Gateway acts as a `reverse proxy`
  - Receives HTTP/JSON requests from clients
  - Translates to gRPC calls
  - Forwards to the appropriate gRPC service
  - Converts gRPC responses back to HTTP/JSON
  - Returns response to the client
- Benefits:
  - **Single Source of Truth**: Proto defines both gRPC and REST APIs
  - **Automatic Generation**: REST endpoints generated from annotations
  - **No Duplicate Code**: Same business logic for both protocols
  - **Type Safety**: JSON payloads validated against proto schemas
## Available REST endpoints
### Device Service

| Service      | Operation         | HTTP Endpoint                                   | gRPC Method       | Request Body                        | Response                          |
| ------------ | ----------------- | ----------------------------------------------- | ----------------- | ----------------------------------- | --------------------------------- |
| Device       | Get Single Device | `GET /api/v1/devices/{id}`                      | `GetDevice`       | N/A                                 | Device object with full details   |
| Device       | Query Devices     | `POST /api/v1/devices`                          | `QueryDevices`    | `DeviceQueryFilter` with pagination | List of devices matching criteria |
| Device       | Get Device Count  | `POST /api/v1/devices/count`                    | `GetDeviceCount`  | `DeviceQueryFilter`                 | Count statistics                  |
| Event        | Query Events      | `POST /api/v1/events`                           | `QueryEvent`      | Event query filters                 | List of events with metadata      |
| Organization | Add Organization  | `POST /api/v1/OrganizationService/Organization` | `AddOrganization` | Organization details                | Created organization object       |

### HTTP method mapping

| HTTP Method | Usage | Example |
|-------------|-------|---------|
| `GET` | Retrieve single resource | `GET /api/v1/devices/{id}` |
| `POST` | Create resource or complex queries | `POST /api/v1/devices` (query) |
| `PUT` | Update entire resource | Not currently used |
| `PATCH` | Partial update | Not currently used |
| `DELETE` | Remove resource | Not currently used |

### Request/Response format
#### Request format
- All POST endpoints accept JSON request bodies that map to proto message fields:
- **Proto Definition:**
```protobuf
message QueryDeviceRequest {
  DeviceQueryFilter filter = 1;
  core.type.Pagination pagination = 2;
}
````
* **HTTP Request:**
```json
POST /api/v1/devices
Content-Type: application/json
{
  "filter": {
    "deviceIdIn": ["device-001", "device-002"],
    "deviceStateIn": ["ACTIVE"]
  },
  "pagination": {
    "pageSize": 20,
    "pageNumber": 1
  }
}
```
#### Response format
* Responses are JSON representations of proto messages:
* **Proto Definition:**
```protobuf
message QueryDeviceResponse {
  repeated Device devices = 1;
  core.type.Meta meta = 2;
}
```
* **HTTP Response:**
```json
{
  "devices": [
    {
      "deviceId": "device-001",
      "meteringPointId": "mp-123",
      "deviceState": "ACTIVE"
    }
  ],
  "meta": {
    "totalCount": 150,
    "pageNumber": 1,
    "pageSize": 20
  }
}
```
### Field naming in REST
* REST endpoints use **camelCase** for JSON fields, automatically converted from proto **snake_case**:
* Conversion handled automatically by gRPC-Gateway runtime

| Proto Field (snake_case) | JSON Field (camelCase) |
|--------------------------|------------------------|
| `device_id` | `deviceId` |
| `metering_point_id` | `meteringPointId` |
| `last_communication_time` | `lastCommunicationTime` |
| `page_size` | `pageSize` |

### URL path parameters
* For GET requests, path parameters are extracted from the URL:
* **Proto Annotation:**
```protobuf
rpc GetDevice(GetDeviceRequest) returns (GetDeviceResponse) {
  option (google.api.http) = {
    get: "/api/v1/devices/{id}"
  };
}
```
* **HTTP Request:**
```
GET /api/v1/devices/device-12345
```
* The `{id}` placeholder maps to the `id` field in `GetDeviceRequest`
### Error handling
* REST endpoints return standard HTTP status codes:

| Status Code | Meaning | gRPC Status |
|-------------|---------|-------------|
| 200 OK | Success | OK |
| 400 Bad Request | Invalid input | INVALID_ARGUMENT |
| 401 Unauthorized | Authentication required | UNAUTHENTICATED |
| 403 Forbidden | Permission denied | PERMISSION_DENIED |
| 404 Not Found | Resource not found | NOT_FOUND |
| 500 Internal Server Error | Server error | INTERNAL |
| 503 Service Unavailable | Service down | UNAVAILABLE |

* Error response example:
```json
{
  "error": "Invalid device ID format",
  "code": 3,
  "message": "INVALID_ARGUMENT"
}
```
### Why only some endpoints have REST
* Not all gRPC methods have HTTP annotations
* REST endpoints provided for:
  * **Common Query Operations**: Frequently accessed by web/mobile clients
  * **Third-party Integration**: Standard HTTP/JSON for external systems
  * **Simple CRUD Operations**: Straightforward create/read operations
* gRPC-only operations:

  * Device registration and state updates
  * Bulk data uploads (CSV)
  * Streaming operations
  * Internal service-to-service calls
  * High-frequency operations requiring performance

### Protocol selection guide

| Client Type           | Recommended Protocol | Reason                               |
| --------------------- | -------------------- | ------------------------------------ |
| Web Browser           | GraphQL or REST      | Standard HTTP, easy debugging        |
| Mobile App            | GraphQL or REST      | Flexible queries, standard libraries |
| External Integration  | REST                 | Widely supported, no special tooling |
| Internal Microservice | gRPC Native          | Performance, streaming, type safety  |
| Bulk Operations       | gRPC Native          | Efficient binary protocol            |
| Real-time Updates     | gRPC Native          | Bi-directional streaming             |
