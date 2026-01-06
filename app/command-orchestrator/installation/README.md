# Installation
- [Build](#build)
- [Run](#run)
## Build and run steps
```
mvn clean install -DskipTests=true -D"checkstyle.skip"=true
```
- Optionally update snapshots and releases during build
```
mvn clean install -DskipTests=true -D"checkstyle.skip"=true -U
```
## Run
```
mvn spring-boot:run
```
