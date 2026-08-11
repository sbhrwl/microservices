# Integration methodology
- [App](app/README.md): LandisGyr2026RS
  - Elokuu,202608
## Phases

| Phase | Title                                         | Learning Objectives / Tasks                                                                                                                                                      | Deliverable                                                               |
| ----: | --------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------- |
| **0** | **[Environment Setup](phase-0/README.md)**    | • Install Java<br>• Configure Gradle<br>• Create Git repository<br>• Configure IntelliJ<br>• Create Docker Compose<br>• Start PostgreSQL<br>• Start ActiveMQ                     | Fully working development environment                                     |
| **1** | **[Project Skeleton](phase-1/README.md)**     | • Create the Gradle project<br>• Create modules:<br>    • contract<br>    • model<br>    • soap-api<br>    • integration<br>    • messaging<br>    • persistence<br>    • common | Compilable modular project                                                |
| **2** | **[XML & JAXB](phase-2/README.md)**           | • XML<br>• XSD<br>• Namespaces<br>• JAXB<br>• Marshalling<br>• Unmarshalling                                                                                                     | Generate Java classes from XSD and successfully serialize/deserialize XML |
| **3** | **[Creating a WSDL](phase-3/README.md)**      | • SOAP fundamentals<br>• WSDL                                                                                                                                                    | Create a complete WSDL contract for the service                           |
| **4** | **[SOAP with Apache CXF](phase-4/README.md)** | • Apache CXF<br>• SOAP Faults<br>• Contract-first development                                                                                                                    | Working SOAP endpoint with generated request and response objects         |
| **5** | **[Apache Camel](phase-5/README.md)**         | • Camel Routes<br>• Processors<br>• Error Handling<br>• Logging<br>• Content-Based Routing                                                                                       | Working Camel routes invoking the SOAP service                            |
| **6** | **[Testing](phase-6/Test-Jupiter.md)**        | • Unit tests<br>• Integration tests<br>• SOAP endpoint testing<br>• Camel route testing<br>• Database testing<br>• JMS testing                                                   | High-confidence automated test suite                                      |


