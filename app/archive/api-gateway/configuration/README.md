# Configuration
* [Configuration file](#configuration-file)
* [Environment variables](#environment-variables)
* [Runtime modes](#runtime-modes)
* [Dapr configuration](#dapr-configuration)
* [Script-based configuration](#script-based-configuration)
## Configuration file
* Configuration is centralized in `src/common/config.ts`.
```typescript
// File: c:\Git\gfc-app\api-gateway\src\index.ts
import config from "./common/config";
const isDev = config.ENV !== "production";
const isHttp2 = config.ENV !== "local";
```

* The configuration object includes at minimum:
  * `ENV` property for environment detection
## Environment variables
* Environment variables are set via npm scripts using `cross-env`.
  * `NODE_ENV` - Controls application environment mode
    * Values: `local`, `production`
    * Used to determine Apollo Server landing page behavior and HTTP/2 enablement
  * `DAPR_ENABLED` - Controls Dapr sidecar integration
    * Values: `true`, `false`
    * Set to `false` for standalone mode without Dapr
* **Example from package.json:**
```json
"start": "npm run build && cross-env NODE_ENV=local DAPR_ENABLED=false node dist/index.js"
```

## Runtime modes
* The application supports multiple runtime modes configured via npm scripts.
* **Local mode without Dapr:**
  * Sets `NODE_ENV=local` and `DAPR_ENABLED=false`.
```bash
npm run start
```
* **Local mode with Dapr:**
  * Sets `NODE_ENV=local` and runs with Dapr sidecar.
```bash
npm run start:dapr
```
* **Production mode:**
  * Runs compiled code from `dist/` without setting environment variables (assumes production defaults).
```bash
npm run start:prod
```
## Dapr configuration
* When running with Dapr sidecar, configuration is provided via command-line arguments.
* **Dapr run command structure:**
```bash
dapr run --app-id api-gateway --app-protocol http --dapr-grpc-port 50001 --scheduler-host-address=""
```

* **Parameters:**
* `--app-id api-gateway` - Application identifier for service discovery
* `--app-protocol http` - Protocol Dapr uses to communicate with this application
* `--dapr-grpc-port 50001` - Port where Dapr sidecar listens for gRPC calls
* `--scheduler-host-address=""` - Disables Dapr scheduler component
## Script-based configuration
* Additional configuration is embedded in npm scripts.
* **Cache-optimized scripts:**
  * Scripts with `:cache` suffix skip protocol buffer and GraphQL code generation:
  * These execute only TypeScript compilation and file copying, reducing startup time during development.
```bash
npm run start:cache
npm run start:dapr:cache
```
* **Platform-specific scripts:**
  * Build scripts automatically detect operating system via `run-script-os`:
    * `prebuild:win32` - Executes `protoc.bat` on Windows
    * `prebuild:default` - Executes `protoc.sh` on Linux/macOS
