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
2026-08-05T12:47:43.517+03:00  INFO 13296 --- [nio-8080-exec-2] i.e.s.MeterRegistrationServiceImpl       : ========== SOAP REQUEST RECEIVED ==========
2026-08-05T12:47:43.518+03:00  INFO 13296 --- [nio-8080-exec-2] i.e.s.MeterRegistrationServiceImpl       : GSRN              : 735999123456789011
2026-08-05T12:47:43.518+03:00  INFO 13296 --- [nio-8080-exec-2] i.e.s.MeterRegistrationServiceImpl       : Meter Serial      : MS-123456
2026-08-05T12:47:43.518+03:00  INFO 13296 --- [nio-8080-exec-2] i.e.s.MeterRegistrationServiceImpl       : Customer ID       : CUST-1001
2026-08-05T12:47:43.518+03:00  INFO 13296 --- [nio-8080-exec-2] i.e.s.MeterRegistrationServiceImpl       : Relay Number      : 1
2026-08-05T12:47:43.518+03:00  INFO 13296 --- [nio-8080-exec-2] i.e.s.MeterRegistrationServiceImpl       : Relay State       : ON
2026-08-05T12:47:43.518+03:00  INFO 13296 --- [nio-8080-exec-2] i.e.s.MeterRegistrationServiceImpl       : Timestamp         : 2026-08-03T18:30:00Z
2026-08-05T12:47:43.520+03:00  INFO 13296 --- [nio-8080-exec-2] meter-registration-route                 : Camel received request for GSRN=735999123456789011
Relay ON requested for MS-123456
2026-08-05T12:47:43.523+03:00  INFO 13296 --- [nio-8080-exec-2] meter-registration-route                 : Camel completed registration. Response=SUCCESS
2026-08-05T12:47:43.524+03:00  INFO 13296 --- [nio-8080-exec-2] i.e.s.MeterRegistrationServiceImpl       : Sending SOAP response
2026-08-05T12:47:43.524+03:00  INFO 13296 --- [nio-8080-exec-2] i.e.s.MeterRegistrationServiceImpl       : Status            : SUCCESS
2026-08-05T12:47:43.524+03:00  INFO 13296 --- [nio-8080-exec-2] i.e.s.MeterRegistrationServiceImpl       : Message           : Meter registered successfully
2026-08-05T12:47:43.524+03:00  INFO 13296 --- [nio-8080-exec-2] i.e.s.MeterRegistrationServiceImpl       : Registration ID   : 1b0afa42-53a5-461d-924b-49e300dab595
2026-08-05T12:47:43.524+03:00  INFO 13296 --- [nio-8080-exec-2] i.e.s.MeterRegistrationServiceImpl       : ===========================================
```

### Request 2
```xml
<relayState>OFF</relayState>
```

Expected log:

```text
2026-08-05T12:48:23.247+03:00  INFO 13296 --- [nio-8080-exec-4] i.e.s.MeterRegistrationServiceImpl       : ========== SOAP REQUEST RECEIVED ==========
2026-08-05T12:48:23.248+03:00  INFO 13296 --- [nio-8080-exec-4] i.e.s.MeterRegistrationServiceImpl       : GSRN              : 735999123456789011
2026-08-05T12:48:23.248+03:00  INFO 13296 --- [nio-8080-exec-4] i.e.s.MeterRegistrationServiceImpl       : Meter Serial      : MS-123456
2026-08-05T12:48:23.248+03:00  INFO 13296 --- [nio-8080-exec-4] i.e.s.MeterRegistrationServiceImpl       : Customer ID       : CUST-1001
2026-08-05T12:48:23.248+03:00  INFO 13296 --- [nio-8080-exec-4] i.e.s.MeterRegistrationServiceImpl       : Relay Number      : 1
2026-08-05T12:48:23.248+03:00  INFO 13296 --- [nio-8080-exec-4] i.e.s.MeterRegistrationServiceImpl       : Relay State       : OFF
2026-08-05T12:48:23.248+03:00  INFO 13296 --- [nio-8080-exec-4] i.e.s.MeterRegistrationServiceImpl       : Timestamp         : 2026-08-03T18:30:00Z
2026-08-05T12:48:23.249+03:00  INFO 13296 --- [nio-8080-exec-4] meter-registration-route                 : Camel received request for GSRN=735999123456789011
Relay OFF requested for MS-123456
2026-08-05T12:48:23.252+03:00  INFO 13296 --- [nio-8080-exec-4] meter-registration-route                 : Camel completed registration. Response=SUCCESS
2026-08-05T12:48:23.253+03:00  INFO 13296 --- [nio-8080-exec-4] i.e.s.MeterRegistrationServiceImpl       : Sending SOAP response
2026-08-05T12:48:23.253+03:00  INFO 13296 --- [nio-8080-exec-4] i.e.s.MeterRegistrationServiceImpl       : Status            : SUCCESS
2026-08-05T12:48:23.253+03:00  INFO 13296 --- [nio-8080-exec-4] i.e.s.MeterRegistrationServiceImpl       : Message           : Meter registered successfully
2026-08-05T12:48:23.253+03:00  INFO 13296 --- [nio-8080-exec-4] i.e.s.MeterRegistrationServiceImpl       : Registration ID   : 9d66eb47-be21-4150-a3dd-706e68ce16c7
2026-08-05T12:48:23.253+03:00  INFO 13296 --- [nio-8080-exec-4] i.e.s.MeterRegistrationServiceImpl       : ===========================================
```