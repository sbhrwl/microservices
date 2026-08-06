# Testing
- [Jupiter setup](#jupiter-setup)
- [Add test](#add-test)
  - [Verify processor and the SOAP endpoint](#verify-processor-and-the-soap-endpoint)
  - [Camel route testing](#camel-route-testing)
    - [Test case](#test-case)
    - [What this test proves](#what-this-test-proves)
## Jupiter setup
* Add JUnit dependencies
* Since you're using Spring Boot 3.5.5, the easiest approach is to use Spring Boot's test starter.
* It already includes:
    * JUnit Jupiter
    * Mockito
    * AssertJ
    * Spring Test
* Update [`integration/build.gradle`](meter-registration-service/integration/build.gradle)
    * Add this dependency: `testImplementation "org.springframework.boot:spring-boot-starter-test"`
    * Add a task: `useJUnitPlatform`
* Run: `.\gradlew :integration:test`
```text
C:\Git\practice\microservices\enterprise-integration\phase-5\meter-registration-service>.\gradlew :integration:test
Calculating task graph as no cached configuration is available for tasks: :integration:test

> Task :soap-api:wsdl2java
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

BUILD SUCCESSFUL in 7s
7 actionable tasks: 2 executed, 5 up-to-date
Configuration cache entry stored.
C:\Git\practice\microservices\enterprise-integration\phase-5\meter-registration-service>
```
## Add test
### Verify processor and the SOAP endpoint
- [`MeterRegistrationProcessorImplTest`](meter-registration-service/integration/src/test/java/integration/enterprise/service/MeterRegistrationProcessorImplTest.java)
- Run: `.\gradlew :integration:test --rerun-tasks`
  - Open html report at location: `meter-registration-service/integration/build/reports/tests/test/index.html` 
```text
C:\Git\practice\microservices\enterprise-integration\phase-5\meter-registration-service>.\gradlew :integration:test --rerun-tasks
Reusing configuration cache.

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

> Task :integration:test

MeterRegistrationProcessorImplTest > shouldRegisterMeterSuccessfully() PASSED

BUILD SUCCESSFUL in 8s
9 actionable tasks: 9 executed
Configuration cache entry reused.
C:\Git\practice\microservices\enterprise-integration\phase-5\meter-registration-service>
```
### Camel route testing
- Flow
```text
JUnit Test
     │
ProducerTemplate
     │
direct:registerMeter
     │
Camel Route
     │
Processor
     │
Response
```
- Add Camel's JUnit 5 testing library to [`libs.versions.toml`](meter-registration-service/gradle/libs.versions.toml)
- Update [`integration/build.gradle`](meter-registration-service/integration/build.gradle)
#### Test case
- Camel route test will
  - start only a lightweight Camel context,
  - send a MeterRegistrationRequest to direct:registerMeter,
  - verify that the returned MeterRegistrationResponse contains the expected values.
- From there, we'll learn Camel testing features like `MockEndpoint`, `AdviceWith`, route replacement, and route coverage, which are heavily used in production integration projects.
- Create [`MeterRegistrationRouteTest.java`](meter-registration-service/integration/src/test/java/integration/enterprise/service/MeterRegistrationRouteTest.java)
- What this test proves
```text
JUnit
   │
ProducerTemplate
   │
direct:registerMeter
   │
MeterRegistrationRoute
   │
MeterRegistrationProcessor
   │
MeterRegistrationResponse
```
- Run: `.\gradlew :integration:test --rerun-tasks`
  - Open html report at location: `meter-registration-service/integration/build/reports/tests/test/index.html`
```text
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
2026-08-06T10:10:50.022+03:00  INFO 44012 --- [ionShutdownHook] o.a.c.impl.engine.AbstractCamelContext   : Apache Camel 4.14.0 (camel-1) is shutting down (timeout:45s)
2026-08-06T10:10:50.027+03:00  INFO 44012 --- [ionShutdownHook] o.a.c.impl.engine.AbstractCamelContext   : Routes stopped (total:1)
2026-08-06T10:10:50.027+03:00  INFO 44012 --- [ionShutdownHook] o.a.c.impl.engine.AbstractCamelContext   :     Stopped meter-registration-route (direct://registerMeter)
2026-08-06T10:10:50.031+03:00  INFO 44012 --- [ionShutdownHook] o.a.c.impl.engine.AbstractCamelContext   : Apache Camel 4.14.0 (camel-1) shutdown in 9ms (uptime:1s)

> Task :integration:test

MeterRegistrationRouteTest > shouldProcessMeterRegistrationRoute() PASSED

MeterRegistrationRouteTest > contextLoads() PASSED

MeterRegistrationProcessorImplTest > shouldRegisterMeterSuccessfully() PASSED

BUILD SUCCESSFUL in 24s
9 actionable tasks: 4 executed, 5 up-to-date
```

| Test                                                               | Purpose                             | Type                   |
| ------------------------------------------------------------------ | ----------------------------------- | ---------------------- |
| `MeterRegistrationProcessorImplTest`                               | Tests business logic only           | Unit test              |
| `MeterRegistrationRouteTest.contextLoads()`                        | Verifies Spring Boot + Camel wiring | Context test           |
| `MeterRegistrationRouteTest.shouldProcessMeterRegistrationRoute()` | Sends a real message through Camel  | Route integration test |
