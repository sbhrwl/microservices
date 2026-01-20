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
  * Handle not found
  * Handle errors
### Frontend
* [ ] Setup apollo client
* [ ] Build search ui
* [ ] Add validation
* [ ] Create graphql query service
* [ ] Call query from component
