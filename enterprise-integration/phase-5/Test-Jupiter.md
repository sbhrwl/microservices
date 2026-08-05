# Testing
- [Jupiter setup](#jupiter-setup)
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