# Certificates
- [Certificates](#certificates)
- [Client keystore](#client-keystore)
- [Truststore](#truststore)
- [mTLS](#mtls)
## [Certificates](https://github.com/sbhrwl/system_design/blob/main/docs/designprinciples/security/foundations/README.md)
- `C:\Git\gfc-app\flex-hub-connector\src\main\dist\etc\system\certs\datahub-connector-dev-keystore.p12`
- `C:\Git\gfc-app\flex-hub-connector\src\main\dist\etc\system\certs\datahub-connector-dev-keystore.p12.secret`
- `C:\Git\gfc-app\flex-hub-connector\src\main\dist\etc\system\certs\gfc-ca-truststore.p12`
- `system.conf`
```text
system-user {
  tls-client-keystore {
    type = "PKCS12"
    path = "file:flex-hub-connector/src/main/dist/etc/system/certs/datahub-connector-dev-keystore.p12"
    # path = "file:/app/etc/system/certs/datahub-connector-dev-keystore.p12"
    alias = "gfc-data-hub-connector"
    password = "changeit"
  }
  tls-trust-keystore {
    type = "PKCS12"
    path = "file:flex-hub-connector/src/main/dist/etc/system/certs/gfc-ca-truststore.p12"
    # path = "file:/app/etc/system/certs/tls-truststore.jks"
    password = "changeit"
  }
}
```
## Client keystore
- FHC client key store (`datahub-connector-dev-keystore.p12`) has
  - Private key
  - Certificate
  - Certificate chain
- `datahub-connector-dev-keystore.p12.secret` contains the password needed to unlock the `.p12` keystore
## Truststore
- Truststore (`gfc-ca-truststore.p12`) is FHC's list of trusted certificate authorities.
- Keystore = MY ID
- Truststore = WHO I TRUST
```text
Keystore = MY ID
Truststore = WHO I TRUST

                  FLEX HUB CONNECTOR
                         FHC
                          |
             +------------+------------+
             |                         |
             | TLS Client Identity     | TLS Trust
             |                         |
             v                         v
       CLIENT KEYSTORE            TRUSTSTORE
             |                         |
             |                         |
             v                         v
 datahub-connector-              gfc-ca-truststore
 dev-keystore.p12                .p12
             |                         |
             |                         |
             v                         v
      "This is me"              "I trust these"
             |                         |
             |                         |
             v                         v
     gfc-data-hub-                 GFC CA
     connector                     certificates
```
## mTLS
```text
mTLS-certificates/
├── datahub-connector-dev.crt
├── datahub-connector-dev.csr
├── datahub-connector-dev.key
├── datahub-connector-dev-cert.cnf
├── datahub-connector-dev-keystore.jks
├── datahub-connector-dev-keystore.p12
├── datahub-simulator.crt
├── datahub-simulator.csr
├── datahub-simulator.key
├── datahub-simulator-cert.cnf
├── datahub-simulator-keystore.jks
├── datahub-simulator-keystore.p12
├── gfc-ca.crt
├── gfc-ca.key
├── gfc-ca.srl
└── gfc-ca-truststore.p12
```
| File | Loaded by | Purpose |
| --- | --- | --- |
| `datahub-connector-dev-keystore.jks` (or `.p12`) | Client | Client's identity (private key + certificate) |
| `datahub-simulator-keystore.jks` (or `.p12`) | Server | Server's identity (private key + certificate) |
| `gfc-ca-truststore.p12` | Client | Trusts the server's certificate |
| `gfc-ca-truststore.p12` | Server | Trusts the client's certificate |

- Everything else is used to create these keystores and certificates, not during normal application startup:
```
Development / Certificate Creation
──────────────────────────────────
gfc-ca.key
gfc-ca.crt
gfc-ca.srl

datahub-connector-dev.key
datahub-connector-dev.csr
datahub-connector-dev.crt
datahub-connector-dev-cert.cnf

datahub-simulator.key
datahub-simulator.csr
datahub-simulator.crt
datahub-simulator-cert.cnf

            │
            ▼

Application Runtime
───────────────────
Client  ──► datahub-connector-dev-keystore.jks
         └─► gfc-ca-truststore.p12

Server  ──► datahub-simulator-keystore.jks
         └─► gfc-ca-truststore.p12
```
- Application Runtime
  - Client: `datahub-connector-dev-keystore.jks`, `gfc-ca-truststore.p12`
  - Server: `datahub-simulator-keystore.jks`, `gfc-ca-truststore.p12`
- At runtime, your Java applications typically need only two files each:
  - Keystore "Who am I?"
  - Truststore "Who do I trust?"
- The `.key`, `.csr`, `.crt`, `.cnf`, and `.srl` files are primarily part of the certificate generation process.
