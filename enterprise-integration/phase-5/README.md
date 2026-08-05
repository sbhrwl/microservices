# Apache camel

| Milestone                                              | Goal                                                                  | Result                                                                                                                              |
|--------------------------------------------------------|-----------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| **[1. Prerequisites](Prerequisites.md)**               |    | Soap endpoint serves requests and delivers response                                                                                 |
| **[2. Camel setup](CamelSetup.md)**                    |    | Create route `MeterRegistrationRoute.java` and inject producer template in `MeterRegistrationServiceImpl.java`                      |
| **[3. Camel processor](CamelProcessor.md)**            |    | Create processors `MeterRegistrationValidationProcessor.java` (exception handling) and `MeterRegistrationEnrichmentProcessor.java`) |
| **[4. Content-based routing](ContentBasedRouting.md)** |    | Create processors `RelayOnProcessor.java` and `RelayOffProcessor.java`                                                              |

- [Orchestration](#orchestration)
- [Flow](#flow)
## Orchestration
```text
                 SoapUI
                    │
                    ▼
                CXF Endpoint
                    │
                    ▼
          MeterRegistrationPortType
                    │
                    ▼
       MeterRegistrationServiceImpl
                    │
                    ▼
        MeterRegistrationProcessor
                    │
                    ▼
          Apache Camel Route
                    │
      ┌─────────────┼─────────────┐
      ▼             ▼             ▼
   Validate      Transform     Database
      │             │             │
      └─────────────┼─────────────┘
                    ▼
             Response Builder
                    │
                    ▼
            SOAP Response
```
### Flow
- Install Camel
- Create the first Camel route
- Processor delegates to Camel
- Camel logs the message
- Camel calls a bean
- Camel transforms the response
- Camel validates data
- Camel writes to PostgreSQL
- Camel sends a JMS message (ActiveMQ)
- Exception handling and retries
```text
SOAP Endpoint
      │
      ▼
Processor
      │
      ▼
Camel
      │
 Route
      │
Processors
Beans
Database
MQ
REST
```
