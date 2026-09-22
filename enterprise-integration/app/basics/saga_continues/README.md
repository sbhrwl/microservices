# Saga continues
- [Introduction](#introduction)
- [Layer 1 — What sagas exist?](#layer-1--what-sagas-exist)
- [Layer 2 — What steps exist?](#layer-2--what-steps-exist)
- [Layer 3 — What steps does each saga run?](#layer-3--what-steps-does-each-saga-run)
- [Layer 4 — What does each step do?](#layer-4--what-does-each-step-do)
- [Types of steps](#types-of-steps)
- [Step implementation](#step-implementation)
- [Layer 5 — The engine (heart of it all)](#layer-5--the-engine-heart-of-it-all)
- [Job A — Start](#job-a--start)
- [Job B — Resume (when HES responds)](#job-b--resume-when-hes-responds)
- [Outcomes](#outcomes)
- [Layer 6 — Context & persistence](#layer-6--context--persistence)
- [example: DH-1223-2](#example-dh-1223-2)
## Introduction
```text
DH message arrives (XML)
        │
        ▼
┌─────────────────────────────────────┐
│  ENTRY (gRPC)                       │  ProcessOutboxUseCase or MeteringPointServiceImpl  or  ControlCommandServiceImpl
│  "Something happened in the market" │
└─────────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────────┐
│  ORCHESTRATOR                       │  SagaOrchestrationService
│  "Run the workflow"                 │  (start → run steps → wait → resume)
└─────────────────────────────────────┘
        │
        ├── reads recipe from ──►  SagaDefinitionRegistry  (SagaType → list of Steps)
        │
        ├── runs each step ──────►  app/saga/step/*  (SyncStep / AsyncDeviceStep)
        │
        ├── saves state ─────────►  SagaRepository  (DB: saga_instance + command)
        │
        └── sends to device ─────►  DeviceCommandDispatcher → HES
                                              │
                                              ▼ (later)
                                    notifyCommandExecution → back to Orchestrator
```
| Key File / Location | What to Understand | Primary Question |
| --- | --- | --- |
| `SagaType.java` | Saga inventory | What sagas exist in the system? |
| `SagaDefinitionRegistryAdapter.java` | Step wiring | How are saga steps defined, ordered, and connected? |
| `SagaOrchestrationService.java` | Runtime engine | How does the system execute and manage a saga at runtime? |
| `app/saga/step/*.java` | Step behaviour | What does each individual step actually do, including success/failure handling? |
## Layer 1 — What sagas exist?
- `SagaType.java`
- Incoming message carries a DH code resolved to a `SagaType`
| Control ID | |
| --- | --- |
| `ACCOUNTING_POINT_CONTROLLABILITY_UPDATE` | DH-129-1 |
| `LOAD_CONTROL_NOTIFICATION_PERIOD` | DH-1111-1 |
| `LOAD_CONTROL_STARTING` | DH-1211-1 |
| `LOAD_CONTROL_ENDING` | DH-1211-2 |
| `WEEK_AHEAD_CALENDAR_CONTROL` | DH-1221-2 |
| `SINGLE_CONTROL` | DH-1223-2 |
| `DAY_AHEAD_CONTROL` | DH-1222-2 |
- `SagaType`
```java
public enum SagaType {
  WEEK_AHEAD_CALENDAR_CONTROL("DH-1221-2"),
  DAY_AHEAD_CONTROL("DH-1222-2"),
  SINGLE_CONTROL("DH-1223-2"),
  LOAD_CONTROL_NOTIFICATION_PERIOD("DH-1111-1"),
  LOAD_CONTROL_STARTING("DH-1211-1"),
  LOAD_CONTROL_ENDING("DH-1211-2"),
  ACCOUNTING_POINT_CONTROLLABILITY_UPDATE("DH-129-1");
  private final String code;
  SagaType(String code) {
    this.code = code;
  }
  public String getCode() {
    return code;
  }
  public static SagaType fromCode(String code) {
    return Arrays.stream(values())
      .filter(type -> type.code.equals(code))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Unknown market message type: " + code));
  }
}
```
## Layer 2 — What steps exist?
- Steps are defined here: `gfc\core\app\saga\step`
- Step
  - `UpdateControllabilityStep`
  - `RecordLoadControlAuthPeriodStep`
  - `BackupLoadControlTouStep`
  - `LoadStoredToulForRestoreStep`
  - `RestoreLoadControlTouStep`
  - `SingleControlStep`
  - `WeekCalendarCreateTouStep`
  - `WeekCalendarRolloutTouStep`
## Layer 3 — What steps does each saga run?
- `SagaDefinitionRegistryAdapter.java`
- It as a recipe book: saga name → ordered steps
| Saga type | Steps (in order) |
| --- | --- |
| `ACCOUNTING_POINT_CONTROLLABILITY_UPDATE` | `UpdateControllabilityStep` |
| `LOAD_CONTROL_NOTIFICATION_PERIOD` | `RecordLoadControlAuthPeriodStep` |
| `LOAD_CONTROL_STARTING` | `BackupLoadControlTouStep` |
| `LOAD_CONTROL_ENDING` | `LoadStoredTouIdForRestoreStep` `RestoreLoadControlTouStep` |
| `SINGLE_CONTROL` | `SingleControlStep` |
| `WEEK_AHEAD_CALENDAR_CONTROL` | `WeekCalendarCreateTouStep` `WeekCalendarRolloutTouStep` |
| `DAY_AHEAD_CONTROL` | |
- `SagaDefinitionRegistryAdapter`
```java
public SagaDefinitionRegistryAdapter(
  BackupLoadControlTouStep backupLoadControlTOUStep,
  WeekCalendarCreateTouStep weekCalendarCreateTouStep,
  WeekCalendarRolloutTouStep weekCalendarRolloutTouStep,
  SingleControlStep singleControlStep,
  UpdateControllabilityStep updateControllabilityStep,
  LoadStoredTouIdForRestoreStep loadStoredTouIdForRestoreStep,
  RestoreLoadControlTouStep restoreLoadControlTouStep,
  RecordLoadControlAuthPeriodStep recordLoadControlAuthPeriodStep) {
  Map<SagaType, SagaDefinition> map = new EnumMap<>(SagaType.class);
  map.put(
    SagaType.ACCOUNTING_POINT_CONTROLLABILITY_UPDATE,
    new SagaDefinition(
      SagaType.ACCOUNTING_POINT_CONTROLLABILITY_UPDATE, List.of(updateControllabilityStep)));
  map.put(
    SagaType.LOAD_CONTROL_NOTIFICATION_PERIOD,
    new SagaDefinition(
      SagaType.LOAD_CONTROL_NOTIFICATION_PERIOD, List.of(recordLoadControlAuthPeriodStep)));
  map.put(
    SagaType.LOAD_CONTROL_STARTING,
    new SagaDefinition(SagaType.LOAD_CONTROL_STARTING, List.of(backupLoadControlTOUStep)));
  map.put(
    SagaType.LOAD_CONTROL_ENDING,
    new SagaDefinition(
      SagaType.LOAD_CONTROL_ENDING,
      List.of(loadStoredTouIdForRestoreStep, restoreLoadControlTouStep)));
  map.put(
    SagaType.WEEK_AHEAD_CALENDAR_CONTROL,
    new SagaDefinition(
      SagaType.WEEK_AHEAD_CALENDAR_CONTROL,
      List.of(weekCalendarCreateTouStep, weekCalendarRolloutTouStep)));
  map.put(
    SagaType.DAY_AHEAD_CONTROL,
    new SagaDefinition(
      SagaType.DAY_AHEAD_CONTROL,
      List.of(weekCalendarCreateTouStep, weekCalendarRolloutTouStep)));
  map.put(
    SagaType.SINGLE_CONTROL,
    new SagaDefinition(SagaType.SINGLE_CONTROL, List.of(singleControlStep)));
  this.definitions = Map.copyOf(map);
}
```
## Layer 4 — What does each step do?
- Steps are defined here: `gfc\core\app\saga\step`
- Steps: Sync or Async
```java
public class UpdateControllabilityStep
  implements SyncStep<AccountingPointControllabilityUpdateContext> {
  ...
}
public class RecordLoadControlAuthPeriodStep implements SyncStep<LoadControlAuthPeriodContext> {
  ...
}
public class BackupLoadControlTouStep implements AsyncDeviceStep<LoadControlStartingContext> {
  ...
}
public class SingleControlStep implements AsyncDeviceStep<SingleControlContext> {
  ...
}
public class WeekCalendarCreateTouStep implements AsyncDeviceStep<WeekAheadControlContext> {
  ...
}
public class WeekCalendarRolloutTouStep implements AsyncDeviceStep<WeekAheadControlContext> {
  ...
}
public class LoadStoredTouIdForRestoreStep implements SyncStep<LoadControlEndingContext> {
  ...
}
public class RestoreLoadControlTouStep implements AsyncDeviceStep<LoadControlEndingContext> {
  ...
}
```
## Types of steps
- `gfc\core\domain\saga\step\SyncStep.java`
- `gfc\core\domain\saga\step\AsyncDeviceStep.java`
| Comparison | SyncStep | AsyncDeviceStep |
| --- | --- | --- |
| Kind | Sync | Async |
| When work happens | Now, in-process | Starts now and finishes later when HES/device responds |
| When the step finishes | Inside the same call to `runStep` | When the HES/device response or callback is received |
| Method called | `syncStep.execute(...)` | `asyncStep.start(...)` now, then `asyncStep.onCompleted(...)` later |
| Possible return or outcome | Complete, Continue, Dispatch, or Fail | Initially Waiting, followed later by Complete or Dispatch |
| Saga behaviour | The saga can continue or finish immediately | The saga pauses while waiting for the external response |
| Analogy | “I can finish this step immediately in memory or the database.” | “I send a command to a meter/device and wait for a callback.” |
| Example | Record consent in the database or load the stored TOU ID | Send “read TOU” or “switch relay” to HES, wait for the result, then continue |
- `gfc\core\domain\saga\step\SagaStep.java`
- `gfc\core\domain\saga\step\SagaStepExecution.java`
- `gfc\core\domain\saga\step\StepOutcome.java`
## Step implementation
- Steps are defined here: `gfc\core\app\saga\step`
| Class | Type | Role |
| --- | --- | --- |
| `UpdateControllabilityStep` | SyncStep | Notify Data Hub |
| `RecordLoadControlAuthPeriodStep` | SyncStep | Save consent |
| `BackupLoadControlTouStep` | AsyncDeviceStep | Read TOU from HES |
| `LoadStoredTouIdForRestoreStep` | SyncStep | Load stored TOU, build restore command |
| `RestoreLoadControlTouStep` | AsyncDeviceStep | Restore TOU on HES |
| `SingleControlStep` | AsyncDeviceStep | Single relay control |
| `WeekCalendarCreateTouStep` | AsyncDeviceStep | Create TOU calendar |
| `WeekCalendarRolloutTouStep` | AsyncDeviceStep | Roll out calendar |
## Layer 5 — The engine (heart of it all)
- `SagaOrchestrationService.java`
- It has exactly two jobs
## Job A — Start
```text
start()
  → save saga + command to DB
  → run step 1
  → read outcome → act on it
```
## Job B — Resume (when HES responds)
```text
handleCommandExecutionResult()
  → update command in DB
  → find saga
  → call step.onCompleted()
  → read outcome → act on it
```
- The outcome switch (this is the whole state machine)
  - Complete → saga done
  - Continue → run next step now
  - Waiting → forward command to HES, pause
  - Dispatch → save new command, forward to HES, pause
  - Fail → mark saga failed
- Every saga journey is just start → (outcome loop) → Complete/Fail
## Outcomes
- After a step runs, everything goes through `applyOutcome`. There are five outcomes:
| Outcome | Meaning |
| --- | --- |
| Continue | Run the next step immediately |
| Waiting | Pause until device responds |
| Dispatch | Save a new command and send it to device |
| Complete | Saga is done |
| Fail | Saga failed |
- `applyOutcome`
```java
private void applyOutcome(SagaInstance saga, StepOutcome outcome, int completedStepNumber) {
  switch (outcome) {
    case StepOutcome.Fail(String reason, CommandState commandState, String commandInstanceId) -> {
      sagaRepository.markFailed(saga.getId(), reason, saga.getContext());
      if (commandInstanceId != null && commandState != null) {
        commandRepository.updateState(commandInstanceId, commandState, reason);
      }
    }
    case StepOutcome.Complete(SagaContext updatedContext) -> {
      saga.setContext(updatedContext);
      sagaRepository.markCompleted(saga.getId(), updatedContext);
    }
    case StepOutcome.Waiting(SagaContext updatedContext) -> {
      saga.setContext(updatedContext);
      sagaRepository.advanceStep(saga.getId(), completedStepNumber, updatedContext);
    }
    case StepOutcome.Continue(SagaContext updatedContext) -> {
      int nextStepNumber = completedStepNumber + 1;
      saga.setContext(updatedContext);
      sagaRepository.advanceStep(saga.getId(), nextStepNumber, updatedContext);
      saga.setCurrentStep(nextStepNumber);
      runStep(saga, nextStepNumber, null);
    }
    case StepOutcome.Dispatch(
      SagaContext updatedContext,
      ControlCommand command,
      int stepNumber) -> {
      saga.setContext(updatedContext);
      commandRepository.save(command, saga.getId(), stepNumber);
      sagaRepository.advanceStep(saga.getId(), stepNumber, updatedContext);
      saga.setCurrentStep(stepNumber);
      forwardDeviceCommand(saga, command);
    }
  }
}
```
| Component | Responsibility |
| --- | --- |
| `SagaType.java` | WHAT sagas exist (DH code mapping) |
| `SagaDefinitionRegistryAdapter.java` | WHICH steps, in WHAT ORDER |
| `app/saga/step/*.java` | HOW each step behaves |
| `SagaOrchestrationService.java` | RUNS the steps |
## Layer 6 — Context & persistence
| Concept | Role | Mental model |
| --- | --- | --- |
| `SagaContext` | Data carried through steps | Saga’s notepad (metering point, TOU id, etc.) |
| `SagaInstance` | Running saga in DB | Saga’s file folder (id, current step, state) |
| `ControlCommand` | Device instruction | Work order sent to HES |
| `SagaRepository` | DB access | Filing cabinet for saga state |
## example: DH-1223-2
```text
1. flex-hub gets F35 XML
2. maps to gRPC SendCommand
3. ControlCommandServiceImpl.start(command)
4. SagaOrchestrationService:
   - SagaType = SINGLE_CONTROL
   - recipe = [SingleControlStep]
   - step.start() → Waiting
   - forward to HES → PAUSE
5. HES executes relay command
6. iec61968-connector calls notifyCommandExecution
7. SagaOrchestrationService.handleCommandExecutionResult():
   - SingleControlStep.onCompleted()
   - Complete → mark saga done
```
