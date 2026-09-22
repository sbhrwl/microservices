# Metering points view
- [Overview](#overview)
- [What you see what the API returns](#what-you-see-what-the-api-returns)
- [Layer 1 — Frontend (Angular)](#layer-1--frontend-angular)
  - [Route](#route)
  - [Page load triggers fetch](#page-load-triggers-fetch)
  - [GraphQL query](#graphql-query)
  - [Response NgRx store table](#response-ngrx-store-table)
  - [Key files](#key-files)
- [Layer 2 — API Gateway (Node.js)](#layer-2--api-gateway-nodejs)
  - [Key files](#key-files)
- [Layer 3 — gfc-core (Java)](#layer-3--gfc-core-java)
  - [Key files](#key-files-1)
## Overview
```text
1. User navigates to /metering-points
2. MeteringPointsListComponent constructor
   dispatch SetMeteringPointsList()
3. MeteringPointsFilterEffects
   MeteringPointsFiltersService.applyFlexibilitiesFilter()
4. Apollo POST api-gateway/graphql
   query MeteringPoints { meteringPoints(input: {...}) }
5. api-gateway resolver meteringPoints
   gRPC queryMeteringPoints(authHeader, filter, pagination, order)
6. gfc-core MeteringPointServiceImpl
   AccountingPointQueryService.searchAccountingPoints()
   → PostgreSQL
7. Response bubbles back: proto → GraphQL → Apollo → NgRx → table renders 14 rows
```
## What you see what the API returns

| UI column | GraphQL field | Meaning |
| --- | --- | --- |
| Metering point | `id` | GSRN / external id |
| Device | `meterNumber` + `meterModel` | Meter on that point |
| Device capability | `controllabilityStatus` | BS01–BS04 (/ icons) |
| Consumer consent | `customerConsent.scope` + `validFrom` | Active consent? (/) |
| Market information | `marketParticipationStatus` | ACTIVE vs not in market |

- Count comes from “Metering points 14” `meta.totalCount` in the same query.

## Layer 1 — Frontend (Angular)
### Route
- `frontend/src/app/app.routes.ts`
  - `/metering-points` → `MeteringPointsListComponent`
### Page load triggers fetch
- On init, the list component dispatches: `MeteringPointsListActions.SetMeteringPointsList()`
- That hits `MeteringPointsFilterEffects.ApplyFilter$`, which calls:
  - `meteringPointsFiltersService.applyFlexibilitiesFilter(...)`
### GraphQL query
- `MeteringPointsFiltersService` runs Apollo query `GET_METERING_POINTS`:
```graphql
query MeteringPoints($input: MeteringPointsInput!) {
  meteringPoints(input: $input) {
    items { id, meterNumber, meterModel, controllabilityStatus, marketParticipationStatus,
      customerConsent { ... } }
    meta { totalCount }
  }
}
```
- Filters (capability chips, market, search text, page, sort) are packed into `input.filter`, `input.pagination`, `input.order`.
### Response NgRx store table
- `GetMeteringPointsSuccess` reducer stores items
- `MeteringPointsListComponent` reads store → passes to `MeteringPointsTableComponent`
- Table renders rows + / icons
### Key files

| Role | Path |
| --- | --- |
| Page | `frontend/src/fhc/metering-points/components/metering-points-list/` |
| Table UI | `frontend/src/shared/components/metering-points-table/` |
| Fetch effect | `frontend/src/fhc/metering-points/store/effects/meteringPoints-filter.effect.ts` |
| Apollo service | `frontend/src/fhc/metering-points/services/metering-points-filter.service.ts` |
| GraphQL query | `frontend/src/app/graphql/queries/meteringPoints.queries.ts` |
## Layer 2 — API Gateway (Node.js)
- GraphQL resolver receives `meteringPoints(input)`:
```javascript
// api-gateway/src/resolver-definitions/core/metering-points/metering-points.ts
const response = await client.queryMeteringPoints(request, context.authHeader);
```
- Maps GraphQL args → protobuf `QueryMeteringPointsRequest`
- Calls gfc-core over gRPC (with auth headers from the logged-in user)
- Maps response back to GraphQL shape (`controllabilityStatus` ← `loadControllability`)
### Key files
| Role | Path |
| --- | --- |
| GraphQL schema | `api-gateway/graphql/operations.graphql` |
| Resolver | `api-gateway/src/resolver-definitions/core/metering-points/metering-points.ts` |
| gRPC client | `api-gateway/src/clients/metering-point-client.js` |
## Layer 3 — gfc-core (Java)
- gRPC handler:
```java
// MeteringPointServiceImpl.queryMeteringPoints(...)
GenericPage<AccountingPoint> page =
  accountingPointQueryService.searchAccountingPoints(
    fieldNamesList, filter, sorting, pageNumber, pageSize);
```
- Proto filter → domain `SearchFilter`
- Queries PostgreSQL (`accounting_point` + related data)
- Maps rows → protobuf response `MeteringPoints`
- Note: UI says “metering point”; gfc-core domain object is often `AccountingPoint` — same entity, different naming layer.
### Key files
| Role | Path |
| --- | --- |
| gRPC entry | `gfc-core/.../adapters/inbound/grpc/MeteringPointServiceImpl.java` |
| Business query | `gfc-core/.../app/service/AccountingPointQueryService.java` |
| Persistence | `gfc-core/.../adapters/outbound/persistence/AccountingPointRepositoryAdapter.java` |
- `FIND_BY_CRITERIA`
```java
  public static final String FIND_BY_CRITERIA =
      """
            SELECT ap.id,
                   ap.external_id,
                   ap.meter_number,
                   ap.meter_model,
                   ap.load_controllability,
                   ap.market_eligibility_status,
                   ap.grid_area_id,
                   ap.address_street_name,
                   ap.address_street_number,
                   ap.address_apartment_number,
                   ap.address_postal_code,
                   ap.address_city,
                   ap.created_at,
                   ap.updated_at,
                   c.authorized_party,
                   c.scope,
                   c.valid_from,
                   c.valid_to,
                   io.status AS integration_status
            FROM accounting_point ap

            LEFT JOIN LATERAL (
                SELECT c.*
                FROM customer_consent c
                WHERE c.accounting_point_id_target = ap.id
                ORDER BY c.valid_from DESC, c.id DESC
                LIMIT 1
            ) c ON true

            LEFT JOIN LATERAL (
                SELECT io.status
                FROM integration_outbox io
                WHERE io.parameters->>'externalId' = ap.external_id
                   OR io.parameters->>'meteringPointId' = ap.external_id
                ORDER BY io.id DESC
                LIMIT 1
            ) io ON true
            WHERE TRUE
            <if(free_text)>
              AND (
                  ap.external_id ILIKE '%' || :free_text || '%'
                  OR ap.meter_number ILIKE '%' || :free_text || '%'
                  OR ap.meter_model ILIKE '%' || :free_text || '%'
                  OR ap.grid_area_id ILIKE '%' || :free_text || '%'
              )
            <endif>
            <if(sorted)>
            ORDER BY
            <sortCriteria:{ sort | ap.<sort.field> <sort.order> }; separator=", ">
            <endif>
            LIMIT :limit
            OFFSET :offset
            """;         
```
- `COUNT_BY_CRITERIA`
```java
  public static final String COUNT_BY_CRITERIA =
      """
            SELECT COUNT(*)
            FROM accounting_point ap
            WHERE TRUE
            <if(free_text)>
              AND (
                  ap.external_id ILIKE '%' || :free_text || '%'
                  OR ap.meter_number ILIKE '%' || :free_text || '%'
                  OR ap.meter_model ILIKE '%' || :free_text || '%'
                  OR ap.grid_area_id ILIKE '%' || :free_text || '%'
              )
            <endif>
            """;
```
