# XML & JAXB
- [Add XSD file](#add-xsd-file)
  - [Define schema](#define-schema)
    - [Add the RelayState enumeration](#add-the-relaystate-enumeration)
    - [Add complexType](#add-complextype)
  - [Create the root element](#create-the-root-element)
    - [Why](#why)
- [Generate Java classes from the XSD using JAXB](#generate-java-classes-from-the-xsd-using-jaxb)
  - [Where to generate](#where-to-generate)
  - [Which tool to use](#which-tool-to-use)
  - [Update model/build.gradle](#update-modelbuildgradle)
    - [Add `jaxb` plugin in `gradle/libs.versions.toml`](#add-jaxb-plugin-in-gradlelibsversionstoml)
    - [Configure `model/build.gradle` to generate classes from the XSD in contract](#configure-modelbuildgradle-to-generate-classes-from-the-xsd-in-contract)
  - [Verify the generated files](#verify-the-generated-files)
- [Understanding `MeterRegistrationRequest.java`](#understanding-meterregistrationrequestjava)
  - [MeterRegistrationRequest.java](model/build/generated/sources/xjc/java/integration/enterprise/meter_registration/v1/MeterRegistrationRequest.java)
  - [meter-registration.xsd](contract/src/main/resources/xsd/meter-registration.xsd)
- [Understanding `RelayState.java`](#understanding-relaystatejava)
- [Understanding `ObjectFactory.java`](#understanding-objectfactoryjava)
- [One more important point on XmlRootElement](#one-more-important-point-on-xmlrootelement)
- [Validation constraints](#validation-constraints)
  - [Constrain the GSRN](#constrain-the-gsrn)
  - [Constrain the MeterSerialNumber](#constrain-the-meterserialnumber)
- [Git commit](#git-commit)
## Add XSD file
* Create meter-registration.xsd as `contract\src\main\resources\xsd\meter-registration.xsd`
### Define schema
* Start with the XML declaration and the root `<xs:schema>` element only.
  * `xmlns:xs` → We use the XML Schema language (`xs:string`, `xs:int`, etc.).
  * `targetNamespace` → The unique identifier for **our** schema. SOAP uses this heavily.
  * `xmlns` → Makes all elements belong to our namespace by default.
  * `elementFormDefault="qualified"` → Every XML element must include the namespace. This is the standard approach for SOAP services. 
```xml
<?xml version="1.0" encoding="UTF-8"?>

<xs:schema
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        targetNamespace="http://enterprise.integration/meter-registration/v1"
        xmlns="http://enterprise.integration/meter-registration/v1"
        elementFormDefault="qualified">

</xs:schema>
```

#### Add the RelayState enumeration 
* Add the RelayState enumeration **inside** `<xs:schema>`:
```xml
<xs:simpleType name="RelayState">
    <xs:restriction base="xs:string">
        <xs:enumeration value="ON"/>
        <xs:enumeration value="OFF"/>
    </xs:restriction>
</xs:simpleType>
```
* Tiny XML rule: 
  * Every type (`simpleType`, `complexType`, `element`) belongs inside the `<xs:schema>` root element. 
  * This pattern will continue throughout the XSD.
#### Add complexType 
  * `xs:sequence` means the XML elements must appear in this exact order.
    * If `relayState` appeared before `relayNumber`, XML validation would **fail**. 
```xml
<xs:complexType name="MeterRegistrationRequest">
    <xs:sequence>
        <xs:element name="gsrn" type="xs:string"/>
        <xs:element name="meterSerialNumber" type="xs:string"/>
        <xs:element name="customerId" type="xs:string"/>
        <xs:element name="relayNumber" type="xs:int"/>
        <xs:element name="relayState" type="RelayState"/>
        <xs:element name="timestamp" type="xs:dateTime"/>
    </xs:sequence>
</xs:complexType>
```
### Create the root element
* Add this **below** the `MeterRegistrationRequest` type:
```xml
<xs:element
    name="MeterRegistrationRequest"
    type="MeterRegistrationRequest"/>
```
#### Why?
* There's an important distinction:
  * `xs:complexType` = defines the **structure** (like a Java class).
  * `xs:element` = defines the **actual XML root element**.
* Think of it like:
```java
class MeterRegistrationRequest { ... }   // complexType

new MeterRegistrationRequest();          // element uses the type
```

Without the root `xs:element`, JAXB and Apache CXF won't know what the XML document's root element should be.

Reply **"done"**, and then we'll validate the XSD before generating Java classes with JAXB.

```xml
<xs:element
    name="MeterRegistrationRequest"
    type="MeterRegistrationRequest"/>
```
## Generate Java classes from the XSD using JAXB
### Where to generate
* Keep `contract` as `XSD/WSDL` only, and generate the JAXB classes into the `model` module
  * `contract` = external contracts only.
  * `model` = generated Java objects.
  * Cleaner separation, especially for larger enterprise systems
### Which tool to use 
* Gradle `XJC` plugin
  * `gradle generateJaxb`
  * Automatically generates classes during the build.
  * Best for CI/CD and production projects.
### Update model/build.gradle
#### Add `jaxb` plugin in `gradle\libs.versions.toml`
```
[versions]
jaxb = "4.0.5"
jaxbPlugin = "1.9.1"

[libraries]
jaxb-api = { module = "jakarta.xml.bind:jakarta.xml.bind-api", version.ref = "jaxb" }
jaxb-runtime = { module = "org.glassfish.jaxb:jaxb-runtime", version.ref = "jaxb" }

[plugins]
jaxb = { id = "com.github.bjornvester.xjc", version.ref = "jaxbPlugin" }
```
#### Configure `model/build.gradle` to generate classes from the XSD in contract
```xml
plugins {
    id 'java-library'
    alias(libs.plugins.jaxb)
}
```
* Run `.\gradlew :model:tasks`
```text
C:\Git\practice\microservices\enterprise-integration\phase-2>.\gradlew :model:tasks
Calculating task graph as no cached configuration is available for tasks: :model:tasks

> Task :model:tasks

------------------------------------------------------------
Tasks runnable from project ':model'
------------------------------------------------------------

Build tasks
-----------
assemble - Assembles the outputs of this project.
build - Assembles and tests this project.
buildDependents - Assembles and tests this project and all projects that depend on it.
buildNeeded - Assembles and tests this project and all projects it depends on.
classes - Assembles main classes.
clean - Deletes the build directory.
jar - Assembles a jar archive containing the classes of the 'main' feature.
testClasses - Assembles test classes.
xjc - Generates Java classes from XSD files.

Documentation tasks
-------------------
javadoc - Generates Javadoc API documentation for the 'main' feature.

Help tasks
----------
artifactTransforms - Displays the Artifact Transforms that can be executed in project ':model'.
buildEnvironment - Displays all buildscript dependencies declared in project ':model'.
dependencies - Displays all dependencies declared in project ':model'.
dependencyInsight - Displays the insight into a specific dependency in project ':model'.
help - Displays a help message.
javaToolchains - Displays the detected java toolchains.
outgoingVariants - Displays the outgoing variants of project ':model'.
projects - Displays the sub-projects of project ':model'.
properties - Displays the properties of project ':model'.
resolvableConfigurations - Displays the configurations that can be resolved in project ':model'.
tasks - Displays the tasks runnable from project ':model'.

Verification tasks
------------------
check - Runs all checks.
test - Runs the test suite.

Rules
-----
Pattern: clean<TaskName>: Cleans the output files of a task.
Pattern: build<ConfigurationName>: Assembles the artifacts of a configuration.

To see all tasks and more detail, run gradlew tasks --all

To see more detail about a task, run gradlew help --task <task>

BUILD SUCCESSFUL in 4s
1 actionable task: 1 executed
Configuration cache entry stored.
C:\Git\practice\microservices\enterprise-integration\phase-2>
```
* Tell the plugin where the XSD is
```
plugins {
    id 'java-library'
    alias(libs.plugins.jaxb)
}

xjc {
    xsdDir.set(file("../contract/src/main/resources/xsd"))
}
```
* Run `.\gradlew :model:xjc`
```text
C:\Git\practice\microservices\enterprise-integration\phase-2>.\gradlew :model:xjc
Calculating task graph as no cached configuration is available for tasks: :model:xjc

> Task :model:xjc
integration\enterprise\meter_registration\v1\MeterRegistrationRequest.java
integration\enterprise\meter_registration\v1\ObjectFactory.java
integration\enterprise\meter_registration\v1\RelayState.java
integration\enterprise\meter_registration\v1\package-info.java

BUILD SUCCESSFUL in 15s
1 actionable task: 1 executed
Configuration cache entry stored.
C:\Git\practice\microservices\enterprise-integration\phase-2>
```
* **First real enterprise milestone**.
  * We have just done something that happens in many production SOAP systems:

```text
XSD
   │
   ▼
XJC (JAXB)
   │
   ▼
Generated Java classes
```

* No one wrote **`MeterRegistrationRequest.java`** by hand.
### Verify the generated files
```text
model
├── build
│   ├── generated
│   │   └── sources
│   │       ├── xjc
│   │       │   └── java
│   │       │       └── integration
│   │       │           └── enterprise
│   │       │               └── meter_registration
│   │       │                   └── v1
│   │       │                       ├── MeterRegistrationRequest.java
│   │       │                       ├── ObjectFactory.java
│   │       │                       ├── package-info.java
│   │       │                       └── RelayState.java
│   │       └── resources
│   ├── libs
│   │   └── model-1.0.0-SNAPSHOT.jar
│   └── tmp
│       └── jar
│           └── MANIFEST.MF
├── src
│   └── main
│       └── java
├── build.gradle
└── persistence
```
## Understanding `MeterRegistrationRequest.java`
* Concepts to understand 
  * Why the JAXB annotations are there.
  * How each XSD element became a Java field.
  * Why `RelayState` became a Java `enum`.
* This understanding is essential before we move to **Apache CXF** in Phase 3.
* Observation 1: **JAXB annotations**
  * JAXB serializes the **fields**, not the getters/setters. 
  * This is the most common approach in enterprise SOAP applications.
```java
@XmlAccessorType(XmlAccessType.FIELD)
```
* Observation 2:  **Element order**
  * This comes directly from your XSD's `xs:sequence`. If the order changes, the generated XML changes.
```java
@XmlType(propOrder = {
    "gsrn",
    "meterSerialNumber",
    ...
})
```
* Observation 3:  **Interesting mapping**
  * Many developers expect `LocalDateTime`, but XSD `xs:dateTime` maps to `XMLGregorianCalendar` by default. Later, we'll discuss how to customize this if desired.
```java
XMLGregorianCalendar timestamp;
```
## Understanding `RelayState.java`
```java
public enum RelayState {
    ON,
    OFF
}
```
* This enum came from this XSD:
```xml
<xs:simpleType name="RelayState">
    ...
</xs:simpleType>
```
## Understanding `ObjectFactory.java`
Excellent. This is one of the most important JAXB concepts.

### Why does `ObjectFactory` exist?
* When JAXB needs to create the **root XML element**, it doesn't just need the Java object. 
* It also needs:
  * Element name
  * Namespace
  * Java type
* That's why you see:

```java
private static final QName _MeterRegistrationRequest_QNAME =
    new QName(
        "http://enterprise.integration/meter-registration/v1",
        "MeterRegistrationRequest");
```
* That `QName` is the XML identity of your root element.
* Then JAXB wraps your object:
```java
JAXBElement<MeterRegistrationRequest>
```
* instead of simply using:
```java
MeterRegistrationRequest
```
* because XML has the concept of **elements**, while Java has **objects**.
### One more important point on XmlRootElement
* You'll notice your generated class **does not** have:
```java
@XmlRootElement
```

* Instead, JAXB generated:
  * `ObjectFactory`
  * `JAXBElement`
* This is perfectly valid and happens because your XSD defines a global element separately from the type.
* Later, when we introduce **Apache CXF**, CXF will `use these generated classes automatically`.

Before we move to **Phase 3 (Apache CXF)**, there's one thing I want to add to the schema:
## Validation constraints
* Right now `gsrn` accepts **any** string.
```xml
<xs:element name="gsrn" type="xs:string"/>
```
* In a production system we'd define rules such as:
  * GSRN must be exactly 18 digits.
  * Meter serial number has a maximum length.
  * Customer ID has a pattern.
  * Relay number has a valid range (for example 1 to 4).
* This is where XSD starts enforcing business rules before your Java code even runs.

### Constrain the GSRN
* Instead of:
```xml
<xs:element name="gsrn" type="xs:string"/>
```
* We'll introduce a reusable type
* Add this **above** `MeterRegistrationRequest`:

```xml
<xs:simpleType name="GSRN">
    <xs:restriction base="xs:string">
        <xs:pattern value="[0-9]{18}"/>
    </xs:restriction>
</xs:simpleType>
```
* Then change:
```xml
<xs:element name="gsrn" type="xs:string"/>
```
  * to:
```xml
<xs:element name="gsrn" type="GSRN"/>
```
* Reusability
  * Create `reusable business types`, not just reusable elements. 
  * if tomorrow another request or response contains a GSRN, we reuse the same type:
```xml
<xs:element name="sourceGsrn" type="GSRN"/>
<xs:element name="destinationGsrn" type="GSRN"/>
```
  * instead of duplicating the validation rule.
* Regenerate the JAXB classes:
  * Force regeneration
```powershell
.\gradlew :model:clean :model:xjc
```
```text
C:\Git\practice\microservices\enterprise-integration\phase-2>.\gradlew :model:clean :model:xjc
Calculating task graph as no cached configuration is available for tasks: :model:clean :model:xjc

BUILD SUCCESSFUL in 1s
2 actionable tasks: 1 executed, 1 from cache
Configuration cache entry stored.
C:\Git\practice\microservices\enterprise-integration\phase-2>

```
* Notice these two things:
```
<element name="gsrn" type="GSRN"/>
```
* became
```
protected String gsrn;
```
### Constrain the MeterSerialNumber
* Add
```xml
<xs:simpleType name="MeterSerialNumber">
    <xs:restriction base="xs:string">
        <xs:minLength value="5"/>
        <xs:maxLength value="30"/>
    </xs:restriction>
</xs:simpleType>
```
* Change:
```xml
<xs:element name="meterSerialNumber" type="xs:string"/>
```
to:
```xml
<xs:element name="meterSerialNumber" type="MeterSerialNumber"/>
```
* Run `.\gradlew :model:xjc --rerun-tasks`
```text
C:\Git\practice\microservices\enterprise-integration\phase-2>.\gradlew :model:xjc --rerun-tasks
Reusing configuration cache.

> Task :model:xjc
integration\enterprise\meter_registration\v1\MeterRegistrationRequest.java
integration\enterprise\meter_registration\v1\ObjectFactory.java
integration\enterprise\meter_registration\v1\RelayState.java
integration\enterprise\meter_registration\v1\package-info.java

BUILD SUCCESSFUL in 4s
1 actionable task: 1 executed
Configuration cache entry reused.
C:\Git\practice\microservices\enterprise-integration\phase-2>
```
## Git commit
```git
git add .
git commit -m "Phase 2: XSD to JAXB"
git push -u origin main
```