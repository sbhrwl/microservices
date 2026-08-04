# Gradle

## Build
- `./gradlew build`
## Run
- `./gradlew run`

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
