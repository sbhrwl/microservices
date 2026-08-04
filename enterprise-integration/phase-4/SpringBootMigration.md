# Sprint boot migration
- [Goal](#goal)
- [Convert the `integration` module into a Spring Boot application](#convert-the-integration-module-into-a-spring-boot-application)
  - [Add Spring Boot plugin](#add-spring-boot-plugin)
  - [Add Spring Dependency Management plugin](#add-spring-dependency-management-plugin)
  - [Update build.gradle for integration project](#update-buildgradle-for-integration-project)
    - [Why `java` instead of `java-library`](#why-java-instead-of-java-library)
- [Checkpoint 4](#checkpoint-4)
- [Create the Spring Boot main class](#create-the-spring-boot-main-class)
- [Checkpoint 5](#checkpoint-5)
## Goal
```text
XSD
   │
   ▼
JAXB (model)
   │
   ▼
WSDL
   │
   ▼
Apache CXF wsdl2java
   │
   ▼
Generated Service Interface
   │
   ▼
Spring Boot
   │
   ▼
Apache CXF Servlet
   │
   ▼
SOAP Endpoint
   │
   ▼
SoapUI
```

## Convert the `integration` module into a Spring Boot application.
### Add Spring Boot plugin
* In the **root** `libs.versions.toml` add:
```toml
[versions]
springBoot = "3.5.5"
```

* Under `[plugins]`:
```toml
spring-boot = { id = "org.springframework.boot", version.ref = "springBoot" }
```
### Add Spring Dependency Management plugin
* Also under `[versions]`:
```toml
springDependencyManagement = "1.1.7"
```
* Under `[plugins]`:
```toml
spring-dependency-management = { id = "io.spring.dependency-management", version.ref = "springDependencyManagement" }
```
### Update build.gradle for integration project
* Update `integration/build.gradle` to convert it into a Spring Boot application
```text
plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    id 'java'
}

dependencies {
    implementation project(':soap-api')

    implementation libs.cxf.rt.frontend.jaxws
    implementation libs.cxf.rt.transports.http

    implementation 'org.springframework.boot:spring-boot-starter'
}
```
#### Why `java` instead of `java-library`?
* The `integration` module is now an **application**, not a library.
    * `model` → library
    * `soap-api` → library
    * `integration` → executable Spring Boot application
* So `java` is the appropriate plugin.
## Checkpoint 4
* Run:
```bash
.\gradlew :integration:build --no-configuration-cache
```
```text
C:\Git\practice\microservices\enterprise-integration\phase-4>.\gradlew :integration:build --no-configuration-cache

> Task :soap-api:wsdl2java
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

> Task :model:xjc
integration\enterprise\meter_registration\v1\MeterRegistrationRequest.java
integration\enterprise\meter_registration\v1\MeterRegistrationResponse.java
integration\enterprise\meter_registration\v1\ObjectFactory.java
integration\enterprise\meter_registration\v1\RelayState.java
integration\enterprise\meter_registration\v1\package-info.java

BUILD SUCCESSFUL in 55s
10 actionable tasks: 7 executed, 3 up-to-date
C:\Git\practice\microservices\enterprise-integration\phase-4>
```
### 
```text
contract
    │
    ├── XSD
    └── WSDL
          │
          ▼
model
    │
    └── JAXB classes (XJC)
          │
          ▼
soap-api
    │
    └── JAX-WS interfaces (Apache CXF wsdl2java)
          │
          ▼
integration
    │
    └── Spring Boot + Apache CXF runtime
```
## Create the Spring Boot main class
* Create `IntegrationApplication.java`
```text
integration
└── src
    └── main
        └── java
            └── integration
                └── enterprise
                    └── IntegrationApplication.java
```
* Run: `.\gradlew :integration:bootRun`
```text
C:\Git\practice\microservices\enterprise-integration\phase-4>.\gradlew :integration:bootRun
Calculating task graph as no cached configuration is available for tasks: :integration:bootRun

> Task :soap-api:wsdl2java
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

> Task :integration:bootRun

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.5.5)

2026-08-03T20:25:53.537+03:00  INFO 15284 --- [           main] i.enterprise.IntegrationApplication      : Starting IntegrationApplication using Java 21.0.9 with PID 15284 (C:\Git\practice\microservices\enterprise-integration\phase-4\integration\build\classes\java\main started by SabharwalR in C:\Git\practice\microservices\enterprise-integration\phase-4\integration)
2026-08-03T20:25:53.540+03:00  INFO 15284 --- [           main] i.enterprise.IntegrationApplication      : No active profile set, falling back to 1 default profile: "default"
2026-08-03T20:25:54.328+03:00  INFO 15284 --- [           main] i.enterprise.IntegrationApplication      : Started IntegrationApplication in 1.19 seconds (process running for 1.781)

BUILD SUCCESSFUL in 13s
9 actionable tasks: 5 executed, 4 up-to-date
Configuration cache entry stored.
C:\Git\practice\microservices\enterprise-integration\phase-4>
```
## Checkpoint 5
```text
✓ contract      → XSD + WSDL
✓ model         → JAXB classes generated
✓ soap-api      → JAX-WS interfaces generated by Apache CXF
✓ integration   → Spring Boot application starts
```