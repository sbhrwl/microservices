# Field projection and performance
- [FieldMask usage](#fieldmask-usage)
- [GraphQL selection mapping](#graphql-selection-mapping)
- [Performance considerations](#performance-considerations)
## Fieldmask usage
- `FieldMask` is used in proto requests to specify which fields to return.
- Applied in:
  - `GetOrganizationRequest.field_projections`
  - `GetOrganizationsRequest.field_projections`
- Reduces payload size by returning only requested fields.
- Enables backend to optimize queries and serialization.
## graphql selection mapping
- GraphQL selection sets are converted to FieldMask paths in the gateway resolver.
- Nested fields are flattened for FieldMask:
  - Example: `settings.warrantyRules.activationDate` → `settings.warranty_rules.activation_date`
- Only selected fields are sent in the proto request.
- Example mapping table:

| GraphQL Field | Proto FieldMask Path |
|---------------|--------------------|
| `orgCode` | `org_code` |
| `name` | `name` |
| `settings.deviceIdentifier` | `settings.device_identifier` |
| `settings.warrantyRules.eventTrigger` | `settings.warranty_rules.event_trigger` |
| `settings.warrantyRules.activationDate` | `settings.warranty_rules.activation_date` |
| `settings.warrantyRules.warrantyDurationYears` | `settings.warranty_rules.warranty_duration_years` |

## Performance considerations
- Using FieldMask `avoids fetching unnecessary data` from proto service.
- Reduces network payload between GraphQL gateway and OrganizationService.
- Reduces processing time in both gateway and backend service.
- Deeply nested fields should be requested only when required.
- For lists, requesting only needed fields improves serialization and reduces client response time.
