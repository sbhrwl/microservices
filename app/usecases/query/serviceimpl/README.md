# ServiceImpl
- [Overview](#overview)
- [Class role](#class-role)
- [gRPC methods](#grpc-methods)
- [Queryflexibilities flow](#queryflexibilities-flow)
- [Upload flow](#upload-flow)
- [Confirmuploadflexibilities flow](#confirmuploadflexibilities-flow)
  - [Overview](#overview)
  - [Method signature](#method-signature)
  - [Current behavior](#current-behavior)
  - [Request](#request)
  - [Response](#response)
  - [Code flow](#code-flow)
  - [Notes](#notes)
- [Helper methods](#helper-methods)
- [Current limitations](#current-limitations)
## Overview
- gRPC service implementation inside **gfc-core**
- Exposes flexibility read and upload APIs
- Registered via Dagger `@GrpcService`
## Class role
- file: `gfc-core/src/main/java/com/landisgyr/gfc/grpc/FlexibilityServiceImpl.java`
- runtime identity: **Dapr app-id = gfc-service**
- responsibility:
  - serve gRPC requests
  - orchestrate query, upload, confirm flows
  - return protobuf responses
## gRPC methods
### Queryflexibilities
- entry point for GraphQL → gRPC calls
- currently **mock implementation**
- ignores request filters and pagination
### Getflexibility
- stub only
- not implemented
### Uploadflexibilities
- accepts CSV via gRPC
- validates headers
- returns **preview summary**
- does not persist data
### Confirmuploadflexibilities
- confirms previous upload
- returns **mock import summary**
- no database write
## Queryflexibilities flow
- GraphQL resolver calls gRPC `queryFlexibilities`
- Dapr routes call to gfc-core
- method generates in-memory flexibilities
- response returned immediately
- flow:
  - GraphQL api gateway
  - dapr sidecar
  - `gfc-core` gRPC server
  - `queryFlexibilities()`
  - mock response
## Upload flow
- client uploads CSV file
- CSV parsed with Apache Commons CSV
- required headers validated:
  - `FlexibilityId`
  - `Tenant`
- Summary returned:
  - total rows
  - invalid rows
  - error details
- no persistence
## Confirmuploadflexibilities flow
### Overview
- confirms a previously uploaded flexibilities CSV
- implemented in **gfc-core**
- current logic is **stub / mock only**
- **file**: `gfc-core/src/main/java/com/landisgyr/gfc/grpc/FlexibilityServiceImpl.java`
### Method signature
```java
@Override
public void confirmUploadFlexibilities(
    FlexibilityPb.ConfirmUploadFlexibilitiesRequest request,
    StreamObserver<FlexibilityPb.ConfirmUploadFlexibilitiesResponse> responseObserver)
```
### Current behavior
* logs the provided upload id
* builds a hardcoded import summary
* returns response immediately
* completes gRPC stream
### Request
* `upload_id`
  * UUID returned by `uploadFlexibilities`
  * not validated
```protobuf
ConfirmUploadFlexibilitiesRequest {
  string upload_id
}
```
## Response
```protobuf
ConfirmUploadFlexibilitiesResponse {
  string upload_id
  ImportSummary import_summary {
    int32 total_rows
    int32 imported_rows
    int32 failed_rows
    ErrorDetails error_details
  }
}
```
### Mock data returned
* total rows: 100
* imported rows: 88
* failed rows: 12
* errors:
  * duplicate flexibility id
  * invalid tenant format
  * unknown flexibility type
  * missing required fields
  * invalid numeric format
### Code flow
* log upload id
* build `ImportSummary` with mock values
* call `responseObserver.onNext(response)`
* call `responseObserver.onCompleted()`
### Notes
* no database interaction
* no real import logic
* no upload id validation
* reuses same error set as `uploadFlexibilities`
* intended placeholder for future implementation
## Helper methods
- `getFlexibilityType`
  - maps baseId + index → type
- `getOrDefault`
  - zero-safe integer fallback
- `toProjectionFields`
  - expands `deviceAliasId` into serial identifiers
## Current limitations
- no MongoDB access
- no auth validation applied
- no filtering or sorting
- no pagination support
- all responses are mocked
