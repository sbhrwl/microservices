# Microservices in action
* Each service = single responsibility.
* Decoupling via Message Broker (asynchronous communication).
* Database updates via Storage Service.
* Independent scaling (e.g., multiple Protocol Adapter pods).
* Observability: each service logs independently.
* Resilience: retry, dead-letter queues, and fallback patterns.
---

⚙️ Section 3: Microservices in Action (10 min)


---

🧱 Slide 1 – Why Microservices Matter

Visual: Compare monolith (single box) vs microservices (connected nodes).
Key points:
Each service = single, well-defined responsibility.
Enables parallel development and independent deployment.
Easier fault isolation and scaling.
Speaker note:

> “This shift allows teams to move fast without breaking the entire system — think of each service as its own mini-application.”
---

🔄 Slide 2 – Communication Through the Message Broker

Visual: Message Broker in the center with arrows to multiple services.

Key points:
Asynchronous messaging = decoupling.
Broker ensures delivery even if services restart.
Supports scaling via queue-based load distribution.


Speaker note:

> “Instead of calling each other directly, services talk through the broker — this keeps the system loose and resilient.”
---

💾 Slide 3 – Storage Service Integration

Visual: Storage Service connected to DB with arrows from Bridge and Hub.

Key points:
All DB operations pass through Storage Service.
Maintains data consistency and auditing.
Updates request status at multiple stages.

Speaker note:

> “No service talks directly to the database — the Storage Service acts as our controlled gateway.”





---

🚀 Slide 4 – Independent Scaling

Visual: Kubernetes pods horizontally scaling Protocol Adapter and Bridge.

Key points:
Each service scales independently.
Autoscaling based on message queue depth or CPU load.
Enables efficient cloud resource usage.

Speaker note:

> “If one component faces heavy load — say, Protocol Adapter — we simply add more pods without touching others.”
---

🧠 Slide 5 – Observability & Resilience

Visual: Monitoring dashboard icons (Grafana/Prometheus), retry arrows.

Key points:
Centralized logs and metrics.
Retry and dead-letter queue patterns.
Circuit breakers for resilience.

Speaker note:

> “In production, observability is the oxygen — it’s how we detect failures before users do.”
