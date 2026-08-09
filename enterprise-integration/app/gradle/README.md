# Gradle
## version
- `gradle -v`
## Build
- `.\gradlew build`
## Run
- `.\gradlew run`

## Cleanup
- `.\gradlew clean`
- A fresh `.\gradlew clean` is good practice after copying, but deleting the old build and `.gradle` directories removes any doubt.
```
rmdir /s /q build
rmdir /s /q model\build
rmdir /s /q contract\build
rmdir /s /q .gradle
```
## Generate jaxb from xsd
```
.\gradlew :model:xjc --rerun-tasks --no-build-cache
```
## Verify project properties
```
.\gradlew :soap-api:properties
```
## init
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
