# Graphql API surface
- [Queries](#queries)
- [Mutations](#mutations)
- [Resolver mapping table](#resolver-mapping-table)
## Queries

| Query | Arguments | Returns | Notes |
|-------|-----------|---------|-------|
| `organization` | `orgCode: String!` | `Organization` | Resolves via `GetOrganization` RPC |
| `organizations` | none | `Organizations` | Resolves via `GetOrganizations` RPC |

## Mutations

| Mutation | Arguments | Returns | Notes |
|----------|-----------|---------|-------|
| `addOrganization` | `orgCode: String!`, `name: String!`, `parent: String`, `settings: SettingsInput` | `Organization` | Resolves via `AddOrganization` RPC |
| `updateOrganizationSettings` | `input: UpdateOrganizationSettingsInput!` | `Organization` | Resolves via `UpdateOrganizationSettings` RPC |

## Resolver mapping table
- GraphQL operations mapped to proto RPCs:

| GraphQL Operation | Arguments | Proto RPC | Input Mapping | Output Mapping |
|------------------|-----------|-----------|---------------|----------------|
| `organization` | `orgCode` | `GetOrganization` | GraphQL `orgCode` → `GetOrganizationRequest.org_code` | `GetOrganizationResponse.organization` → `Organization` |
| `organizations` | none | `GetOrganizations` | None / FieldMask from selection set | `GetOrganizationsResponse.organizations` → `Organizations.items` |
| `addOrganization` | `orgCode`, `name`, `parent`, `settings` | `AddOrganization` | Inputs → `AddOrganizationRequest` | `AddOrganizationResponse.organization` → `Organization` |
| `updateOrganizationSettings` | `input` | `UpdateOrganizationSettings` | Inputs → `UpdateOrganizationSettingsRequest` | `UpdateOrganizationSettingsResponse.organization` → `Organization` |
