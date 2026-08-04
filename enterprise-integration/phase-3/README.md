# Creating a wsdl
- [Overview](#overview)
- [Create wsdl directory](#create-wsdl-directory)
- [Create meter-registration.wsdl](#create-meter-registrationwsdl)
  - [Create the root `<definitions>`](#create-the-root-definitions)
    - [Why these namespaces](#why-these-namespaces)
  - [Import the XSD](#import-the-xsd)
    - [Why to do this](#why-to-do-this)
  - [Create the request message](#create-the-request-message)
    - [Why `element` instead of `type`](#why-element-instead-of-type)
  - [Add meter registration response to xsd](#add-meter-registration-response-to-xsd)
    - [Regenerate the JAXB classes](#regenerate-the-jaxb-classes)
    - [Verification](#verification)
  - [Add the response message to wsdl](#add-the-response-message-to-wsdl)
  - [Define the service contract](#define-the-service-contract)
    - [What this means](#what-this-means)
    - [What we've built so far](#what-weve-built-so-far)
  - [Add the SOAP binding](#add-the-soap-binding)
    - [Why `style="document"`](#why-styledocument)
  - [Add the service endpoint](#add-the-service-endpoint)
- [What each section does](#what-each-section-does)
- [Verification](#verification)
  - [Verification checklist](#verification-checklist)
- [Improvements](#improvements)
  - [Using a URI for soapAction](#using-a-uri-for-soapaction)
  - [Service endpoint](#service-endpoint)
- [Validating wsdl](#validating-wsdl)
- [Apache CXF](#apache-cxf)
## Overview
- In enterprise SOAP development, the flow is:
```text
XSD
↓
WSDL
↓
CXF
↓
Java Endpoint
↓
Business Logic
```
- So, we will not write a SOAP service first.
- We will write the **`contract`** first.
## Create wsdl directory
* Keep a clean separation
  * `xsd/` → Data model (types)
  * `wsdl/` → Service contract (operations)
## Create meter-registration.wsdl 
- [meter-registration.xsd](contract/src/main/resources/xsd/meter-registration.xsd)
- [meter-registration.wsdl](contract/src/main/resources/wsdl/meter-registration.wsdl)
```text
contract/src/main/resources/wsdl/meter-registration.wsdl
```
### Create the root `<definitions>`
* Add below definitions

```xml
<?xml version="1.0" encoding="UTF-8"?>

<wsdl:definitions
        xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
        xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:tns="http://enterprise.integration/meter-registration/v1"
        xmlns:mr="http://enterprise.integration/meter-registration/v1"
        targetNamespace="http://enterprise.integration/meter-registration/v1">

</wsdl:definitions>
```
#### Why these namespaces?
* `wsdl` → WSDL elements (`definitions`, `message`, `portType`, ...)
* `soap` → SOAP-specific binding information.
* `xs` → Used when importing XML Schema.
* `tns` (**Target Namespace**) → Refers to **our own** service and types.
* `mr` → the XML schema types we're importing
### Import the XSD
* Inside `<wsdl:definitions>`, add:
```xml
<wsdl:types>
    <xs:schema>
        <xs:import
            namespace="http://enterprise.integration/meter-registration/v1"
            schemaLocation="../xsd/meter-registration.xsd"/>
    </xs:schema>
</wsdl:types>
```

* So your WSDL now looks like:

```text
<wsdl:definitions>
    ...
    <wsdl:types>
        ...
    </wsdl:types>
</wsdl:definitions>
```
#### Why to do this?
* This is where the WSDL says:

> "I'm not defining the data structures myself. They're defined in this XSD."
* This separation is one of the key ideas behind **contract-first SOAP**:
  * **XSD** = Data model
  * **WSDL** = Service contract
### Create the request message
* Add this **below** `<wsdl:types>`:

```xml
<wsdl:message name="MeterRegistrationRequestMessage">
    <wsdl:part
        name="request"
        element="mr:MeterRegistrationRequest"/>
</wsdl:message>
```
* Your structure now becomes:

```text
definitions
├── types
├── message (request)
```
#### Why `element` instead of `type`?
* This is an important SOAP concept.
* Our XSD defines:
```xml
<xs:element
    name="MeterRegistrationRequest"
    type="MeterRegistrationRequest"/>
```
* SOAP document/literal style exchanges **XML elements**, not raw types.
* So the WSDL references the **global element**:
```xml
element="mr:MeterRegistrationRequest"
```
* instead of the complex type.
### Add meter registration response to xsd
* We **cannot** create the response message yet vecause our XSD only contains:
  * ✅ `MeterRegistrationRequest`
* It **does not** contain:
  * ❌ `MeterRegistrationResponse`
* A WSDL should not invent XML structures, It references elements already defined in the XSD.
* Go back to `meter-registration.xsd` and add a new response type.
* Create:

```xml
<xs:complexType name="MeterRegistrationResponse">
    <xs:sequence>
        <xs:element name="status" type="xs:string"/>
        <xs:element name="message" type="xs:string"/>
        <xs:element name="registrationId" type="xs:string"/>
    </xs:sequence>
</xs:complexType>

<xs:element
    name="MeterRegistrationResponse"
    type="MeterRegistrationResponse"/>
```
* Place it after the `MeterRegistrationRequest` definition and before the closing `</xs:schema>`.

#### Regenerate the JAXB classes
* Remember to cleanup **stale build artifacts** from phase 2 as we had copied the folder
```text
rmdir /s /q build
rmdir /s /q model\build
rmdir /s /q contract\build
rmdir /s /q .gradle
```

```powershell
.\gradlew clean
.\gradlew :model:xjc --rerun-tasks --no-build-cache
```
```text
C:\Git\practice\microservices\enterprise-integration\phase-3>.\gradlew :model:xjc --rerun-tasks
Starting a Gradle Daemon (subsequent builds will be faster)
Reusing configuration cache.

> Task :model:xjc
integration\enterprise\meter_registration\v1\MeterRegistrationRequest.java
integration\enterprise\meter_registration\v1\ObjectFactory.java
integration\enterprise\meter_registration\v1\RelayState.java
integration\enterprise\meter_registration\v1\package-info.java

BUILD SUCCESSFUL in 16s
1 actionable task: 1 executed
Configuration cache entry reused.
C:\Git\practice\microservices\enterprise-integration\phase-3>
```
#### Verification
```text
MeterRegistrationRequest.java
MeterRegistrationResponse.java
ObjectFactory.java
RelayState.java
package-info.java
```
### 
Perfect! 🎉

This is exactly why I prefer **verifying before changing architecture**.

The problem was **not**:

* ❌ Your XSD
* ❌ JAXB
* ❌ The XJC plugin

It was **stale generated artifacts/build state** carried over when you copied `phase-2` to `phase-3`.

### Lesson learned

When you copy a Gradle project to create a new phase, **never copy**:

```text
.gradle/
build/
**/build/
.idea/   (optional, but I usually don't)
```

Only keep:

```text
src/
gradle/
gradlew
gradlew.bat
settings.gradle
build.gradle
gradle.properties
libs.versions.toml
```

A fresh `gradlew clean` is good practice after copying, but deleting the old `build` and `.gradle` directories removes any doubt.

---

## Add the response message to wsdl
```xml
<wsdl:message name="MeterRegistrationResponseMessage">
    <wsdl:part
        name="response"
        element="mr:MeterRegistrationResponse"/>
</wsdl:message>
```
* fter that, your WSDL will contain:
```text
definitions
├── types
├── MeterRegistrationRequestMessage
└── MeterRegistrationResponseMessage
```
### Define the service contract
* The **`portType`** answers the question: 
> **What operation does this service provide?**
* Add these two `wsdl:message` definitions:
```xml
<wsdl:portType name="MeterRegistrationPortType">

    <wsdl:operation name="registerMeter">

        <wsdl:input
                message="tns:MeterRegistrationRequestMessage"/>

        <wsdl:output
                message="tns:MeterRegistrationResponseMessage"/>

    </wsdl:operation>

</wsdl:portType>
```
#### What this means
* Think of the WSDL as layers:
```text
XSD
│
├── MeterRegistrationRequest
└── MeterRegistrationResponse

        │
        ▼

Messages
├── MeterRegistrationRequestMessage
└── MeterRegistrationResponseMessage

        │
        ▼

Operation
registerMeter(request) → response
```

* The `portType` is essentially the **Java interface** of a SOAP service.
* If this were Java, it would resemble:
```java
MeterRegistrationResponse registerMeter(
    MeterRegistrationRequest request);
```
* We're defining that interface in XML instead of Java.
#### What we've built so far
* Notice that **nothing here says SOAP yet**.
* This is an **abstract service contract**:
  * Data ✔
  * Messages ✔
  * Operations ✔
  * Protocol ❌

```text
WSDL
├── types
│   └── imports XSD
│
├── message
│   └── MeterRegistrationRequestMessage
│
├── message
│   └── MeterRegistrationResponseMessage
│
└── portType
    └── registerMeter()
```
### Add the SOAP binding
* This layer is what makes it a **SOAP** service.
* Add this below `</wsdl:portType>`:

```xml
<wsdl:binding
        name="MeterRegistrationBinding"
        type="tns:MeterRegistrationPortType">

    <soap:binding
            style="document"
            transport="http://schemas.xmlsoap.org/soap/http"/>

    <wsdl:operation name="registerMeter">

        <soap:operation
                soapAction="registerMeter"/>

        <wsdl:input>
            <soap:body use="literal"/>
        </wsdl:input>

        <wsdl:output>
            <soap:body use="literal"/>
        </wsdl:output>

    </wsdl:operation>

</wsdl:binding>
```
#### Why `style="document"`?
* There are two SOAP styles:
  * **RPC** (legacy, rarely used today)
  * **Document** (the modern standard)
* We're using **document/literal**, which is the industry standard and what Apache CXF expects by default.
### Add the service endpoint
* Add this **below** the `<wsdl:binding>`:
```xml
<wsdl:service name="MeterRegistrationService">

    <wsdl:port
            name="MeterRegistrationPort"
            binding="tns:MeterRegistrationBinding">

        <soap:address
                location="http://localhost:8080/services/meter-registration"/>

    </wsdl:port>

</wsdl:service>
```

* Our WSDL is now complete.
## What each section does
* **XSD** = Java classes (DTOs)
* **message** = Method parameters/return values
* **portType** = Java interface
* **binding** = HTTP + SOAP implementation details
* **service** = Deployment URL
```text
definitions
│
├── types
│     Defines the XML data (imports XSD)
│
├── messages
│     Wrap the request and response elements
│
├── portType
│     Defines the service operations (interface)
│
├── binding
│     Says "this interface uses SOAP document/literal"
│
└── service
      Specifies where the service is available
```
## Verification
* What you've built from scratch

```text
meter-registration.xsd
        │
        ▼
Defines XML data types
        │
        ▼
meter-registration.wsdl
├── types
├── messages
├── portType
├── binding
└── service
        │
        ▼
Complete SOAP contract
```
### Verification checklist
* Make sure your WSDL has all six sections:
  * ✅ `<wsdl:definitions>`
  * ✅ `<wsdl:types>`
  * ✅ Two `<wsdl:message>` definitions
  * ✅ `<wsdl:portType>`
  * ✅ `<wsdl:binding>`
  * ✅ `<wsdl:service>`

* At that point, the flow will look like:
```text
Client
   │
SOAP XML
   │
Apache CXF
   │
JAXB
   │
Generated Java classes
   │
Your service implementation
```
* That's the bridge between the contract you've just created and a working SOAP service.
## IMprovements 
### Using a URI for soapaction
- Instead of `soapAction` as this:
```xml
<soap:operation soapAction="registerMeter"/>
```
- I recommend using a URI:
```xml
<soap:operation
    soapAction="http://enterprise.integration/meter-registration/v1/registerMeter"/>
```
- This avoids ambiguity and is common in enterprise SOAP services.
### Service endpoint
- This is fine for development:
```xml
http://localhost:8080/services/meter-registration
```
- Later, CXF will actually publish the endpoint there.
- Your contract now looks like this:

```text
meter-registration.wsdl
│
├── Types
│     └── Imports meter-registration.xsd
│
├── Messages
│     ├── MeterRegistrationRequestMessage
│     └── MeterRegistrationResponseMessage
│
├── PortType
│     └── registerMeter()
│
├── Binding
│     └── SOAP Document/Literal
│
└── Service
      └── http://localhost:8080/services/meter-registration
```
## Validating wsdl
* Validate this WSDL with a proper parser before we start CXF.
* Reason:
  * A text editor only checks XML syntax.
  * A WSDL validator checks:
    * imported XSD
    * message references
    * portType references
    * binding references
    * service references
* If it passes validation, we know the contract is sound before generating Java code.
* This mirrors how enterprise integration teams typically work:
```text
XSD
    ↓
WSDL
    ↓
Validate WSDL
    ↓
Generate Java (CXF)
    ↓
Implement Service
    ↓
Test SOAP
```

## [Apache CXF](https://github.com/sbhrwl/microservices/blob/main/enterprise-integration/phase-4/README.md)
- Instead of hand written wsdl, use Apache CXF 
  - Validate this WSDL.
  - Generate Java interfaces and JAXB classes.
  - Implement the service.
  - Expose the endpoint.
- That's how enterprise teams typically work. Hand-writing one WSDL is valuable because it teaches the contract. Hand-writing dozens is just repetitive.
