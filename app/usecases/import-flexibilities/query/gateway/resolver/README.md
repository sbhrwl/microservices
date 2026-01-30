# Resolver
- [What this resolver does](#what-this-resolver-does)
- [Flow](#flow)
- [Request handling](#request-handling)
- [Response mapping](#response-mapping)
- [Error handling](#error-handling)
- [Why this pattern matters](#why-this-pattern-matters)
- [Next](#next)
## What this resolver does
* Implements the **GraphQL `flexibilities` query**
* Acts as a bridge between **GraphQL** and a **gRPC Flexibility service**
* Translates:
  * GraphQL arguments → gRPC request
  * gRPC response → GraphQL response shape
## Flow
* Reads the auth token from `context`
* Creates a new `FlexibilityClient`
* Builds a `QueryFlexibilitiesRequest` protobuf object
* Calls `queryFlexibilities` via gRPC
* Maps the result into GraphQL-friendly fields
* Handles and normalizes errors
## Request handling
* Builds a `QueryFlexibilitiesRequest` protobuf object
* Conditionally builds:
  * `filter` object (only if provided in GraphQL input)
  * `pagination` object (page size and page number)
* Uses protobuf’s `.create()` to ensure type-safe gRPC requests
## Response mapping
* Converts gRPC items into GraphQL fields:
  * `id`
  * `name`
  * `flexibilityType`
* Extracts `totalCount` from gRPC metadata
* Provides safe defaults for missing values
## Error handling
* Wraps failures in a `GraphQLError`
* Uses a standard `INTERNAL_SERVER_ERROR` code
* Logs the original error for debugging
## Why this pattern matters
* Keeps GraphQL thin and transport-agnostic
* Centralizes gRPC logic in the client
* Makes GraphQL a clean façade over backend services
## Next
* Draw a request flow diagram
* Show how filters should be wired through
* Refactor this to reuse the singleton client
