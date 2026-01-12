# Enum and scalar translation rules
- [Enum mapping](#enum-mapping)
- [Scalar mapping](#scalar-mapping)
- [Nullability rules](#nullability-rules)
## Enum mapping
- Proto enums are converted to GraphQL enums in the gateway resolver.
- Conversion ensures type safety and avoids mismatch between layers.
- Example mapping tables:

| Proto Enum | GraphQL Enum | Notes |
|------------|--------------|-------|
| `DeviceIdentifierType.SERIAL_NUMBER` | `DeviceIdentifierType.SerialNumber` | Maps proto serial number type to GraphQL |
| `DeviceIdentifierType.UTILITY_SERIAL_NUMBER` | `DeviceIdentifierType.UtilitySerialNumber` | Maps proto utility serial number type |
| `WarrantyEventTrigger.STATE_CHANGED_TO_OPERATION` | `WarrantyEventTriggerType.StateChangedToOperation` | Maps event trigger for warranty rules |
| `WarrantyEventTrigger.STATE_CHANGED_TO_INVENTORY` | `WarrantyEventTriggerType.StateChangedToInventory` | Maps event trigger for warranty rules |
| `WarrantyEventTrigger.EVENT_TRIGGER_NOT_DEFINED` | Default mapped to `StateChangedToInventory` | Fallback if undefined in proto |
## Scalar mapping
- Proto `google.type.Date` → GraphQL `Date` scalar.
- Proto `google.protobuf.Timestamp` → GraphQL `DateTime` scalar.
- Scalars ensure consistent formatting across gateway and client.
- Custom scalars may include validation for format and null checks.
## Nullability rules
- Proto optional fields map to nullable GraphQL fields.
- Required proto fields map to non-null GraphQL fields (e.g., `orgCode`, `name`).
- Gateway enforces null checks during input → proto conversion.
- Missing optional fields are omitted in proto messages and GraphQL responses.

