# Gradle build
- Build
```
Gradle build file
│
├── Plugins
│   └── git metadata generation
│
├── Dependencies
│   ├── gRPC
│   ├── Camel / JMS / ActiveMQ
│   ├── Dagger
│   ├── JAXB
│   ├── Reactor
│   └── Testing
│
├── Sources
│   ├── src/main/java
│   └── generated sources
│
├── Compile/Test config
│   ├── annotation processors
│   └── JUnit 5
│
├── Jar packaging
│   ├── main class = Bootstrap
│   └── runtime deps in libs/
│
└── Runtime tasks
    ├── run
    └── createDistribution
```
- Run
```
Run via Gradle
    │
    ▼
Check project graph
    │
    ├─ generate git info
    ├─ process resources
    ├─ generate code (wsdl2java)
    ├─ compile API
    ├─ build API jar
    ├─ compile simulator
    │
    ▼
Run Bootstrap.main()
    │
    ▼
Logback → Camel → CXF → Jetty starts
```
