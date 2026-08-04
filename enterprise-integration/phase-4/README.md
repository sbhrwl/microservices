# Apache CXF

| Milestone                                                 | Goal                                                          | Result                                                                                                            |
|-----------------------------------------------------------|---------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------|
| **[1. CXF Build Setup](CXF-BuildSetup.md)**               | Configure Gradle and CXF tooling                              | `wsdl2java` works (`MeterRegistrationPortType.java` and `MeterRegistrationService.java` classes generated)        |
| **[2. Service Implementation](ServiceImplementation.md)** | Implement generated interface                                 | Implement `MeterRegistrationPortTypeImpl.java`                                                                    |
| **[3. SOAP Endpoint](PublishEndpoint.md)**                | Publish SOAP service with `Endpoint.publish()`                | Implement `EndpointPublisher.java` and SOAP endpoint `http://localhost:8080/meter-registration?wsdl` is reachable |
| **[4. Spring Boot Migration](SpringBootMigration.md)**    | Convert integration module to Spring Boot                     | Spring Boot application starts                                                                                    |
| **[5. Integrate Apache CXF](IntegrateApacheCXF.md)**      | Replace standalone publisher with Spring-managed CXF endpoint | Production-style SOAP service, testable with SoapUI                                                               |
| **[6. Test](Test.md)**                                    | Test Soap requests                                            | Verify logs and Soap response                                                               |

### Next step
* The next logical step is to stop returning a hardcoded response and introduce the application's business layer properly.

```text
SOAP Endpoint
      │
      ▼
Processor
      │
      ▼
Repository (stub)
      │
      ▼
Database (later in Phase 6)
```
* Initially the repository will be an in-memory stub so that when we introduce PostgreSQL and Flyway later, we only replace the repository implementation, leaving the endpoint and processor unchanged.
* This is the same layering you'll find in many production Spring applications and sets us up nicely for the remaining phases.
