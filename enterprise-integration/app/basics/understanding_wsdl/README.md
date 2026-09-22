# Understanding WSDL
- [Layers](#layers)
- [Layer 1. Types (business objects)](#layer-1-types-business-objects)
- [Layer 2. Global elements](#layer-2-global-elements)
- [Layer 3. WSDL messages](#layer-3-wsdl-messages)
- [Layer 4. PortType (service interface)](#layer-4-porttype-service-interface)
- [Layer 5. Binding](#layer-5-binding)
- [Layer 6. Service](#layer-6-service)
- [Business pattern](#business-pattern)
- [Request/Response pattern](#requestresponse-pattern)
- [Internal Data Flow](#internal-data-flow)
- [Overall naming pattern](#overall-naming-pattern)
- [Tracing SendMessageRequest](#tracing-sendmessagerequest)
- [Bindings](#bindings)
## Layers
- `C:\Git\gfc-app\data-hub-api\src\main\resources\wsdl\Messages.wsdl`
- `C:\Git\gfc-app\flex-hub-connector\src\test\resources\F35_LoadControlMessage-DH-1223-2.xml`
- `C:\Git\gfc-app\data-hub-environments\ven02\peek-message.xml`
| WSDL Layer | Purpose | Example from this WSDL |
| --- | --- | --- |
| 1. Types | Defines XML schema (data model) | `SendMessageRequest_Type`, `MessageContainer_Type` |
| 2. Elements | Public XML root elements | `SendMessageRequest`, `SendMessageResponse` |
| 3. Messages | Wrap XML elements into WSDL messages | `<wsdl:message name="SendMessageRequest">` |
| 4. PortType | Defines service operations (interface) | `sendMessage()`, `peekMessage()` |
| 5. Binding | Maps interface to SOAP | SOAP Document/Literal |
| 6. Service | Actual endpoint URL | `https://localhost:1234/` |
## Layer 1. Types (business objects)
- Think of these as Java POJOs.
| XML Type | Similar Java Class | Used By |
| --- | --- | --- |
| `MessageContainer_Type` | MessageContainer | Send/Process Request |
| `ResponseMessageContainer_Type` | ResponseMessageContainer | Process/Peek Response |
| `Payload_Type` | Payload | Request |
| `ResponsePayload_Type` | ResponsePayload | Response |
| `SendMessageRequest_Type` | SendMessageRequest | `sendMessage()` |
| `SendMessageResponse_Type` | SendMessageResponse | `sendMessage()` |
| `ProcessMessageRequest_Type` | ProcessMessageRequest | `processMessage()` |
| `ProcessMessageResponse_Type` | ProcessMessageResponse | `processMessage()` |
| `PeekMessageRequest_Type` | PeekMessageRequest | `peekMessage()` |
| `PeekMessageResponse_Type` | PeekMessageResponse | `peekMessage()` |
| `DequeueMessageRequest_Type` | DequeueMessageRequest | `dequeueMessage()` |
| `DequeueMessageResponse_Type` | DequeueMessageResponse | `dequeueMessage()` |
| `CMSFault_Type` | CMSFault | Fault |
## Layer 2. Global elements
- Every operation exposes exactly one XML root element.
- Pattern:
```text
ComplexType
    ↓
Global Element
```
| Element | Uses Type |
| --- | --- |
| `SendMessageRequest` | `SendMessageRequest_Type` |
| `SendMessageResponse` | `SendMessageResponse_Type` |
| `ProcessMessageRequest` | `ProcessMessageRequest_Type` |
| `ProcessMessageResponse` | `ProcessMessageResponse_Type` |
| `PeekMessageRequest` | `PeekMessageRequest_Type` |
| `PeekMessageResponse` | `PeekMessageResponse_Type` |
| `DequeueMessageRequest` | `DequeueMessageRequest_Type` |
| `DequeueMessageResponse` | `DequeueMessageResponse_Type` |
| `CMSFault` | `CMSFault_Type` |
- Notice the naming convention:
```text
<TypeName>_Type
     ↓
<TypeName>
```
## Layer 3. WSDL messages
- A WSDL Message is simply a wrapper around one XML element.
- Pattern:
```text
Element
    ↓
WSDL Message
```
- Example
| WSDL Message | Contains |
| --- | --- |
| `SendMessageRequest` | element=`SendMessageRequest` |
| `SendMessageResponse` | element=`SendMessageResponse` |
| `ProcessMessageRequest` | `ProcessMessageRequest` |
| `ProcessMessageResponse` | `ProcessMessageResponse` |
| `PeekMessageRequest` | `PeekMessageRequest` |
| `PeekMessageResponse` | `PeekMessageResponse` |
| `DequeueMessageRequest` | `DequeueMessageRequest` |
| `DequeueMessageResponse` | `DequeueMessageResponse` |
| Fault | `CMSFault` |
## Layer 4. PortType (service interface)
- This is basically a Java interface.
- Equivalent Java:
```java
interface MarketMessagingB2BInboundService {
  SendMessageResponse sendMessage(SendMessageRequest);
  ProcessMessageResponse processMessage(ProcessMessageRequest);
  PeekMessageResponse peekMessage(PeekMessageRequest);
  DequeueMessageResponse dequeueMessage(DequeueMessageRequest);
}
```
- In WSDL
| Operation | Request | Response | Fault |
| --- | --- | --- | --- |
| `sendMessage` | `SendMessageRequest` | `SendMessageResponse` | `CMSFault` |
| `processMessage` | `ProcessMessageRequest` | `ProcessMessageResponse` | `CMSFault` |
| `peekMessage` | `PeekMessageRequest` | `PeekMessageResponse` | `CMSFault` |
| `dequeueMessage` | `DequeueMessageRequest` | `DequeueMessageResponse` | `CMSFault` |
- Notice every operation has exactly:
  - one request
  - one response
  - one fault
- Classic document/literal wrapped style.
## Layer 5. Binding
- Binding says:
- "Expose this interface using SOAP."
- Pattern:
```text
PortType
    ↓
SOAP Binding
```
- Every operation repeats the same structure.
| Operation | SOAP Action |
| --- | --- |
| `sendMessage` | `sendMessage` |
| `processMessage` | `processMessage` |
| `peekMessage` | `peekMessage` |
| `dequeueMessage` | `dequeueMessage` |
- All use
  - `style=document`
  - `use=literal`
- This is the modern SOAP best practice.
## Layer 6. Service
- Finally the service exposes one endpoint.
| Service | Port | Endpoint |
| --- | --- | --- |
| `marketMessagingB2BInboundServiceV01` | `marketMessagingB2BInboundServiceV01HTTPEndpoint` | `https://localhost:1234/soap/FGR?organisationUser=MyOrganisationB2BUser` |
```xml
<wsdl:service name="marketMessagingB2BInboundServiceV01">
  <wsdl:port
    name="marketMessagingB2BInboundServiceV01HTTPEndpoint"
    binding="tns:marketMessagingB2BInboundServiceV01HTTPEndpointBinding">
    <soap:address
      location="https://localhost:1234/soap/FGR?organisationUser=MyOrganisationB2BUser"/>
  </wsdl:port>
</wsdl:service>
```
| XML Element | Purpose | Think of it as |
| --- | --- | --- |
| `wsdl:service` | Groups one or more endpoints for the service | Service container |
| `wsdl:port` | Defines one endpoint using a specific binding | Endpoint definition |
| `soap:address` | Specifies the actual URL where the service is available | Physical address |
```text
Service
│
├── Name
│     marketMessagingB2BInboundServiceV01
│
└── Port
      │
      ├── Name
      │     marketMessagingB2BInboundServiceV01HTTPEndpoint
      │
      ├── Binding
      │     marketMessagingB2BInboundServiceV01HTTPEndpointBinding
      │
      └── Address
            https://localhost:1234/soap/FGR?organisationUser=MyOrganisationB2BUser
```
| WSDL | Real-world analogy |
| --- | --- |
| service | Company |
| port | Reception desk |
| binding | Language/protocol spoken at the reception (SOAP 1.2, HTTP, etc.) |
| soap:address | Street address of the office |
## Business pattern
- The four operations reveal an asynchronous message queue workflow.
| Operation | Purpose | Input | Output |
| --- | --- | --- | --- |
| `sendMessage` | Submit a new message | Payload | DocumentReferenceNumber |
| `processMessage` | Submit and receive processed response | Payload | Response Payload |
| `peekMessage` | Check for waiting messages | Optional MessageDomains | Message if available |
| `dequeueMessage` | Remove a processed message | DocumentReferenceNumber | Empty response |
- This resembles a message broker API:
```text
Producer
	|
	| sendMessage()
	|
	v
Message Queue
	|
	| peekMessage()
	|
	v
Consumer
	|
	| dequeueMessage()
```
## Request/Response pattern
- All operations follow the same naming convention.
| Request Type | Response Type |
| --- | --- |
| `SendMessageRequest` | `SendMessageResponse` |
| `ProcessMessageRequest` | `ProcessMessageResponse` |
| `PeekMessageRequest` | `PeekMessageResponse` |
| `DequeueMessageRequest` | `DequeueMessageResponse` |
- This symmetry makes the API predictable.
## Internal Data Flow
```text
Client
   │
   ▼
Request Element
   │
   ▼
Complex Type
   │
   ▼
WSDL Message
   │
   ▼
PortType Operation
   │
   ▼
SOAP Binding
   │
   ▼
SOAP Endpoint
```
## Overall naming pattern
- The WSDL consistently follows this hierarchy:
| Layer | Naming Pattern | Example |
| --- | --- | --- |
| XML Schema Type | `<Operation>Name_Type` | `SendMessageRequest_Type` |
| XML Element | `<Operation>Name` | `SendMessageRequest` |
| WSDL Message | `<Operation>Name` | `SendMessageRequest` |
| PortType Operation | camelCase verb | `sendMessage` |
| SOAP Action | Same as operation | `sendMessage` |
| Java Method (generated by Apache CXF) | Same as operation | `sendMessage()` |
- This is a textbook WSDL-first, document/literal wrapped contract. It's highly regular: once you understand one operation (`sendMessage`), the other three follow the exact same structure, differing only in the request and response payloads. This consistency is one reason enterprise frameworks like Apache CXF can generate clean Java interfaces and JAXB classes directly from the WSDL.
## Tracing SendMessageRequest
```text
sendMessage()
    │
    ▼
<SendMessageRequest>          (xs:element)
│
└── <MessageContainer>        (xs:element)
      │
      └── <Payload>           (xs:element)
            │
            └── <xs:any/>     (any XML document)
```
```xml
<xs:complexType name="Payload_Type">
  <xs:sequence>
    <xs:any processContents="skip" namespace="##any"/>
  </xs:sequence>
</xs:complexType>
```
- Mapping to the schema types:
| XML Tag | Schema Type |
| --- | --- |
| `<SendMessageRequest>` | `SendMessageRequest_Type` |
| `<MessageContainer>` | `MessageContainer_Type` |
| `<Payload>` | `Payload_Type` |
| `<xs:any/>` | Any valid XML |
- This means the SOAP body for `sendMessage` starts like:
```xml
<SendMessageRequest>
  <MessageContainer>
    <Payload>
      <!-- Any XML goes here -->
    </Payload>
  </MessageContainer>
</SendMessageRequest>
```
- Everything inside `<Payload>` is not defined by this WSDL.
- It is supplied by another XML schema or business document.
## Bindings
- `bindings.xjc`
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<jaxb:bindings
  xmlns:jaxb="https://jakarta.ee/xml/ns/jaxb" xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:xjc="https://jakarta.ee/xml/ns/jaxb/xjc"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="https://jakarta.ee/xml/ns/jaxb https://jakarta.ee/xml/ns/jaxb/bindingschema_3_0.xsd"
  jaxb:extensionBindingPrefixes="xjc"
  version="3.0">
  <jaxb:globalBindings>
    <jaxb:javaType
      name="java.time.ZonedDateTime"
      xmlType="xs:dateTime"
      parseMethod="com.landisgyr.gfc.data_hub_api.jaxb.ZonedDateTimeAdapter.unmarshal"
      printMethod="com.landisgyr.gfc.data_hub_api.jaxb.ZonedDateTimeAdapter.marshal" />
    <jaxb:javaType
      name="java.time.LocalDate"
      xmlType="xs:date"
      parseMethod="com.landisgyr.gfc.data_hub_api.jaxb.DateAdapter.unmarshal"
      printMethod="com.landisgyr.gfc.data_hub_api.jaxb.DateAdapter.marshal" />
  </jaxb:globalBindings>
</jaxb:bindings>
```
- The binding file customizes JAXB code generation
- Generate cleaner, modern Java classes without changing the WSDL or XSD
- It tells JAXB/CXF how XML Schema types should be mapped to Java types instead of using the defaults.
| XML Type | Default JAXB Type | Customized Java Type |
| --- | --- | --- |
| `xs:dateTime` | `XMLGregorianCalendar` | `ZonedDateTime` |
| `xs:date` | `XMLGregorianCalendar` | `LocalDate` |
- The adapter methods handle the conversion:
  - `parseMethod` XML → Java (unmarshal)
  - `printMethod` Java → XML (marshal)
- So when `wsdl2java` generates classes, you'll get:
```java
// Default
XMLGregorianCalendar createdDate;
// With binding
ZonedDateTime createdDate;
```
