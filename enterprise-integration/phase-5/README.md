# Apache camel
- [Prerequisites](Prerequisites.md)
## Orchestrate with Apache Camel
- Install Camel
- Create the first Camel route
- Processor delegates to Camel
- Camel logs the message
- Camel calls a bean
- Camel transforms the response
- Camel validates data
- Camel writes to PostgreSQL
- Camel sends a JMS message (ActiveMQ)
- Exception handling and retries
### Orchestration
```text
                 SoapUI
                    │
                    ▼
                CXF Endpoint
                    │
                    ▼
          MeterRegistrationPortType
                    │
                    ▼
       MeterRegistrationServiceImpl
                    │
                    ▼
        MeterRegistrationProcessor
                    │
                    ▼
          Apache Camel Route
                    │
      ┌─────────────┼─────────────┐
      ▼             ▼             ▼
   Validate      Transform     Database
      │             │             │
      └─────────────┼─────────────┘
                    ▼
             Response Builder
                    │
                    ▼
            SOAP Response
```
### Flow
```text
SOAP Endpoint
      │
      ▼
Processor
      │
      ▼
Camel
      │
 Route
      │
Processors
Beans
Database
MQ
REST
```
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
### Verify
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
