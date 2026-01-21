## Components
- [Frontend](frontend/README.md)
- [Gateway](gateway/README.md)
- [Backend](backend/README.md)
## Use cases
- [Register sensor](docs/register-sensor/README.md)
- [Search sensor](docs/search-sensor/README.md)
- [Update postcode](docs/update-postcode/README.md)
- [GraphQL playground](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/graphql/gateway/README.md#graphql-playground)
## Quick view
### Backend
* [ ] Define `.proto`
* [ ] Generate grpc code
* [ ] Create domain model
* [ ] Create mongodb repository
* [ ] Build application service
* [ ] Implement grpc server
  * Accept request
  * Call application service
  * Build response
  * Return response
  * Handle errors
### Gateway
* [ ] Setup graphql server
* [ ] Define schema
* [ ] Generate ts types from proto
* [ ] Setup grpc client
* [ ] Implement resolver
  * Receive args
  * Map args from camelCase → snake_case
  * Create grpc `request` using generated ts type
  * Call grpc
  * Handle response using generated ts type
  * Map `response` from snake_case → camelCase
  * Handle errors
### Frontend
* [ ] Setup apollo client
* [ ] Generate TypeScript types from GraphQL schema
  * [ ] Use GraphQL Code Generator (or equivalent)
  * [ ] Input
    * [ ] GraphQL schema
    * [ ] Queries and mutations
  * [ ] Output
    * [ ] Query result types
    * [ ] Mutation result types
    * [ ] Variable types
* [ ] Build UI (search / register / update)
* [ ] Add validation
* [ ] Create GraphQL query or mutation service
  * [ ] Use generated TypeScript types in `apollo.query()` / `apollo.mutate()`
* [ ] Call query or mutation from component
<img src="images/checklist.jpg">

<details>
  <summary>mermaid</summary>

```mermaid

flowchart TD
  subgraph "Frontend"
    F1["Setup Apollo client"]
    F2["Build search UI"]
    F3["Add validation"]
    F4["Create GraphQL query/mutation service"]
    F5["Call query/mutation from component"]

    F1 --> F2 --> F3 --> F4 --> F5
  end

  subgraph "Gateway"
    G1["Setup GraphQL server"]
    G2["Define schema"]
    G3["Generate TS types from proto"]
    G4["Setup gRPC client"]

    subgraph "Implement resolver"
      R1["Receive args"]
      R2["Map camelCase to snake_case"]
      R3["Create gRPC request using generated TS type"]
      R4["Call gRPC"]
      R5["Handle response using generated TS type"]
      R6["Map snake_case to camelCase"]
      R7["Handle not found"]
      R8["Handle errors"]

      R1 --> R2 --> R3 --> R4 --> R5 --> R6 --> R7 --> R8
    end

    G1 --> G2 --> G3 --> G4 --> R1
  end

  subgraph "Backend"
    B1["Define .proto"]
    B2["Generate gRPC code"]
    B3["Create domain model"]
    B4["Create MongoDB repository"]
    B5["Build application service"]

    subgraph "Implement gRPC server"
      S1["Accept request"]
      S2["Call application service"]
      S3["Build response"]
      S4["Return response"]
      S5["Handle errors"]

      S1 --> S2 --> S3 --> S4 --> S5
    end

    B1 --> B2 --> B3 --> B4 --> B5 --> S1
  end

  F5 --> G1
  R4 --> B1
```
</details>