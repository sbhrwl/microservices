# Observability
- [Scope](#scope)
- [Health checks (Dapr -> app)](#health-checks-dapr--app)
- [Logging](#logging)
- [Tracing (Zipkin)](#tracing-zipkin)
- [Example: run with debug sidecar logs (JAR)](#example-run-with-debug-sidecar-logs-jar)
- [Troubleshooting signals](#troubleshooting-signals)

## Scope
- Covers what the service exposes for health and basic observability based on repository evidence
- Includes `Dapr` app health over `gRPC`, application logging, and an external `Zipkin` UI reference when tracing is enabled

## Health checks (Dapr -> app)
- Implemented `gRPC` method:
  - Service: `AppCallbackHealthCheck` (implemented by `HealthService`)
  - `RPC`: `healthCheck(google.protobuf.Empty) -> HealthCheckResponse`
- `Dapr` sidecar settings (examples used in repository materials):
  - `--enable-app-health-check`
  - `--app-health-probe-interval 15`
  - `--app-health-probe-timeout 3000`
  - `--app-protocol grpc`
  - `--app-port` must match `iec61968-connector.dapr-grpc-callback-server.listen`
- Runtime behavior (from `HealthService`):
  - When `iec61968-connector.ping-message-bus-in-health-check = true`:
    - Publishes a test message via `Dapr` pub/sub:
      - `pubsub`: `iec4hes-activemq`
      - `topic`: `SMOC_HEALTH_CHECK`
      - `data`: `"ping"`
      - `metadata`: `ttlInSeconds=5`
    - On success:
      - Logs `"Connectivity with activemq is OK"`
      - Returns a `HealthCheckResponse`
    - On failure:
      - Logs `"activemq unreachable: ..."`
      - Propagates the error to `Dapr`
  - When `false`:
    - Returns a `HealthCheckResponse` immediately without accessing the message bus

## Logging
- Backed by `SLF4J` and `Logback`
- Configured via system property:
  - `-Dlogback.configurationFile=src/main/dist/etc/logback.xml`
- The provided `logback.xml` controls log levels and appenders
- At minimum, `HealthService` emits:
  - Success message bus probe: `"Connectivity with activemq is OK"`
  - Failure message bus probe: `"activemq unreachable: ..."` with stack trace
- Increase `Dapr` sidecar verbosity for troubleshooting with:
  - `--log-level debug`

## Tracing (Zipkin)
- If `Dapr` tracing to `Zipkin` is enabled externally, traces are available at:
  - `http://localhost:9411/zipkin/`
- This repository does not configure tracing
  - Assumes external `Dapr` and `Zipkin` configuration when required

## Example: run with debug sidecar logs (JAR)
```bash
# File: /home/user/gfc-app/iec61968-connector/scripts/run-with-dapr-debug.sh
dapr run --resources-path ./components/ --app-id iec61968-connector --app-protocol grpc --app-port 5006 --enable-app-health-check --app-health-probe-interval 15 --app-health-probe-timeout 3000 --log-level debug -- java -Dconfig.file=src/main/dist/etc/application.conf -Dlogback.configurationFile=src/main/dist/etc/logback.xml -jar target/iec61968-connector-1.0.jar
```

## Troubleshooting signals
* **Immediate health probe failures**
  * Confirm `--app-port` equals `iec61968-connector.dapr-grpc-callback-server.listen` (default `9090`)
* **Health probe errors with message bus ping enabled**
  * Ensure a `Dapr` pub/sub component named `iec4hes-activemq` exists under the configured `--resources-path`
  * Check application logs for `"activemq unreachable"` details
* **No or missing application logs**
  * Verify `-Dlogback.configurationFile` points to a valid `logback.xml`
