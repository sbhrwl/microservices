# Installation
- [Maven build and run steps](#maven-build-and-run-steps)
## maven build and run steps
- build
```
mvn clean install -DskipTests=true -D"checkstyle.skip"=true
```
  - optionally update snapshots and releases during build
```
mvn clean install -DskipTests=true -D"checkstyle.skip"=true -U
```

- run the application (after the sidecar is running)
  - export DAPR_GRPC_PORT to match the sidecar flag
```
$env:DAPR_GRPC_PORT=50011
```
  - start the service using the packaged JAR and provided configs
```
java -D"config.file=src/main/dist/etc/application.conf" -D"logback.configurationFile=src/main/dist/etc/logback.xml" -D"log.appender"=STDOUT -jar target/device-hub-1.0.jar
```
