# Security
* [Scope and evidence](#scope-and-evidence)
* [Message-level security capabilities](#message-level-security-capabilities-from-schemas)
* [Configuration and logging](#configuration-and-logging)
* [Dapr and transport](#dapr-and-transport)
* [Next steps](#next-steps)
## Message-level security capabilities (from schemas)
* **Replay detection**
  * `Message.xsd` defines `Header.ReplayDetection` of type `ReplayDetectionType` with `Nonce` and `Created`
  * Schema documentation states it is “used to detect and prevent replay attacks”
  * No code evidence that this feature is populated or enforced at runtime
* **Security credential change**
  * `EndDeviceControls.xsd` includes an `EndDeviceControl` action choice named `SecurityCredentialChange`
  * Indicates a capability to carry a control related to credentials; no runtime usage shown
## Configuration and logging
* Configuration commonly provided via **environment variables** and `application.conf`
* `Logback` is used for logging; no evidence of sensitive data redaction or retention policies
* No secrets, keys, or credential files are present in the provided snippets
## Dapr and transport
* `Dapr` sidecar is used with `gRPC`
* Repository materials do not indicate TLS, mTLS, or authentication settings
* Health endpoints are enabled; no access control details are shown
## Next steps
* Confirm whether `Header.ReplayDetection` is produced/validated by the connector
* Document how `SecurityCredentialChange` is used (if at all) and any safeguards around it
* Provide expected security settings for:
  * `gRPC` listener (`TLS`, certificates, ciphers)
  * `Dapr` sidecar policies (authentication, mTLS)
  * `Message bus` connectivity (credentials, TLS settings)
* Add guidance for secret handling in configuration (environment variables vs. files vs. secret stores)
