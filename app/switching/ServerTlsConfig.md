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
- [File extensions](#file-extensions)
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
## File extensions
### `.jks` (Java Keystore)
- A `.jks` file is a repository of security certificates and private keys used almost exclusively within the **Java runtime environment**.
* **What it holds**
  * It can contain private keys (with their corresponding public certificates) and trusted root certificates (often called a "truststore").
* **Common Use Cases:**
  * Securing Java-based web servers (like Tomcat).
  * Signing Android applications (`.apk` or `.aab`) before uploading them to the Google Play Store.
* **Security:** The file itself is encrypted and password-protected. Individual keys inside the store can also have their own separate passwords.
### `.p12` (PKCS #12)
- A `.p12` file (Public-Key Cryptography Standards #12) is an **industry-standard, portable format** for storing cryptographic keys and certificates.
- It is functionally very similar to a `.jks` file, but it is universally recognized across almost all operating systems and languages, not just Java.
- `.pfx` is another extension used for this exact same format.
* **What it holds:**
  * A private key paired with its public key certificate, and often the chain of certificates that proves its authenticity.
* **Common Use Cases:**
  * Installing personal identity certificates into web browsers for client authentication.
  * Securing email communications (S/MIME).
  * Configuring VPN connections.
* *Tip:* Because it is a universal standard, Java modern frameworks now often use `.p12` instead of the older, proprietary `.jks`.
* **Security:** Highly encrypted and password-protected.
### `.secret` (Generic Secret Data)
- Unlike the other two, `.secret` is **not a standardized cryptographic file format**.
- It is a generic extension used by developers to store sensitive information in plain text or simple configuration formats.
* **What it holds:**
  * It usually contains raw API keys, database passwords, OAuth tokens, or private passphrases. It is often formatted as a simple text file, a `.json`, or a `.env` file inside a project.
* **Common Use Cases:**
  * Storing local environment credentials for a software application.
* *Crucial Warning:* These files are often added to `.gitignore` files so developers don't accidentally leak their passwords on GitHub.
* **Security:** It relies entirely on the security of the filesystem it resides on. If someone gets access to the file, they can usually read it with a basic text editor.

| Extension | Standardized? | Main Ecosystem | What's usually inside? |
| --- | --- | --- | --- |
| **`.jks`** | Yes | Java / Android | Certificates and private keys for Java apps. |
| **`.p12`** | Yes (Universal) | Windows, Mac, Linux, iOS | Certificates and private keys for browsers, VPNs, and servers. |
| **`.secret`** | No | General Development | Raw passwords, API keys, or configuration tokens. |
