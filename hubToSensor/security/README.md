# Security
- [Slides](Slides.md)
- [Markdown](Markdown.md)
### **Slide 1 — Title Slide**
**Title:** Keys, Certificates, and End-to-End Security in IoT & Smart Meters
**Subtitle:** From Device to Microservices, Message Brokers, Databases, and UI
**Content:**
* Your Name / Course / Date
  **Talking Points:**
* Welcome students
* Goal: understand **how cryptography and security measures protect devices, services, brokers, databases, and users**
* Lecture includes **practical demos**
**Visual cue:** IoT devices → cloud → message broker → database → microservices → dashboard illustration
### **Slide 2 — Problem Hook: Why Security Matters**
**Content:**
* Thousands of smart meters collecting and sending data
* Threats:
  * Fake readings
  * Command hijacking
  * Data leaks
* Questions:
  * How do we know a meter is genuine?
  * How do we ensure data integrity?
  * How do we secure the message broker and database storing this data?
**Analogy:** “Receiving a letter from someone claiming to be your bank — how do you know it’s real?”
**Visual cue:** Diagram: meters → central server → broker → database
### **Slide 3 — Overview: Security Chain**
**Content / Visual:**
* Diagram: Device → Server → Microservices → Message Broker → Database → UI → User
* Each layer requires **different security measures**:
  * Keys, certificates, TLS/mTLS
  * Authentication & authorization
  * Role-based access control (RBAC)
  * Data encryption in transit & at rest
### **Slide 4 — Cryptography Basics**
**Bullet Points:**
* Symmetric keys: same key encrypts & decrypts
* Asymmetric keys: public/private pairs
* Digital signatures ensure integrity
* Hashing ensures message authenticity
**Analogy:** Mailbox key (symmetric) vs signed letter (asymmetric + signature)
**Talking Point:** Why **asymmetric keys** are ideal for IoT devices
**Visual cue:** Table comparing symmetric vs asymmetric
### **Slide 5 — Certificates & PKI Basics**
**Bullet Points:**
* X.509 certificates: Subject, Issuer, Public Key, Validity
* Certificate Authorities (CAs)
* Trust chain: device → CA → server
**Analogy:** Certificates = passports issued by government authority
**Visual cue:** Certificate example diagram
### **Slide 6 — Device-to-Server Communication (TLS)**
**Bullet Points:**
* Devices send readings over **TLS**
* Certificates ensure **device identity**
* Prevents MITM attacks and data tampering
**Demo cue:** Terminal example: `openssl s_client -connect server:443`
**Analogy:** Smart meter carries a “passport” — server verifies it
**Visual cue:** Smart meter → TLS → Server diagram
### **Slide 7 — Demo: Device Certificates**
**Demo Steps:**
1. Generate key pair: `openssl genrsa -out device.key 2048`
2. Create CSR: `openssl req -new -key device.key -out device.csr`
3. Sign with CA: `openssl x509 -req -in device.csr -CA ca.crt -CAkey ca.key -out device.crt`
4. Verify certificate: `openssl verify -CAfile ca.crt device.crt`
**Talking Point:** Shows how a smart meter proves identity
**Visual cue:** Terminal screenshots or flow diagram
### **Slide 8 — Service-to-Service Security (mTLS)**
**Bullet Points:**
* Microservices communicate securely with **mTLS**
* Certificates verify identity of both client & server
* Encrypted communication prevents eavesdropping
**Analogy:** Each microservice has a “passport” to prove trust
**Visual cue:** Microservices diagram with mTLS arrows
### **Slide 9 — Message Broker Security**
**Bullet Points:**
* Brokers (Kafka, RabbitMQ) handle **service-to-service communication**
* Security measures:
  * TLS/mTLS for message encryption
  * Authentication via client certificates, username/password, or SASL
  * Role-based access control (RBAC) for topics/queues
**Analogy:** Broker = secure post office that checks sender identity and permissions
**Visual cue:** Microservice → broker with TLS & auth labels
**Demo cue:** Show Kafka or RabbitMQ TLS + RBAC config snippet
### **Slide 10 — Database Security**
**Bullet Points:**
* Databases store device data securely
* Security measures:
  * TLS connections to database
  * Client authentication via certificates or credentials
  * Principle of least privilege (service can only access needed tables/rows)
  * Optional: column-level encryption for sensitive data
**Analogy:** Database = safe deposit box; each service has a key with restricted access
**Visual cue:** Microservices → database with access permissions highlighted
### **Slide 11 — Authentication & Authorization on UI**
**Bullet Points:**
* Users log in via **HTTPS-protected dashboard**
* Backend checks identity (OAuth/SSO/JWT)
* Role-based access ensures users see only allowed meters
**Demo cue:** Screenshot of dashboard showing restricted access
**Talking Point:** Certificates protect channel, auth protects **who can see/control**
### **Slide 12 — API Security Beyond TLS**
**Bullet Points:**
* REST API calls to fetch or send device data
* Use **HTTPS + JWT / API keys**
* Role-based access control ensures only authorized access
**Visual cue:** REST API request diagram: client → HTTPS → server
**Talking Point:** Device security + service security + broker/database + user access = end-to-end trust
### **Slide 13 — Demo Tie-in to UI / REST**
**Demo Steps:**
1. Fetch device data over HTTPS with JWT:

   ```bash
   curl -H "Authorization: Bearer <JWT_TOKEN>" https://server/api/device/123/data
   ```
2. Show only allowed data is returned
**Talking Point:** Demonstrates **end-to-end flow: device → service → broker/database → UI → authorized user**
**Visual cue:** Terminal response or Postman screenshot
### **Slide 14 — Real-World Applications & Pitfalls**
**Bullet Points:**
* IoT firmware updates
* TLS/mTLS for ingestion and service communication
* JWT for UI/API sessions
* Database/broker best practices: avoid default creds, enforce RBAC, encrypt in transit
* Common mistakes: expired certs, weak keys, over-permissive access
**Talking Point:** Emphasize **layered security**: device → service → broker → database → UI
### **Slide 15 — Interactive Recap / Quiz**
**Bullet Points / Questions:**
* Why TLS/mTLS is essential even with device keys?
* Why HTTPS + JWT for UI/API calls?
* How does RBAC help secure message brokers and databases?
* What happens if a CA is compromised?
**Optional:** discuss real-world IoT breaches (misconfigured certs, weak broker security)
### **Slide 16 — Closing Takeaways**
**Bullet Points:**
* Devices: keys + certificates → trust & encryption
* Services: mTLS → secure service-to-service communication
* Brokers: TLS + auth + RBAC → secure messaging
* Databases: TLS + user roles + encryption → secure storage
* Users: HTTPS + JWT/auth → secure access
* **Combined:** end-to-end trust from devices → services → brokers → databases → UI → user
**Visual cue:** Full end-to-end security diagram
