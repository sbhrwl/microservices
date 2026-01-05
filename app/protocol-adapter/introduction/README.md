# Introduction
- Provides a Java-based connector that consumes messages from an `iec4hes ActiveMQ` source and forwards them to a `device hub`
- Runs alongside a Dapr sidecar using `gRPC` and exposes `health endpoint`s via Dapr
- Built with Maven and Java 25, and can be packaged as a native image using **`GraalVM`**
- Offers configurable message bus topics and network owner via `environment variables` and `application.conf`
- Includes extensive `IEC-related XSD schemas` under `resources`, suggesting validated or structured payloads
- Supports `local development` via `Dapr CLI` and `observability` via `Zipkin`
- Uses `Logback` for logging configuration
