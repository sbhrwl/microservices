# Project structure
- [Initialize gradle](#initialize-gradle)
- [Clean up default app module](#clean-up-default-app-module)
- [Create the root buildgradle file](#create-the-root-buildgradle-file)
- [Build project](#build-project)
- [Create common module](#create-common-module)
  - [Register the module](#register-the-module)
- [Add more modules](#add-more-modules)
- [Update settingsgradle](#update-settingsgradle)
- [Verify that every module participates in the build](#verify-that-every-module-participates-in-the-build)
- [Build the project](#build-the-project)
- [Make modules a java library](#make-modules-a-java-library)
- [Configure all Java modules from the root instead of repeating configuration in every module](#configure-all-java-modules-from-the-root-instead-of-repeating-configuration-in-every-module)
  - [Explanation](#explanation)
- [Git commit](#git-commit)
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
## Add more modules
- Create these folders, each with an empty `build.gradle` and `src/main/java`
```
contract
model
soap-api
integration
messaging
persistence
```
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
├── contract
│   ├── src
│   │   └── main
│   │       └── java
│   └── build.gradle
├── gradle
├── integration
│   ├── src
│   │   └── main
│   │       └── java
│   └── build.gradle
├── messaging
│   ├── src
│   │   └── main
│   │       └── java
│   └── build.gradle
├── model
│   ├── src
│   │   └── main
│   │       └── java
│   └── build.gradle
├── persistence
│   ├── src
│   │   └── main
│   │       └── java
│   └── build.gradle
├── soap-api
│   ├── src
│   │   └── main
│   │       └── java
│   └── build.gradle
├── .gitattributes
├── .gitignore
├── build.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── README.md
└── settings.gradle
```
## Update settings.gradle
- Add these lines to `settings.gradle`
```
include(
    "common",
    "contract",
    "model",
    "soap-api",
    "integration",
    "messaging",
    "persistence"
)
```
## Verify that every module participates in the build
- List all projects (modules) in your build.
  - Verify that all modules are registered in settings.gradle.
  - Check that Gradle recognizes your project structure.
  - See the project hierarchy.
- `.\gradlew projects`
```
C:\Git\practice\microservices\enterprise-integration\phase-1>.\gradlew projects
Calculating task graph as configuration cache cannot be reused because file 'settings.gradle' has changed.

> Task :projects

Projects:

------------------------------------------------------------
Root project 'meter-registration-service'
------------------------------------------------------------

Location: C:\Git\practice\microservices\enterprise-integration\phase-1

Project hierarchy:

Root project 'meter-registration-service'
+--- Project ':common'
+--- Project ':contract'
+--- Project ':integration'
+--- Project ':messaging'
+--- Project ':model'
+--- Project ':persistence'
\--- Project ':soap-api'

Project locations:

project ':common' - \common
project ':contract' - \contract
project ':integration' - \integration
project ':messaging' - \messaging
project ':model' - \model
project ':persistence' - \persistence
project ':soap-api' - \soap-api

To see a list of the tasks of a project, run gradlew <project-path>:tasks
For example, try running gradlew :common:tasks

BUILD SUCCESSFUL in 1s
1 actionable task: 1 executed
Configuration cache entry stored.
C:\Git\practice\microservices\enterprise-integration\phase-1>
```
## Build the project
- `.\gradlew build`
```
C:\Git\practice\microservices\enterprise-integration\phase-1>.\gradlew build
Calculating task graph as configuration cache cannot be reused because file 'settings.gradle' has changed.

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

BUILD SUCCESSFUL in 1s
1 actionable task: 1 executed
Configuration cache entry stored.
C:\Git\practice\microservices\enterprise-integration\phase-1>
```
## Make modules a java library
- The previous build run isn't a real `build` yet because none of the modules apply the `java` plugin.
- We'll make the `common` module a Java library.
  - In `common/build.gradle`, add:
```groovy
plugins {
    id 'java-library'
}
```
  - Build only `common` module
```powershell
.\gradlew :common:build
```
```
C:\Git\practice\microservices\enterprise-integration\phase-1>.\gradlew :common:build
Calculating task graph as no cached configuration is available for tasks: :common:build

BUILD SUCCESSFUL in 3s
1 actionable task: 1 executed
Configuration cache entry stored.
```
- Now do the same for the remaining modules (Leave `soap-api` **empty** for now. We'll make it a Spring Boot application later.).
  - Add to each `build.gradle`:
```groovy
plugins {
    id 'java-library'
}
```
- Then run:
```powershell
.\gradlew build
```
```
C:\Git\practice\microservices\enterprise-integration\phase-1>.\gradlew build
Calculating task graph as configuration cache cannot be reused because file 'common\build.gradle' has changed.

BUILD SUCCESSFUL in 2s
6 actionable tasks: 5 executed, 1 up-to-date
Configuration cache entry updated for no projects, no projects up-to-date.
C:\Git\practice\microservices\enterprise-integration\phase-1>
```
## Configure all Java modules from the root instead of repeating configuration in every module
- In the **root** `build.gradle`, replace its contents with:
```groovy
allprojects {
    group = 'com.enterprise.integration'
    version = '1.0.0-SNAPSHOT'

    repositories {
        mavenCentral()
    }
}

subprojects {
    plugins.withId('java') {
        java {
            toolchain {
                languageVersion = JavaLanguageVersion.of(21)
            }
        }
    }

    plugins.withId('java-library') {
        java {
            toolchain {
                languageVersion = JavaLanguageVersion.of(21)
            }
        }
    }
}
```
- Then run:
```powershell
.\gradlew build
```
```
C:\Git\practice\microservices\enterprise-integration\phase-1>.\gradlew build
Calculating task graph as configuration cache cannot be reused because file 'build.gradle' has changed.

BUILD SUCCESSFUL in 3s
6 actionable tasks: 6 up-to-date
Configuration cache entry stored.
C:\Git\practice\microservices\enterprise-integration\phase-1>
```
### Explanation
* `subprojects`
```groovy
subprojects {
    ...
}
```
  * Runs the enclosed configuration for **every child module**.
  * Instead of writing the same code in below modules, you write it once.
    * `common`
    * `contract`
    * `model`
    * `soap-api`
    * `integration`
    * `messaging`
    * `persistence`
* `plugins.withId(...)`
  * Only apply the configuration **if the module uses the `Java` plugin**. 
  * This avoids errors for non-Java modules.
* `toolchain`
  * Tells Gradle to build the project using **Java 21**, regardless of the default JDK on the machine.
```groovy
toolchain {
    languageVersion = JavaLanguageVersion.of(21)
}
```
* Why we have both?
  * `plugins.withId("java") { ... }`
  * `plugins.withId("java-library") { ... }`
* Because some modules might use java, while others use java-library. 
  * The code applies the same Java 21 toolchain configuration to both.
## Git commit
```git
git add .
git commit -m "Phase 1: Create Gradle multi-module project skeleton"
git push -u origin main
```
