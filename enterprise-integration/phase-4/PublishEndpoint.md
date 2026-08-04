# Publish SOAP endpoint
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
## Add the Apache CXF runtime
* So far we've only used **CXF Tools** (`WSDLToJava`). 
* Those are build-time tools.
* Now we need the **CXF Runtime**, which actually hosts a SOAP endpoint.
## Update `gradle/libs.versions.toml`
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
## Update `integration/build.gradle`
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

## Build
* Run: `.\gradlew :integration:dependencies --configuration runtimeClasspath --no-configuration-cache`
* Look for: `org.apache.cxf:cxf-rt-frontend-jaxws:4.1.3`
## Publish the endpoint now
### Readiness
* ✅ cxf-core
* ✅ cxf-rt-bindings-soap
* ✅ cxf-rt-databinding-jaxb
* ✅ cxf-rt-wsdl
* ✅ jakarta.xml.ws
* ✅ JAXB runtime
* That means your project is now ready to **host** a SOAP service.
### Create a class in `integration/src/main/java`:
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
### Why are we using `Endpoint.publish()`?
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
### Verify setup after EndpointPublisher.java
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
### Add the CXF HTTP transport
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
## Verify
* Run exactly one command:
```bash
.\gradlew :integration:dependencies --configuration runtimeClasspath --no-configuration-cache
```
* You should now see something like:
```text
org.apache.cxf:cxf-rt-transports-http:4.1.3
```