# Apache camel

| Milestone                                              | Goal                                                                  | Result                                                                                                            |
|--------------------------------------------------------|-----------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------|
| **[1. Prerequisites](Prerequisites.md)**               | SOAP endpoint                                                         | `wsdl2java` works (`MeterRegistrationPortType.java` and `MeterRegistrationService.java` classes generated)        |
| **[2. Camel setup](CamelSetup.md)**                    | Create route and inject producer template | Update `MeterRegistrationServiceImpl.java` and `MeterRegistrationRoute.java`                                    |
| **[3. Camel processor](CamelProcessor.md)**            |                         |  |

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