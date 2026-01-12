# Introduction
- [Purpose](#purpose)
- [Scope](#scope)
- [Out of scope](#out-of-scope)
- [Audience](#audience)
## purpose
- Describes the technical implementation of the **Organization API** in our system.
- Explains how Organization data is modeled, exposed via GraphQL, and resolved through the backend gRPC services.
- Serves as a reference for developers who need to understand or extend the Organization functionality.
## scope
- Covers the Organization domain entities and their relationships.
- Covers the GraphQL types, inputs, and resolvers.
- Covers the mapping from GraphQL to gRPC (OrganizationService).
- Covers field projection, enum, and scalar translation rules.
- Covers execution flows for both read and write operations.
## out of scope
- Other domain APIs (Device, Event, Tag, etc.)
- Deployment and infrastructure details unrelated to Organization.
- Detailed frontend implementation.
## audience
- Backend developers integrating or extending Organization functionality.
- GraphQL gateway maintainers.
- Frontend developers who need to understand the data model and available queries/mutations.
