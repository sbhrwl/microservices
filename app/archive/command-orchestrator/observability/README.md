# Observability
- [Overview](#overview)
- [Logging setup](#logging-setup)
- [Tracing with zipkin](#tracing-with-zipkin)
- [Health and diagnostics endpoints](#health-and-diagnostics-endpoints)

## Overview
- Logging is implemented with SLF4J and Logback (with Logstash JSON encoder available)
- Tracing references Zipkin for local setups
- Health checks are exposed via an HTTP endpoint compatible with Dapr

## Logging setup

| aspect                                   | details                                                                                                                          |
| ---------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------- |
| Dependencies (from `pom.xml`)            | - `org.slf4j:slf4j-api` <br> - `ch.qos.logback:logback-classic` <br> - `net.logstash.logback:logstash-logback-encoder`           |
| Configuration file                       | `src/main/dist/etc/logback.xml` <br> Can be selected at runtime with `-Dlogback.configurationFile=src/main/dist/etc/logback.xml` |
| Runtime hint (from README / run command) | `-Dlog.appender=STDOUT` (used by the provided Logback configuration)                                                             |
| Usage in code                            | SLF4J `LoggerFactory` used in components such as `DeviceHub` and `ActorCallbackController`                                       |

## Tracing with zipkin
- Zipkin UI: `http://localhost:9411/zipkin/`
## Health and diagnostics endpoints
- HTTP health endpoint
  - `GET` `/healthz`
  - Registered in `HealthCheckController`, `Dapr compliant`
