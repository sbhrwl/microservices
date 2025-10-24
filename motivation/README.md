# Microservices
* [Motivation for shift to microservices](#motivation-for-shift-to-microservices)
* [Evolution of software architecture](#evolution-of-software-architecture)
* [The monolith era — strengths and struggles](#the-monolith-era--strengths-and-struggles)
* [The shift to microservices](#the-shift-to-microservices)
* [Enabling microservices — devops & cloud](#enabling-microservices--devops--cloud)
* [Scalability and modularity](#scalability-and-modularity)
* [Observability and resilience](#observability-and-resilience)
* [Communication between microservices](#communication-between-microservices)
* [Approach for desiging microservices](#approach-for-desiging-microservices)
* [Best practices](#best-practices)
* [Microservice example: `Generate message`](generatemessage/README.md)
* [Spring framework](springframework/README.md)
* [Protobuf](protobuf/README.md)
  * [gRPC setup](gRPC/README.md)
* [Kafka consumer groups](kafka/README.md)
## Motivation for shift to microservices
* Growing software complexity and scalability demands require flexible architectures
* Monoliths struggle with large teams, slow releases, and system-wide failures
  * Better scalability?
  * Independent deployments?
  * Fault isolation?
  * Tech stack modernization?
  * Integration with cloud-native platforms?
* **Microservices provide modularity, agility, and independent evolution**
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
## Communication between microservices
* **Event-driven** (e.g., using Pub/Sub to trigger each stage asynchronously), or
* **Request-response** (e.g., one microservice calling the next via REST or gRPC)?
## Approach for desiging microservices
* Services to be bounded strictly by **function** (e.g., `parsing, generating, protocol conversion`)?
  * **Pros:**
    * High cohesion and single responsibility.
    * Easier to scale individual pieces (e.g., only scale the Gateway if protocol load is high).
    * Teams can own narrow technical areas.
  * **Cons:**
    * Requires more coordination between services.
    * More inter-service communication (potential latency/debug complexity).
  * **Best when:**
    * You expect uneven loads across stages.
    * You have clear technical boundaries and DevOps maturity.
* Services to be composed around **use-cases** (e.g., “`Command Lifecycle Service`” handling multiple stages)?
  * **Pros:**
    * Fewer services, more end-to-end ownership.
    * Good for early-stage systems or if your team is small.
    * Less orchestration/communication overhead.
  * **Cons:**
    * Can grow into a monolith if not managed well.
    * Harder to isolate performance bottlenecks.
  * **Best when:**
    * Your use cases are strongly coupled.
    * Simpler ops and release cycles are a priority.
## Best practices
* Start with **coarser-grained services** and break them down as you scale.
* Use **domain boundaries** (bounded contexts in DDD) to guide decomposition.
* Observe runtime behavior before splitting (e.g., if `Command Generator` spikes under load, consider isolating XML creation).
* Services that interact with **infrastructure or protocols** (e.g., Protocol Gateway) are often better as independent functions.
