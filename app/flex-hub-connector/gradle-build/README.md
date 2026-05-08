# Gradle build
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
