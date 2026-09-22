# Protos
- [Proto file](#proto-file)
- [Generated classes](#generated-classes)
- [ControlCommandPb.java](#controlcommandpbjava)
- [enums become Java enums](#enums-become-java-enums)
- [ControlCommandServiceGrpc.java](#controlcommandservicegrpcjava)
- [Server side](#server-side)
- [Client-side stub](#client-side-stub)
- [Mapping this directly to your existing code](#mapping-this-directly-to-your-existing-code)
- [Complete relationship](#complete-relationship)
## Proto file
- `control_command_service.proto`
```protobuf
syntax = "proto3";
package core.api.control_commands.v1;
import "google/protobuf/timestamp.proto";
import "google/protobuf/field_mask.proto";
import "core/type/search.proto";
import "google/protobuf/empty.proto";
option java_package = "com.landisgyr.gfc.api.control_commands.v1";
option java_outer_classname = "ControlCommandPb";
option go_package = "core/api/control_commands/v1;control_commands";
service ControlCommandService {
  rpc GetCommand(GetCommandRequest) returns (GetCommandResponse) {}
  rpc QueryCommands(QueryCommandsRequest) returns (QueryCommandsResponse) {}
  rpc SendCommand(SendCommandRequest) returns (SendCommandResponse) {}
  rpc NotifyCommandExecution(NotifyCommandExecutionResult) returns (google.protobuf.Empty) {}
}
// =====================================================
// GetCommand
// =====================================================
message GetCommandRequest {
  string id = 1; // command instance id
  google.protobuf.FieldMask field_projections = 2;
}
message GetCommandResponse {
  Command command = 1;
}
// =====================================================
// QueryCommandsRequest
// =====================================================
message QueryCommandsRequest {
  CommandQueryFilter filter = 2;
  Order order = 3;
  core.type.Pagination pagination = 4;
  google.protobuf.FieldMask field_projections = 5;
}
message QueryCommandsResponse {
  Commands commands = 1;
}
message CommandQueryFilter {
  string command_target_in = 1;
}
message Commands {
  core.type.Meta meta = 1;
  repeated Command items = 2;
}
message Command {
  string id = 1;
  CommandTarget command_target = 2;
  SingleControlCmdParameters single_control_parameters = 10;
  WeekAheadControlCmdParameters week_ahead_control_parameters = 15;
  CommandExecution command_execution = 3;
}
// =====================================================
// SendCommandRequest
// =====================================================
message SendCommandRequest {
  // Optional command instance ID used for tracking.
  string id = 1;
  CommandTarget command_target = 2;
  SingleControlCmdParameters single_control_parameters = 10;
  WeekAheadControlCmdParameters week_ahead_control_parameters = 15;
  // Optional. The moment the actual request was created by external API. Typically when Datahub LCS created.
  google.protobuf.Timestamp requested_at = 4;
  // Optional. The moment GFC actually started processing the command. Correspond when the command was peeked from Datahub, or User from UI send command.
  google.protobuf.Timestamp initiated_at = 5;
  // If set, command will be executed at scheduled time.
  // If value is absent, command is executed immediately.
  google.protobuf.Timestamp scheduled_at = 6;
  /** Optional. Identification of the party that made the control request. Typically LCS identification */
  string requested_by = 7;
  /** EnergyBusinessProcess: Uses Datahub event identifications (e.g. DH-111-1, DH-1223-2) */
  string market_process_id = 8;
}
message SingleControlCmdParameters {
  RelayControlType command_type = 1;
  DesiredTarget state = 2;
  int32 expiration_time_minutes = 3;
  // Required when command type is OPEN_COMMAND & PULSE_COMMAND
  int32 command_duration_minutes = 4;
}
message WeekAheadControlCmdParameters{
  repeated DayControl day_controls = 1;
}
message DayControl {
  DayOfWeek day = 1;
  repeated ControlAction control_actions = 2;
}
message ControlAction {
  string start_time = 1;
  string end_time = 2;
  DesiredTarget desired_relay_state = 3;
}
enum DayOfWeek {
  DAY_OF_WEEK_UNSPECIFIED = 0;
  MONDAY = 1;
  TUESDAY = 2;
  WEDNESDAY = 3;
  THURSDAY = 4;
  FRIDAY = 5;
  SATURDAY = 6;
  SUNDAY = 7;
}
message SendCommandResponse {
  string id = 1;
}
message CommandTarget {
  CommandTargetType command_target_type = 1;
  // Mandatory for CommandTargetType.SINGLE_FLEXIBILITY
  string flexibility = 2;
  // Mandatory for CommandTargetType.GROUP
  string group = 3;
  // Mandatory for CommandTargetType.MULTIPLE_FLEXIBILITY
  repeated string flexibilities = 4;
  // Mandatory for CommandTargetType.METERING_POINT_RELAY
  string metering_point_id = 5;
  // Mandatory for CommandTargetType.METERING_POINT_RELAY
  string relay_id = 6;
}
// =====================================================
// NotifyCommandExecution
// =====================================================
message NotifyCommandExecutionResult {
  string command_id = 1;
  google.protobuf.Timestamp timestamp = 3;
  CommandState state = 4;
  string failure_reason = 5;
  repeated DeviceCommandExecution device_executions = 6;
}
message DeviceCommandExecution {
  string serial_number = 2;
  DeviceCommandExecutionState state = 4;
  string failure_reason = 5;
}
message CommandExecution {
  CommandState command_state = 1;
  google.protobuf.Timestamp changed_at = 2;
  // The moment the actual request was created by external API. Typically when Datahub LCS created.
  google.protobuf.Timestamp requested_at = 3;
  // The moment GFC actually started processing the command. Correspond when the command was peeked from Datahub, or User from UI send command.
  google.protobuf.Timestamp initiated_at = 4;
  // The moment HES acknowledged the command.
  google.protobuf.Timestamp accepted_at = 5;
  // The moment HES rejected the command.
  google.protobuf.Timestamp rejected_at = 6;
  // The moment HES signaled successful command execution
  google.protobuf.Timestamp succeeded_at = 7;
  // The moment HES signaled failure command execution
  google.protobuf.Timestamp failed_at = 8;
  google.protobuf.Timestamp timed_out_at = 9;
  string failure_reason = 10;
}
enum CommandTargetType {
  COMMAND_TARGET_TYPE_UNSPECIFIED = 0;
  SINGLE_FLEXIBILITY = 1 ;
  MULTIPLE_FLEXIBILITY = 2;
  GROUP = 3;
  METERING_POINT_RELAY = 4;
}
enum CommandState {
  COMMAND_STATE_UNSPECIFIED = 0;
  INITIATED = 1 ;
  ACCEPTED = 2;
  REJECTED = 3;
  SUCCEEDED = 4;
  PARTIAL_SUCCEEDED = 5;
  FAILED = 6;
  TIMED_OUT = 7;
}
enum DeviceCommandExecutionState {
  DEVICE_EXECUTION_STATE_UNSPECIFIED = 0;
  DEVICE_SUCCEEDED = 4;
  DEVICE_FAILED = 6;
  DEVICE_TIMED_OUT = 7;
}
enum RelayControlType {
  COMMAND_TYPE_UNSPECIFIED = 0;
  DIRECT = 1;
  OPEN = 2;
  PULSE = 3;
}
enum DesiredTarget {
  DESIRED_TARGET_UNSPECIFIED = 0;
  ON = 1;
  OFF = 2;
}
// Enum for Sorting Order
enum Order {
  ORDER_UNSPECIFIED = 0;
  CHANGED_AT_ASC = 1;
  CHANGED_AT_DESC = 2;
}
```
## Generated classes
- A large outer class containing your messages/enums, because of:
  - `option java_outer_classname = "ControlCommandPb";`
- gRPC service/stub classes, because of:
  - `service ControlCommandService`
- Given `java_package`:
  - `option java_package = "com.landisgyr.gfc.api.control_commands.v1";`
```text
gfc-apis
└── build
    └── generated
        └── sources
            └── proto
                └── main
                    └── java
                        └── com/landisgyr/gfc/api/control_commands/v1
                            └── ControlCommandPb.java
```
```
gfc-apis
└── build
    └── generated
        └── sources
            └── proto
                └── main
                    └── grpc
                        └── com/landisgyr/gfc/api/control_commands/v1
                            └── ControlCommandServiceGrpc.java
```
## ControlCommandPb.java
- Proto messages become nested generated Java classes inside `ControlCommandPb`.
- For example:
```protobuf
message SendCommandRequest {
  string id = 1;
  CommandTarget command_target = 2;
  ...
}
```
- becomes approximately:
  - `ControlCommandPb.SendCommandRequest`
- And you can create it using the generated builder:
```java
ControlCommandPb.SendCommandRequest request =
  ControlCommandPb.SendCommandRequest.newBuilder()
    .setId("123")
    .setCommandTarget(...)
    .build();
```
- Similarly:
| Proto message | Generated Java type |
| --- | --- |
| `SendCommandRequest` | `ControlCommandPb.SendCommandRequest` |
| `SendCommandResponse` | `ControlCommandPb.SendCommandResponse` |
| `Command` | `ControlCommandPb.Command` |
| `CommandTarget` | `ControlCommandPb.CommandTarget` |
| `SingleControlCmdParameters` | `ControlCommandPb.SingleControlCmdParameters` |
| `WeekAheadControlCmdParameters` | `ControlCommandPb.WeekAheadControlCmdParameters` |
| `DayControl` | `ControlCommandPb.DayControl` |
| `ControlAction` | `ControlCommandPb.ControlAction` |
| `NotifyCommandExecutionResult` | `ControlCommandPb.NotifyCommandExecutionResult` |
| `DeviceCommandExecution` | `ControlCommandPb.DeviceCommandExecution` |
| `CommandExecution` | `ControlCommandPb.CommandExecution` |
- The nested messages follow the same pattern.
## enums become Java enums
- For example:
```protobuf
enum DesiredTarget {
  DESIRED_TARGET_UNSPECIFIED = 0;
  ON = 1;
  OFF = 2;
}
```
- becomes something accessible through:
  - `ControlCommandPb.DesiredTarget`
- So you can write:
  - `ControlCommandPb.DesiredTarget.ON`
- Likewise:
| Proto enum | Java |
| --- | --- |
| `CommandTargetType` | `ControlCommandPb.CommandTargetType` |
| `CommandState` | `ControlCommandPb.CommandState` |
| `DeviceCommandExecutionState` | `ControlCommandPb.DeviceCommandExecutionState` |
| `RelayControlType` | `ControlCommandPb.RelayControlType` |
| `DesiredTarget` | `ControlCommandPb.DesiredTarget` |
| `Order` | `ControlCommandPb.Order` |
## ControlCommandServiceGrpc.java
- This is particularly important for stubs
- Your proto contains:
```protobuf
service ControlCommandService {
  rpc GetCommand(...) returns (...);
  rpc QueryCommands(...) returns (...);
  rpc SendCommand(...) returns (...);
  rpc NotifyCommandExecution(...) returns (...);
}
```
- The gRPC generator creates: `ControlCommandServiceGrpc`
- Inside it you'll get several important classes.
## Server side
- Something like: `ControlCommandServiceGrpc.ControlCommandServiceImplBase`
- Your GFC Core implementation extends this:
```java
public class ControlCommandServiceImpl
  extends ControlCommandServiceGrpc.ControlCommandServiceImplBase {
```
- Then you implement:
```java
@Override
public void sendCommand(
  ControlCommandPb.SendCommandRequest request,
  StreamObserver<ControlCommandPb.SendCommandResponse> responseObserver) {
  ...
}
```
- That's our server implementation.
## Client-side stub
- The generated code gives you:
  - `ControlCommandServiceGrpc.ControlCommandServiceBlockingStub`
- You create it with:
```java
ControlCommandServiceGrpc.ControlCommandServiceBlockingStub stub =
  ControlCommandServiceGrpc.newBlockingStub(channel);
```
- Then:
  - `stub.sendCommand(request);`
- calls the remote:
  - `rpc SendCommand(...)`
- So the chain is:
```text
.proto
   │
   │ protoc + gRPC plugin
   ▼
ControlCommandServiceGrpc.java
   │
   ├── ImplBase
   │      ↑
   │      │
   │   GFC Core server
   │
   └── BlockingStub
          ↑
          │
       Flex-Hub Connector
```
## Mapping this directly to your existing code
- You already have:
```java
ControlCommandPb.SendCommandRequest request =
  ProtoMapper.INSTANCE.toProto(sendSingleControlCommand);
```
- That is `ControlCommandPb.SendCommandRequest` generated from this
```protobuf
message SendCommandRequest {
  ...
}
```
- Then:
  - `controlCommandGrpcClient.sendRelayControlCmd(tenantId, request);`
- eventually does:
  - `tenantAwareStub.sendCommand(request);`
- where:
  - `tenantAwareStub`
- is:
  - `ControlCommandServiceGrpc.ControlCommandServiceBlockingStub`
- And `sendCommand()` itself exists because your proto says:
```protobuf
rpc SendCommand(SendCommandRequest)
  returns (SendCommandResponse);
```
- So your proto is effectively defining the contract that generates the Java API you are using.
## Complete relationship
```text
                    control_commands.proto
                           │
                           │ generate proto
                           ▼
              ┌─────────────────────────────┐
              │      Generated Java         │
              │                             │
              │  ControlCommandPb           │
              │    ├─ SendCommandRequest    │
              │    ├─ SendCommandResponse   │
              │    ├─ Command                │
              │    ├─ CommandTarget         │
              │    ├─ ...                   │
              │    └─ enums                 │
              │                             │
              │  ControlCommandServiceGrpc  │
              │    ├─ ImplBase              │
              │    ├─ BlockingStub          │
              │    ├─ FutureStub            │
              │    └─ AsyncStub             │
              └──────────────┬──────────────┘
                             │
               ┌─────────────┴──────────────┐
               │                            │
               ▼                            ▼
          Flex-Hub Connector             GFC Core
          ─────────────────              ────────
          
          BlockingStub                  ImplBase
               │                            │
               │ sendCommand()              │ sendCommand()
               │                            │
               └────────── gRPC ───────────►│  
```
