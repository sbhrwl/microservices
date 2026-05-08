# Data hub simulator
- [Gradle build](#gradle-build)
## Gradle build
- This Gradle build creates a Docker‑deployable Java simulator application using `Apache Camel` + `CXF to expose SOAP and REST services`, packaged as an executable JAR with external dependencies and embedded Git build metadata.
```
Gradle Build
│
├── Plugins
│   └── Git properties (build traceability)
│
├── Variables
│   └── CXF version
│
├── Dependencies
│   ├── Internal modules
│   │   └── data-hub-api
│   │
│   ├── Core infrastructure
│   │   ├── Logging
│   │   └── Typesafe Config
│   │
│   ├── Integration layer
│   │   └── Apache Camel
│   │       ├── SOAP (CXF)
│   │       └── REST (JAX-RS)
│   │
│   ├── Web services
│   │   ├── CXF JAX-WS (SOAP)
│   │   ├── CXF JAX-RS (REST)
│   │   └── Jetty HTTP
│   │
│   └── Serialization & APIs
│       ├── Jackson (Jakarta)
│       └── Jakarta WS / JWS / RS
│
├── Packaging
│   ├── Executable JAR
│   │   ├── Main class
│   │   ├── External libs
│   │   └── JVM module opens
│   │
│   └── Security cleanup
│
├── Distribution
│   ├── App JAR
│   ├── Libs folder
│   └── Config folder
│
└── Build metadata
    └── Git commit info
```
