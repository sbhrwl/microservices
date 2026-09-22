# Pain points
- [Goal](#goal)
- [Step 1 – Started with plain Java](#step-1--started-with-plain-java)
- [Step 2 – Added dependencies](#step-2--added-dependencies)
- [Step 3 – App became the assembler](#step-3--app-became-the-assembler)
- [Pain Point 1](#pain-point-1)
- [Pain Point 2](#pain-point-2)
- [Pain Point 3](#pain-point-3)
- [Pain Point 4](#pain-point-4)
- [Key realization](#key-realization)
- [What Dagger promises](#what-dagger-promises)
## Goal
- Understand why Dependency Injection exists before learning Dagger.
## Step 1 – Started with plain Java
- Created four simple classes:
  - `App`
  - `MeterRegistrationService`
  - `MeterRegistrationProcessor`
  - `RegistrationRepository`
- No Spring.
- No Dagger.
- No frameworks.
## Step 2 – Added dependencies
- Gradually built this dependency graph:
```text
MeterRegistrationService
        │
        ▼
MeterRegistrationProcessor
        │
        ├──────────────► Validator
        │
        ▼
RegistrationRepository
        │
        ▼
DatabaseConnection
```
- Each class declares what it needs through its constructor.
- Example:
```java
public MeterRegistrationProcessor(
  RegistrationRepository repository,
  Validator validator)
```
- The processor does not create these objects.
- It simply says:
  - "Give me these dependencies."
- This is constructor injection.
## Step 3 – App became the assembler
- `App` manually created and wired every object:
```java
DatabaseConnection connection =
  new DatabaseConnection();
  
RegistrationRepository repository =
  new RegistrationRepository(connection);
  
Validator validator =
  new Validator();
  
MeterRegistrationProcessor processor =
  new MeterRegistrationProcessor(
    repository,
    validator);
	
MeterRegistrationService service =
  new MeterRegistrationService(processor);

service.registerMeter();
```
## Pain Point 1
- Every dependency added to a class forced changes in `App`.
- Example:
- Adding `Validator` required changing:
```java
new MeterRegistrationProcessor(
  repository,
  validator);
```
## Pain Point 2
- `App` must know every concrete class.
- It knows:
  - `DatabaseConnection`
  - `RegistrationRepository`
  - `Validator`
  - `MeterRegistrationProcessor`
  - `MeterRegistrationService`
- As the application grows, this list grows too.
## Pain Point 3
- `App` must know the correct creation order.
- Example:
```text
DatabaseConnection
        ↓
RegistrationRepository
        ↓
MeterRegistrationProcessor
        ↓
MeterRegistrationService
```
- Creating them in the wrong order causes compilation errors.
## Pain Point 4
- `App` performs object wiring.
- It decides:
  - what gets created
  - when it gets created
  - who receives it
- This process is called object wiring.
## Key realization
- Nothing is wrong with using `new`.
- The problem is manual wiring.
- As the application grows, `App` becomes responsible for constructing the entire object graph.
## What Dagger promises
- Instead of writing:
```java
new DatabaseConnection();
new RegistrationRepository(...);
new Validator();
new MeterRegistrationProcessor(...);
new MeterRegistrationService(...);
```
- you describe the dependency graph, and Dagger generates this wiring code for you at compile time.
- One sentence to remember
  - Dagger is not about avoiding `new`; it is about removing the burden of manually wiring an application's object graph.
- I think this sentence captures the essence of why Dagger exists. From here on, every Dagger feature (`@Inject`, `@Module`, `@Component`) will simply automate a part of the manual wiring you've already done.
