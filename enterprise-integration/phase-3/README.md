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