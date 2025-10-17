# Configuration and service discovery
* Config per environment (Helm values, ConfigMaps).
* Service discovery via Kubernetes DNS.
* Optional mention of service mesh (Istio or Linkerd).
---

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




