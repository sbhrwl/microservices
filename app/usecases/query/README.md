# Flexibilities query flow
- [GraphQL query entry](#graphql-query-entry)
- [Schema definition](#schema-definition)
- [Resolver execution](#resolver-execution)
- [gRPC client](#grpc-client)
- [Dapr service invocation](#dapr-service-invocation)
- [Response flow (reverse)](#response-flow-reverse)
## GraphQL query entry
- **File:** GraphQL Playground / client: `http://127.0.0.1:4000/api/graphql`
- Query
```graphql
query ExampleQuery($input: FlexibilitiesInput) {
  flexibilities(input: $input) {
    items {
      flexibilityType
      name
      id
    }
  }
}
````
## Schema definition
* **File:** `graphql/operations.graphql`
  * Defines the flexibilities `query and mutations signature`
* **File:** `graphql/core/api/v1/flexibility.graphql`
  * Defines `FlexibilitiesInput`
  * Defines `FlexibilityQueryFilter`
  * Defines `Flexibilities` types
## Resolver execution
* **File:** `src/resolver-definitions/core/flexibilities/flexibilities.ts`
* Receives GraphQL arguments and context
* Creates `FlexibilityClient` instance
* Builds `QueryFlexibilitiesRequest` protobuf object
* Calls `client.queryFlexibilities()`
## gRPC client
* **File:** `src/clients/flexibility-client.ts`
* `queryFlexibilities()` method
* Retrieves Dapr proxy
* Creates metadata with auth token
* Makes gRPC call via Dapr
## Dapr service invocation
* Dapr sidecar discovers `gfc-core` service via mDNS
* Routes gRPC request to target service
## Response flow (reverse)
* gRPC response returned to `FlexibilityClient`
* Protobuf mapped to GraphQL types
* Response returned to resolver
* GraphQL formats and returns final response

---

# QueryFlexibilities Flow Documentation
## Overview
GraphQL query flow from API Gateway → gRPC → gfc-service → MongoDB
## Flow Diagram
```
Client → API Gateway (GraphQL) → Dapr Sidecar → gfc-service (gRPC) → MongoDB
```
## Step-by-Step Flow
### 1. **GraphQL Request** (Client → API Gateway)
```graphql
query {
  flexibilities(input: { 
    filter: { ... }
    pagination: { pageSize: 10, pageNumber: 1 }
  }) {
    items { id, name, flexibilityType }
    meta { totalCount }
  }
}
```

### 2. **Resolver Execution** (API Gateway)
**File:** `api-gateway/src/resolver-definitions/core/flexibilities/flexibilities.ts`
- Extracts auth token from context
- Creates `QueryFlexibilitiesRequest` protobuf message
- Calls `FlexibilityClient.queryFlexibilities()`

### 3. **gRPC Client Call** (API Gateway → Dapr)
**File:** `api-gateway/src/clients/flexibility-client.ts`
- Creates Dapr metadata with token
- Invokes gRPC via Dapr proxy
- Target: `app-id: gfc-service`
### 4. **Dapr Service Invocation**
```
API Gateway Dapr (port 4000) 
  ↓ 
gfc-service Dapr (port 50012)
  ↓
gfc-service gRPC (port 9090)
```
### 5. **gRPC Service Handler** (gfc-service)
**Proto:** `gfc-apis/proto/core/api/flexibility/v1/flexibility.proto`
- Validates JWT token (Keycloak)
- Parses filter & pagination
- Queries MongoDB
### 6. **MongoDB Query** (gfc-service)
**Database:** `gfc-dev`  
**Collection:** `flexibilities` (assumed)
- Applies filters
- Executes pagination
- Returns results + totalCount
### 7. **Response Chain**
```
MongoDB → gfc-service → Dapr → API Gateway → Client
```
## Key Components

| Component | Port | Protocol | Purpose |
|-----------|------|----------|---------|
| API Gateway | 4000 | HTTP/GraphQL | Client interface |
| API Gateway Dapr | - | gRPC | Service mesh |
| gfc-service | 9090 | gRPC | Business logic |
| gfc-service Dapr | 50012 | gRPC | Service mesh |
| MongoDB | 27017 | TCP | Data persistence |

## Authentication Flow
1. Client sends JWT in `Authorization` header
2. API Gateway extracts token → context
3. Token forwarded via Dapr metadata
4. gfc-service validates against Keycloak
5. Checks realm: `gfc`, client: `test-client-01`
## Error Handling
- **GraphQL errors:** Thrown as `GraphQLError` with codes
- **gRPC errors:** Caught and wrapped in GraphQL errors
- **Auth failures:** Return `UNAUTHENTICATED` status
## Configuration Dependencies
- **API Gateway:** `BACKEND_API`, Keycloak config
- **gfc-service:** MongoDB credentials, Keycloak validation
- **Dapr:** App IDs, ports, config.
