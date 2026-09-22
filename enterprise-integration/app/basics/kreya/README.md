# Kreya
- [Overview](#overview)
- [Kreya setup](#kreya-setup)
- [Import protobuf definitions](#import-protobuf-definitions)
- [gfc-core (localhost:50051)](#gfc-core-localhost50051)
- [iec61968-connector (localhost:50052)](#iec61968-connector-localhost50052)
- [flex-hub-connector (localhost:50053)](#flex-hub-connector-localhost50053)
- [Troubleshooting](#troubleshooting)
## Overview
| Service | Host (local) | Proto package | Auth |
| --- | --- | --- | --- |
| `gfc-core` | `localhost:50051` | `core.api.*` | `Authorization: Bearer <JWT>` or `Tenant-Id: tenant-6411802010007` |
| `iec61968-connector` | `localhost:50052` | `iec61968_connector.api.v1` | `Tenant-Id` required for `DeviceInteraction`; not required for `Iec4HesConnectivity` |
| `flex-hub-connector` | `localhost:50053` | `flexhub_connector.api.v1` | `Tenant-Id` required on all RPCs |
## Kreya setup
- Download Kreya from `kreya.app`.
- File → New project (e.g. `gfc-grpc`).
- Add three Environments (Project → Environments):
| Name | API URL | Metadata (all operations) |
| --- | --- | --- |
| `gfc-core-local` | `localhost:50051` | `Tenant-Id: tenant-6411802010007` |
| `iec61968-local` | `localhost:50052` | `Tenant-Id: tenant-6411802010007` |
| `flexhub-local` | `localhost:50053` | `Tenant-Id: tenant-6411802010007` |
## Import protobuf definitions
- Project → Importers → Add importer → gRPC proto directories
| Setting | Value |
| --- | --- |
| Proto directories | `gfc-apis/proto` |
| Import paths | `gfc-apis/proto` |
- Kreya resolves `google/protobuf/*` imports automatically.
- Our protos use relative imports such as `core/type/search.proto`, so the import path must be the proto root directory.
- Click Run importers (refresh icon) after proto changes.
- Enable Create missing operations so Kreya generates an operation per RPC.
- Default ports come from each module's `envfile.env`.
- Start services locally (IntelliJ run configs or Gradle) before invoking from Kreya.
- Tip for local dev:
  - `gfc-core` accepts a `Tenant-Id` metadata header and skips JWT validation when it is present.
  - Connectors always require `Tenant-Id`.
  - Use `tenant-6411802010007`
## gfc-core (localhost:50051)
| Service | RPC | Type | Notes |
| --- | --- | --- | --- |
| `RevisionService` | `GetRevisionInfo` | unary | Health-check / build info; empty request |
| `FlexibilityService` | `GetFlexibility` | unary | Get one flexibility by id |
| `FlexibilityService` | `QueryFlexibilities` | unary | Paginated list with filters |
| `FlexibilityService` | `UploadFlexibilities` | unary | CSV upload (bytes = base64) |
| `FlexibilityService` | `ConfirmUploadFlexibilities` | unary | Confirm staged CSV import |
| `MeteringPointService` | `GetMeteringPoint` | unary | Get metering point by GSRN |
| `MeteringPointService` | `QueryMeteringPoints` | unary | Paginated search |
| `MeteringPointService` | `UpdateControllability` | unary | Set BS01–BS04 controllability |
| `MeteringPointService` | `ImportMeteringPointCsv` | client streaming | AIM pipeline; send multiple `CsvChunk` messages |
| `MeteringPointService` | `NotifyAuthorisation` | unary | DH-1111 / DH-1211 market messages |
| `ControlCommandService` | `GetCommand` | unary | Get command by id |
| `ControlCommandService` | `QueryCommands` | unary | Paginated command history |
| `ControlCommandService` | `SendCommand` | unary | Single or week-ahead calendar control |
| `ControlCommandService` | `NotifyCommandExecution` | unary | HES execution callback (internal) |
| `EventService` | `QueryEvents` | unary | Market / saga event log |
## iec61968-connector (localhost:50052)
| Service | RPC | Type | Tenant-Id |
| --- | --- | --- | --- |
| `DeviceInteraction` | `SendCommand` | unary | required |
| `DeviceInteraction` | `BatchSendCommand` | unary | required |
| `Iec4HesConnectivity` | `ListConnectivity` | unary | not required |
## flex-hub-connector (localhost:50053)
| Service | RPC | Type | Notes |
| --- | --- | --- | --- |
| `FlexibilityHub` | `PeekMessages` | unary | Poll Data Hub queue (empty request) |
| `FlexibilityHub` | `SendLoadControlConfirmation` | unary | Report command outcome to Data Hub |
| `FlexibilityHub` | `SendAccountingPointControllabilityInfo` | unary | Report BS01–BS04 to Data Hub |
## Troubleshooting
| Symptom | Likely cause | Fix |
| --- | --- | --- |
| `UNAVAILABLE` / connection refused | Service not running | Start gfc-core / connector; check port in `envfile.env` |
| Authorization header not found | Missing auth on gfc-core | Add `Tenant-Id` or `Authorization: Bearer …` in environment metadata |
| Tenant-Id header not found | Missing header on connector | Add `Tenant-Id: tenant-6411802010007` to environment |
| `NOT_FOUND` on Get/query | No seed data | Import metering points or run business-flow tests to understand required setup |
| Importer cannot resolve imports | Wrong import path | Set import path to `gfc-apis/proto` |
| Invalid enum / field name | JSON naming | Use snake_case field names matching the `.proto` definitions |
