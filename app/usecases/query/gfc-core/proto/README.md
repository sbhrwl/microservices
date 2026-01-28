# Flexibility proto
- [Package & options](#package--options)
- [Services](#services)
- [Upload messages](#upload-messages)
- [Confirm upload messages](#confirm-upload-messages)
- [Flexibility retrieval](#flexibility-retrieval)
- [Flexibility schema](#flexibility-schema)
- [Query filter](#query-filter)
- [Order enum](#order-enum)
- [Flexibilities collection](#flexibilities-collection)
## Package & options
- **Package:** core.api.flexibility.v1
- **Java package:** com.landisgyr.gfc.api.v1.flexibility
- **Go package:** gfc/api/flexibility/v1;apiv1
## Services
**FlexibilityService** exposes 4 RPCs:
- GetFlexibility(GetFlexibilityRequest) → GetFlexibilityResponse
- QueryFlexibilities(QueryFlexibilitiesRequest) → QueryFlexibilitiesResponse
- UploadFlexibilities(UploadCsvRequest) → UploadCsvResponse
- ConfirmUploadFlexibilities(ConfirmUploadFlexibilitiesRequest) → ConfirmUploadFlexibilitiesResponse
## Upload messages
- **UploadCsvRequest:** metadata, content, filename
- **UploadCsvResponse:** upload_id, csv_summary
- **CsvSummary:** file_metadata, total_rows, invalid_rows, flexibility_type_counts, error_details
- **FileMetadata:** filename, file_size_bytes, uploaded_at
- **FlexibilityTypeCount:** flexibility_type, count
- **ErrorDetails:** errors: RowError[]
- **RowError:** row_number, column_name, error_message
## Confirm upload messages
- **ConfirmUploadFlexibilitiesRequest:** upload_id
- **ConfirmUploadFlexibilitiesResponse:** upload_id, import_summary
- **ImportSummary:** total_rows, imported_rows, failed_rows, error_details
## Flexibility retrieval
- **GetFlexibilityRequest:** org_code, id, field_projections
- **GetFlexibilityResponse:** flexibility: Flexibility
- **QueryFlexibilitiesRequest:** org_codes, filter, order, pagination, field_projections
- **QueryFlexibilitiesResponse:** flexibilities: Flexibilities
## Flexibility schema
- **Flexibility:** id, name, flexibility_type
## Query filter
- **FlexibilityQueryFilter:** flexibility_id_in[], flexibility_name_in[], flexibility_type_in[]
## Order enum
- DEVICE_ORDER_NOT_DEFINED = 0
- DEVICE_ID_ASC = 1
- DEVICE_ID_DESC = 2
- DEVICE_MODEL_ASC = 3
- DEVICE_MODEL_DESC = 4
## Flexibilities collection
- **Flexibilities:** meta, items: Flexibility[]