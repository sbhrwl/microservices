# ServerTlsConfig
- [Overview](#overview)
- [Tls flow](#tls-flow)
- [Code interpretation](#code-interpretation)
- [Important tls settings](#important-tls-settings)
- [Key concept](#key-concept)
- [Related files](#related-files)

# Overview
- `ServerTlsConfig` is the server-side TLS wiring for the Data Hub Simulator.
- It does not process SOAP business logic.
- Its responsibility is:
  - Build the SSL/TLS parameters.
  - Load the server identity.
  - Load trusted client certificate authorities.
  - Configure mutual TLS (mTLS).
  - Provide TLS settings that `Bootstrap` attaches to the HTTPS listener on port `8443`.
- The SOAP endpoint itself is served later by `CamelRoutes`.

# Tls flow

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

    G --> H["Bind TLS params to Jetty port 8443"]
  end

  subgraph "Request time"
    I["Flex Hub Connector client cert"] --> J["TLS handshake"]
    H --> J

    J --> K["ClientAuthentication required"]
    K --> L["Validate client cert chain against truststore"]

    L --> M["CXF HTTPS SOAP endpoint /soap/FGR"]
    M --> N["Log SOAP operation + peer certificate subject DN"]
    N --> O["marketMessagingSoapService"]
  end
```
