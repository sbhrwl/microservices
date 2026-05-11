# Gradle build
- [Introduction](#introduction)
- [Gradle sections](#gradle-sections)
- [Task flow](#task-flow)
  - [Local run](#local-run)
  - [Packaging / Docker](#packaging-/-docker)
- [JAR](#jar)
## Introduction
- Build
```
Gradle build file
│
├── Plugins
│   └── git metadata generation
│
├── Dependencies
│   ├── gRPC
│   ├── Camel / JMS / ActiveMQ
│   ├── Dagger
│   ├── JAXB
│   ├── Reactor
│   └── Testing
│
├── Sources
│   ├── src/main/java
│   └── generated sources
│
├── Compile/Test config
│   ├── annotation processors
│   └── JUnit 5
│
├── Jar packaging
│   ├── main class = Bootstrap
│   └── runtime deps in libs/
│
└── Runtime tasks
    ├── run
    └── createDistribution
```
- Run
```
Run via Gradle
    │
    ▼
Check project graph
    │
    ├─ generate git info
    ├─ process resources
    ├─ generate code (wsdl2java)
    ├─ compile API
    ├─ build API jar
    ├─ compile simulator
    │
    ▼
Run Bootstrap.main()
    │
    ▼
Logback → Camel → CXF → Jetty starts
```

## Gradle sections
- What They Do → When They Run
- **Git info → Resources → Compile → (Run locally OR Package JAR → Create distribution)**

| Section                         | Gradle Task(s) Involved                    | Purpose                                         | Runs When                       |
| ------------------------------- | ------------------------------------------ | ----------------------------------------------- | ------------------------------- |
| **Git information generation**  | `generateGitProperties`                    | Creates `git-info.properties` with git metadata | Before resources are processed  |
| **processResources dependency** | `processResources → generateGitProperties` | Ensures git info file exists in resources       | Any build/run needing resources |
| **JavaCompile config**          | `compileJava`, `compileTestJava`           | Sets UTF‑8, keeps parameter names, tunes Dagger | Every Java compilation          |
| **Testing configuration**       | `test`                                     | Uses JUnit 5, detailed test logging             | When tests are run              |
| **JAR configuration**           | `jar`                                      | Creates app JAR, sets main class & manifest     | When packaging                  |
| **Distribution task**           | `createDistribution`                       | Builds Docker‑ready folder structure            | When distributing               |
| **Run task**                    | `run (JavaExec)`                           | Runs app locally from classpath                 | Local development               |
| **IntelliJ IDEA config**        | *(no Gradle task)*                         | Marks generated sources in IDE                  | IDE usage only                  |

## Task flow
### Local run
- `./gradlew run`

| Order | Task                    | Why                                 |
| ----- | ----------------------- | ----------------------------------- |
| 1     | `generateGitProperties` | Needed by resources                 |
| 2     | `processResources`      | Collects resources (incl. git info) |
| 3     | `compileJava`           | Compiles source + generated code    |
| 4     | `classes`               | Aggregates compiled output          |
| 5     | `run`                   | Starts `Bootstrap.main()`           |

### Packaging / Docker
- `./gradlew createDistribution`

| Order | Task                    | Why                     |
| ----- | ----------------------- | ----------------------- |
| 1     | `generateGitProperties` | Embed git metadata      |
| 2     | `processResources`      | Prepare resources       |
| 3     | `compileJava`           | Build classes           |
| 4     | `jar`                   | Create application JAR  |
| 5     | `createDistribution`    | Copy JAR, libs, configs |

## JAR
- JAR ↔ Distribution Relationship

| JAR Manifest Entry       | Distribution Task Action              |
| ------------------------ | ------------------------------------- |
| `Class-Path: libs/*.jar` | Copies dependencies into `dist/libs/` |
| `Main-Class`             | JAR is runnable                       |
| Version metadata         | Used for runtime diagnostics          |
