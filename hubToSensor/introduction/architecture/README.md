## Architecture
- [Overview](#overview)
- [Services and flow of information](#services-and-flow-of-information)
- [Key aspects](#key-aspects)
## Overview
<img src="images/architecture.jpg">

## Services and flow of information
1. **Flexibility Hub Simulator → Message Broker**
   * Publishes flexibility requests/events over **TLS-secured connections**.
2. **Message Broker → Flexibility Bridge**
   * Consumes requests over **TLS**.
   * **Creates request records in the database via Storage Service**.
   * Pushes requests back to the broker for protocol conversion.
3. **Message Broker → Protocol Adapter Service**
   * Consumes requests over **TLS**, converts them to the target protocol, and republishes to the broker.
4. **Message Broker → HES Simulator**
   * Consumes converted requests over **TLS**.
   * Simulates execution and sends a **response** (success/failure) back to the broker.
5. **Message Broker → Protocol Adapter Service**
   * Consumes simulated responses over **TLS**, parses them, and republishes to the broker.
6. **Message Broker → Flexibility Bridge**
   * Consumes parsed responses over **TLS**.
   * **Updates the final status of requests in the database via Storage Service**.
   * Publishes **final responses** to the broker for the Flexibility Hub Simulator.
7. **Message Broker → Flexibility Hub Simulator**
   * Consumes final responses over **TLS** to track the **status of its requests**.
8. **Data API Layer → User Interface**
   * Exposes APIs to fetch request statuses, telemetry, and results.
   * **UI and Data API Layer are secured via Keycloak**.
   * UI is exposed over **HTTPS**.
## Key aspects
* **Storage Service** handles all database operations.
* **HES Simulator** generates responses.
* **Message Broker** orchestrates async communication and is secured via **TLS**.
* **Flexibility Hub Simulator** tracks requests through broker responses.
* **UI and Data API Layer** secured with **Keycloak**, with HTTPS for encrypted client access.
* **[Data store and API design](datastore/README.md)**


---
#### 🖥️ **Slide 1 – System Architecture**

* **Visual:** Clean layered diagram showing all services.
* **Content:**
  * Flex Hub Simulator
  * Message Broker (TLS)
  * Flexibility Bridge
  * Protocol Adapter
  * HES Simulator
  * Storage Service
  * Data API + UI (Keycloak secured)
* **Speaker note:**

  > “This is our end-to-end simulation ecosystem — each box is a microservice working independently yet securely connected through TLS.”

---

#### 🔁 **Slide 2 – Request Flow: From Creation to Response**

* **Visual:** Sequential arrows or animation showing message movement.
* **Flow:**

  1. Flex Hub Simulator creates request → Storage Service (saves to DB)
  2. Request → Message Broker (TLS)
  3. Flexibility Bridge → Protocol Adapter → Message Broker
  4. HES Simulator simulates response → Message Broker
  5. Flexibility Bridge updates status via Storage Service → Message Broker → Flex Hub Simulator
* **Speaker note:**

  > “Here’s how one command travels across services and comes back as a simulated response.”

---

#### 🧩 **Slide 3 – Microservice Roles**

* **Visual:** Table or grid with two columns (Service | Purpose).
* **Examples:**

  * Flex Hub Simulator – Generates requests and listens for results.
  * Flexibility Bridge – Manages orchestration & status updates.
  * Protocol Adapter – Performs protocol conversion.
  * HES Simulator – Mocks the external system’s response.
  * Storage Service – Centralized DB access and updates.
  * Data API + UI – Secure access via Keycloak.
* **Speaker note:**

  > “Notice how each service owns a single responsibility — this is the power of microservices.”

---

#### 🔐 **Slide 4 – Security & Transport**

* **Visual:** Lock icons over lines, Keycloak logo near UI, TLS label near broker.
* **Content:**

  * UI + API secured by Keycloak (OIDC).
  * All broker communications → TLS.
  * HTTPS exposure for UI.
* **Speaker note:**

  > “Security isn’t an afterthought — every service talks over TLS, and access is controlled centrally.”

---

#### ☁️ **Slide 5 – Scalability & Modularity**

* **Visual:** Cloud background, multiple pods of Protocol Adapter/Bridge.
* **Content:**

  * Independent scaling per service.
  * Asynchronous messaging for elasticity.
  * Fault-isolation by design.
* **Speaker note:**

  > “We can scale or upgrade any part without stopping the system — that’s the real-world advantage.”

---

🎬 Section 9: Live Demo / Walkthrough (10 min)


---

🖥️ Slide 1 – End-to-End Flow Overview

Visual: Simplified diagram showing:
Flex Hub Simulator → Message Broker → Flexibility Bridge → Protocol Adapter → HES Simulator → Message Broker → Storage Service → UI.

Speaker note:

> “Here’s the full journey of a request — from creation to final status update in the UI.”





---

🔄 Slide 2 – Step-by-Step Simulation

Visual: Animated arrows or numbered steps showing:

1. Flex Hub Simulator creates request.


2. Storage Service saves request.


3. Message Broker forwards to Flexibility Bridge.


4. Protocol Adapter converts protocol and returns to broker.


5. HES Simulator responds to broker.


6. Flexibility Bridge updates status via Storage Service.


7. Flex Hub Simulator consumes final response.



Speaker note:

> “We can see how microservices interact asynchronously while keeping the system resilient.”





---

🐳 Slide 3 – Deployment Demonstration

Visual: Screenshot or schematic of Helm deployment:

helm install / helm upgrade logs.

Multiple pods for services showing scaling.


Speaker note:

> “This is how we deploy the system in Kubernetes using Helm — everything is automated and repeatable.”





---

📊 Slide 4 – Live Status & Monitoring

Visual: Grafana dashboard or logging console.

Speaker note:

> “While the demo runs, we monitor logs and metrics — verifying performance, errors, and request lifecycle.”





---

This section should tie theory to practice, leaving students with a concrete mental model of your architecture and flow.

Next, we can draft Section 10: Q&A + Career Insights (5 min). Do you want me to do that?

