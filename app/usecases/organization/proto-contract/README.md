# Proto contract
- [RPCs](#rpcs)
- [Messages](#messages)
- [Enums](#enums)
- [FieldMask usage](#fieldmask-usage)
## RPCs

| RPC                            | Input                         | Output               | Description                                                   |
| ------------------------------ | ----------------------------- | -------------------- | ------------------------------------------------------------- |
| **GetOrganization**            | `GetOrganizationRequest`<br>- `org_code` (string)<br>- `field_projections` (optional)       | `GetOrganizationResponse`<br>- `organization`   | Retrieves a single organization by `orgCode`.  |
| **GetOrganizations**           | `GetOrganizationsRequest`<br>- `field_projections` (optional)      | `GetOrganizationsResponse`<br>- `organizations` (list)  | Retrieves all organizations, with optional field filtering.   |
| **AddOrganization**            | `AddOrganizationRequest`<br>- `parent` (optional)<br>- `org_code` (string)<br>- `name` (string)<br>- `settings` (Settings) | `AddOrganizationResponse`<br>- `organization`   | Creates a new organization with optional parent and settings. |
| **UpdateOrganizationSettings** | `UpdateOrganizationSettingsRequest`<br>- `org_code` (string)<br>- `settings` (Settings)   | `UpdateOrganizationSettingsResponse`<br>- `organization` | Updates the settings for an existing organization.      |

## Messages

| Entity           | Attribute                 | Type                                                              | Description                               |
| ---------------- | ------------------------- | ----------------------------------------------------------------- | ----------------------------------------- |
| **Organization** | `name`                    | string                                                            | Organization name                         |
|                  | `org_code`                | string                                                            | Unique identifier                         |
|                  | `parent`                  | string (optional)                                                 | Optional parent organization              |
|                  | `settings`                | Settings                                                          | Device and warranty configuration         |
| **Settings**     | `device_identifier`       | enum (`SERIAL_NUMBER`, `UTILITY_SERIAL_NUMBER`)                   | Type of device identifier used            |
|                  | `warranty_rules`          | list of `WarrantyRule`                                            | Rules defining warranty conditions        |
| **WarrantyRule** | `activation_date`         | date                                                              | Date when the rule becomes active         |
|                  | `event_trigger`           | enum (`STATE_CHANGED_TO_OPERATION`, `STATE_CHANGED_TO_INVENTORY`) | Event activating the warranty             |
|                  | `warranty_duration_years` | int                                                               | Duration of warranty if rule is triggered |

## Enums

| Enum                     | Values                                                                                      | Description                                   |
| ------------------------ | ------------------------------------------------------------------------------------------- | --------------------------------------------- |
| **DeviceIdentifierType** | `SERIAL_NUMBER`<br>`UTILITY_SERIAL_NUMBER`                                                  | Type of device identifier used in `Settings`. |
| **WarrantyEventTrigger** | `EVENT_TRIGGER_NOT_DEFINED`<br>`STATE_CHANGED_TO_OPERATION`<br>`STATE_CHANGED_TO_INVENTORY` | Event that triggers a warranty rule.          |

## Fieldmask usage
- FieldMask allows partial selection of fields for read requests.
- Used in `GetOrganizationRequest` and `GetOrganizationsRequest` to optimize performance.
- GraphQL selection sets are mapped to FieldMask paths in the gateway.
- Only selected fields are returned by the proto service, reducing payload.
