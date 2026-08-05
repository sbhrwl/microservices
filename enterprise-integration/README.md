# Enterprise integration learning project
- [App](app/README.md): LandisGyr2026RS
  - Elokuu,202608
- [Goal](#goal)
- [Technology Stack](#technology-stack)
- [Project Scenario](#project-scenario)
- [Learning Objectives](#learning-objectives)
- [Project Phases](#project-phases)
  - [Phase 0 – Environment Setup](#phase-0--environment-setup)
  - [Phase 1 – Project Skeleton](#phase-1--project-skeleton)
  - [Phase 2 – XML & JAXB](#phase-2--xml--jaxb)
  - [Phase 3 – Creating a wsdl](#phase-3--creating-a-wsdl)
  - [Phase 4 – Soap with Apache CXF](#phase-4--soap-with-apache-cxf)
  - [Phase 5 – Apache Camel](#phase-5--apache-camel)
  - [Phase 6 – Testing](#phase-6--testing)
## Goal
- Build a production-style enterprise integration application from scratch using modern Java technologies while understanding the architectural reasoning behind every design decision.
- The objective is not simply to learn APIs, but to understand how enterprise integration systems are designed, implemented, tested, and maintained.
## Technology Stack
* Java 21
* Gradle
* Spring Boot 3
* Apache Camel 4
* Apache CXF 4
* SOAP
* WSDL
* JAXB
* ActiveMQ (JMS)
* PostgreSQL
* Flyway
* Docker & Docker Compose
* JUnit 5
## Project Scenario
* **Energy meter registration service**
  * A SOAP service receives requests containing a GSRN and meter information.
* The application:
1. Receives a SOAP request.
2. Validates the XML.
3. Converts XML into Java objects using JAXB.
4. Stores the request in PostgreSQL.
5. Sends a message to ActiveMQ.
6. Processes the message through Camel routes.
7. Updates the database.
8. Returns an appropriate SOAP response.
9. Handles retries, failures, and dead-letter scenarios.

## Learning Objectives
* XML fundamentals
* XML Namespaces
* XSD
* WSDL
* SOAP Messaging
* JAXB
* Apache CXF
* Apache Camel
* Enterprise Integration Patterns (EIPs)
* JMS
* ActiveMQ
* PostgreSQL integration
* Flyway database migrations
* Spring Boot integration
* Docker Compose
* Exception handling
* Transactions
* Correlation IDs
* Logging
* Dead Letter Queues (DLQ)
* Unit Testing
* Integration Testing
* Most importantly:
  * Why each technology exists
  * When to use it
  * Architectural trade-offs
  * Common production pitfalls
## Project Phases
### [Phase 0 – Environment Setup](phase-0/README.md)
* Install Java
* Configure Gradle
* Create Git repository
* Configure IntelliJ
* Create Docker Compose
* Start PostgreSQL
* Start ActiveMQ
* Deliverable: A fully working development environment.
### [Phase 1 – Project Skeleton](phase-1/README.md)
* Create the Gradle project.
* Modules:
  * contract
  * model
  * soap-api
  * integration
  * messaging
  * persistence
  * common
* Deliverable: Compilable modular project.
### [Phase 2 – XML & JAXB](phase-2/README.md)
* Learn:
  * XML
  * XSD
  * Namespaces
  * JAXB
  * Marshalling
  * Unmarshalling
* Deliverable: Generate Java classes from XSD and successfully serialize/deserialize XML.
### [Phase 3 – Creating a wsdl](phase-3/README.md)
* Learn:
  * SOAP
  * WSDL
### [Phase 4 – Soap with Apache CXF](phase-4/README.md)
* Learn:
  * CXF
  * SOAP Faults
  * Contract-first development
* Deliverable: Working SOAP endpoint with generated request and response objects.
### [Phase 5 – Apache Camel](phase-5/README.md)
* Learn:
  * Camel Routes
  * Processors
  * Error Handling
  * Logging
  * Content based routing
* Deliverable: Working Camel routes invoking the SOAP service.
### [Phase 6 – Testing](phase-5/Test-Jupiter.md)
* Implement:
  * Unit tests
  * Integration tests
  * SOAP endpoint testing
  * Camel route testing
  * Database testing
  * JMS testing
* Deliverable: High-confidence automated test suite.
