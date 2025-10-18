# Microservices
* [Motivation](#motivation)
* [Evolution of software architecture](#evolution-of-software-architecture)
* [The monolith era — strengths and struggles](#the-monolith-era--strengths-and-struggles)
* [The shift to microservices](#the-shift-to-microservices)
* [Enabling microservices — devops & cloud](#enabling-microservices--devops--cloud)
* [Scalability and modularity](#scalability-and-modularity)
* [Observability and resilience](#observability-and-resilience)
* [Key takeaway](#key-takeaway)
## Motivation
* Growing software complexity and scalability demands require flexible architectures
* Monoliths struggle with large teams, slow releases, and system-wide failures
* Microservices provide modularity, agility, and independent evolution
## Evolution of software architecture
* Shift from monoliths to microservices and cloud-native systems
* **2000s:** Monoliths
* **2010s:** Microservices adoption
* **2020s:** Cloud-native + DevOps ecosystems
* Goal: improve scalability, resilience, and speed of delivery
* Analogy: monolith as a single building; microservices as independent departments
## The monolith era — strengths and struggles
* Initially simple to develop and deploy
* Challenges:
  * Tight coupling and large codebases
  * Difficult to scale individual features
  * One failure can impact the whole system
  * Slow and risky releases
* *Speaker note:* a small bug can ripple across the system
## The shift to microservices
* Designed to address monolith limitations
* Advantages:
  * Independent development and deployment
  * Fault isolation and resilience
  * Parallel team workflows and faster iterations
* Industry adoption: Netflix, Amazon, Uber
* *Speaker note:* modular services evolve separately, reducing systemic risk
## Enabling microservices — devops & cloud
* Automation and orchestration are critical:
  * CI/CD pipelines: code → build → test → deploy → monitor
  * Cloud-native tools: containers, registries, Kubernetes
* *Speaker note:* microservices without automation and cloud support are hard to sustain
## Scalability and modularity
- Each microservice scales independently.
- Asynchronous messaging provides elasticity.
  - Autoscaling driven by queue depth or CPU usage
- Fault isolation ensures system resilience.
- Optimizes cloud efficiency and responsiveness
- *Speaker note:* Services can scale or upgrade independently without system downtime.
## Observability and resilience
* Centralized logging and metrics collection
* Retry and dead-letter queues for reliability
* Circuit breakers to prevent cascading failures
* *Speaker note:* observability enables proactive system management
## Key takeaway
* Microservices break complexity into manageable, independent units
* Enable agility, reliability, and scalable growth
* *Speaker note:* principles of microservices shape real-world system design and operations
