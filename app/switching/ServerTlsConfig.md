# ServerTlsConfig
# Index
- [Overview](#overview)
- [Tls flow](#tls-flow)
  - [Application startup flow](#application-startup-flow)
  - [Client connection flow](#client-connection-flow)
- [Code interpretation](#code-interpretation)
  - [Loading the server identity](#loading-the-server-identity)
  - [Initializing key manager](#initializing-key-manager)
  - [Loading trusted clients](#loading-trusted-clients)
  - [Initializing trust manager](#initializing-trust-manager)
- [Important tls settings](#important-tls-settings)
  - [Client authentication](#client-authentication)
  - [Tls version](#tls-version)
  - [Cipher suites](#cipher-suites)
  - [Sni host check](#sni-host-check)
- [Security boundary](#security-boundary)
  - [Protected endpoint](#protected-endpoint)
  - [Unprotected endpoint](#unprotected-endpoint)
- [Logging consideration](#logging-consideration)
  - [Logged information](#logged-information)
  - [Security impact](#security-impact)
- [Key concept](#key-concept)
  - [Keystore](#keystore)
  - [Truststore](#truststore)
  - [Mutual tls](#mutual-tls)
- [Related files](#related-files)
## Overview
- ServerTlsConfig` is the server-side TLS configuration component for the Data Hub Simulator.
- Its responsibility is to prepare the HTTPS security layer.
- It does not handle SOAP business logic or process business messages.
- The main responsibilities are:
  - Build SSL/TLS parameters.
  - Load the server identity.
  - Load trusted client certificate authorities.
  - Configure mutual TLS (mTLS).
  - Provide TLS settings that `Bootstrap` attaches to the HTTPS listener on port `8443`.
- The SOAP endpoint itself is created later by `CamelRoutes`.
- The purpose of this configuration is to ensure that communication between the simulator and the client is **authenticated and encrypted**.
- Unlike normal HTTPS, where only the server proves its identity, mTLS requires both sides to prove who they are:
  - Server proves its identity to the client.
  - Client proves its identity to the server.
- This is achieved using certificates and cryptographic keys.
## Tls flow
```mermaid
flowchart TD
  subgraph "Startup"
    A["Bootstrap.main"] --> B["ServerTlsConfig.createTlsParams()"]

    B --> C["Load datahub-simulator-keystore.jks"]
    C --> D["KeyManagerFactory"]

    B --> E["Load gfc-ca-truststore.jks"]
    E --> F["TrustManagerFactory"]

    D --> G["TLSServerParameters"]
    F --> G

    G --> H["Bind TLS params to Jetty HTTPS listener port 8443"]
  end

  subgraph "Request time"
    I["Flex Hub Connector client certificate"] --> J["TLS handshake"]

    H --> J

    J --> K["ClientAuthentication required"]

    K --> L["Validate client certificate chain against truststore"]

    L --> M["CXF HTTPS SOAP endpoint /soap/FGR"]

    M --> N["Log SOAP operation and peer certificate subject DN"]

    N --> O["marketMessagingSoapService"]
  end
````

### Application startup flow
* `Bootstrap` calls `ServerTlsConfig.createTlsParams()`.
* The server keystore is loaded.
* The truststore is loaded.
* TLS parameters are created.
* The TLS configuration is attached to the HTTPS listener.
### Client connection flow
* The client starts a TLS handshake.
* The server presents its certificate.
* The client presents its certificate.
* The server validates the client certificate.
* A secure HTTPS SOAP connection is established.

## Code interpretation
- The code can be understood as four main security building blocks.
### Loading the server identity
```java
KeyStore keyStore = KeyStore.getInstance("JKS");

String keyStorePassword = "GFCkeystore";

try (InputStream in =
    Thread.currentThread()
        .getContextClassLoader()
        .getResourceAsStream("datahub-simulator-keystore.jks")) {
}
```
* What it does:
  * Loads the Java KeyStore (`JKS`) file:
    * `datahub-simulator-keystore.jks`
* Opens it using:
  * `GFCkeystore`
* Why:
  * The keystore contains the server credentials:
    * Server private key.
    * Server certificate.
* The server uses these credentials during TLS handshake to prove its identity.
### Initializing key manager

```java
KeyManagerFactory kmf =
    KeyManagerFactory.getInstance(
        KeyManagerFactory.getDefaultAlgorithm()
    );

kmf.init(
    keyStore,
    keyStorePassword.toCharArray()
);
```

* What it does:
  * Converts the keystore into TLS-ready server credentials.
* Why:
  * The `KeyManager` provides the server certificate and private key when the TLS connection starts.
* It answers the question:
  * "Which certificate should this server present?"
### Loading trusted clients
```java
KeyStore trustStore = KeyStore.getInstance("JKS");

String trustKeyStorePassword = "GFCtruststore";

try (InputStream in =
    Thread.currentThread()
        .getContextClassLoader()
        .getResourceAsStream("gfc-ca-truststore.jks")) {
}
```
* What it does:
  * Loads:
    * `gfc-ca-truststore.jks`
* Why:
  * The truststore contains certificates that the server trusts.
  * These are usually:
    * Client certificates.
    * Certificate Authority (CA) certificates that signed client certificates.
* The server uses this information to decide whether a connecting client is trusted.
### Initializing trust manager

```java
TrustManagerFactory tmf =
    TrustManagerFactory.getInstance(
        TrustManagerFactory.getDefaultAlgorithm()
    );

tmf.init(trustStore);
```
* What it does:
  * Converts the truststore into certificate validation rules.
* Why:
  * The `TrustManager` validates certificates presented by clients.
* It answers the question:
  * "Should I trust this client certificate?"
## Important tls settings
### Client authentication

```java
clientAuthentication.setRequired(true);
clientAuthentication.setWant(true);
```

* `setRequired(true)`:
  * Enables mandatory client authentication.
  * The client must provide a valid certificate.
  * A client without a trusted certificate is rejected.
* This is what makes the connection mutual TLS.
  * `setWant(true)`:
* Requests a client certificate.
* In this configuration it is effectively redundant because `required(true)` already enforces the requirement.
### TLS version

```java
tlsParams.setSecureSocketProtocol("TLSv1.3");
```
* What it does:
  * Restricts communication to TLS 1.3.
* Why:
  * Prevents older insecure TLS versions from being used.
### Cipher suites
```java
tlsParams.setCipherSuites(List.of(
    "TLS_AES_256_GCM_SHA384",
    "TLS_AES_128_GCM_SHA256"
));
```

* What it does:
  * Limits the allowed encryption algorithms.
* Why:
  * Ensures only approved strong cipher suites are used.
### SNI host check

```java
tlsParams.setSniHostCheck(false);
```

* What it does:
  * Disables hostname validation using Server Name Indication (SNI).
* Why:
  * Useful in simulator or local development environments.
* Example:
  * Client connects using:
    * `localhost`
  * Certificate contains:
    * `datahub-server.company.com`
  * Without disabling SNI validation, the TLS handshake may fail.
* For production environments this setting should be reviewed carefully.
## Security boundary
* `ServerTlsConfig` protects only the HTTPS communication path.
* In `Bootstrap`:
  * TLS configuration is attached to:
    * HTTPS listener.
    * Port `8443`.
* However, `CamelRoutes` also defines:
  * Plain HTTP SOAP route.
  * Port `9090`.
* Therefore:
  * Port `8443` is protected by TLS and mTLS.
  * Port `9090` does not use this TLS configuration.
* This class secures the TLS endpoint, not every endpoint in the application.

## Logging consideration
* Encryption protects data while it travels between systems, but it does not automatically protect application logs.
  * `CamelRoutes` enables CXF logging on the HTTPS SOAP endpoint.
* It logs:
  * SOAP operation.
  * Peer certificate subject DN when available.
* This helps with:
  * Debugging.
  * Request tracing.
  * Certificate-based auditing.
* However, excessive SOAP logging can expose sensitive payload information.
* Logging levels should be reviewed before production deployment.

## Key concept
* Keystore = who the server is
  * Contains:
    * Server certificate.
    * Server private key.
  * Purpose:
    * Prove server identity.
  * Meaning:
    * "This is who I am."
* Truststore = who the server trusts
  * Contains:
    * Trusted CA certificates.
    * Trusted client certificate chains.
  * Purpose:
    * Validate incoming client certificates.
  * Meaning:
    * "This is who I trust."
* Mutual tls
  * mTLS combines both:
    * Server proves identity.
    * Client proves identity.
    * Both sides establish trust before communication begins.
## Related files
* `ServerTlsConfig.java`
* `Bootstrap.java`
* `CamelRoutes.java`
* `SSLContextParameterFactory.java`
* `how-to-manage-certificates.md`
