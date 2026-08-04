# Apache CXF

| Milestone                                                 | Goal                                                          | Result                                                                                                            |
|-----------------------------------------------------------|---------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------|
| **[1. CXF Build Setup](CXF-BuildSetup.md)**               | Configure Gradle and CXF tooling                              | `wsdl2java` works (`MeterRegistrationPortType.java` and `MeterRegistrationService.java` classes generated)        |
| **[2. Service Implementation](ServiceImplementation.md)** | Implement generated interface                                 | Implement `MeterRegistrationPortTypeImpl.java`                                                                    |
| **[3. SOAP Endpoint](PublishEndpoint.md)**                | Publish SOAP service with `Endpoint.publish()`                | Implement `EndpointPublisher.java` and SOAP endpoint `http://localhost:8080/meter-registration?wsdl` is reachable |
| **[5. Spring Boot Migration]()**                          | Convert integration module to Spring Boot                     | Spring Boot application starts                                                                                    |
| **[6. CXF + Spring Boot]()**                              | Replace standalone publisher with Spring-managed CXF endpoint | Production-style SOAP service, testable with SoapUI                                                               |

- [Goal](#goal)




- [Setup Springboot plugin](#setup-springboot-plugin)
  - [Convert the `integration` module into a Spring Boot application](#convert-the-integration-module-into-a-spring-boot-application)
    - [Add Spring Boot plugin](#add-spring-boot-plugin)
    - [Add Spring Dependency Management plugin](#add-spring-dependency-management-plugin)
    - [Update build.gradle for integration project](#update-buildgradle-for-integration-project)
      - [Why `java` instead of `java-library`](#why-java-instead-of-java-library)
    - [Verify](#verify)
  - [Checkpoint 4](#checkpoint-4)
    - [Create the Spring Boot main class](#create-the-spring-boot-main-class)
  - [Checkpoint 5](#checkpoint-5)
  - [Integrate Apache CXF with Spring Boot](#integrate-apache-cxf-with-spring-boot)
    - [Add the CXF Spring Boot Starter](#add-the-cxf-spring-boot-starter)
      - [Update `gradle/libs.versions.toml`](#update-gradlelibsversionstoml)
      - [Update `integration/build.gradle`](#update-integrationbuildgradle)
      - [Verify](#verify)
      - [Implement the generated service interface](#implement-the-generated-service-interface)
      - [Publish the service with Apache CXF](#publish-the-service-with-apache-cxf)
        - [Bus](#bus)
        - [`EndpointImpl`](#endpointimpl)
        - [`publish()`](#publish)
- [Test with SoapUI](#test-with-soapui)
## Goal
* Generate Java code **from the WSDL** (WSDL-first approach).

```text
XSD
   │
   ▼
WSDL
   │
   ▼
Apache CXF wsdl2java
   │
   ├── Service Interface
   ├── Request/Response classes
   ├── ObjectFactory
   └── JAXB annotations
```
* Eventually you'll implement the generated interface.


## Publish the SOAP endpoint

### Setup Springboot plugin
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

#### Convert the `integration` module into a Spring Boot application.
##### Add Spring Boot plugin
* In the **root** `libs.versions.toml` add:
```toml
[versions]
springBoot = "3.5.5"
```

* Under `[plugins]`:
```toml
spring-boot = { id = "org.springframework.boot", version.ref = "springBoot" }
```
##### Add Spring Dependency Management plugin
* Also under `[versions]`:
```toml
springDependencyManagement = "1.1.7"
```
* Under `[plugins]`:
```toml
spring-dependency-management = { id = "io.spring.dependency-management", version.ref = "springDependencyManagement" }
```
##### Update build.gradle for integration project
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
###### Why `java` instead of `java-library`?
* The `integration` module is now an **application**, not a library.
  * `model` → library
  * `soap-api` → library
  * `integration` → executable Spring Boot application
* So `java` is the appropriate plugin.
#### Verify
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
### Checkpoint 4
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
#### Create the Spring Boot main class
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
### Checkpoint 5
```text
✓ contract      → XSD + WSDL
✓ model         → JAXB classes generated
✓ soap-api      → JAX-WS interfaces generated by Apache CXF
✓ integration   → Spring Boot application starts
```
### Integrate Apache CXF with Spring Boot
#### Add the CXF Spring Boot Starter
* Update `gradle/libs.versions.toml`, Add under `[libraries]`
```toml
cxf-spring-boot-starter = { module = "org.apache.cxf:cxf-spring-boot-starter-jaxws", version.ref = "cxf" }
```
#### Then update `integration/build.gradle`
* Replace these two dependencies:
```groovy
implementation libs.cxf.rt.frontend.jaxws
implementation libs.cxf.rt.transports.http
```
* with:
```groovy
implementation libs.cxf.spring.boot.starter
```
* We're doing this because the starter already pulls in the runtime modules (`cxf-rt-frontend-jaxws`, `cxf-rt-transports-http`, and other required CXF components), so we avoid managing them individually.
#### Verify
* Run: `.\gradlew :integration:build --no-configuration-cache`
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

BUILD SUCCESSFUL in 15s
10 actionable tasks: 7 executed, 3 up-to-date
C:\Git\practice\microservices\enterprise-integration\phase-4>
```
Excellent. We're now entering the **Spring Boot + Apache CXF** part of the project.

#### Implement the generated service interface
* Create the package:
```text
integration
└── src
    └── main
        └── java
            └── integration
                └── enterprise
                    └── service
```

* Create: [`MeterRegistrationServiceImpl.java`](integration/src/mian/java/integration/enterprise/service/MeterRegistrationServiceImpl.java)
  * Why `@Service`?
    * Right now we're only registering it as a Spring bean.
    * CXF will discover this bean later when we publish the endpoint.
    * Notice that we are **not** adding `@WebService` here. 
    * The SOAP contract is already defined by the generated interface (`MeterRegistrationPortType`). Keeping the implementation focused on business logic is a common pattern.
* Run: `.\gradlew :integration:build`
```text
C:\Git\practice\microservices\enterprise-integration\phase-4>.\gradlew :integration:build
Calculating task graph as no cached configuration is available for tasks: :integration:build

> Task :soap-api:wsdl2java
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

BUILD SUCCESSFUL in 7s
10 actionable tasks: 6 executed, 4 up-to-date
Configuration cache entry stored.
C:\Git\practice\microservices\enterprise-integration\phase-4>
```
#### Publish the service with Apache CXF
* Create this package:
```text
integration
└── src
    └── main
        └── java
            └── integration
                └── enterprise
                    └── config
```

* Create: [`CxfConfig.java`](integration/src/main/java/integration/enterprise/config/CxfConfig.java)
  * `Bus`
    ```java
    Bus bus
    ```
    * The **CXF Bus** is the central runtime of Apache CXF.
    * Think of it like this:
      ```text
      Spring Boot
            │
            ▼
       CXF Bus
            │
            ├── SOAP
            ├── WSDL
            ├── Interceptors
            ├── JAXB
            └── Endpoints
      ```
    * The starter automatically creates the `Bus` bean.
  * `EndpointImpl`
    
      ```java
      EndpointImpl endpoint = new EndpointImpl(bus, service);
      ```
      * This tells CXF: "Expose this Java object as a SOAP service."
      * The `service` is your implementation of the generated interface:
      ```text
      MeterRegistrationServiceImpl
              │
      implements
              ▼
      MeterRegistrationPortType
      ```
* `publish()`
```java
endpoint.publish("/meter-registration");
```

* This publishes the endpoint at:`/services/meter-registration`
  * The `/services` prefix comes from the CXF Spring Boot starter's default servlet mapping.
* Run: `.\gradlew :integration:bootRun`
  * At that point, your application will expose a live SOAP endpoint backed by the generated WSDL contract.
```text
C:\Git\practice\microservices\enterprise-integration\phase-4>.\gradlew :integration:bootRun
Reusing configuration cache.

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

2026-08-03T21:49:24.155+03:00  INFO 2368 --- [           main] i.enterprise.IntegrationApplication      : Starting IntegrationApplication using Java 21.0.9 with PID 2368 (C:\Git\practice\microservices\enterprise-integration\phase-4\integration\build\classes\java\main started by SabharwalR in C:\Git\practice\microservices\enterprise-integration\phase-4\integration)
2026-08-03T21:49:24.155+03:00  INFO 2368 --- [           main] i.enterprise.IntegrationApplication      : No active profile set, falling back to 1 default profile: "default"
2026-08-03T21:49:24.841+03:00  INFO 2368 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2026-08-03T21:49:24.841+03:00  INFO 2368 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2026-08-03T21:49:24.841+03:00  INFO 2368 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.44]
2026-08-03T21:49:24.905+03:00  INFO 2368 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2026-08-03T21:49:24.905+03:00  INFO 2368 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 719 ms
2026-08-03T21:49:25.053+03:00  INFO 2368 --- [           main] o.a.c.w.s.f.ReflectionServiceFactoryBean : Creating Service {http://service.enterprise.integration/}MeterRegistrationServiceImplService from class integration.enterprise.meter_registration.v1.MeterRegistrationPortType
2026-08-03T21:49:25.292+03:00  INFO 2368 --- [           main] org.apache.cxf.endpoint.ServerImpl       : Setting the server's publish address to be /meter-registration
2026-08-03T21:49:25.519+03:00  INFO 2368 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2026-08-03T21:49:25.519+03:00  INFO 2368 --- [           main] i.enterprise.IntegrationApplication      : Started IntegrationApplication in 1.648 seconds (process running for 1.926)
<============-> 93% EXECUTING [1m 9s]
> :integration:bootRun
```

## Test with SoapUI
* `http://localhost:8080/services`
```
Available SOAP services:
MeterRegistrationPortType
registerMeter
Endpoint address: http://localhost:8080/services/meter-registration
WSDL : {http://service.enterprise.integration/}MeterRegistrationServiceImplService
Target namespace: http://service.enterprise.integration/
```
* `http://localhost:8080/services/meter-registration?wsdl`
```xml
<wsdl:definitions
        xmlns:xsd="http://www.w3.org/2001/XMLSchema"
        xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
        xmlns:tns="http://service.enterprise.integration/"
        xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
        xmlns:ns2="http://schemas.xmlsoap.org/soap/http"
        xmlns:ns1="http://enterprise.integration/meter-registration/v1"
        name="MeterRegistrationServiceImplService"
        targetNamespace="http://service.enterprise.integration/">

  <wsdl:import
          namespace="http://enterprise.integration/meter-registration/v1"
          location="http://localhost:8080/services/meter-registration?wsdl=MeterRegistrationPortType.wsdl"/>

  <wsdl:binding
          name="MeterRegistrationServiceImplServiceSoapBinding"
          type="ns1:MeterRegistrationPortType">

    <soap:binding
            style="document"
            transport="http://schemas.xmlsoap.org/soap/http"/>

    <wsdl:operation name="registerMeter">

      <soap:operation
              soapAction="http://enterprise.integration/meter-registration/v1/registerMeter"
              style="document"/>

      <wsdl:input name="registerMeter">
        <soap:body use="literal"/>
      </wsdl:input>

      <wsdl:output name="registerMeterResponse">
        <soap:body use="literal"/>
      </wsdl:output>

    </wsdl:operation>

  </wsdl:binding>

  <wsdl:service name="MeterRegistrationServiceImplService">

    <wsdl:port
            name="MeterRegistrationServiceImplPort"
            binding="tns:MeterRegistrationServiceImplServiceSoapBinding">

      <soap:address
              location="http://localhost:8080/services/meter-registration"/>

    </wsdl:port>

  </wsdl:service>

</wsdl:definitions>
```
* Request
```xml
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:v1="http://enterprise.integration/meter-registration/v1">

   <soapenv:Header/>

   <soapenv:Body>
      <v1:MeterRegistrationRequest>
         <v1:gsrn>735999123456789012</v1:gsrn>
         <v1:meterSerialNumber>MS-123456</v1:meterSerialNumber>
         <v1:customerId>CUST-1001</v1:customerId>
         <v1:relayNumber>1</v1:relayNumber>
         <v1:relayState>ON</v1:relayState>
         <v1:timestamp>2026-08-03T18:30:00Z</v1:timestamp>
      </v1:MeterRegistrationRequest>
   </soapenv:Body>

</soapenv:Envelope>
```
* Response
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <MeterRegistrationResponse xmlns="http://enterprise.integration/meter-registration/v1">
         <status>SUCCESS</status>
         <message>Meter registered successfully</message>
         <registrationId>REG-10001</registrationId>
      </MeterRegistrationResponse>
   </soap:Body>
</soap:Envelope>
```

## Refactoring
```text
SoapUI
   │
   ▼
CXF Endpoint
   │
   ▼
MeterRegistrationServiceImpl
   │
   ▼
MeterRegistrationProcessor
   │
   ▼
MeterRegistrationProcessorImpl
   │
   ▼
Response
```
- Clean build: `.\gradlew clean build --no-configuration-cache`
- Start application: `.\gradlew :integration:bootRun`
### Where we are now
* ✅ WSDL-first contract
* ✅ JAXB model generation
* ✅ CXF service interface generation
* ✅ Spring Boot integration
* ✅ Published SOAP endpoint
* ✅ End-to-end SoapUI test
* ✅ Business logic separated from transport layer
### Next step
* The next logical step is to stop returning a hardcoded response and introduce the application's business layer properly.

```text
SOAP Endpoint
      │
      ▼
Processor
      │
      ▼
Repository (stub)
      │
      ▼
Database (later in Phase 6)
```
* Initially the repository will be an in-memory stub so that when we introduce PostgreSQL and Flyway later, we only replace the repository implementation, leaving the endpoint and processor unchanged.
* This is the same layering you'll find in many production Spring applications and sets us up nicely for the remaining phases.
