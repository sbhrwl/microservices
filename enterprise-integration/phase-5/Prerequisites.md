# Prerequisites
- [Cleanup](#cleanup)
- [Build](#build)
- [Verification](#verification)
    - [Contract](#contract)
    - [Model](#model)
    - [Soap API](#soap-api)
    - [Integration](#integration)
- [Run Spring Boot application](#run-spring-boot-application)
- [Verify logs](#verify-logs)
- [Summary](#summary)
## Cleanup
- Remove all build folders from all directories
```
rmdir /s /q build
rmdir /s /q contract\build
rmdir /s /q model\build
rmdir /s /q soap-api\build
rmdir /s /q integration\build
rmdir /s /q .gradle
```
- Perform `.\gradlew clean`
- Verify `tasks` for model module `.\gradlew :model:tasks --all`
- Verify `properties` for model module `.\gradlew :model:properties`
## Build
- [libs.versions.toml](meter-registration-service/gradle/libs.versions.toml)
- Build the project and modules underneath it `.\gradlew build`
## Verification
### Contract
- [meter-registration.xsd](meter-registration-service/contract/src/main/resources/xsd/meter-registration.xsd)
- [meter-registration.wsdl](meter-registration-service/contract/src/main/resources/wsdl/meter-registration.wsdl)
- [meter-registration-bindings.xml](meter-registration-service/contract/src/main/resources/wsdl/meter-registration-bindings.xml)
```text
contract
├── src
│   └── main
│       ├── java
│       └── resources
│           ├── wsdl
│           │   ├── meter-registration.wsdl
│           │   └── meter-registration-bindings.xml
│           └── xsd
│               └── meter-registration.xsd
└── build.gradle
```
### Model
- [JAXB plugin - XJC Gradle configuration](meter-registration-service/model/build.gradle)
- Generate java classes: `.\gradlew :model:xjc`
```text
model
├── build
│   ├── classes
│   ├── generated
│   │   └── sources
│   │       ├── annotationProcessor
│   │       ├── headers
│   │       └── xjc
│   │           └── java
│   │               └── integration
│   │                   └── enterprise
│   │                       └── meter_registration
│   │                           └── v1
│   │                               ├── MeterRegistrationRequest.java
│   │                               ├── MeterRegistrationResponse.java
│   │                               ├── ObjectFactory.java
│   │                               ├── package-info.java
│   │                               └── RelayState.java
│   │       └── resources
│   └── libs
│       └── model-1.0.0-SNAPSHOT.jar
```
### Soap api
- [CXF Gradle configuration](meter-registration-service/soap-api/build.gradle)
- Task `wsdl2java` generates java classes MeterRegistrationPortType.java and MeterRegistrationService.java from WSDL using Apache CXF
```text
soap-api
├── build
│   ├── classes
│   ├── generated
│   │   └── sources
│   │       ├── annotationProcessor
│   │       ├── headers
│   │       └── wsdl2java
│   │           └── integration
│   │               └── enterprise
│   │                   └── meter_registration
│   │                       └── v1
│   │                           ├── MeterRegistrationPortType.java
│   │                           └── MeterRegistrationService.java
│   └── libs
│       └── soap-api-1.0.0-SNAPSHOT.jar
```
### Integration
- Configure endpoint [CxfConfig.java](meter-registration-service/integration/src/main/java/integration/enterprise/config/CxfConfig.java)
- Publishes SOAP endpoint [EndpointPublisher.java](meter-registration-service/integration/src/main/java/integration/EndpointPublisher.java)
- [MeterRegistrationProcessor.java](meter-registration-service/integration/src/main/java/integration/enterprise/service/MeterRegistrationProcessor.java)
- [MeterRegistrationProcessorImpl.java](meter-registration-service/integration/src/main/java/integration/enterprise/service/MeterRegistrationProcessorImpl.java)
- [MeterRegistrationServiceImpl.java](meter-registration-service/integration/src/main/java/integration/enterprise/service/MeterRegistrationServiceImpl.java)
- [MeterRegistrationPortTypeImpl.java](meter-registration-service/integration/src/main/java/enterprise/meter_registration/v1/MeterRegistrationPortTypeImpl.java)
- [build.gradle](meter-registration-service/integration/build.gradle) 
  - `.\gradlew :integration:dependencies --configuration runtimeClasspath --no-configuration-cache`
```text
integration
├── build
│   ├── classes
│   ├── generated
│   ├── libs
│   │   ├── integration-1.0.0-SNAPSHOT.jar
│   │   └── integration-1.0.0-SNAPSHOT-plain.jar
│   ├── tmp
│   └── resolvedMainClassName
├── src
│   └── main
│       └── java
│           ├── enterprise
│           │   └── meter_registration
│           │       └── v1
│           │           └── MeterRegistrationPortTypeImpl.java
│           └── integration
│               └── enterprise
│                   ├── config
│                   │   └── CxfConfig.java
│                   ├── service
│                   │   ├── MeterRegistrationProcessor.java
│                   │   ├── MeterRegistrationProcessorImpl.java
│                   │   └── MeterRegistrationServiceImpl.java
│                   ├── IntegrationApplication.java
│                   └── EndpointPublisher.java
└── build.gradle
```
## Run spring boot application
* `.\gradlew :integration:bootRun`
## Verify logs
```text
026-08-04T14:03:39.199+03:00  INFO 26120 --- [           main] i.enterprise.IntegrationApplication      : Starting IntegrationApplication using Java 21.0.9 with PID 26120 (C:\Git\practice\microservices\enterprise-integration\phase-5\meter-registration-service\integration\build\classes\java\main started by SabharwalR in C:\Git\practice\microservices\enterprise-integration\phase-5\meter-registration-service\integration)
2026-08-04T14:03:39.201+03:00  INFO 26120 --- [           main] i.enterprise.IntegrationApplication      : No active profile set, falling back to 1 default profile: "default"
2026-08-04T14:03:40.148+03:00  INFO 26120 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2026-08-04T14:03:40.160+03:00  INFO 26120 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2026-08-04T14:03:40.160+03:00  INFO 26120 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.44]
2026-08-04T14:03:40.243+03:00  INFO 26120 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2026-08-04T14:03:40.244+03:00  INFO 26120 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1007 ms
2026-08-04T14:03:40.433+03:00  INFO 26120 --- [           main] o.a.c.w.s.f.ReflectionServiceFactoryBean : Creating Service {http://enterprise.integration/meter-registration/v1}MeterRegistrationService from class integration.enterprise.meter_registration.v1.MeterRegistrationPortType
2026-08-04T14:03:40.731+03:00  INFO 26120 --- [           main] org.apache.cxf.endpoint.ServerImpl       : Setting the server's publish address to be /meter-registration
2026-08-04T14:03:40.974+03:00  INFO 26120 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2026-08-04T14:03:40.981+03:00  INFO 26120 --- [           main] i.enterprise.IntegrationApplication      : Started IntegrationApplication in 2.155 seconds (process running for 2.546)
2026-08-04T14:03:52.241+03:00  INFO 26120 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : ========== SOAP REQUEST RECEIVED ==========
2026-08-04T14:03:52.242+03:00  INFO 26120 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : GSRN              : 735999123456789011
2026-08-04T14:03:52.243+03:00  INFO 26120 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Meter Serial      : MS-123456
2026-08-04T14:03:52.243+03:00  INFO 26120 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Customer ID       : CUST-1001
2026-08-04T14:03:52.243+03:00  INFO 26120 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Relay Number      : 1
2026-08-04T14:03:52.243+03:00  INFO 26120 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Relay State       : ON
2026-08-04T14:03:52.243+03:00  INFO 26120 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Timestamp         : 2026-08-03T18:30:00Z
2026-08-04T14:03:52.243+03:00  INFO 26120 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Sending SOAP response
2026-08-04T14:03:52.243+03:00  INFO 26120 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Status            : SUCCESS
2026-08-04T14:03:52.243+03:00  INFO 26120 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Message           : Meter registered successfully
2026-08-04T14:03:52.243+03:00  INFO 26120 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Registration ID   : e3560936-e180-4cc7-8099-04b8a5a16b23
2026-08-04T14:03:52.243+03:00  INFO 26120 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : ===========================================
<============-> 93% EXECUTING [44s]
> IDLE
> :integration:bootRun
```
## Summary

| Class                            | Created by               | Purpose                       |
| -------------------------------- | ------------------------ | ----------------------------- |
| `MeterRegistrationRequest`       | JAXB (XJC) from the XSD  | Request DTO                   |
| `MeterRegistrationResponse`      | JAXB (XJC) from the XSD  | Response DTO                  |
| `MeterRegistrationPortType`      | Apache CXF (`wsdl2java`) | SOAP service interface        |
| `MeterRegistrationServiceImpl`   | **You**                  | SOAP endpoint implementation  |
| `MeterRegistrationProcessor`     | **You**                  | Business logic abstraction (refactoring)    |
| `MeterRegistrationProcessorImpl` | **You**                  | Business logic implementation (refactoring) |
