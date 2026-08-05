# Camle setup
- [Add Apache Camel dependencies](#add-apache-camel-dependencies)
    - [Update `gradle/libs.versions.toml`](#update-gradlelibsversionstoml)
    - [Update `integration/build.gradle`](#update-integrationbuildgradle)
    - [Verify build](#verify-build)
- [Create Camel Route](#create-camel-route)
    - [Verify Camel starts](#verify-camel-starts)
- [Inject Camel's `ProducerTemplate`](#inject-camels-producertemplate)
    - [Change in flow](#change-in-flow)
- [Checkpoint](#checkpoint)
## Add Apache Camel dependencies
* Install Camel into the project and verify Spring Boot recognizes it.
### Update [`gradle/libs.versions.toml`](meter-registration-service/gradle/libs.versions.toml)
* Add a Camel version:

```toml
[versions]
camel = "4.14.0"
```

* Then add these libraries under `[libraries]`:
    * We're intentionally starting with the minimum required dependencies.
    * We'll add components like `camel-jms`, `camel-jdbc`, or `camel-cxf` only when we need them.
```toml
camel-spring-boot = { module = "org.apache.camel.springboot:camel-spring-boot-starter", version.ref = "camel" }
camel-core = { module = "org.apache.camel:camel-core", version.ref = "camel" }
```
### Update [`integration/build.gradle`](meter-registration-service/integration/build.gradle)
* Add:

```groovy
implementation libs.camel.spring.boot
implementation libs.camel.core
```
### Verify build
* Run: `.\gradlew :integration:build`
```text
C:\Git\practice\microservices\enterprise-integration\phase-5\meter-registration-service>.\gradlew :integration:build
Calculating task graph as no cached configuration is available for tasks: :integration:build

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

BUILD SUCCESSFUL in 28s
10 actionable tasks: 6 executed, 4 up-to-date
Configuration cache entry stored.
C:\Git\practice\microservices\enterprise-integration\phase-5\meter-registration-service>
```
## Create Camel Route
* Create a new package:

```text
integration
└── enterprise
    └── route
```

* Create: [`MeterRegistrationRoute.java`](meter-registration-service/integration/src/main/java/enterprise/route/MeterRegistrationRoute.java)
* Application now has an internal Camel endpoint:
```text
direct:registerMeter
```
* Think of `direct:` as an **in-memory function call**.
    * Nothing outside your application can call it.

```
SOAP
   │
   ▼
Processor
   │
   ▼
direct:registerMeter
```
### Verify Camel starts
* Run: `.\gradlew :integration:bootRun`
* Spring Boot will discover:
    * `@Component`
    * `RouteBuilder`
    * register the route automatically

```text
2026-08-04T14:49:11.777+03:00  INFO 28712 --- [           main] o.a.c.impl.engine.AbstractCamelContext   : Apache Camel 4.14.0 (camel-1) is starting
2026-08-04T14:49:11.786+03:00  INFO 28712 --- [           main] o.a.c.impl.engine.AbstractCamelContext   : Routes startup (total:1)
2026-08-04T14:49:11.786+03:00  INFO 28712 --- [           main] o.a.c.impl.engine.AbstractCamelContext   :     Started meter-registration-route (direct://registerMeter)
2026-08-04T14:49:11.786+03:00  INFO 28712 --- [           main] o.a.c.impl.engine.AbstractCamelContext   : Apache Camel 4.14.0 (camel-1) started in 7ms (build:0ms init:0ms start:7ms boot:1s65ms)
2026-08-04T14:49:11.788+03:00  INFO 28712 --- [           main] i.enterprise.IntegrationApplication      : Started IntegrationApplication in 2.993 seconds (process running for 3.585)
<============-> 93% EXECUTING [1m 7s]
```
## Inject Camel's ProducerTemplate
- Update [`MeterRegistrationServiceImpl.java`](meter-registration-service/integration/src/main/java/integration/enterprise/service/MeterRegistrationServiceImpl.java)
- Update [`MeterRegistrationRoute.java`](meter-registration-service/integration/src/main/java/enterprise/route/MeterRegistrationRoute.java)
### Change in flow
- Instead of
```text
SOAP
  │
  ▼
Processor
```
- Now
```text
SOAP
  │
  ▼
ProducerTemplate
  │
  ▼
direct:registerMeter
  │
  ▼
Camel Route
  │
  ▼
Processor
```
## Checkpoint
- Send a SOAP request and verify logs
```text
2026-08-05T10:34:55.270+03:00  INFO 41664 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2026-08-05T10:34:55.445+03:00  INFO 41664 --- [           main] o.a.c.impl.engine.AbstractCamelContext   : Apache Camel 4.14.0 (camel-1) is starting
2026-08-05T10:34:55.453+03:00  INFO 41664 --- [           main] o.a.c.impl.engine.AbstractCamelContext   : Routes startup (total:1)
2026-08-05T10:34:55.453+03:00  INFO 41664 --- [           main] o.a.c.impl.engine.AbstractCamelContext   :     Started meter-registration-route (direct://registerMeter)
2026-08-05T10:34:55.453+03:00  INFO 41664 --- [           main] o.a.c.impl.engine.AbstractCamelContext   : Apache Camel 4.14.0 (camel-1) started in 7ms (build:0ms init:0ms start:7ms boot:1s500ms)
2026-08-05T10:34:55.455+03:00  INFO 41664 --- [           main] i.enterprise.IntegrationApplication      : Started IntegrationApplication in 4.117 seconds (process running for 4.729)
2026-08-05T10:34:58.359+03:00  INFO 41664 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : ========== SOAP REQUEST RECEIVED ==========
2026-08-05T10:34:58.359+03:00  INFO 41664 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : GSRN              : 735999123456789011
2026-08-05T10:34:58.359+03:00  INFO 41664 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Meter Serial      : MS-123456
2026-08-05T10:34:58.359+03:00  INFO 41664 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Customer ID       : CUST-1001
2026-08-05T10:34:58.359+03:00  INFO 41664 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Relay Number      : 1
2026-08-05T10:34:58.359+03:00  INFO 41664 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Relay State       : ON
2026-08-05T10:34:58.359+03:00  INFO 41664 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Timestamp         : 2026-08-03T18:30:00Z
2026-08-05T10:34:58.369+03:00  INFO 41664 --- [nio-8080-exec-1] meter-registration-route                 : Camel received request for GSRN=735999123456789011
2026-08-05T10:34:58.370+03:00  INFO 41664 --- [nio-8080-exec-1] meter-registration-route                 : Camel completed registration. Response=SUCCESS
2026-08-05T10:34:58.370+03:00  INFO 41664 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Sending SOAP response
2026-08-05T10:34:58.370+03:00  INFO 41664 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Status            : SUCCESS
2026-08-05T10:34:58.370+03:00  INFO 41664 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Message           : Meter registered successfully
2026-08-05T10:34:58.370+03:00  INFO 41664 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : Registration ID   : 4708a665-ef86-416f-a63a-dd6a25674708
2026-08-05T10:34:58.370+03:00  INFO 41664 --- [nio-8080-exec-1] i.e.s.MeterRegistrationServiceImpl       : ===========================================
<============-> 93% EXECUTING [21s]
```
```text
SOAP REQUEST RECEIVED
        ↓
Camel route invoked
        ↓
Camel route completed
        ↓
Sending SOAP response
```
- From here onward we can insert anything into the route:
    - Logging
    - Validation
    - Transformation
    - Routing
    - JMS/ActiveMQ
    - Database
    - REST calls
    - Error handling
    - Retry logic
- without changing the SOAP service