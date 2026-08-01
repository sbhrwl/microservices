# Project structure
## Initialize gradle
- `cd C:\Git\practice\microservices\enterprise-integration\phase-1`
- `gradle init`
```text
PS C:\Git\practice\microservices\enterprise-integration\phase-1> gradle init
Starting a Gradle Daemon (subsequent builds will be faster)

Found existing files in the project directory: 'C:\Git\practice\microservices\enterprise-integration\phase-1'.
Directory will be modified and existing files may be overwritten.  Continue? (default: no) [yes, no] yes

Select type of build to generate:
  1: Application
  2: Library
  3: Gradle plugin
  4: Basic (build structure only)
Enter selection (default: Application) [1..4] application
Please enter a value between 1 and 4: 1

Select implementation language:
  1: Java
  2: Kotlin
  3: Groovy
  4: Scala
  5: C++
  6: Swift
Enter selection (default: Java) [1..6] 1

Enter target Java version (min: 7, default: 21): 21

Project name (default: phase-1): meter-registration-service

Select application structure:
  1: Single application project
  2: Application and library project
Enter selection (default: Single application project) [1..2] 1

Select build script DSL:
  1: Kotlin
  2: Groovy
Enter selection (default: Kotlin) [1..2] 2

Select test framework:
  1: JUnit 4
  2: TestNG
  3: Spock
  4: JUnit Jupiter
Enter selection (default: JUnit Jupiter) [1..4] 4

Generate build using new APIs and behavior (some features may change in the next minor release)? (default: no) [yes, no] yes


> Task :init
Learn more about Gradle by exploring our Samples at https://docs.gradle.org/9.3.1/samples/sample_building_java_applications.html

BUILD SUCCESSFUL in 4m 38s
1 actionable task: 1 executed
PS C:\Git\practice\microservices\enterprise-integration\phase-1>
```

```
phase-1
├── app
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── org
│   │   │   │       └── example
│   │   │   │           └── App.java
│   │   │   └── resources
│   │   └── test
│   │       ├── java
│   │       │   └── org
│   │       │       └── example
│   │       │           └── AppTest.java
│   │       └── resources
│   └── build.gradle
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── libs.versions.toml
├── .gitattributes
├── .gitignore
├── gradle.properties
├── gradlew
├── gradlew.bat
├── README.md
└── settings.gradle
```
## Clean up default app module
```
phase-1
├── .idea
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── libs.versions.toml
├── .gitattributes
├── .gitignore
├── gradle.properties
├── gradlew
├── gradlew.bat
├── README.md
└── settings.gradle
```
## Create the root build.gradle file
```
phase-1
├── .idea
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── libs.versions.toml
├── .gitattributes
├── .gitignore
├── build.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── README.md
└── settings.gradle
```
- Delete any new `include(...)` lines from `settings.gradle`
## Build project
- `.\gradlew build`
```
C:\Git\practice\microservices\enterprise-integration\phase-1>.\gradlew build
Calculating task graph as no cached configuration is available for tasks: build

> Task :buildEnvironment
Daemon JVM: Eclipse Temurin JDK 21 (21.0.9+10-LTS)
  | Location:           C:\Users\SabharwalR\.jdks\temurin-21.0.9
  | Language Version:   21
  | Vendor:             Eclipse Temurin
  | Architecture:       amd64
  | Is JDK:             true


------------------------------------------------------------
Root project 'meter-registration-service'
------------------------------------------------------------

classpath
No dependencies

A web-based, searchable dependency report is available by adding the --scan option.

BUILD SUCCESSFUL in 2s
1 actionable task: 1 executed
Configuration cache entry stored.
C:\Git\practice\microservices\enterprise-integration\phase-1>
```
## Create common module
```
phase-1
├── .gradle
├── .idea
├── build
├── common
│   ├── src
│   │   └── main
│   │       └── java
│   └── build.gradle
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── libs.versions.toml
├── .gitattributes
├── .gitignore
├── build.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── README.md
└── settings.gradle
```
### Register the module
- In `settings.gradle`
  - add: `include("common")`
- In **root** `build.gradle`, add
```
allprojects {
    group = 'com.enterprise.integration'
    version = '1.0.0-SNAPSHOT'
}
```
- Build `.\gradlew build`
```

Calculating task graph as configuration cache cannot be reused because file 'common\build.gradle' has changed.

> Task :projects

Projects:

------------------------------------------------------------
Root project 'meter-registration-service'
------------------------------------------------------------

Location: C:\Git\practice\microservices\enterprise-integration\phase-1

Project hierarchy:

Root project 'meter-registration-service'
\--- Project ':common'

Project locations:

project ':common' - \common

To see a list of the tasks of a project, run gradlew <project-path>:tasks
For example, try running gradlew :common:tasks

BUILD SUCCESSFUL in 2s
1 actionable task: 1 executed
Configuration cache entry stored.
C:\Git\practice\microservices\enterprise-integration\phase-1>
```
