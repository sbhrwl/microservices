# Import flexibilities
- [File upload](file-upload/README.md)
- [Confirm upload](confirm-upload/README.md)
- [Query flexibilities](query/README.md)
---
- [Dapr service invocation](#dapr-service-invocation)
- [Protos](#protos)
- [GraphQL schema](#graphql-schema)
- [Response](#response)
## Dapr service invocation
* Dapr sidecar discovers `gfc-core` service via mDNS
* Routes gRPC request to target service
```mermaid
flowchart TD
    A["api gateway dapr (port 4000)"]
    B["gfc-core dapr (port 50012)"]
    C["gfc-core grpc (port 9090)"]

    A --> B
    B --> C
```
## Protos
<img src="images/protos.png">

<details>
  <summary>mermaid</summary>

```mermaid
classDiagram
direction TD

class UploadCsvRequest {
  string metadata
  bytes content
  string filename
}

class UploadCsvResponse {
  string upload_id
  CsvSummary csv_summary
}

class CsvSummary {
  FileMetadata file_metadata
  int32 total_rows
  int32 invalid_rows
  FlexibilityTypeCount[] flexibility_type_counts
  ErrorDetails error_details
}

class FileMetadata {
  string filename
  int64 file_size_bytes
  Timestamp uploaded_at
}

class FlexibilityTypeCount {
  string flexibility_type
  int32 count
}

class ErrorDetails {
  RowError[] errors
}

class RowError {
  int32 row_number
  string column_name
  string error_message
}

class ConfirmUploadFlexibilitiesRequest {
  string upload_id
}

class ConfirmUploadFlexibilitiesResponse {
  string upload_id
  ImportSummary import_summary
}

class ImportSummary {
  int32 total_rows
  int32 imported_rows
  int32 failed_rows
  ErrorDetails error_details
}

%% Relationships
UploadCsvResponse --> CsvSummary
CsvSummary --> FileMetadata
CsvSummary --> FlexibilityTypeCount
CsvSummary --> ErrorDetails
ErrorDetails --> RowError

ConfirmUploadFlexibilitiesResponse --> ImportSummary
ImportSummary --> ErrorDetails

%% RPC calls
class GRPC {
  +UploadFlexibilities(UploadCsvRequest) returns UploadCsvResponse
  +ConfirmUploadFlexibilities(ConfirmUploadFlexibilitiesRequest) returns ConfirmUploadFlexibilitiesResponse
}

GRPC --> UploadCsvRequest
GRPC --> UploadCsvResponse
GRPC --> ConfirmUploadFlexibilitiesRequest
GRPC --> ConfirmUploadFlexibilitiesResponse
```
</details>

## GraphQL schema
<img src="images/graphql.png">

<details>
  <summary>mermaid</summary>

```mermaid
classDiagram
direction TD

class ConfirmUploadFlexibilitiesInput {
  String uploadId
}

class ConfirmFlexibilityUploadResult {
  String uploadId
  ImportSummary importSummary
}

class ImportSummary {
  Int totalRows
  Int importedRows
  Int failedRows
  ErrorDetails errorDetails
}

class ErrorDetails {
  RowError[] errors
}

class RowError {
  Int rowNumber
  String columnName
  String errorMessage
}

%% Relationships
ConfirmFlexibilityUploadResult --> ImportSummary
ImportSummary --> ErrorDetails
ErrorDetails --> RowError
```
</details>

## Response
```json
{
  "uploadId": "a4fed697-c437-44e4-846f-d0e093283c6f",
  "csvSummary": {
    "fileMetadata": {
      "filename": "Flexibilities-L540.csv",
      "fileSizeBytes": "13157",
      "uploadedAt": "2026-01-28T19:49:08.264Z"
    },
    "totalRows": 100,
    "invalidRows": 12,
    "flexibilityTypeCounts": [
      {
        "flexibilityType": "Boiler",
        "count": 45
      },
      {
        "flexibilityType": "Heat pump",
        "count": 32
      },
      {
        "flexibilityType": "Lighting",
        "count": 28
      },
      {
        "flexibilityType": "PV",
        "count": 18
      }
    ],
    "errorDetails": {
      "errors": [
        {
          "rowNumber": 5,
          "columnName": "FlexibilityId",
          "errorMessage": "Duplicate FlexibilityId found: FLEX-001"
        },
        {
          "rowNumber": 12,
          "columnName": "Tenant",
          "errorMessage": "Invalid tenant format: expected alphanumeric"
        },
        {
          "rowNumber": 18,
          "columnName": "FlexibilityId",
          "errorMessage": "Duplicate FlexibilityId found: FLEX-042"
        },
        {
          "rowNumber": 23,
          "columnName": "FlexibilityType",
          "errorMessage": "Unknown flexibility type: InvalidType"
        },
        {
          "rowNumber": 31,
          "columnName": "Name",
          "errorMessage": "Missing required field: Name"
        },
        {
          "rowNumber": 45,
          "columnName": "FlexibilityId",
          "errorMessage": "Duplicate FlexibilityId found: FLEX-078"
        },
        {
          "rowNumber": 67,
          "columnName": "Capacity",
          "errorMessage": "Invalid format: expected numeric value"
        },
        {
          "rowNumber": 89,
          "columnName": "Location",
          "errorMessage": "Missing required field: Location"
        }
      ]
    }
  }
}
```
