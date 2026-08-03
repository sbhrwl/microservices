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

## Inspect generated code
## Understand every generated class
## Implement the service
## Publish the SOAP endpoint
## Test with SoapUI
