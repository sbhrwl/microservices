# Organization API
## Domain shape
- `Organization`, `Settings`, `WarrantyRule`

| Concept            | GraphQL (your schema)      | gRPC / Proto (your schema) | What this means          |
| ------------------ | -------------------------- | -------------------------- | ------------------------ |
| Organization model | `type Organization`        | `message Organization`     | Same domain object       |
| Identifier         | `orgCode: String`          | `string org_code`          | Naming difference only   |
| Settings           | `type Settings`            | `message Settings`         | Direct 1:1 mapping       |
| Warranty rules     | `[WarrantyRule]`           | `repeated WarrantyRule`    | List semantics identical |
| Enums              | `WarrantyEventTriggerType` | `WarrantyEventTrigger`     | Proto adds safe default  |
| Dates              | `Date` (scalar)            | `google.type.Date`         | Proto is stricter        |

## Inputs vs requests 
- GraphQL separates:
  - What you send → `input`
  - What you get → `type`
- **Services**

| GraphQL  | Proto      |
| -------- | ---------- |
| `input`  | `*Request` |
| `type`   | `message`  |
| Mutation | RPC        |

| Operation        | GraphQL                           | gRPC / Proto                        | Practical impact                 |
| ---------------- | --------------------------------- | ----------------------------------- | -------------------------------- |
| Add organization | `AddOrganizationInput` (implied)  | `AddOrganizationRequest`            | Same payload                     |
| Update settings  | `UpdateOrganizationSettingsInput` | `UpdateOrganizationSettingsRequest` | GraphQL separates input/output   |
| Input validation | Schema-level                      | Mostly runtime                      | GraphQL catches more at boundary |
| Partial updates  | Client controls fields            | Entire message unless masked        | GraphQL safer for UIs            |

## Reads and field selection

| Use case            | GraphQL               | gRPC / Proto           |
| ------------------- | --------------------- | ---------------------- |
| Fetch org name only | Client selects `name` | Must use `FieldMask`   |
| Fetch deep settings | Natural nesting       | Explicit projection    |
| Overfetch risk      | None                  | High without masks     |
| Backend complexity  | Low                   | Higher (mask handling) |

- GraphQL:
```graphql
organization(orgCode: "A1") {
  name
  settings {
    deviceIdentifier
  }
}
```

- Proto:
```proto
field_projections: "name,settings.device_identifier"
```
## How they complement each other
- Typical flow
```
Frontend
  ↓ GraphQL
GraphQL Gateway
  ↓ gRPC
OrganizationService
```

### Why this works beautifully
* GraphQL becomes a **query lens**, not a data store.
* Your **proto** defines the *truth*
* Your **GraphQL** defines the *experience*
* Proto optimizes **speed and safety**
* GraphQL optimizes **flexibility and usability**
* Together, they form a clean contract stack

## Example of update settings
### Frontend (GraphQL)
* Sends only changed fields
* Doesn’t care about RPC boundaries
### Gateway
* Maps GraphQL input → `UpdateOrganizationSettingsRequest`
* Calls gRPC service
### OrganizationService (proto)
* Validates rules
* Applies business logic
* Returns full Organization
