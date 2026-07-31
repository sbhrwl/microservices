# env file
- Use env vars only as **`overrides`**, not as the primary config source.
```
# Include following parameters to JVM options (In IntelliJ, Add VM options in Run/Debug configurations)
# -Dconfig.file=flex-hub-connector/src/main/dist/etc/application.conf -Dlogback.configurationFile=flex-hub-connector/src/main/dist/etc/logback.xml -Dlog.appender=STDOUT --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/jdk.internal.misc=ALL-UNNAMED

GRPC_SERVER_PORT=50053

GFC_CORE_ADDRESS=localhost:50051
```

## JVM options
- **`-D...`** → configuration & logging
- **`--add-opens ...`** → bypass Java module restrictions for frameworks (Netty, gRPC, Camel)

| JVM Option                                                                     | Purpose            | What it effectively does                                      |
| ------------------------------------------------------------------------------ | ------------------ | ------------------------------------------------------------- |
| `-Dconfig.file=flex-hub-connector/src/main/dist/etc/application.conf`          | App configuration  | Tells the app **which config file to load** (Typesafe Config) |
| `-Dlogback.configurationFile=flex-hub-connector/src/main/dist/etc/logback.xml` | Logging config     | Points Logback to the **logging configuration file**          |
| `-Dlog.appender=STDOUT`                                                        | Logging behavior   | Forces logs to go to **stdout** (Docker-friendly)             |
| `--add-opens java.base/java.lang=ALL-UNNAMED`                                  | Java module access | Allows reflection into `java.lang`                            |
| `--add-opens java.base/java.util=ALL-UNNAMED`                                  | Java module access | Allows reflection into `java.util`                            |
| `--add-opens java.base/jdk.internal.misc=ALL-UNNAMED`                          | Java module access | Allows access to **JDK internal APIs**                        |

## Service startup
```
[ This Service ]
   │
   │ LISTENS on
   ▼
GRPC_SERVER_PORT = 50053

   │
   │ CALLS
   ▼
GFC_CORE_ADDRESS = localhost:50051
```
