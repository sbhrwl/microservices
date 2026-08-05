# Content-based routing
- [Content-based routing](#content-based-routing)
    - [Introduction](#introduction)
    - [Create two processors](#create-two-processors)
- [Checkpoint](#checkpoint)
- [Test](#test)
    - [Request 1](#request-1)
    - [Request 2](#request-2)
## Introduction
- The route decides **where to send a message based on its content**.
  - Move on to Content-based routing (`choice()`, `when()`, `otherwise()`) 
- Imagine a real meter registration system:

```text
Relay State = ON
        │
        ├── Activate relay
        │
Relay State = OFF
        │
        └── Deactivate relay
```

- Instead of writing inside your service

```java
if (...) {
   ...
} else {
   ...
}
```

- Camel does it declaratively
## Create two processors
- Create [`RelayOnProcessor.java`](meter-registration-service/integration/src/main/java/enterprise/processor/RelayOnProcessor.java)
- Create [`RelayOffProcessor.java`](meter-registration-service/integration/src/main/java/enterprise/processor/RelayOffProcessor.java)
- Inject them to the route
- Add Choice
```java
from("direct:registerMeter")
        .routeId("meter-registration-route")
        .log("Camel received request for GSRN=${body.gsrn}")
        .process(validationProcessor)
        .process(enrichmentProcessor)
        .choice()
        .when(simple("${body.relayState} == 'ON'"))
        .process(relayOnProcessor)
        .when(simple("${body.relayState} == 'OFF'"))
        .process(relayOffProcessor)
        .otherwise()
        .log("Unknown relay state")
        .end()
        .bean(processor, "register")
        .log("Camel completed registration. Response=${body.status}");
}
```
## Chekcpoint
* Route now looks like this:

```text
SOAP Request
      │
      ▼
Validation
      │
      ▼
Enrichment
      │
      ▼
       choice()
      /        \
Relay ON     Relay OFF
      \        /
       ▼
Business Service
       ▼
SOAP Response
```
## Test
### Request 1
```xml
<relayState>ON</relayState>
```
Expected log:

```text
Relay ON requested for MS-123456
```

### Request 2
```xml
<relayState>OFF</relayState>
```

Expected log:

```text
Relay OFF requested for MS-123456
```