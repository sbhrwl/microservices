# Service implementation
- [Goal](#goal)
- [Configure Integration module](#configure-integration-module)
  - [Add dependency on soap-api](#add-dependency-on-soap-api)
- [Implement the generated interface](#implement-the-generated-interface)
  - [Warnings](#warnings)
- [Checkpoint 3](#checkpoint-3)
## Goal
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
## Configure Integration module
* The SOAP implementation belongs in **`integration`**, not `soap-api`.
### Add dependency on soap-api
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
### Checkpoint 3
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