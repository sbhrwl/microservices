# Goals of transition
* [Introduction](#introduction)
* [Communication between microservices](#communication-between-microservices)
* [Approach for desiging microservices](#approach-for-desiging-microservices)
* [Best practices](#best-practices)
* [Microservice example: `Generate message`](generatemessage/README.md)
* [Spring framework](springframework/README.md)
* [Protobuf usage](#protobuf-usage)
* [Kafka consumer groups](#kafka-consumer-groups)
## Introduction
* Motivation for shift to microservices
  * Better scalability?
  * Independent deployments?
  * Fault isolation?
  * Tech stack modernization?
  * Integration with cloud-native platforms?
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

## Protobuf usage
* **Internal microservices** often use Protobuf, especially when built with **gRPC** or communicating via **message brokers**.
* Protobuf helps ensure **performance, compactness, and schema evolution** in internal systems.
* [Protobuf setup guide for Java projects](protobuf/README.md)

| Use Case                      | Protobuf Usage | Notes                                                              |
| ----------------------------- | -------------- | ------------------------------------------------------------------ |
| Internal microservices        | ✅ Yes          | Common with gRPC or REST+Protobuf in performance-critical systems  |
| Public REST APIs              | ❌ No           | JSON preferred due to readability and broad client compatibility   |
| Message brokers (Kafka, etc.) | ✅ Yes          | Widely used with schema registries for efficient, compact messages |
| Event-driven architecture     | ✅ Yes          | Used for structured, fast, and evolvable event formats             |

## Kafka consumer groups
- A **Kafka consumer group** is a set of consumers that share the work of consuming messages from topics.
- Each partition of a topic is consumed by only one consumer in the group at a time, enabling **parallel processing** without duplication.
### Analogy
- Think of a consumer group as a team of workers on an assembly line:
  - The topic partitions are conveyor belts carrying tasks (messages).
  - Each worker (consumer) handles tasks from one or more belts.
  - No two workers do the same task, ensuring efficiency and no duplicates.
### Examples
- **Single consumer group:**  
  One group with multiple consumers balances load across partitions.  
  Good for horizontal scaling and simplicity.
- **Multiple consumer groups:**  
  Different groups independently consume the same topic data.  
  Useful if you have different applications or analytics that need all the data separately.
### Decision Points

| Scenario                        | Recommendation                 |
|--------------------------------|-------------------------------|
| Single ingestion service only   | Single consumer group          |
| Multiple independent consumers  | Multiple consumer groups       |
| Need scalability & load balance | Single group with multiple consumers |

---

Choose a **single consumer group** if your ingestion service is the sole consumer and you want easy scaling.

Choose **multiple consumer groups** if different services need the same data independently.