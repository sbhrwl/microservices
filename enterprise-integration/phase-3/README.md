# SOAP using Apache CXF
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
    targetNamespace="http://enterprise.integration/meter-registration/v1">

</wsdl:definitions>
```
#### Why these namespaces?
* `wsdl` → WSDL elements (`definitions`, `message`, `portType`, ...)
* `soap` → SOAP-specific binding information.
* `xs` → Used when importing XML Schema.
* `tns` (**Target Namespace**) → Refers to **our own** service and types.
