# Apache CXF
- [Goal](#goal)
- [Add Apache CXF plugin](#add-apache-cxf-plugin)
- [Configure buildgradle of project soap-api](#configure-buildgradle-of-project-soap-api)
  - [Why depend on model](#why-depend-on-model)
  - [Why api instead of implementation](#why-api-instead-of-implementation)
- [Verify CXF tooling setup](#verify-cxf-tooling-setup)
- [Add the Gradle task that runs Apache CXF WSDLToJava](#add-the-gradle-task-that-runs-apache-cxf-wsdlto-java)
  - [Configure the CXF wsdl2java task](#configure-the-cxf-wsdl2java-task)
  - [Observations](#observations)
  - [Configure CXF to reuse the existing JAXB model module](#configure-cxf-to-reuse-the-existing-jaxb-model-module)
- [Make Gradle compile the generated CXF sources](#make-gradle-compile-the-generated-cxf-sources)
  - [Observations](#observations-1)
  - [Working version of libsversion.toml](#working-version-of-libsversion-toml)
  - [Working version of buildgradle](#working-version-of-buildgradle)
  - [Verify the generated interface](#verify-the-generated-interface)
# Index

- [Implement the service](#implement-the-service)
  - [Configure Integration module](#configure-integration-module)
    - [Add dependency on soap-api](#add-dependency-on-soap-api)
  - [Implement the generated interface](#implement-the-generated-interface)
    - [Warnings](#warnings)
    - [Accomplishment](#accomplishment)
- [Publish the SOAP endpoint](#publish-the-soap-endpoint)
  - [Add the Apache CXF runtime](#add-the-apache-cxf-runtime)
    - [Update `gradle/libs.versions.toml`](#update-gradlelibsversionstoml)
    - [Update `integration/build.gradle`](#update-integrationbuildgradle)
    - [Build](#build)
    - [Publish the endpoint now](#publish-the-endpoint-now)
      - [Readiness](#readiness)
      - [Create a class in `integration/src/main/java`](#create-a-class-in-integrationsrcmainjava)
      - [Why are we using `Endpoint.publish()`?](#why-are-we-using-endpointpublish)
      - [Verify setup after `EndpointPublisher.java`](#verify-setup-after-endpointpublisherjava)
      - [Add the CXF HTTP transport](#add-the-cxf-http-transport)
        - [Verify](#verify)
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
## Add Apache CXF plugin
- Add the CXF code generation plugin.
- Edit [`libs.versions.toml`](gradle/libs.versions.toml)
- Add this under `[versions]`:
```toml
cxf = "4.1.3"
```
- Then under `[libraries]`:
```toml
cxf-tools = { module = "org.apache.cxf:cxf-tools-wsdlto-frontend-jaxws", version.ref = "cxf" }
cxf-jaxb = { module = "org.apache.cxf:cxf-tools-wsdlto-databinding-jaxb", version.ref = "cxf" }
```
- Next, we'll wire these dependencies into the `soap-api` module.
### Configure build.gradle of project soap-api
* We should not generate code in `soap-api`
* Our modules are:
```text
common
contract
integration
messaging
model
persistence
soap-api
```
* Think about their responsibilities:
  * **contract** → XSD + WSDL
  * **model** → JAXB classes from XSD
  * **soap-api** → JAX-WS service interface generated from WSDL
* Notice the distinction:
  * `model` owns the **data model**.
  * `soap-api` owns the **service contract**.
* This separation is very common in enterprise integration projects.
* Let's configure `soap-api/build.gradle`.
* Add:
```groovy
plugins {
  id 'java-library'
}

configurations {
  cxfCodegen
}

dependencies {
  api project(':model')

  cxfCodegen libs.cxf.tools
  cxfCodegen libs.cxf.jaxb
}
```
#### Why depend on model?
* Because we've **already generated**:
```
MeterRegistrationRequest
MeterRegistrationResponse
RelayState
ObjectFactory
```
* We don't want CXF generating those classes again.
* Instead, we want it to generate only things like:
```
MeterRegistrationPortType.java
MeterRegistrationService.java
```
* and reuse the existing JAXB model.
#### Why api instead of implementation?
* Imagine another module later:

```text
integration
    │
    ▼
soap-api
    │
    ▼
model
```

* If `soap-api` exposes methods like:
```java
MeterRegistrationResponse registerMeter(MeterRegistrationRequest request);
```
  * then consumers of `soap-api` also need access to `MeterRegistrationRequest` and `MeterRegistrationResponse`.
  * Using `api project(":model")` makes those types visible transitively, which is exactly what you want for an API module.
### Verify CXF tooling setup
* Run the Gradle command against the soap-api module to check the dependencies of this project.
  * It does not compile, does not execute CXF, and does not create files 
```text
.\gradlew :soap-api:dependencies --configuration cxfCodegen --no-configuration-cache
```
* Verify below in the output
```text
cxfCodegen

+--- org.apache.cxf:cxf-tools-wsdlto-frontend-jaxws:4.1.3
\--- org.apache.cxf:cxf-tools-wsdlto-databinding-jaxb:4.1.3
```
* ✅ CXF WSDL frontend is available
* ✅ CXF JAXB databinding is available
* ✅ Required transitive dependencies are downloaded
* ✅ Gradle configuration is correct
## Add the Gradle task that runs Apache CXF WSDLToJava
* Add to `build.gradle`
```text
tasks.register('wsdl2java', JavaExec) {

    group = "code generation"
    description = "Generate Java classes from WSDL using Apache CXF"

    classpath = configurations.cxfCodegen

    mainClass = "org.apache.cxf.tools.wsdlto.WSDLToJava"

    args(
            "-d",
            layout.buildDirectory.dir("generated/sources/wsdl2java").get().asFile.absolutePath,
            "../contract/src/main/resources/wsdl/meter-registration.wsdl"
    )
}
```
* Run `.\gradlew :soap-api:tasks --all --no-configuration-cache`
* Verify
```text
Code generation tasks
---------------------
wsdl2java - Generate Java classes from WSDL using Apache CXF
```
### Configure the CXF wsdl2java task
* Replace task with this
```text
tasks.register('wsdl2java', JavaExec) {

    group = "code generation"
    description = "Generate Java classes from WSDL using Apache CXF"

    classpath = configurations.cxfCodegen

    mainClass = "org.apache.cxf.tools.wsdlto.WSDLToJava"

    def outputDir = layout.buildDirectory.dir("generated/sources/wsdl2java")

    args(
            "-d",
            outputDir.get().asFile.absolutePath,
            "../contract/src/main/resources/wsdl/meter-registration.wsdl"
    )
}
```
* Dry run `.\gradlew :soap-api:wsdl2java --dry-run`
```text
C:\Git\practice\microservices\enterprise-integration\phase-4>.\gradlew :soap-api:wsdl2java --dry-run
Calculating task graph as no cached configuration is available for tasks: :soap-api:wsdl2java
:soap-api:wsdl2java SKIPPED

BUILD SUCCESSFUL in 1s
Configuration cache entry stored.
```
* Execute task to generate java code: `.\gradlew :soap-api:wsdl2java --no-configuration-cache`
* It created a `build` folder
```text
soap-api
├── build
│   └── generated
│       └── sources
│           └── wsdl2java
│               └── integration
│                   └── enterprise
│                       └── meter_registration
│                           └── v1
│                               ├── MeterRegistrationPortType.java
│                               ├── MeterRegistrationRequest.java
│                               ├── MeterRegistrationResponse.java
│                               ├── MeterRegistrationService.java
│                               ├── ObjectFactory.java
│                               ├── package-info.java
│                               └── RelayState.java
├── src
└── build.gradle
```
### Observations
* We have **successfully executed CXF wsdl2java**. ✅
```text
soap-api/build/generated/sources/wsdl2java
└── integration
    └── enterprise
        └── meter_registration
            └── v1
                ├── MeterRegistrationPortType.java
                ├── MeterRegistrationRequest.java
                ├── MeterRegistrationResponse.java
                ├── MeterRegistrationService.java
                ├── ObjectFactory.java
                ├── package-info.java
                └── RelayState.java
```

* CXF generated **both**:
  * `JAX-WS service classes` (expected)
    * These are what we wanted from `soap-api`.
```text
MeterRegistrationPortType.java
MeterRegistrationService.java
```
  * `JAXB model classes` (not expected)
    * These duplicate what we already have in `model`
```text
MeterRegistrationRequest.java
MeterRegistrationResponse.java
RelayState.java
ObjectFactory.java
package-info.java
```
### Configure CXF to reuse the existing JAXB model module
* Create a CXF JAXB binding file:
  * This will tell CXF: Use existing JAXB classes from `com.enterprise.integration...` instead of generating new ones.
```
contract/src/main/resources/wsdl/meter-registration-bindings.xml
```
```xml
<jaxws:bindings
        xmlns:jaxws="https://jakarta.ee/xml/ns/jaxws"
        xmlns:jaxb="https://jakarta.ee/xml/ns/jaxb"
        xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
        xmlns:xsd="http://www.w3.org/2001/XMLSchema"
        wsdlLocation="meter-registration.wsdl">

  <jaxws:bindings node="wsdl:definitions/wsdl:types/xsd:schema">
    <jaxb:globalBindings generateElementProperty="false"/>
  </jaxws:bindings>

</jaxws:bindings>
```
* After that we update the Gradle task with:
```text
args(
        "-b",
        "../contract/src/main/resources/wsdl/meter-registration-bindings.xml",

        "-nexclude",
        "http://enterprise.integration/meter-registration/v1",

        "-d",
        layout.buildDirectory.dir("generated/sources/wsdl2java").get().asFile.absolutePath,

        "../contract/src/main/resources/wsdl/meter-registration.wsdl"
)
```
* Regenerate: `.\gradlew :soap-api:clean :soap-api:wsdl2java --no-configuration-cache`
* Inspect generated code: `tree soap-api/build/generated/sources/wsdl2java /F`
```text
C:\Git\practice\microservices\enterprise-integration\phase-4>tree soap-api/build/generated/sources/wsdl2java /F
Folder PATH listing for volume Windows
Volume serial number is BE23-B38B
C:\GIT\PRACTICE\MICROSERVICES\ENTERPRISE-INTEGRATION\PHASE-4\SOAP-API\BUILD\GENERATED\SOURCES\WSDL2JAVA
└───integration
    └───enterprise
        └───meter_registration
            └───v1
                    MeterRegistrationPortType.java
                    MeterRegistrationService.java
```
## Make Gradle compile the generated CXF sources
* Update `soap-api/build.gradle`
```text
plugins {
    id 'java-library'
}

configurations {
    cxfCodegen
}

dependencies {
    api project(':model')

    cxfCodegen libs.cxf.tools
    cxfCodegen libs.cxf.jaxb
}

sourceSets {
    main {
        java {
            srcDir layout.buildDirectory.dir("generated/sources/wsdl2java")
        }
    }
}

tasks.register('wsdl2java', JavaExec) {

    group = "code generation"
    description = "Generate Java classes from WSDL using Apache CXF"

    classpath = configurations.cxfCodegen

    mainClass = "org.apache.cxf.tools.wsdlto.WSDLToJava"

    args(
            "-b",
            "../contract/src/main/resources/wsdl/meter-registration-bindings.xml",

            "-nexclude",
            "http://enterprise.integration/meter-registration/v1",

            "-d",
            layout.buildDirectory.dir("generated/sources/wsdl2java").get().asFile.absolutePath,

            "../contract/src/main/resources/wsdl/meter-registration.wsdl"
    )
}
```
* Run: `.\gradlew :soap-api:clean :soap-api:build --no-configuration-cache`
```text
C:\Git\practice\microservices\enterprise-integration\phase-4>.\gradlew :soap-api:clean :soap-api:build --no-configuration-cache

> Task :model:xjc
integration\enterprise\meter_registration\v1\MeterRegistrationRequest.java
integration\enterprise\meter_registration\v1\MeterRegistrationResponse.java
integration\enterprise\meter_registration\v1\ObjectFactory.java
integration\enterprise\meter_registration\v1\RelayState.java
integration\enterprise\meter_registration\v1\package-info.java

BUILD SUCCESSFUL in 3s
5 actionable tasks: 5 executed
C:\Git\practice\microservices\enterprise-integration\phase-4>
```
### Observations
* This is a clean build.
* We have reached the end of the **CXF wsdl2java setup phase**.
* What just happened:
```text
: model:xjc
        |
        v
Generated JAXB classes
        |
        v
: soap-api:wsdl2java
        |
        v
Generated JAX-WS service classes
        |
        v
: soap-api:compileJava
        |
        v
soap-api.jar
```
* The important architectural goal is now achieved:
```text
                 contract
              (XSD + WSDL)
                    |
          +---------+---------+
          |                   |
          v                   v
        model             soap-api
          |                   |
          | XJC               | CXF wsdl2java
          |                   |
          v                   v
 JAXB request/response     JAX-WS interface
 classes                   service endpoint
```
### Working version of libs.version.toml
```text
[versions]
jaxb = "4.0.5"
jaxbPlugin = "1.9.1"
cxf = "4.1.3"

[libraries]
jaxb-api = { module = "jakarta.xml.bind:jakarta.xml.bind-api", version.ref = "jaxb" }
jaxb-runtime = { module = "org.glassfish.jaxb:jaxb-runtime", version.ref = "jaxb" }
jaxws-api = { module = "jakarta.xml.ws:jakarta.xml.ws-api", version = "4.0.2" }

cxf-tools = { module = "org.apache.cxf:cxf-tools-wsdlto-frontend-jaxws", version.ref = "cxf" }
cxf-jaxb = { module = "org.apache.cxf:cxf-tools-wsdlto-databinding-jaxb", version.ref = "cxf" }

[plugins]
jaxb = { id = "com.github.bjornvester.xjc", version.ref = "jaxbPlugin" }
```
### Working version of build.gradle
```text
plugins {
    id 'java-library'
}

configurations {
    cxfCodegen
}

dependencies {
    api project(':model')

    implementation libs.jaxws.api
    implementation libs.jaxb.api

    cxfCodegen libs.cxf.tools
    cxfCodegen libs.cxf.jaxb
}

sourceSets {
    main {
        java {
            srcDir layout.buildDirectory.dir("generated/sources/wsdl2java")
        }
    }
}

tasks.register('wsdl2java', JavaExec) {

    group = "code generation"
    description = "Generate Java classes from WSDL using Apache CXF"

    classpath = configurations.cxfCodegen

    mainClass = "org.apache.cxf.tools.wsdlto.WSDLToJava"

    args(
            "-b",
            "../contract/src/main/resources/wsdl/meter-registration-bindings.xml",

            "-nexclude",
            "http://enterprise.integration/meter-registration/v1",

            "-d",
            layout.buildDirectory.dir("generated/sources/wsdl2java").get().asFile.absolutePath,

            "../contract/src/main/resources/wsdl/meter-registration.wsdl"
    )
}

tasks.named("compileJava") {
    dependsOn("wsdl2java")
}
```
### Verify  the generated interface
* The generated classes:
  *`MeterRegistrationPortType`
    * JAX-WS service interface
      * **Contains SOAP operations**
  *`MeterRegistrationService`
    * JAX-WS service **endpoint wrapper**
* Open: `soap-api/build/generated/sources/wsdl2java/integration/enterprise/meter_registration/v1/MeterRegistrationPortType.java`
```java
package integration.enterprise.meter_registration.v1;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 4.1.3
 * 2026-08-03T14:24:06.348+03:00
 * Generated source version: 4.1.3
 *
 */
@WebService(targetNamespace = "http://enterprise.integration/meter-registration/v1", name = "MeterRegistrationPortType")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface MeterRegistrationPortType {

    @WebMethod(action = "http://enterprise.integration/meter-registration/v1/registerMeter")
    @WebResult(name = "MeterRegistrationResponse", targetNamespace = "http://enterprise.integration/meter-registration/v1", partName = "response")
    public MeterRegistrationResponse registerMeter(

        @WebParam(partName = "request", name = "MeterRegistrationRequest", targetNamespace = "http://enterprise.integration/meter-registration/v1")
        MeterRegistrationRequest request
    );
}

```
* We want to inspect:
1. The generated method signature:
```java
MeterRegistrationResponse registerMeter(
    MeterRegistrationRequest request
)
```
2. The annotations:
```java
@WebService
@WebMethod
@RequestWrapper
@ResponseWrapper
```
3. Whether CXF correctly references the JAXB classes from `model`.
## Implement the service
* The generated classes:
  *`MeterRegistrationPortType`
  * JAX-WS service interface
    * **Contains SOAP operations**
      *`MeterRegistrationService`
  * JAX-WS service **endpoint wrapper**
* Current module responsibilities:
```text
contract      → XSD + WSDL
model         → JAXB classes (XJC)
soap-api      → Generated JAX-WS contract (CXF)
integration   → SOAP endpoint implementation
messaging     → JMS
persistence   → Database
common        → Shared utilities
```
### Configure Integration module
* The SOAP implementation belongs in **`integration`**, not `soap-api`.
#### Add dependency on soap-api
* Update `integration/build.gradle` to include dependency to `soap-api`
```groovy
plugins {
    id 'java-library'
}

dependencies {
    implementation project(":soap-api")
}
```
* Run: `.\gradlew :integration:dependencies --no-configuration-cache`
* Verify 
```text
compileClasspath
\--- project :soap-api
     \--- project :model
```
## Implement the generated interface
* Create [`integration\src\main\java\enterprise\meter_registration\v1\MeterRegistrationPortTypeImpl.java`](integration\src\main\java\enterprise\meter_registration\v1\MeterRegistrationPortTypeImpl.java)
```text
WSDL
   │
   ▼
Apache CXF
   │
   ├── MeterRegistrationPortType
   └── MeterRegistrationService
            │
            ▼
Your implementation
            │
            ▼
MeterRegistrationPortTypeImpl
```
* Build: `.\gradlew :integration:build --no-configuration-cache`
```text
C:\Git\practice\microservices\enterprise-integration\phase-4>.\gradlew :integration:build --no-configuration-cache

> Task :soap-api:wsdl2java
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

> Task :integration:compileJava
warning: unknown enum constant ParameterStyle.BARE
  reason: class file for jakarta.jws.soap.SOAPBinding$ParameterStyle not found
warning: unknown enum constant XmlAccessType.FIELD
  reason: class file for jakarta.xml.bind.annotation.XmlAccessType not found
warning: unknown enum constant XmlAccessType.FIELD
3 warnings

[Incubating] Problems report is available at: file:///C:/Git/practice/microservices/enterprise-integration/phase-4/build/reports/problems/problems-report.html

BUILD SUCCESSFUL in 4s
8 actionable tasks: 4 executed, 4 up-to-date
C:\Git\practice\microservices\enterprise-integration\phase-4>
```
### Warnings
```
unknown enum constant ParameterStyle.BARE
class file for jakarta.jws.soap.SOAPBinding$ParameterStyle not found

unknown enum constant XmlAccessType.FIELD
class file for jakarta.xml.bind.annotation.XmlAccessType not found
```
* We will change `build.gradle` of `soap-api`:
```text
implementation libs.jaxws.api
implementation libs.jaxb.api
```
to
```text
api libs.jaxws.api
api libs.jaxb.api
```
* Build: `.\gradlew :integration:build --no-configuration-cache`
```text
C:\Git\practice\microservices\enterprise-integration\phase-4>.\gradlew :integration:build --no-configuration-cache

> Task :soap-api:wsdl2java
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

BUILD SUCCESSFUL in 4s
8 actionable tasks: 3 executed, 5 up-to-date
C:\Git\practice\microservices\enterprise-integration\phase-4>
```
### Accomplishment
```text
contract
    │
    ├── XSD
    └── WSDL
          │
          ▼
model
    │
    └── JAXB classes
          │
          ▼
soap-api
    │
    ├── Generated JAX-WS interface
    ├── Generated Service class
    └── Depends on model
          │
          ▼
integration
    │
    └── Implements MeterRegistrationPortType
```
## Publish the SOAP endpoint
### Add the Apache CXF runtime
* So far we've only used **CXF Tools** (`WSDLToJava`). Those are build-time tools.
* Now we need the **CXF Runtime**, which actually hosts a SOAP endpoint.
#### Update `gradle/libs.versions.toml`
* Keep the existing CXF tool libraries. 
* Add one new library:
```toml
[libraries]

jaxb-api = { module = "jakarta.xml.bind:jakarta.xml.bind-api", version.ref = "jaxb" }
jaxb-runtime = { module = "org.glassfish.jaxb:jaxb-runtime", version.ref = "jaxb" }

cxf-tools = { module = "org.apache.cxf:cxf-tools-wsdlto-frontend-jaxws", version.ref = "cxf" }
cxf-jaxb = { module = "org.apache.cxf:cxf-tools-wsdlto-databinding-jaxb", version.ref = "cxf" }

# NEW
cxf-rt-frontend-jaxws = { module = "org.apache.cxf:cxf-rt-frontend-jaxws", version.ref = "cxf" }
```
#### Update `integration/build.gradle`
* Add the runtime dependency:

```groovy
dependencies {

    implementation project(":soap-api")

    implementation libs.cxf.rt.frontend.jaxws
}
```
* Notice how Gradle maps:
```text
cxf-rt-frontend-jaxws
```

to

```groovy
libs.cxf.rt.frontend.jaxws
```

#### Build
* Run: `.\gradlew :integration:dependencies --configuration runtimeClasspath --no-configuration-cache`
* Look for: `org.apache.cxf:cxf-rt-frontend-jaxws:4.1.3`
#### Publish the endpoint now
##### Readiness
* ✅ cxf-core
* ✅ cxf-rt-bindings-soap
* ✅ cxf-rt-databinding-jaxb
* ✅ cxf-rt-wsdl
* ✅ jakarta.xml.ws
* ✅ JAXB runtime
* That means your project is now ready to **host** a SOAP service.
#### Create a class in `integration/src/main/java`:
```
integration/
└── src/
    └── main/
        └── java/
            └── integration/
                └── EndpointPublisher.java
```

with the following code:

```java
package integration;

import integration.enterprise.meter_registration.v1.MeterRegistrationPortTypeImpl;
import jakarta.xml.ws.Endpoint;

public class EndpointPublisher {

    public static void main(String[] args) {

        Endpoint.publish(
                "http://localhost:8080/meter-registration",
                new MeterRegistrationPortTypeImpl()
        );

        System.out.println("SOAP endpoint published.");
        System.out.println("http://localhost:8080/meter-registration?wsdl");
    }
}
```
#### Why are we using `Endpoint.publish()`?
* Because it's part of the **Jakarta JAX-WS API**.
* It lets you understand SOAP publishing without introducing Spring Boot, CXF Bus configuration, or servlet containers.
* The architecture looks like this:

```text
Client
   │
HTTP
   │
   ▼
Endpoint.publish(...)
   │
Apache CXF Runtime
   │
MeterRegistrationPortTypeImpl
```

* This is the simplest possible SOAP server and is great for learning.
#### Verify setup after EndpointPublisher.java
* After creating `EndpointPublisher.java`, run:

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

BUILD SUCCESSFUL in 10s
8 actionable tasks: 5 executed, 3 up-to-date
C:\Git\practice\microservices\enterprise-integration\phase-4>
```
#### Add the CXF HTTP transport 
* `Endpoint.publish(...)` is part of the **Jakarta XML Web Services API**, but **Apache CXF doesn't require you to use it**. 
* Since this learning project is specifically about **Apache CXF**, it's better to use **CXF's own publishing API**. 
* That way you'll understand how CXF actually hosts services, not just the generic JAX-WS API.
* So let's use CXF directly.
  * CXF also needs an HTTP transport to listen for requests.
* Add this to `libs.versions.toml`:

```toml
cxf-rt-transports-http = { module = "org.apache.cxf:cxf-rt-transports-http", version.ref = "cxf" }
```

Then in `integration/build.gradle`:

```groovy
implementation libs.cxf.rt.transports.http
```
##### Verify
* Run exactly one command:
```bash
.\gradlew :integration:dependencies --configuration runtimeClasspath --no-configuration-cache
```
* You should now see something like:
```text
org.apache.cxf:cxf-rt-transports-http:4.1.3
```

## Test with SoapUI
