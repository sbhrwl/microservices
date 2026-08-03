# Apache CXF

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
#### Observations
Good. Now we have reached an important checkpoint. We **successfully executed CXF wsdl2java**. ✅

Let's record what happened before changing anything.

Current generated output:

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

## Observation
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
#### Configure CXF to reuse the existing JAXB model module
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

## Implement the service
## Publish the SOAP endpoint
## Test with SoapUI
