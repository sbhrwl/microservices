# Error handling and responsibility boundaries
- [Validation errors](#validation-errors)
- [Transport errors](#transport-errors)
- [Business logic errors](#business-logic-errors)
- [Error propagation](#error-propagation)
## Validation errors
- GraphQL gateway validates input arguments before calling proto services.
- Examples:
  - `orgCode` cannot be empty or null.
  - `Settings.deviceIdentifier` must be a valid enum.
  - `WarrantyRule.activationDate` must be a valid date.
- Validation errors are returned immediately to the client without hitting gRPC.
## Transport errors
- Errors during gRPC/Dapr communication:
  - Network failure.
  - Service unavailable.
  - Timeout.
- Gateway captures and returns transport errors with appropriate GraphQL error messages.
## Business logic errors
- OrganizationService enforces domain invariants:
  - Unique `orgCode`.
  - Non-conflicting warranty rules.
  - Valid parent organization if provided.
- Violations result in business error responses in proto messages.
- Gateway maps business errors to GraphQL errors.
## Error propagation
- GraphQL errors follow standard GraphQL error format:
  - `message`: human-readable error.
  - `path`: query/mutation path where error occurred.
  - `extensions.code`: optional error code.
- Example mapping table:

| Error Type | Source | How Propagated |
|------------|--------|----------------|
| Validation | GraphQL Gateway | Returned immediately in GraphQL response |
| Transport | Dapr/gRPC | Captured and returned as GraphQL error with code |
| Business Logic | OrganizationService | Mapped to GraphQL error with message and path |
