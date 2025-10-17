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

☁️ Section 4: Containerization & Cloud Deployment (15 min)


---

🐳 Slide 1 – From Code to Container

Visual: Developer → Dockerfile → Image → Container icons.

Key points:

Each service packaged as a Docker image.

Ensures consistent runtime across environments.

Lightweight, fast to deploy, easy to version.


Speaker note:

> “Containers make ‘it works on my machine’ a thing of the past — the same image runs everywhere.”





---

🏗️ Slide 2 – Artifact Registry Integration

Visual: Pipeline pushing Docker images to Artifact Registry.

Key points:

CI pipeline builds and tags images (e.g., bridge:v1.0).

Pushed to secure registry (GCR, ECR, or private).

Versioning allows rollbacks and controlled releases.


Speaker note:

> “Every successful build becomes an artifact — a reusable, traceable snapshot of your code.”





---

⚙️ Slide 3 – Deploying with Helm Charts

Visual: Helm chart box → Kubernetes cluster (multiple pods).

Key points:

Helm manages Kubernetes manifests.

Separates configuration (values.yaml) from templates.

Simplifies upgrades and rollbacks.


Speaker note:

> “Helm is like a package manager for Kubernetes — one command can deploy your whole ecosystem.”





---

🔐 Slide 4 – Secrets, Certificates & Secure Configs

Visual: Locks around ConfigMaps, Secrets, TLS certificates.

Key points:

Secrets managed via Kubernetes Secrets or Vault.

Certificates auto-managed with cert-manager.

TLS and HTTPS ensure encrypted communication.


Speaker note:

> “Every service must assume the network is untrusted — encryption and secret management make it safe.”





---

🚀 Slide 5 – Deployment Flow Overview

Visual: CI/CD flow diagram
Build → Push → Helm Deploy → Monitor.

Key points:

Build artifacts via CI.

Push to registry.

Helm deploys to K8s.

Monitoring ensures rollout health.


Speaker note:

> “This end-to-end automation closes the loop — from developer code to production containers in minutes.”





---

Would you like me to continue with Section 5: CI/CD & DevOps Pipeline (15 min) next?

Perfect 👍 moving on to Section 5: CI/CD & DevOps Pipeline (15 min) — this part ties together automation, quality assurance, and deployment reliability, giving students a real sense of how modern software delivery works.


---

⚙️ Section 5: CI/CD & DevOps Pipeline (15 min)


---

🔄 Slide 1 – What Is CI/CD?

Visual: Infinite loop diagram (CI ↔ CD).

Key points:

CI (Continuous Integration): frequent code merges, testing, and build automation.

CD (Continuous Delivery/Deployment): automatic rollout after tests pass.

Faster feedback, fewer manual errors.


Speaker note:

> “CI/CD is the heartbeat of DevOps — it keeps innovation flowing safely to production.”





---

🧰 Slide 2 – Pipeline Stages

Visual: Pipeline flow (Code → Build → Test → Push → Deploy → Monitor).

Key points:

Code commit: triggers pipeline.

Build stage: creates Docker image.

Test stage: unit, integration, and performance tests.

Push: artifact registry.

Deploy: Helm charts to Kubernetes.

Monitor: live health checks.


Speaker note:

> “Every commit travels this same journey — automated, traceable, and secure.”





---

🧩 Slide 3 – Branching & Environments

Visual: Git branches (feature/* → develop → main → production).

Key points:

Branch strategy ensures code stability.

Deployments to dev, staging, and production.

Approvals before production rollout.


Speaker note:

> “Teams collaborate without stepping on each other’s toes — branches are your safety nets.”





---

🧠 Slide 4 – Automation & Quality Gates

Visual: Jenkins or GitLab CI dashboard mock-up.

Key points:

Static analysis and test coverage checks.

Quality gates prevent regressions.

Automatic rollback if a stage fails.


Speaker note:

> “Automation is trust — every merge is verified before it touches production.”





---

📊 Slide 5 – Monitoring & Feedback Loop

Visual: ELK/Prometheus/Grafana icons with alert arrows.

Key points:

Log aggregation (ELK).

Metrics (Prometheus).

Visualization (Grafana).

Alerts and dashboards for continuous improvement.


Speaker note:

> “DevOps doesn’t stop at deployment — feedback drives the next improvement cycle.”





---

Would you like to proceed to Section 6: Security & Compliance (15 min) next?

Excellent 👌 moving on to Section 6: Security & Compliance (15 min) — this section emphasizes how security is integrated across the system rather than added later, making it both technical and industry-relevant.


---

🔐 Section 6: Security & Compliance (15 min)


---

🧱 Slide 1 – Security by Design

Visual: Shield icon across entire architecture diagram.

Key points:

Security is embedded from the start, not bolted on later.

Every service communicates over secure channels.

Least privilege and isolation at every layer.


Speaker note:

> “We build security into the foundation — not as a patch, but as a principle.”





---

🧍‍♂️ Slide 2 – Authentication & Authorization

Visual: Keycloak icon near UI and API gateway.

Key points:

Keycloak manages users, tokens, and roles.

OAuth2.0 and OpenID Connect for identity.

Role-based access for APIs and UI.


Speaker note:

> “Keycloak ensures only the right users and services access the right data.”





---

🔒 Slide 3 – Secure Communication

Visual: Padlocks on message flows; TLS/mTLS labels on broker links.

Key points:

All service-to-service communication secured via TLS/mTLS.

HTTPS for UI and external access.

Certificates managed via cert-manager in Kubernetes.


Speaker note:

> “Every byte moving between services is encrypted — no plain-text traffic anywhere.”





---

🧰 Slide 4 – Secrets & Certificates Management

Visual: Vault or Kubernetes Secrets icon with rotation arrows.

Key points:

Sensitive data stored in Kubernetes Secrets or HashiCorp Vault.

Automatic rotation and revocation policies.

Environment isolation for secrets.


Speaker note:

> “Secrets are like passwords — they need vaults, not code comments.”





---

🧾 Slide 5 – Compliance & Auditability

Visual: Checklist with compliance icons (ISO 27001, GDPR, etc.).

Key points:

Audit logs capture every command and response.

Traceability across message flow (Broker → Bridge → Adapter → HES).

Aligns with enterprise standards (ISO, NERC CIP, IEC 62351).


Speaker note:

> “Security isn’t just about encryption — it’s about accountability and traceability.”





---

Would you like to proceed next to Section 7: Scalability & Cloud Resilience (10 min)?

Perfect 👌 moving on to Section 7: Scalability & Cloud Resilience (10 min) — focusing on how microservices handle load, failures, and growth in a cloud-native setup.


---

🚀 Section 7: Scalability & Cloud Resilience (10 min)


---

📈 Slide 1 – Horizontal Scaling

Visual: Multiple pods for Protocol Adapter and Flexibility Bridge in Kubernetes.

Key points:

Each service can scale independently based on CPU or message queue depth.

Autoscaling ensures performance under heavy load.


Speaker note:

> “If one service is overloaded, we just spin up more instances — the rest of the system keeps running smoothly.”





---

⚖️ Slide 2 – Load Balancing

Visual: Incoming traffic distributed among service pods.

Key points:

Internal load balancing for microservices.

External load balancing for UI/API endpoints.

Ensures even distribution of requests.


Speaker note:

> “Load balancers prevent hotspots and make sure no single pod becomes a bottleneck.”





---

💥 Slide 3 – Fault Tolerance

Visual: Pod crash icon → self-healing arrow.

Key points:

Kubernetes restarts failed pods automatically.

Dead-letter queues and retry mechanisms for message loss.

Circuit breakers to isolate failing services.


Speaker note:

> “Failures happen — but the system recovers automatically without human intervention.”





---

🔄 Slide 4 – Deployment Strategies

Visual: Blue-green or rolling update illustration.

Key points:

Rolling updates for zero-downtime upgrades.

Blue-green or canary deployments for safe feature rollouts.


Speaker note:

> “We can deploy updates safely without affecting users, which is critical in production systems.”





---

💰 Slide 5 – Cost Optimization

Visual: Cloud resource scaling graph (scale-to-zero, variable pods).

Key points:

Idle services can scale down to reduce cost.

Dynamic scaling ensures optimal cloud resource usage.


Speaker note:

> “Scalability isn’t just about performance — it’s about using resources efficiently to save costs.”





---

Next, we can move to Section 8: Configuration & Service Discovery (5 min).
Do you want me to draft that now?

Perfect 👌 moving on to Section 8: Configuration & Service Discovery (5 min) — focusing on how services find each other and manage environment-specific configurations.


---

⚙️ Section 8: Configuration & Service Discovery (5 min)


---

🗂️ Slide 1 – Environment-Specific Configuration

Visual: ConfigMaps / values.yaml icon → microservices.

Key points:

Configurations separated from code using Helm values or ConfigMaps.

Allows different setups for dev, staging, and production.

No hardcoded secrets or endpoints in the code.


Speaker note:

> “This separation ensures that moving between environments is safe and predictable.”





---

🧭 Slide 2 – Service Discovery

Visual: Kubernetes DNS resolving service names to pods.

Key points:

Microservices discover each other via Kubernetes DNS or internal service registry.

No need to hardcode IP addresses.

Supports scaling: new pods automatically register and are reachable.


Speaker note:

> “Service discovery is like having a dynamic phonebook — any service can find another at runtime.”





---

🌐 Slide 3 – Optional: Service Mesh

Visual: Istio or Linkerd overlay with arrows between services.

Key points:

Handles routing, retries, load balancing, and security automatically.

Adds observability and telemetry without changing service code.


Speaker note:

> “Service mesh is optional, but in large systems it adds an extra layer of resilience and insight.”





---

Next, we can move to Section 9: Live Demo / Walkthrough (10 min).
Shall I draft that next?

Perfect 👌 moving on to Section 9: Live Demo / Walkthrough (10 min) — this section shows the end-to-end flow in action to make the concepts tangible for students.


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

