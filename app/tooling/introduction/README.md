# Introduction
* [Overview](#overview)
* [Purpose and scope](#purpose-and-scope)
* [Problem statement](#problem-statement)
* [Relationship to parent gfc application](#relationship-to-parent-gfc-application)
* [Why multiple tools exist](#why-multiple-tools-exist)
* [Typical usage scenarios](#typical-usage-scenarios)
* [Scope limitations](#scope-limitations)
* [Uncertainty notes](#uncertainty-notes)
## Overview
* Development tooling support
* **Purpose**: Test data generation and database population tooling for the application
* **System Type**: Collection of utilities - Java message producer + Node.js MongoDB schema loader
* **Technologies**: 
  * Java (IEC 61968 test producer)
  * Node.js with Mongoose (MongoDB schema management)
  * MongoDB (data persistence)
* **Problem Solved**: Generates test events/messages for IEC 61968 protocol testing and populates MongoDB with schema-based data
* **Typical Use**: Development/testing environment setup - run producers to generate test data, run schema loader to initialize database
* **Key Architecture**: Separate tooling components in `tooling/` directory - decoupled producer and database initialization utilities
* **Intended Users**: Developers working on the GFC application who need test data and database setup
* **Domain Context**: Smart grid/utility metering systems (IEC 61968 is a standard for utility application integration)
## Purpose and scope
* Development-only tooling for local development, testing, and database initialization
* Designed to operate independently of production systems
* Consists of two standalone utilities
  * IEC 61968 test producer implemented in Java
  * MongoDB schema loader implemented in Node.js
## Problem statement
* Smart grid applications require standards-compliant test data
* IEC 61968 introduces complex message structures
* Local development requires databases with valid schema structures
* Manual data creation is error-prone and time-consuming
* Production environments must not be used for development testing
## Relationship to parent gfc application
* GFC is a smart metering and utility grid communication system
* GFC implements IEC 61968 for utility application integration
* GFC uses MongoDB for persistence
* GFC processes events and alarms from utility devices
* The tooling suite enables
  * Hardware-independent testing
  * Standards-compliant message validation
  * Local database initialization aligned with production schemas
  * Simulation of meter-related events
## Why multiple tools exist
* The tooling suite separates message generation from database initialization
* Each tool addresses a distinct concern
* Each tool can be used independently depending on the workflow
### IEC 61968 test producer (Java)
* Generates IEC 61968-compliant test messages
* Aligns with enterprise utility technology stacks
* Reuses domain models and serialization logic
* Supports
  * Gas meter event generation
  * Bulk device range creation
  * Weighted random event distribution
  * Classpath and filesystem configuration
### MongoDB schema loader (Node.js)
* Initializes MongoDB using schema definitions
* Uses Mongoose for ODM-based schema management
* Supports
  * GeoJSON-based location data
  * Specialized numeric types for meter values
  * Environment-based configuration via `.env`
### Separation rationale
* Different execution lifecycles for messaging and database setup
* Technology choices optimized per task
* Reduced maintenance complexity
* Improved reusability across environments
## Typical usage scenarios
* Initializing local development databases
* Generating synthetic event streams for integration testing
* Simulating multiple devices during load testing
* Supporting feature development and debugging
