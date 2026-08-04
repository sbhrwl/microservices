# Apache camel
- [Prerequisites](Prerequisites.md)
## Orchestrate with Apache Camel
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
### Orchestration
```text
                 SoapUI
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
