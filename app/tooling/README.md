# Tooling
* [Introduction](introduction/README.md)
  * Purpose and scope of the tooling suite, relationship to the parent GFC application, problem statement, and why multiple tools exist
* [Architecture](architecture/README.md)
  * Separation between producer and schema loader components, technology stacks (Java vs Node.js), data flow, and high-level component interactions
* [Installation](installation/README.md)
  * Prerequisites and setup for Java runtime, Maven, Node.js, npm, and project build steps for each tool
* [Configuration](configuration/README.md)
  * Environment variables, `.env` usage, MongoDB connection details, message broker configuration, and configuration defaults and overrides
* [Data models](data-models/README.md)
  * MongoDB schemas defined via Mongoose, supported data types (`geojson`, `int32`, `long`), and schema responsibilities
* [Runtime behavior](runtime-behavior/README.md)
  * How to execute each tool, entry points (`ClassicProducer.main()`, npm scripts), runtime modes, and expected outputs
* [Testing](testing/README.md)
  * Test data generation patterns, validation strategies, and how the tools are used to support IEC 61968 test scenarios

* **Purpose**: Test data generation and database population tooling for a Grid Field Communication (GFC) application
* **System Type**: Collection of utilities - Java message producer + Node.js MongoDB schema loader
* **Technologies**: 
  - Java (IEC 61968 test producer)
  - Node.js with Mongoose (MongoDB schema management)
  - MongoDB (data persistence)
* **Problem Solved**: Generates test events/messages for IEC 61968 protocol testing and populates MongoDB with schema-based data
* **Typical Use**: Development/testing environment setup - run producers to generate test data, run schema loader to initialize database
* **Key Architecture**: Separate tooling components in `tooling/` directory - decoupled producer and database initialization utilities
* **Intended Users**: Developers working on the GFC application who need test data and database setup
* **Domain Context**: Smart grid/utility metering systems (IEC 61968 is a standard for utility application integration)
