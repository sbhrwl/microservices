# Extending the organization api safely
- [Adding new fields](#adding-new-fields)
- [Adding new RPCs](#adding-new-rpcs)
- [Backward compatibility](#backward-compatibility)
## Adding new fields
- New fields should first be added to proto messages with optional or nullable types.
- Corresponding GraphQL types should add the new fields as nullable or optional.
- Gateway resolvers should map proto → GraphQL with safe defaults.
- Example:

| Proto Field | GraphQL Field | Notes |
|-------------|---------------|-------|
| `locationCode` (optional string) | `locationCode` (String) | Added safely without breaking existing clients |
| `settings.maxDevices` (optional int) | `maxDevices` (Int) | Added to SettingsInput and Settings types |

## Adding new RPCs
- New functionality should be introduced via new RPCs, not modifying existing ones.
- Corresponding GraphQL operations (Query or Mutation) should be added.
- Example:

| New RPC | GraphQL Operation | Notes |
|---------|-----------------|-------|
| `ArchiveOrganization` | `archiveOrganization(orgCode: String!)` | Archiving without affecting `GetOrganization` |
| `BulkUpdateSettings` | `bulkUpdateOrganizationSettings(input: [UpdateOrganizationSettingsInput!]!)` | Handles batch updates |

## Backward compatibility
- Existing proto messages and RPCs must not remove fields or change types.
- GraphQL schema should maintain existing types, inputs, and enums.
- Gateway must handle unknown proto fields gracefully (ignore or default).
- Versioning strategy may be used for breaking changes (e.g., `v1.OrganizationService` → `v2.OrganizationService`).
