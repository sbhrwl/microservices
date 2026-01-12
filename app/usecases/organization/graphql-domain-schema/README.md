# Graphql domain schema
- [Types](#types)
- [Inputs](#inputs)
- [Enums](#enums)
- [Scalars](#scalars)
- [Proto mapping](#proto-mapping)
## Types

| Type              | Field                   | Type                     | Description                       |
| ----------------- | ----------------------- | ------------------------ | --------------------------------- |
| **Organization**  | `orgCode`               | String                   | Unique identifier                 |
|                   | `name`                  | String                   | Organization name                 |
|                   | `parent`                | String (optional)        | Optional parent organization      |
|                   | `settings`              | Settings                 | Device and warranty configuration |
| **Settings**      | `deviceIdentifier`      | DeviceIdentifierType     | Type of device identifier         |
|                   | `warrantyRules`         | [WarrantyRule]           | List of warranty rules            |
| **WarrantyRule**  | `eventTrigger`          | WarrantyEventTriggerType | Event activating the warranty     |
|                   | `activationDate`        | Date                     | Rule activation date              |
|                   | `warrantyDurationYears` | Int                      | Duration of warranty if triggered |
| **Organizations** | `items`                 | [Organization]           | Wrapper for list of organizations |

## Inputs

| Input                               | Field                   | Type                     | Description                       |
| ----------------------------------- | ----------------------- | ------------------------ | --------------------------------- |
| **OrganizationInput**               | `orgCode`               | String                   | Unique identifier                 |
| **SettingsInput**                   | `deviceIdentifier`      | DeviceIdentifierType     | Type of device identifier         |
|                                     | `warrantyRules`         | [WarrantyRuleInput]      | List of warranty rules            |
| **WarrantyRuleInput**               | `eventTrigger`          | WarrantyEventTriggerType | Event activating the warranty     |
|                                     | `activationDate`        | Date                     | Rule activation date              |
|                                     | `warrantyDurationYears` | Int                      | Duration of warranty if triggered |
| **UpdateOrganizationSettingsInput** | `orgCode`               | String                   | Organization identifier           |
|                                     | `settings`              | SettingsInput            | Updated settings                  |

## Enums

| Enum                         | Values                                                 | Description                         |
| ---------------------------- | ------------------------------------------------------ | ----------------------------------- |
| **DeviceIdentifierType**     | `SerialNumber`<br>`UtilitySerialNumber`                | Type of device identifier           |
| **WarrantyEventTriggerType** | `StateChangedToOperation`<br>`StateChangedToInventory` | Event that triggers a warranty rule |

## Scalars

| Scalar       | Maps to            | Description                  |
| ------------ | ------------------ | ---------------------------- |
| **Date**     | `google.type.Date` | Date representation in proto |
| **DateTime** | Timestamp          | For timestamps if needed     |
| **JsonMap**  | Generic JSON map   | Flexible fields              |

## Proto mapping

| GraphQL type/input | Proto mapping     | Notes                      |
| ------------------ | ----------------- | -------------------------- |
| `Organization`     | `Organization`    | Mirrors proto message      |
| `Settings`         | `Settings`        | Mirrors proto message      |
| `WarrantyRule`     | `WarrantyRule`    | Mirrors proto message      |
| Enums              | Proto enums       | Safe conversion in gateway |
| Inputs             | Used in mutations | Construct proto requests   |
