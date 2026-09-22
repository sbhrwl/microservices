# Side panel
- [Overview](#overview)
- [Key files](#key-files)
## Overview
```text
User clicks MP row "641180201669796567"
        │
        ├──────────────────────────────────────┐
        │                                      │
        ▼                                      ▼
GET_METERING_POINT_DETAIL              GET_MESSAGES (filtered)
  meteringPoint(id)                      marketMessages(
                                           meteringPointEq: id
                                         )
        │                                      │
        ▼                                      ▼
gRPC getMeteringPoint                  gRPC queryMarketMessages
        │                                      │
        ▼                                      ▼
accounting_point + consent             saga_instance (this MP only)
        │                                      │
        ▼                                      ▼
Overview cards (top)                   Message table (bottom)
```
## Key files
| Area | Path |
| --- | --- |
| Row click / drawer | `metering-points-list.component.ts/html` |
| Overview UI | `metering-point-overview/` |
| Consent logic | `consent.helper.ts` |
| Detail fetch effect | `meteringPoint-details.effects.ts` |
| Detail GraphQL | `GET_METERING_POINT_DETAIL` in `meteringPoints.queries.ts` |
| Side messages | `side-panel-messages-list.component.ts` |
| Side messages effect | `side-panel-messages-filter.effect.ts` |
| Side messages service | `side-panel-messages-filter.service.ts` |
| API: detail | `api-gateway/.../metering-point/metering-point.ts` |
| API: messages | `api-gateway/.../market-messages/market-messages.ts` |
| Core: detail | `MeteringPointServiceImpl.getMeteringPoint()` |
| Core: messages | `MarketMessageQueryService` / `SagaRepository` |
