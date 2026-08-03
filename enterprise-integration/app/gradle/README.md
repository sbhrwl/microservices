# Gradle

## Build

## Cleanup
- A fresh `.\gradlew clean` is good practice after copying, but deleting the old build and `.gradle` directories removes any doubt.
```
rmdir /s /q build
rmdir /s /q model\build
rmdir /s /q contract\build
rmdir /s /q .gradle
.\gradlew clean
```
## Generate jaxb from xsd
```
.\gradlew :model:xjc --rerun-tasks --no-build-cache
```
