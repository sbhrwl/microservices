# Side panel- Meta data
- [Overview](#overview)
- [What triggers it](#what-triggers-it)
- [UI component](#ui-component)
- [Fields mapping](#fields-mapping)
## Overview
- When you click a metering point row, two things load in parallel in the right drawer:
  - overview cards (top) and
  - messages for that MP (bottom).
## What triggers it
```text
Click row in metering-points-table
openSidePanel(flexibilityId)
dispatch SelectMeteringPoint
navigate to /metering-points/{id}/overview
mat-drawer opens (router-outlet)
MeteringPointOverviewComponent loads
```
## UI component
- `MeteringPointOverviewComponent` — six key-figure boxes + messages below.
## Fields mapping
| Card in UI | GraphQL / model field | Source in DB |
| --- | --- | --- |
| 78034187 + meter model | `meterNumber`, `meterModel` | `accounting_point` |
| Relay 2 / Controlled relay | `relay` (derived) | controllability / relay config |
| 6401060100008 Load control provider | `customerConsent.authorizedParty` | `customer_consent` |
| Device is capable + 3.9.2026 | relay `NOT_CONTROLLABLE` + `updatedAt` | controllability update time |
| Active + 15.8.2026 | `customerConsent.validFrom`/`validTo` | consent period (computed in `consent.helper.ts`) |
| In market + date range | `market` (from `marketParticipationStatus`) | market eligibility state |
- Left list stays on `meteringPoints` query (all MPs).
- Right panel runs two separate reads.
- Consent label (Active / Expired / Not started) is frontend-only logic in `consent.helper.ts`
- compares today vs `validFrom` / `validTo`.
