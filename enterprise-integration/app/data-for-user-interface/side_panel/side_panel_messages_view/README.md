# Side panel- Messages view
- [Overview](#overview)
- [Difference from global Messages screen](#difference-from-global-messages-screen)
- [Data fetch chain](#data-fetch-chain)
## Overview
- Embedded inside overview:
```html
<landisgyr-side-panel-messages-list />
```
## Difference from global Messages screen
| | Global `/messages` | Side panel messages |
| --- | --- | --- |
| GraphQL query | Same `marketMessages` | Same `marketMessages` |
| Filter | All MPs, date range | `meteringPointEq: selectedId` |
| Effect | `MessagesFilterEffects` | `SidePanelMessagesEffects` |
| Service | `MessagesFilterService` | `SidePanelMessagesFilterService` |
## Data fetch chain
```text
SidePanelMessagesList mounts
  → SetSidePanelMessagesList()
  → SidePanelMessagesEffects
  → applyMessagesFilter(..., expandedMeteringPointId, ...)
  → GraphQL marketMessages with filter.meteringPointEq = "641180201669796567"
  → api-gateway → gRPC queryMarketMessages
  → gfc-core saga_repository.findByCriteria (filtered by MP)
  → SagaInstance → MarketMessage
```
