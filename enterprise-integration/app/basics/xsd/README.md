# XSD
- [XSD files](#xsd-files)
- [Flow](#flow)
## XSD files
- `C:\Git\gfc-app\data-hub-api\src\main\resources\masterdata\F35_LoadControlMessage.xsd`
  - `C:\Git\gfc-app\data-hub-api\src\main\resources\masterdata\elements\F35_LoadControlMessage_ElementTypes.xsd`
  - `C:\Git\gfc-app\data-hub-api\src\main\resources\common\elements\PEC_ProcessEnergyContext_ElementTypes.xsd`
  - `C:\Git\gfc-app\data-hub-api\src\main\resources\common\elements\HDR_Header_ElementTypes.xsd`
  - `C:\Git\gfc-app\data-hub-api\src\main\resources\common\DataTypes.xsd`
- JAXB/XJC follows the `xs:include` and `xs:import` directives automatically, as long as all referenced XSD files are available at the specified locations.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:rsm="urn:fi:Datahub:mif:masterdata:F35_LoadControlMessage:v1"
  xmlns:hdr="urn:fi:Datahub:mif:common:HDR_Header:elements:v1"
  xmlns:pec="urn:fi:Datahub:mif:common:PEC_ProcessEnergyContext:elements:v1"
  xmlns:elm="urn:fi:Datahub:mif:masterdata:F35_LoadControlMessage:elements:v1"
  xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"
  targetNamespace="urn:fi:Datahub:mif:masterdata:F35_LoadControlMessage:v1"
  elementFormDefault="qualified"
  attributeFormDefault="unqualified"
  version="2.7.0.3">
  <xs:import
    namespace="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"
    schemaLocation="../common/oasis-200401-wss-wssecurity-utility-1.0.xsd"/>
  <xs:import
    namespace="urn:fi:Datahub:mif:masterdata:F35_LoadControlMessage:elements:v1"
    schemaLocation="elements/F35_LoadControlMessage_ElementTypes.xsd"/>
  <xs:import
    namespace="urn:fi:Datahub:mif:common:HDR_Header:elements:v1"
    schemaLocation="../common/elements/HDR_Header_ElementTypes.xsd"/>
  <xs:import
    namespace="urn:fi:Datahub:mif:common:PEC_ProcessEnergyContext:elements:v1"
    schemaLocation="../common/elements/PEC_ProcessEnergyContext_ElementTypes.xsd"/>
  <xs:element
    name="LoadControlMessageMessage"
    type="rsm:LoadControlMessageMessage_Type"/>
  <xs:complexType name="LoadControlMessageMessage_Type">
    <xs:sequence>
      <xs:element
        name="LoadControlMessage"
        type="rsm:LoadControlMessage_Type"
        minOccurs="1"
        maxOccurs="1"/>
    </xs:sequence>
    <xs:attribute
      ref="wsu:Id"
      use="optional"/>
  </xs:complexType>
  <xs:complexType name="LoadControlMessage_Type">
    <xs:sequence>
      <xs:element
        name="Header"
        type="hdr:Header_Type"
        minOccurs="1"
        maxOccurs="1"/>
      <xs:element
        name="ProcessEnergyContext"
        type="pec:ProcessEnergyContext_Type"
        minOccurs="1"
        maxOccurs="1"/>
      <xs:element
        name="Transaction"
        type="elm:LoadControlMessage_Type"
        minOccurs="1"
        maxOccurs="1"/>
    </xs:sequence>
  </xs:complexType>
</xs:schema>
```
## Flow
```text
sendMessage()
    │
    ▼
<SendMessageRequest>
    │
    ▼
<MessageContainer>
    │
    ▼
<Payload>
    │
    ▼
<LoadControlMessageMessage>
    │
    ▼
<LoadControlMessage>
    ├── <Header>
    │       └── ... → simple types (DataTypes.xsd)
    │
    ├── <ProcessEnergyContext>
    │       ├── <EnergyBusinessProcess>
    │       │       └── EnergyBusinessProcess_S10_Type
    │       │               └── xsd:string → String
    │       ├── <EnergyBusinessProcessRole>
    │       │       └── EnergyBusinessProcessRole_S3_Type
    │       │               └── xsd:string → String
    │       └── <EnergyIndustryClassification>
    │               └── EnergyIndustryClassification_S3_Type
    │                       └── xsd:string → String
    │
    └── <Transaction>
            ├── <PartyIdentification>
            │       └── PartyIdentification_S13_9_Required_Type
            │               └── xsd:string → String
            ├── <MeteringPointUsedDomainLocation>
            │       └── <Identification>
            │               └── Identification_S90_9_Required_Type
            │                       └── xsd:string → String
            └── <EndDeviceControl>
                    ├── <Identification> → xsd:string
                    ├── <RelayIdentification> → xsd:string
                    ├── <ExecutionTimeStamp> → xsd:dateTime
                    ├── <Description> → xsd:string
                    ├── <EndDeviceControlType> → ...
                    ├── <DeviceTiming>* → ...
                    ├── <CalendarDay>* → ...
                    └── <ControlDetails> → ...
```
