# Features
- [Add dependencies](#add-dependencies)
- [@Inject](#inject)
- [@Component](#component)
- [What have we taught Dagger](#what-have-we-taught-dagger)
- [Who is still creating the objects?](#who-is-still-creating-the-objects)
- [Checkpoint](#checkpoint)
- [@Singleton, @Binds, @Provides](#singleton-binds-provides)
## Add dependencies
- Update `gradle/libs.versions.toml`
- Under `[versions]`
  - `dagger = "2.57"`
- Under `[libraries]`
  - `dagger = { module = "com.google.dagger:dagger", version.ref = "dagger" }`
  - `dagger-compiler = { module = "com.google.dagger:dagger-compiler", version.ref = "dagger" }`
- Tell the app module to use Dagger
- Modify only `app/build.gradle`
```gradle
dependencies {
  implementation libs.guava
  implementation libs.dagger
  annotationProcessor libs.dagger.compiler
}
```
- Run: `.\gradlew build`
## @Inject
- Up to now, this constructor has been saying: "I need a RegistrationRepository and a Validator." Now we'll teach Dagger to read that.
- Modify `MeterRegistrationProcessor`
- Add one import
- Then annotate the constructor:
```java
@Inject
public MeterRegistrationProcessor(
  RegistrationRepository repository,
  Validator validator) {
  this.repository = repository;
  this.validator = validator;
}
```
- When Dagger sees:
```java
@Inject
public MeterRegistrationProcessor(...)
```
- it reads it as: "If anyone asks me for a MeterRegistrationProcessor, I know how to build one. I need a RegistrationRepository and a Validator."
## @Component
- Create a new file: `AppComponent.java`
- Asking Dagger: "Dagger, build me an application that can give me a MeterRegistrationService."
- You're probably wondering: "Who implements AppComponent?"
- Answer: Dagger will
- It generates a class called: `DaggerAppComponent` during compilation.
- Build
```text
Compilation failed; see the compiler output below.
C:\Git\practice\microservices\enterprise-integration\dagger-learning\app\src\main\java\org\example\AppComponent.java:6: error: [Dagger/MissingBinding] org.example.MeterRegistrationService cannot be provided without an @Inject constructor or an @Provides-annotated method.
public interface AppComponent {
^
  org.example.MeterRegistrationService is requested at
      [org.example.AppComponent] org.example.AppComponent.meterRegistrationService()
1 error
* Try:
> Check your code and dependencies to fix the compilation error(s)
> Run with --scan to get full insights from a Build Scan (powered by Develocity).
BUILD FAILED in 3s
1 actionable task: 1 executed
```
- Dagger says: "Nice constructor... But nobody told me I am allowed to use it."
- Each `@Inject` constructor is like telling Dagger: "You may construct this class."
- Eventually, Dagger will know how to walk from:
```text
MeterRegistrationService
        ↓
MeterRegistrationProcessor
        ↓
RegistrationRepository
        ↓
DatabaseConnection
```
## What have we taught Dagger
```text
App
new DatabaseConnection()
new RegistrationRepository(...)
new Validator()
new MeterRegistrationProcessor(...)
new MeterRegistrationService(...)
```
- One constructor at a time, we added `@Inject`.
- Now Dagger knows the entire graph:
```text
MeterRegistrationService
        │
        ▼
MeterRegistrationProcessor
      ┌─┴───────────┐
      ▼             ▼
RegistrationRepository   Validator
          │
          ▼
DatabaseConnection
```
## Who is still creating the objects?
- Still `App`
- We'll replace all of this:
```java
DatabaseConnection connection = ...
RegistrationRepository repository = ...
Validator validator = ...
MeterRegistrationProcessor processor = ...
MeterRegistrationService service = ...
```
- With
```java
AppComponent component =
  DaggerAppComponent.create();
MeterRegistrationService service =
  component.meterRegistrationService();
service.registerMeter();
```
- to replace manual wiring
## Checkpoint
| Step | Status |
| --- | --- |
| Why DI exists | |
| Constructor injection | |
| Manual object wiring | |
| Pain points of manual wiring | |
| `@Inject` constructors | |
| `@Component` | |
| Dagger validates the object graph | |
| Dagger generates wiring code | |
| `@Module` | |
| `@Provides` | |
| Scopes (`@Singleton`) | |
| Binding interfaces (`@Binds`) | |
| Qualifiers (`@Named`, custom qualifiers) | |
| Subcomponents / Component dependencies | |
| Best practices | |
## @Singleton, @Binds, @Provides
- `@Singleton` = HOW MANY
- `@Binds` = WHO
- `@Provides` = HOW
| Annotation | Simple meaning | Think of it as | Example |
| --- | --- | --- | --- |
| `@Singleton` | Create one instance per Dagger Component | One shared resource | `@Singleton Database` |
| `@Binds` | Tell Dagger which implementation to use | Connect interface → implementation | `Repository` → `UserRepositoryImpl` |
| `@Provides` | Tell Dagger how to create an object | A recipe | `new Database(...)` |

```text
@Binds
Repository ──────────────→ UserRepositoryImpl
             "use this implementation"

@Provides
Database ────────────────→ new Database(...)
             "here's how to create it"

@Singleton
Database
   ↓
ONE shared instance
within the Dagger Component
```
