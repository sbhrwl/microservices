# Summary
- [Key architectural principles](#key-architectural-principles)
- [GraphQL ↔ gRPC patterns](#graphql-grpc-patterns)
- [Performance and reliability](#performance-and-reliability)
## Key architectural principles
- `OrganizationService` is the single source of truth for all Organization data.
- GraphQL gateway exposes flexible, frontend-friendly APIs while delegating data operations to proto services.
- `Field-level selection` via `FieldMask` reduces unnecessary data transfer.
- Enum and scalar translation ensures consistency across layers.
- Error handling separates validation, transport, and business logic responsibilities.
## Graphql ↔ gRPC patterns
- GraphQL `Query` and `Mutation` operations map to proto RPCs.
- Gateway resolvers handle:
  - Input conversion (GraphQL → proto)
  - FieldMask mapping from selection sets
  - Enum and scalar translation
  - Output conversion (proto → GraphQL)
- Tables summarize common mappings:

| GraphQL Operation | Proto RPC | Notes |
|------------------|-----------|-------|
| `organization` | `GetOrganization` | Maps `orgCode` and FieldMask |
| `organizations` | `GetOrganizations` | Maps selection set to FieldMask |
| `addOrganization` | `AddOrganization` | Maps GraphQL input to proto request |
| `updateOrganizationSettings` | `UpdateOrganizationSettings` | Maps GraphQL input to proto request |

## Performance and reliability
- FieldMask reduces payload and improves serialization performance.
- Only requested fields are fetched from proto services.
- Deeply nested queries should be requested carefully for performance.
- Gateway handles transport retries and error mapping to GraphQL.
- Maintaining backward compatibility ensures clients remain stable during extensions.
