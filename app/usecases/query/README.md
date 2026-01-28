# Flexibilities query flow
- [GraphQL query entry](#graphql-query-entry)
- [Schema definition](#schema-definition)
- [Resolver execution](#resolver-execution)
- [gRPC client](#grpc-client)
- [Dapr service invocation](#dapr-service-invocation)
- [Response flow (reverse)](#response-flow-reverse)
## GraphQL query entry
- **File:** GraphQL Playground / client
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
  * Defines the `flexibilities` query signature
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
