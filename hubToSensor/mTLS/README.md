# One-way and mutual TLS in ActiveMQ
- When securing communication between an **ActiveMQ broker** and a **client**, TLS ensures data is encrypted and that identities can be verified.
- Depending on your security needs, you can configure either **one-way TLS** or **mutual TLS (mTLS)**.
## One-way TLS
- **`Server authentication only)`**
- In one-way TLS, only the **server (ActiveMQ broker)** presents a certificate.
- The client verifies this certificate against its **truststore** to ensure it’s talking to the legitimate broker.
- **Client requirements:**
  - Needs only a **truststore** containing the broker’s certificate (or its CA).
  - Does **not** need its own certificate or private key.
- **Server requirements:**
  - Needs a **keystore** with its certificate and private key to prove its identity.
- **Usage example:**
  - Ideal for standard producer/consumer clients that just need secure communication but don’t need to authenticate themselves individually.

### Mutual TLS (mTLS)
- Mutual TLS adds **client authentication** on top of encryption.
- Both sides — the broker and the client — present and validate certificates.
- **Client requirements:**
  - A **keystore** containing:
    - Its own **certificate**
    - Its **private key**
  - A **truststore** containing the broker’s certificate (or its CA).
- **Server (broker) requirements:**
  - A **keystore** with its certificate and private key.
  - A **truststore** containing the CA or certificates of trusted clients.
- **Example key and certificate setup:**
```bash
# Convert JKS to PKCS12 (includes both cert and private key)
keytool -importkeystore \
  -srckeystore client_xx.ks \
  -srcalias client_xx \
  -destkeystore client_xx.p12 \
  -deststoretype PKCS12
```
- This `.p12` file can then be loaded by your client for TLS authentication.

## Key takeaway

| Mode        | Client certificate? | Client private key? | Use case                                                 |
| ----------- | ------------------- | ------------------- | -------------------------------------------------------- |
| One-way TLS | ❌                   | ❌                   | Basic encryption and server identity verification        |
| Mutual TLS  | ✅                   | ✅                   | Both sides authenticate each other for stronger security |
