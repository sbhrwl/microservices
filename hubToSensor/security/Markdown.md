# index

* [Problem statement](#problem-statement)
* [Motivation](#motivation)
* [Security chain overview](#security-chain-overview)
* [Cryptography basics](#cryptography-basics)
* [Certificates and pki](#certificates-and-pki)
* [Device to server communication](#device-to-server-communication)
* [Service to service security](#service-to-service-security)
* [Message broker security](#message-broker-security)
* [Database security](#database-security)
* [Authentication and authorization](#authentication-and-authorization)
* [API security](#api-security)
* [Implementation demos](#implementation-demos)
* [Common pitfalls](#common-pitfalls)
* [Takeaways](#takeaways)

# Problem statement
* IoT and smart meters produce sensitive data transmitted to central systems.
* Threats: fake readings, command hijacking, data leaks.
* Critical questions:
  * How to verify device authenticity?
  * How to ensure data integrity?
  * How to secure message brokers and databases?
## Motivation
* End-to-end security ensures trust from device to user.
* Layered security mitigates operational and cybersecurity risks.
## Security chain overview
* End-to-end path: Device → Server → Microservices → Message Broker → Database → UI → User.
* Each layer requires:
  * Encryption (TLS/mTLS)
  * Authentication and authorization
  * Role-based access control (RBAC)
  * Data integrity validation
## Cryptography basics
* Symmetric encryption: same key for encrypt/decrypt.
* Asymmetric encryption: public/private key pairs.
* Digital signatures ensure integrity.
* Hashing prevents tampering.
* Analogy: mailbox key (symmetric) vs signed letter (asymmetric + signature).
## Certificates and PKI
* X.509 certificates: Subject, Issuer, Public Key, Validity.
* Certificate Authorities (CAs) establish trust chains.
* Trust chain: device → CA → server.
* Certificates act as digital passports verifying identity.
## Device to server communication
* Devices transmit data over TLS.
* Certificates verify device identity.
* Prevents MITM attacks and tampering.
* Demo steps:
  * Generate key pair: `openssl genrsa -out device.key 2048`
  * Create CSR: `openssl req -new -key device.key -out device.csr`
  * Sign with CA: `openssl x509 -req -in device.csr -CA ca.crt -CAkey ca.key -out device.crt`
  * Verify certificate: `openssl verify -CAfile ca.crt device.crt`
## Service to service security
* Microservices communicate via mTLS.
* Both client and server certificates ensure mutual authentication.
* Encryption prevents eavesdropping.
* Analogy: each service carries a “passport” for trusted communication.
* Demo: configure mTLS between services, verify mutual handshake.
## Message broker security
* Brokers handle inter-service messaging (Kafka, RabbitMQ).
* Security measures:
  * TLS/mTLS for encrypted communication.
  * Authentication via client certificates, credentials, or SASL.
  * Role-based access control (RBAC) on topics/queues.
* Ensures only authorized services can publish/subscribe.
* Demo: setup broker TLS and RBAC, show publish/subscribe authorization enforcement.
## Database security
* Databases store device and service data securely.
* Security measures:
  * TLS connections for encryption in transit.
  * Client authentication via certificates or credentials.
  * Principle of least privilege for services.
  * Optional column-level encryption for sensitive fields.
* Demo: configure TLS database connection and role-based access for services.
## Authentication and authorization
* Users access dashboards via HTTPS.
* Backend validates identity using OAuth, SSO, or JWT.
* Role-based access controls enforce visibility and operation restrictions.
* Demo: login with JWT, show limited access based on roles.
# API security
* REST API calls secured with HTTPS.
* JWT or API keys enforce authentication and authorization.
* Ensures device data access is restricted to authorized clients.
* Demo: `curl -H "Authorization: Bearer <JWT_TOKEN>" https://server/api/device/123/data`
## Implementation demos
* Device certificate lifecycle: key generation, CSR, CA signing, verification.
* Service-to-service mTLS handshake.
* Broker TLS setup with RBAC enforcement.
* Database TLS connection with role-based access.
* API call with JWT demonstrating restricted data access.
## Common pitfalls
* Default credentials for brokers or databases.
* Unencrypted internal connections.
* Over-permissive RBAC or database roles.
* Expired or weak certificates.
* Lack of consistent layered security enforcement.
## Takeaways
* Devices: keys and certificates for identity and encryption.
* Services: mTLS for trusted service-to-service communication.
* Brokers: TLS, authentication, and RBAC for secure messaging.
* Databases: TLS, user roles, and encryption for data protection.
* Users: HTTPS and JWT/auth for secure access.
* End-to-end trust requires consistent security from devices through services, brokers, databases, and UI to users.
