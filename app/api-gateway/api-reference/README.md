# API reference
* [GraphQL schema](#graphql-schema)
* [Resolver structure](#resolver-structure)
* [Custom scalar types](#custom-scalar-types)
* [Type definitions organization](#type-definitions-organization)
* [REST endpoints](#rest-endpoints)
* [Health endpoint](#health-endpoint)

## GraphQL schema
* GraphQL schema definitions are located in `src/type-defs.ts`.
* Schema files are merged using `@graphql-tools/merge` to create a unified executable schema.
```typescript
// File: c:\Git\gfc-app\api-gateway\src\index.ts
import typesArray from "./type-defs";
const schema = makeExecutableSchema({
  typeDefs: mergeTypeDefs(typesArray),
  resolvers
});
```

## Resolver structure
* Resolvers are organized in domain-specific directories under `src/resolver-definitions/`.
```
src/resolver-definitions/
└── core/
    └── authorization/
```
```typescript
// File: c:\Git\gfc-app\api-gateway\src\index.ts
import resolvers from "./resolvers";
```
* Resolvers are imported from `src/resolvers.ts` and bound to the GraphQL schema.
## Custom scalar types
* The application defines custom GraphQL scalar types for specialized data handling.
* **Documented scalars:**
  * `Date` - Date-only values
  * `DateTime` - Date and time values
  * `JSONMap` - JSON object structures
  * `Latitude` - Geographic latitude coordinates
  * `Longitude` - Geographic longitude coordinates
* **Location:** `src/common/scalars/`
## Type definitions organization
* Type definitions are loaded and merged from multiple files.
* The `@graphql-tools/load-files` package is available as a dependency, suggesting file-based schema loading.
```typescript
import { mergeTypeDefs } from "@graphql-tools/merge";
import typesArray from "./type-defs";
```
## REST endpoints
* The application includes REST endpoints alongside GraphQL.
* `/flexibilities-import` - CSV file import for flexibilities data
```
src/routes/
└── flexibilities-import.route.ts
```
* **Test data location:** `test/Flexibilities-L540.csv`
## Health endpoint
* A health check endpoint is available for Kubernetes liveness probes and Dapr health checks.
* **Endpoint:** `/healthz`
* **Protocols:**
  * HTTP
  * gRPC (application side only, not Dapr sidecar)
* The "`healthz`" endpoint is used by 
  * DAPR for its health check 
  * Kubernetes liveness probes
  * Our application for its health check by dapr.
* **Note:** Health check implementation, response format, and dependency checks are not visible in provided snippets.
