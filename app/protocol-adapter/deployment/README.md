# Deployment
* **Scope**
  * Documents how to run the packaged `JAR` alongside a `Dapr` sidecar using the implemented `gRPC` callback
  * Reflects only behavior and configuration evidenced in the repository and code
* **Prerequisites**
  * Built artifact at `target/iec61968-connector-1.0.jar`
  * `Dapr` CLI installed and initialized (`dapr init`)
  * A `Dapr` pub/sub component for `ActiveMQ` named `iec4hes-activemq` available in the components directory
  * Configuration and logging files available on disk:
    * `src/main/dist/etc/application.conf`
    * `src/main/dist/etc/logback.xml`
* **Ports and config alignment**
  * `gRPC` server listen port is configured via:
    * `iec61968-connector.dapr-grpc-callback-server.listen` (default `9090`)
  * `Dapr --app-port` must match the application listen port
    * If `--app-port 5006` is used, `listen` must also be set to `5006` in `application.conf`
* **Health checks at runtime**
  * The service implements a `gRPC` health callback for `Dapr`:
    * `RPC`: `healthCheck(google.protobuf.Empty) -> HealthCheckResponse`
  * Behavior is controlled by:
    * `iec61968-connector.ping-message-bus-in-health-check` (default `false`)
  * When enabled, the health probe publishes a test message via `Dapr` pub/sub:
    * `pubsub`: `iec4hes-activemq` (hard-coded in `HealthService`)
    * `topic`: `SMOC_HEALTH_CHECK` (hard-coded)
    * `data`: `"ping"`
    * `metadata`: `ttlInSeconds=5`
  * Success log:
    * `"Connectivity with activemq is OK"`
  * Failure log:
    * `"activemq unreachable: ..."` and the error is propagated to `Dapr`
* **Run locally with Dapr (JAR)**
  * Start the application with `Dapr` (adjust paths and ports as needed)
  * **Windows PowerShell**
    ```powershell
    # File: C:\Git\gfc-app\iec61968-connector\scripts\run-with-dapr.ps1
    dapr run --resources-path .\components\ --app-id iec61968-connector --app-protocol grpc --app-port 5006 --enable-app-health-check --app-health-probe-interval 15 --app-health-probe-timeout 3000 --log-level debug -- java -D"config.file"=src/main/dist/etc/application.conf -D"logback.configurationFile"=src/main/dist/etc/logback.xml -jar target/iec61968-connector-1.0.jar
    ```
  * **Bash**
    ```bash
    # File: /home/user/gfc-app/iec61968-connector/scripts/run-with-dapr.sh
    dapr run --resources-path ./components/ --app-id iec61968-connector --app-protocol grpc --app-port 5006 --enable-app-health-check --app-health-probe-interval 15 --app-health-probe-timeout 3000 --log-level debug -- java -Dconfig.file=src/main/dist/etc/application.conf -Dlogback.configurationFile=src/main/dist/etc/logback.xml -jar target/iec61968-connector-1.0.jar
    ```
* **Notes**
  * Ensure the pub/sub component name is `iec4hes-activemq` so the health check publish succeeds
  * If the default `gRPC` listen port (`9090`) is used, update `--app-port` accordingly
  * The application logs resolved configuration at startup and fails if the `iec61968-connector` root path is missing
* **Troubleshooting**
  * **Health probe failing immediately**
    * Verify `--app-port` matches `iec61968-connector.dapr-grpc-callback-server.listen`
  * **Health probe failing with message bus ping enabled**
    * Confirm a valid pub/sub component named `iec4hes-activemq` exists
    * Check logs for `"activemq unreachable"` errors
  * **No logs or configuration not applied**

    * Verify `-Dconfig.file` and `-Dlogback.configurationFile` point to existing files
