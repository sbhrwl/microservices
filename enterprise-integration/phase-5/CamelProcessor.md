# Camel processor
- [Introduction](#introduction)
  - [Example: Validation Processor](#example-validation-processor)
  - [Checkpoint 1](#checkpoint-1)
- [Exception handling](#exception-handling)
  - [Why `handled(false)`?](#why-handledfalse)
- [Enrichment Processor](#enrichment-processor)
  - [Why use Exchange properties?](#why-use-exchange-properties)
  - [Checkpoint 2](#checkpoint-2)
## Introduction
- A Camel **Processor** is one of the core concepts in Camel. 
- It lets you inspect or modify the `Exchange` (message) flowing through a route.
- Instead of putting validation or enrichment in your SOAP service, you put it into reusable processors.
### Example: Validation processor
* Create package
```
integration
└── src
    └── main
        └── java
            └── integration
                └── enterprise
                    └── processor
```
* Create [`MeterRegistrationValidationProcessor`](meter-registration-service/integration/src/main/java/enterprise/processor/MeterRegistrationValidationProcessor.java)
  * We're using the `Exchange` to access the message body instead of passing the request directly.
* Inject it into the route [`MeterRegistrationRoute.java`](meter-registration-service/integration/src/main/java/enterprise/route/MeterRegistrationRoute.java)
  * Update route constructor:
```java
private final MeterRegistrationProcessor processor;
private final MeterRegistrationValidationProcessor validationProcessor;

public MeterRegistrationRoute(
        MeterRegistrationProcessor processor,
        MeterRegistrationValidationProcessor validationProcessor) {

    this.processor = processor;
    this.validationProcessor = validationProcessor;
}
```
* Update the route [`MeterRegistrationRoute.java`](meter-registration-service/integration/src/main/java/enterprise/route/MeterRegistrationRoute.java)

```java
from("direct:registerMeter")
        .routeId("meter-registration-route")
        .log("Camel route invoked")
        .process(validationProcessor)
        .bean(processor, "register")
        .log("Camel route completed");
```
* Build the project: `.\gradlew :integration:build`
* Run: `.\gradlew :integration:bootRun`
* Then test in SoapUI:
  * A valid request should still succeed.
  * An invalid request (for example, remove the `<gsrn>` value or leave it empty) should trigger an `IllegalArgumentException`.
### Checkpoint 1
* Instead of your SOAP endpoint worrying about validation, Camel now owns the integration pipeline.
Your flow becomes:

```text
SOAP
   │
   ▼
CXF
   │
   ▼
Camel
   │
   ▼
Validation Processor
   │
   ▼
Business Processor
   │
   ▼
SOAP Response
```
## Exception handling
- SOAP service is just an adapter, and the integration logic lives in the Camel route.
- Right now, if validation fails, the client gets a Java exception turned into a `SOAP fault by CXF`. 
- We want Camel to intercept it, log it, and prepare the response in a controlled way.
- Update your route:

```java
...

    @Override
    public void configure() {

        onException(IllegalArgumentException.class)
                .handled(false)
                .log(LoggingLevel.ERROR,
                        "Validation failed: ${exception.message}");
...
```
### Why `handled(false)`?
- Curent flow was:

```text
SOAP
   │
   ▼
Camel
   │
Validation
   │
IllegalArgumentException
   │
Camel logs it
   │
CXF converts it to SOAP Fault
   │
SOAP Client
```

- We changed it to:

```text
SOAP
   │
Camel
   │
Validation
   │
Business Exception
   │
Camel Error Handler
   │
Custom SOAP Fault
```

## Enrichment Processor
- Enterprise Camel routes usually don't just validate, They also enrich the exchange with metadata.
- Create: [`MeterRegistrationEnrichmentProcessor.java`](meter-registration-service/integration/src/main/java/enterprise/processor/MeterRegistrationEnrichmentProcessor.java)
  - This doesn't modify the business payload, instead, it adds metadata to the Camel exchange.
- Update the route

```text
SOAP
   │
   ▼
Validation
   │
   ▼
Enrichment
   │
   ▼
Business Processor
```

So the route becomes:

```java
from("direct:registerMeter")
        .routeId("meter-registration-route")
        .log("Camel received request for GSRN=${body.gsrn}")
        .process(validationProcessor)
        .process(enrichmentProcessor)
        .bean(processor, "register")
        .log("Camel completed registration. Response=${body.status}");
```
### Why use Exchange properties?
* In enterprise integrations, you'll often carry values like:
  * Correlation ID
  * Request ID
  * Customer ID
  * Transaction ID
  * Processing start time
  * Retry count
  * Authentication context
* Without changing the SOAP or REST payload itself, exchange properties travel with the message throughout the Camel route.
### Checkpoint 2
* ✅ CXF SOAP Endpoint
* ✅ ProducerTemplate
* ✅ Camel Route
* ✅ Validation Processor
* ✅ Exchange
* ✅ Exception Handling
* ✅ Exchange Properties
* ✅ Headers