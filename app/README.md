# Application
- [Platform engineering](platform-engineering/README.md)
- [bff](api-gateway/README.md)
- [gfc-apis](gfc-apis/README.md)
- [gfc-core](gfc-service/README.md)
- [command-orchestrator](command-orchestrator/README.md)
- [protocol-adapter](protocol-adapter/README.md)
- [Tooling](tooling/README.md)
- Use cases
  - [Switching flexibilities](usecases/switching/README.md)
  - [Organization API](usecases/organization/README.md)
  - [File upload](usecases/fileupload/README.md)
  - [Running the app](usecases/running-app/README.md)
  - [Query flexibilities](usecases/query/README.md)

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