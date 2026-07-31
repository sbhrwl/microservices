# Enterprise integration learning project
- [Introduction](#introduction)
  - [Goal](#goal)
  - [Technology Stack](#technology-stack)
  - [Project Scenario](#project-scenario)
- [Learning Objectives](#learning-objectives)
- [Project Phases](#project-phases)
  - [Phase 0 – Environment Setup](#phase-0--environment-setup)
  - [Phase 1 – Project Skeleton](#phase-1--project-skeleton)
  - [Phase 2 – XML & JAXB](#phase-2--xml--jaxb)
  - [Phase 3 – SOAP using Apache CXF](#phase-3--soap-using-apache-cxf)
  - [Phase 4 – Apache Camel](#phase-4--apache-camel)
  - [Phase 5 – ActiveMQ](#phase-5--activemq)
  - [Phase 6 – PostgreSQL & Flyway](#phase-6--postgresql--flyway)
  - [Phase 7 – Complete Integration Flow](#phase-7--complete-integration-flow)
  - [Phase 8 – Production Features](#phase-8--production-features)
  - [Phase 9 – Testing](#phase-9--testing)
- [Prerequisites](#prerequisites)
  - [Development Tools](#development-tools)
  - [Infrastructure](#infrastructure)
  - [Testing Tools](#testing-tools)
- [Learning Philosophy](#learning-philosophy)
- [Expected Outcome](#expected-outcome)
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

- The project will evolve gradually into a production-quality integration service.
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
### Phase 0 – Environment Setup
* Install Java
* Configure Gradle
* Create Git repository
* Configure IntelliJ
* Create Docker Compose
* Start PostgreSQL
* Start ActiveMQ
* Deliverable: A fully working development environment.
### Phase 1 – Project Skeleton
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
### Phase 2 – XML & JAXB
* Learn:
  * XML
  * XSD
  * Namespaces
  * JAXB
  * Marshalling
  * Unmarshalling
* Deliverable: Generate Java classes from XSD and successfully serialize/deserialize XML.
### Phase 3 – SOAP using Apache CXF
* Learn:
  * SOAP
  * WSDL
  * CXF
  * SOAP Faults
  * Contract-first development
* Deliverable: Working SOAP endpoint with generated request and response objects.
### Phase 4 – Apache Camel
* Learn:
  * Camel Routes
  * Processors
  * EIPs
  * Error Handling
  * Logging
* Deliverable: Working Camel routes invoking the SOAP service.
### Phase 5 – ActiveMQ
* Learn:
  * JMS
  * Queues
  * Producers
  * Consumers
  * Request/Reply
  * Transactions
* Deliverable: Camel publishes and consumes messages through ActiveMQ.
### Phase 6 – PostgreSQL & Flyway
* Learn:
  * Database schema design
  * Flyway migrations
  * Spring Data access
  * Transactions
* Deliverable: Persist requests, responses, and processing status.
### Phase 7 – Complete Integration Flow
* End-to-end flow:
```text
SOAP Client
↓
CXF Endpoint
↓
JAXB
↓
Camel
↓
PostgreSQL
↓
ActiveMQ
↓
Camel Consumer
↓
Business Logic
↓
Database Update
↓
SOAP Response
```

* Deliverable: Fully functioning enterprise integration application.
### Phase 8 – Production Features
* Implement:
  * Correlation IDs
  * Structured logging
  * Retry policies
  * Dead Letter Queue
  * Validation
  * Exception handling
  * Configuration management
* Deliverable: Production-style robustness.
### Phase 9 – Testing
* Implement:
  * Unit tests
  * Integration tests
  * SOAP endpoint testing
  * Camel route testing
  * Database testing
  * JMS testing
* Deliverable: High-confidence automated test suite.
## Prerequisites
* Development Tools
  * Java 21
  * Gradle
  * IntelliJ IDEA
  * Git
  * Docker Desktop
  * Docker Compose
* Infrastructure
  * PostgreSQL
  * ActiveMQ
  * pgAdmin (optional)
* Testing Tools
  * SoapUI
  * Postman
## Learning Philosophy
* Build first, then study the theory behind what was built.
* Understand the architectural reasoning before introducing new frameworks.
* Keep every commit in a runnable state.
* Avoid copying tutorial code without understanding it.
* Treat the project as if it were being developed for production.
## Expected Outcome
* A production-style enterprise integration application.
* A deep understanding of SOAP-based integration.
* Practical experience with Apache Camel and Apache CXF.
* Hands-on knowledge of ActiveMQ and JMS.
* Experience using PostgreSQL and Flyway.
* A portfolio-quality Gradle project demonstrating enterprise integration patterns and modern Java development.
