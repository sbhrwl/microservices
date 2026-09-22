# Messages view
- [Overview](#overview)
  - [What you see API fields](#what-you-see-api-fields)
- [Layer 1 — Frontend](#layer-1--frontend)
  - [Route & trigger](#route--trigger)
  - [GraphQL query](#graphql-query)
  - [Render](#render)
  - [Key files](#key-files)
- [Layer 2 — API Gateway](#layer-2--api-gateway)
- [Layer 3 — gfc-core](#layer-3--gfc-core)
  - [SQL query](#sql-query)
    - [Explanation](#explanation)
    - [Two modes (StringTemplate branches)](#two-modes-stringtemplate-branches)
- [Global messages (no meteringPointId)](#global-messages-no-meteringpointid)
- [Side panel (with meteringPointId)](#side-panel-with-meteringpointid)
- [Why the LEFT JOIN command + coalesce?](#why-the-left-join-command--coalesce)
- [Filters (how they bind)](#filters-how-they-bind)
- [Example — DH-1221-2 on one MP](#example--dh-1221-2-on-one-mp)
- [Mental model](#mental-model)
- [Caveat on COUNT_BY_CRITERIA](#caveat-on-count_by_criteria)
## Overview
- Messages view is a read-only view of `saga_instance` rows
```text
saga_instance (DB)
SagaInstanceMarketMessageMapper
MarketMessage (API shape)
GraphQL marketMessages
Messages table in UI
```
```text
1. User opens /messages (with date filter)
2. SetMessagesList() → MessagesFilterEffects
3. Apollo query marketMessages
4. api-gateway → gRPC queryMarketMessages
5. gfc-core searches saga_instance
6. Each SagaInstance → MarketMessage DTO
7. GraphQL → NgRx → table renders 38 rows
```
### What you see API fields
| UI column | GraphQL field | From saga |
| --- | --- | --- |
| Icons (left) | `state` + `type` | Saga state + DH code |
| Metering point | `meteringPointId` | From saga context / accounting point |
| Message type label | `type` mapped in UI | e.g. DH-1211-2 "Exiting market" |
| DH chip | `type` | `SagaType.getCode()` e.g. DH-1221-2 |
| Start time | `initiatedAt` | Saga `initiatedAt` |
| Duration | `completedAt` − `initiatedAt` | Computed in frontend |
- Duration 0m 0s = saga still running (`completedAt` null) or instant sync saga.
- Duration 0m 3s = saga took 3 seconds (e.g. async HES step for DH-1211-1).
- Status icons: INITIATED (blue play), SUCCEEDED (green), FAILED (red) — from `saga_instance.state`
## Layer 1 — Frontend
### Route & trigger
```text
/messages → MessagesListComponent
→ dispatch SetMessagesList()
→ MessagesFilterEffects.ApplyFilter$
→ MessagesFilterService.applyMessagesFilter(...)
```
- Date range (1.8.2026 - 30.9.2026) goes into `filter.dateTimeInterval`.
### GraphQL query
```graphql
query MarketMessages($input: MarketMessagesInput!) {
  marketMessages(input: $input) {
    items { type, state, stepNumber, meteringPointId, initiatedAt, completedAt }
    meta { totalCount }
  }
}
```
- Filters: message type, state, direction, date range, pagination.
### Render
- `MessagesTableComponent` maps each item → icons + labels + duration via `getDuration()`.
### Key files
| Role | Path |
| --- | --- |
| Page | `frontend/src/fhc/messages/components/messages-list/` |
| Table | `frontend/src/shared/components/messages-table/` |
| Effect | `frontend/src/fhc/messages/store/effects/messages-filter.effect.ts` |
| Apollo service | `frontend/src/fhc/messages/services/messages-filter.service.ts` |
| Query | `frontend/src/app/graphql/queries/messages.queries.ts` |
| DH label map | `frontend/src/shared/models/messages.model.ts` |
## Layer 2 — API Gateway
```javascript
// api-gateway/.../market-messages/market-messages.ts
const response = await client.queryMarketMessages(request, context.authHeader);
```
- GraphQL `marketMessages` → gRPC `queryMarketMessages` on gfc-core.
## Layer 3 — gfc-core
```java
// MarketMessageQueryService
sagaRepository.findByCriteria(filter, ...)
SagaInstanceMarketMessageMapper.toMarketMessagePage(...)
```
- Mapper (`SagaInstanceMarketMessageMapper.java`):
```java
marketMessage.setType(saga.getType().getCode()); // "DH-1211-2"
marketMessage.setState(saga.getState()); // SUCCEEDED / FAILED / INITIATED
marketMessage.setStepNumber(saga.getCurrentStep());
marketMessage.setInitiatedAt(saga.getInitiatedAt());
marketMessage.setCompletedAt(saga.getCompletedAt());
marketMessage.setMeteringPointId(...from context...);
```
- Queries `saga_instance` table — not a separate messages table.
### SQL query
```java
  public static final String FIND_BY_CRITERIA =
      """
            SELECT
                si.id,
                si.instance_id,
                si.type,
                si.accounting_point,
                <if(meteringPointId)>
                coalesce(c.step_number, current_step) AS current_step,
                coalesce(c.current_state, si.current_state) AS current_state,
                coalesce(c.initiated_at, si.created_at) AS initiated_at,
                coalesce(c.completed_at, si.completed_at) AS completed_at,
                <else>
                si.current_state,
                current_step,
                si.initiated_at,
                si.completed_at,
                <endif>
                si.created_at,
                si.context
            FROM saga_instance si
            <if(meteringPointId)>
            LEFT JOIN command c ON si.id = c.saga_id
            <endif>
            WHERE TRUE
            <if(meteringPointId)>
              AND si.accounting_point = :meteringPointId
            <endif>
            <if(state)>
              AND si.current_state IN (<state>)
            <endif>
            <if(type)>
              AND si.type IN (<type>)
            <endif>
            <if(timestamp)>
              AND si.initiated_at BETWEEN :timestampFrom AND :timestampTo
            <endif>
            ORDER BY
            <if(sorted)>
            <sortCriteria:{ sort | si.<sort.field> <sort.order> }; separator=", ">
            <if(meteringPointId)>, current_step DESC<endif>
            <endif>
            LIMIT :limit
            OFFSET :offset
            """;
```
## Explanation
- Find saga runs matching filters, paginated — and when scoped to one metering point, explode multi-command sagas into per-command rows
- `SagaRepositoryAdapter.findByCriteria()` → `MarketMessageQueryService` → Messages UI
## Two modes (StringTemplate branches)
- JDBI StringTemplate turns on `<if(meteringPointId)>` when the filter includes `meteringPointId` (`meteringPointEq` from GraphQL / side panel).
## Global messages (no meteringPointId)
- One row per saga.
```sql
SELECT si.id, si.instance_id, si.type, si.accounting_point,
si.current_state, si.current_step,
si.initiated_at, si.completed_at,
si.created_at, si.context
FROM saga_instance si
WHERE TRUE
AND si.current_state IN (...) -- optional
AND si.type IN (...) -- optional (DH codes)
AND si.initiated_at BETWEEN ... -- optional date range
ORDER BY ...
LIMIT / OFFSET
```
## Side panel (with meteringPointId)
- One row per saga × command (multi-step sagas produce multiple rows).
```sql
SELECT ...
coalesce(c.step_number, si.current_step) AS current_step,
coalesce(c.current_state, si.current_state) AS current_state,
coalesce(c.initiated_at, si.created_at) AS initiated_at,
coalesce(c.completed_at, si.completed_at) AS completed_at,
...
FROM saga_instance si
LEFT JOIN command c ON si.id = c.saga_id
WHERE si.accounting_point = :meteringPointId
...
ORDER BY ..., current_step DESC
```
## Why the LEFT JOIN command + coalesce?
| Saga | Commands | Rows returned |
| --- | --- | --- |
| DH-1111-1 (1 sync step) | 0 commands saved | 1 row (saga fields only; `c.*` is null → falls back to `si.*` via `coalesce`) |
| DH-1221-2 (2 async steps) | 2 commands (create TOU + rollout) | 2 rows — one per command |
| DH-1211-1 (1 command at start) | 1 command | 1 row with command timings |
- `coalesce(c.X, si.X)` means:
  - If a command row exists → use command step, state, initiated/completed times
  - If not → use saga-level values (sync-only sagas like DH-1111-1 / DH-129-1)
- That’s why duration in the UI can differ per step: `initiatedAt` / `completedAt` come from the command when present.
- `ORDER BY ... current_step DESC` — within one saga, step 2 appears before step 1 (newest step first).
## Filters (how they bind)
- From `SearchFilterBindingVisitor` — filter property name = template flag name:
| Filter (from GraphQL) | SQL clause |
| --- | --- |
| `meteringPointEq` | `si.accounting_point = :meteringPointId` + JOIN branch |
| `messageStateIn` | `state` → `si.current_state IN (...)` |
| `messageTypeIn` | `type` → `si.type IN (...)` (DH codes like DH-1211-2) |
| `dateTimeInterval` | `timestamp` → `si.initiated_at BETWEEN :timestampFrom AND :timestampTo` |
## Example — DH-1221-2 on one MP
- Saga id=100, 2 commands:
  - `si`: type=DH-1221-2, accounting_point=641180..., current_step=2, state=SUCCEEDED
  - c step 1: Create TOU — initiated 14:20:00, completed 14:20:02
  - c step 2: Rollout TOU — initiated 14:20:03, completed 14:20:05
- Query returns 2 rows (same `si.id`, different `c` data):
| current_step | initiated_at | completed_at | duration in UI |
| --- | --- | --- | --- |
| 2 | 14:20:03 | 14:20:05 | ~2s |
| 1 | 14:20:00 | 14:20:02 | ~2s |
- Global messages (no MP filter) would return 1 row for that saga (no JOIN).
## Mental model
- `saga_instance` = the "message" / workflow run
- `command` = individual HES steps inside that run
- Global list → 1 row per saga
- Side panel list → 1 row per command (when commands exist)
- state filter always uses `si.current_state`, not `coalesce(c.current_state)` — even in the JOIN branch.
## Caveat on COUNT_BY_CRITERIA
- The count query uses the same JOIN when `meteringPointId` is set, so for multi-command sagas total count can include extra rows (pagination count may count command rows, not saga rows).
- `COUNT_BY_CRITERIA`
```java
  public static final String COUNT_BY_CRITERIA =
      """
            SELECT COUNT(*)
            FROM saga_instance si
            <if(meteringPointId)>
            LEFT JOIN command c ON si.id = c.saga_id
            <endif>
            WHERE TRUE
            <if(meteringPointId)>
              AND si.accounting_point = :meteringPointId
            <endif>
            <if(state)>
              AND si.current_state IN (<state>)
            <endif>
            <if(type)>
              AND si.type IN (<type>)
            <endif>
            <if(timestamp)>
              AND si.initiated_at BETWEEN :timestampFrom AND :timestampTo
            <endif>
            """;
```
