# Integrate Apache CXF with Spring Boot
- [Add the CXF Spring Boot Starter](#add-the-cxf-spring-boot-starter)
- [Update `gradle/libs.versions.toml`](#update-gradlelibsversionstoml)
- [Update `integration/build.gradle`](#update-integrationbuildgradle)
- [Checkpoint 6](#checkpoint-6)
- [Implement the generated service interface](#implement-the-generated-service-interface)
- [Publish the service with Apache CXF](#publish-the-service-with-apache-cxf)
  - [Create config package](#create-config-package)
  - [Create CxfConfig.java](#create-cxfconfigjava)
    - [Bus](#bus)
    - [EndpointImpl](#endpointimpl)
    - [publish()](#publish)
- [Run the spring boot application](#run-the-spring-boot-application)
- [Refactoring](#refactoring)
## Add the CXF Spring Boot Starter
* Update `gradle/libs.versions.toml`, Add under `[libraries]`
```toml
cxf-spring-boot-starter = { module = "org.apache.cxf:cxf-spring-boot-starter-jaxws", version.ref = "cxf" }
```
## Update `integration/build.gradle`
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
## Checkpoint 6
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
## Implement the generated service interface
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

* Create: [`MeterRegistrationServiceImpl.java`](meter-registration-service/integration/src/mian/java/integration/enterprise/service/MeterRegistrationServiceImpl.java)
* Why `@Service`?
    * Right now we're only registering it as a Spring bean.
    * CXF will discover this bean later when we publish the endpoint.
    * Notice that we are **not** adding `@WebService` here.
    * The SOAP contract is already defined by the generated interface (`MeterRegistrationPortType`). 
    * Keeping the implementation focused on business logic is a common pattern.
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
## Publish the service with Apache CXF
### Create config package
```text
integration
└── src
    └── main
        └── java
            └── integration
                └── enterprise
                    └── config
```

### Create [`CxfConfig.java`](meter-registration-service/integration/src/main/java/integration/enterprise/config/CxfConfig.java)
#### Bus
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
#### EndpointImpl
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
#### publish()
* This publishes the endpoint at:`/services/meter-registration`
    * The `/services` prefix comes from the CXF Spring Boot starter's default servlet mapping.
```java
endpoint.publish("/meter-registration");
```
## Run the spring boot application
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
## Refactoring
- `[MeterRegistrationProcessor.java](meter-registration-service\integration\src\main\java\integration\enterprise\service\MeterRegistrationProcessor.java)`
- `[MeterRegistrationProcessorImpl.java](meter-registration-service\integration\src\main\java\integration\enterprise\service\MeterRegistrationProcessorImpl.java)`
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
Checkpoint 7
* ✅ WSDL-first contract
* ✅ JAXB model generation
* ✅ CXF service interface generation
* ✅ Spring Boot integration
* ✅ Published SOAP endpoint
* ✅ End-to-end SoapUI test
* ✅ Business logic separated from transport layer
