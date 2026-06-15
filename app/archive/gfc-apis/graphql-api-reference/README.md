# GraphQL API reference
## Queries
- Read operations

| Query area            | description                                         |
| --------------------- | --------------------------------------------------- |
| Device queries        | Single device lookup and bulk device retrieval      |
| Event query           | Event and log retrieval system                      |
| Authorization queries | Two permission systems: standard and app-level      |
| Organization queries  | Single organization lookup and organization listing |
| Tags query            | Tag management and retrieval                        |

## Mutations

- Write operations

| Mutation area         | description                                                    |
| --------------------- | -------------------------------------------------------------- |
| Device tagging        | Bulk association of tags with devices                          |
| Tag management        | CRUD operations for tags, including create, update, and delete |
| Organization settings | Configuration updates for organizations                        |
  

## Types and scalars
- Data types

| Scalar type   | description                                        |
| ------------- | -------------------------------------------------- |
| Flexible data | `JsonMap` for dynamic or unstructured data storage |
| Temporal      | `DateTime`, `Date` for time-based data             |
| Geospatial    | `Latitude`, `Longitude` for location coordinates   |
