# Error handling
* [Error handling architecture](#error-handling-architecture)
* [GraphQL error handling](#graphql-error-handling)
* [Fastify error handling](#fastify-error-handling)
* [GraphQL armor validation errors](#graphql-armor-validation-errors)
## Error handling architecture
* The application implements error handling at multiple layers
* **Error handling layers:**
  * Fastify HTTP error handling
  * Apollo Server GraphQL error handling
  * GraphQL Armor validation error handling
  * Custom plugin error handling
## GraphQL error handling
* Apollo Server provides standardized GraphQL error responses
* **Error structure:**
  * Errors follow GraphQL specification format
  * Include `extensions` field for additional metadata
  * Support custom error codes
* Validation errors are wrapped in `GraphQLError` with `GRAPHQL_VALIDATION_FAILED` code
```typescript
// File: c:\Git\gfc-app\api-gateway\src\integrations\apollo-escape.ts
async unexpectedErrorProcessingRequest(err: any) {
  throw new GraphQLError(err.error, {
    extensions: {
      code: "GRAPHQL_VALIDATION_FAILED",
    },
  });
}
```
* **Note:** Complete error handling implementation, error formatting, and custom error types are not visible in provided snippets

## Fastify error handling
* Fastify provides built-in error handling for HTTP requests
* **Dependency:**
```json
"fastify": "^5.6.2"
```
* **Note:** Custom error handlers, error serialization, and HTTP status code mapping are not visible in provided snippets
## GraphQL armor validation errors
* GraphQL Armor plugins throw errors when validation fails
* **Validation error types:**
  * Cost limit exceeded
  * Maximum depth exceeded
  * Maximum tokens exceeded
  * Maximum aliases exceeded
  * Maximum directives exceeded
  * Blocked field suggestions
* **Error handling implementation:**
```typescript
// File: c:\Git\gfc-app\api-gateway\src\integrations\apollo-escape.ts
async unexpectedErrorProcessingRequest(err: any) {
  throw new GraphQLError(err.error, {
    extensions: {
      code: "GRAPHQL_VALIDATION_FAILED",
    },
  });
}
```
* All GraphQL Armor validation failures are converted to `GraphQLError` instances
* Error code is set to `GRAPHQL_VALIDATION_FAILED`
* **Note:** Specific error messages, client-facing error details, and error masking configuration are not visible in provided snippets
